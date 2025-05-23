package kr.rion.plugin.item

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestEnable
import kr.rion.plugin.util.Delay
import kr.rion.plugin.util.Global.EscapePlayerCount
import kr.rion.plugin.util.Global.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.adjustToAboveSpecificBlock
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Helicopter
import kr.rion.plugin.util.Helicopter.HelicopterLoc
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import kotlin.math.abs

object FlameGunActions {
    var startEscape = false
    var startEscapecheck = false
    var flaregunstart: BukkitTask? = null
    lateinit var EscapeLocation: Location
    val playersAtParticle = mutableSetOf<Player>()

    fun launchFlare(player: Player) {
        // 플레이어에게 사운드 재생
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.5f)
            playerall.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f)
        }


        // 사용한 아이템의 메타데이터 변경
        val item = player.inventory.itemInMainHand
        val itemMeta = item.itemMeta ?: return
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.ITALIC}${ChatColor.STRIKETHROUGH}플레어건")
        item.itemMeta = itemMeta

        val nbtItem = NBTItem(item)
        nbtItem.setBoolean("flamegun", false)
        item.itemMeta = nbtItem.item.itemMeta


        val initialLoc = player.location.clone().add(0.0, 1.0, 0.0) // 플레이어 머리 위치 (약간 위로) 복제본 생성
        val particleData1 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f)
        val particleData2 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f)

        Delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { onlinePlayer ->
                object : BukkitRunnable() {
                    private var t = 0.0
                    private val loc = initialLoc.clone()

                    override fun run() {
                        t += 1
                        loc.add(0.0, 1.0, 0.0) // 매 틱마다 위치를 위로 이동
                        onlinePlayer.world.spawnParticle(
                            Particle.CLOUD,
                            loc,
                            2,
                            0.0,
                            0.6,
                            0.0,
                            0.0
                        )
                        onlinePlayer.world.spawnParticle(
                            Particle.FLAME,
                            loc,
                            2,
                            0.0,
                            0.6,
                            0.0,
                            0.0
                        )
                        // 80틱 작업 끝나면 중지
                        if (t > 120) {
                            val movedLoc = loc.clone().add(0.0, -5.0, 0.0)
                            onlinePlayer.world.spawnParticle(
                                Particle.REDSTONE,
                                movedLoc,
                                50,
                                1.0,
                                1.0,
                                1.0,
                                0.1,
                                particleData1,
                                true
                            )
                            onlinePlayer.world.spawnParticle(
                                Particle.REDSTONE,
                                loc,
                                50,
                                0.0,
                                0.6,
                                0.0,
                                0.1,
                                particleData2,
                                true
                            )
                            if (!startEscapecheck) {
                                startEscapecheck = true
                                Bukkit.broadcastMessage("${ChatColor.BOLD}${ChatColor.YELLOW}헬기가 오고있습니다..")
                                Delay.delayRun(30 * 20L) {

                                    val newLocation: Location? = initialLoc.world?.let {
                                        adjustToAboveSpecificBlock(
                                            it,
                                            initialLoc.clone().subtract(0.0, 1.0, 0.0),
                                            Material.CAVE_AIR
                                        )
                                    }

                                    if (newLocation == null) {
                                        // 실패 시 작업 수행
                                        Bukkit.broadcastMessage("$prefix §c헬기 도착 위치를 설정하지 못했습니다. 헬기 호출이 중단됩니다.")
                                        chestEnable = false // 플레어건 상자 소환 초기화
                                        Bukkit.getOnlinePlayers().filter { it.scoreboardTags.contains("manager") }
                                            .forEach { managerPlayer ->
                                                managerPlayer.sendMessage("§l§b헬기 랜덤 좌표 생성을 실패하였습니다.")
                                                managerPlayer.sendMessage("§l§b플레어건 상자 소환이 초기화되었습니다. 재소환하실 수 있습니다.")
                                            }
                                        cancel()
                                        return@delayRun // 조기 종료
                                    }

                                    EscapeLocation = newLocation


                                    Helicopter.spawn(
                                        EscapeLocation.clone().add(0.0, 51.0, 0.0)
                                    )
                                    startEscape(player)
                                    startEscape = true
                                    startEscapecheck = false
                                }
                            }
                            cancel()
                        }
                    }
                }.runTaskTimer(Loader.instance, 0L, 1L)
            }
        )
    }

    fun startEscape(player: Player) {
        val escapeDuration = 3L // 1초간 대기

        if (startEscape) {
            player.sendMessage("$prefix ${ChatColor.RED}이미 헬기가 소환되어있습니다!")
            return  // 탈출이 이미 진행 중이면 함수 종료
        }


        flaregunstart = object : BukkitRunnable() {
            var tickCount = 0L
            val playerTickCount = mutableMapOf<Player, Long>() // 플레이어가 움직이지 않은 시간 기록
            val failedPlayers = mutableSetOf<Player>() // 탈출 실패 메시지를 한 번만 보낸 플레이어 목록'

            override fun run() {
                if (EscapePlayerCount >= EscapePlayerMaxCount) {
                    this.cancel()
                    return
                }
                // 모든 플레이어가 탈출 인식 대상
                Bukkit.getOnlinePlayers()
                    .filter {
                        !it.scoreboardTags.contains("manager") &&
                                !it.scoreboardTags.contains("EscapeComplete") &&
                                !it.scoreboardTags.contains("death") &&
                                !it.scoreboardTags.contains("DeathAndAlive") &&
                                !it.scoreboardTags.contains("MissionSuccessEscape")
                    }
                    .forEach { currentPlayer ->

                        // HelicopterLoc 변수가 null인 경우 처리 (헬기 아래 파티클 위치)
                        val startLocation = EscapeLocation.clone().subtract(0.0, 1.0, 0.0)
                        // 플레이어의 현재 위치와 HelicopterLoc 비교
                        val currentLocation = currentPlayer.location

                        // 플레이어가 처음 파티클 위치에 도달한 경우, playersAtParticle에 추가
                        if (!playersAtParticle.contains(currentPlayer) && currentLocation.distance(startLocation) <= 1.0) {
                            playersAtParticle.add(currentPlayer)
                        }
                        if (playersAtParticle.contains(currentPlayer)) { //파티클에 도달한적 있는플레이어만 인식.

                            // 플레이어가 playerloc에서 1칸 이상 움직였는지 확인(y좌표는 위아래로1씩 추가확인)
                            if (abs(currentLocation.y - startLocation.y) <= 1.5 &&
                                currentLocation.distance(startLocation) > 1
                            ) {
                                // 탈출 실패 메시지를 한 번만 보내기 위해 확인
                                if (!failedPlayers.contains(currentPlayer)) {
                                    try {
                                        currentPlayer.spigot()
                                            .sendMessage(
                                                ChatMessageType.ACTION_BAR,
                                                TextComponent("${ChatColor.RED}탈출 실패!")
                                            )
                                        failedPlayers.add(currentPlayer) // 실패한 플레이어 등록
                                    } catch (e: Exception) {
                                        Bukkit.getLogger().warning("액션바 전송 중 오류 발생: ${e.message}")
                                    }
                                }
                                // 탈출 실패로 인한 카운터 초기화
                                playerTickCount[currentPlayer] = 0L
                            } else {
                                // 플레이어가 playerloc 근처에서 가만히 있었을 경우
                                playerTickCount[currentPlayer] = playerTickCount.getOrDefault(currentPlayer, 0L) + 1
                                // 실패한 플레이어가 파티클 위치 근처에 다시 있으면 failedPlayers에서 제거
                                if (failedPlayers.contains(currentPlayer)) {
                                    failedPlayers.remove(currentPlayer)
                                }
                            }


                            // 3초 동안 가만히 있었으면 탈출 성공 처리
                            if (playerTickCount[currentPlayer]!! >= escapeDuration) {
                                try {
                                    currentPlayer.sendTitle("${ChatColor.GREEN}탈출 성공!", "", 10, 70, 20)
                                    currentPlayer.playSound(
                                        currentPlayer.location,
                                        Sound.ENTITY_PLAYER_LEVELUP,
                                        1.0f,
                                        1.0f
                                    )
                                    currentPlayer.addScoreboardTag("Escape")
                                    playerTickCount[currentPlayer] = 0L // 성공 후 카운터 초기화
                                } catch (e: Exception) {
                                    Bukkit.getLogger().warning("타이틀 전송 중 오류 발생: ${e.message}")
                                }
                            }
                        }
                    }
                Delay.delayForEachPlayer(
                    Bukkit.getOnlinePlayers(),
                    action = {
                        // 파티클 소환 (HelicopterLoc 기준, 50칸 아래로)
                        if (HelicopterLoc != null) {
                            try {
                                for (i in 0..55) {
                                    val particleLocation = HelicopterLoc!!.clone().subtract(0.0, i.toDouble(), 0.0)
                                    HelicopterLoc!!.world?.spawnParticle(
                                        Particle.END_ROD,
                                        particleLocation,
                                        1,
                                        0.0,
                                        0.0,
                                        0.0,
                                        0.0
                                    )
                                }
                            } catch (e: Exception) {
                                Bukkit.getLogger().warning("파티클 생성 중 오류 발생: ${e.message}")
                            }
                        } else {
                            Bukkit.getLogger().warning("HelicopterLoc의 저장된 좌표가 없습니다. 파티클을 생성할 수 없습니다.")
                        }
                    }
                )
                tickCount += 1
            }
        }.runTaskTimer(Loader.instance, 0L, 20L)  // 매 1초마다 실행
    }

}