package kr.rion.plugin.item

import kr.rion.plugin.Loader
import kr.rion.plugin.util.Helicopter
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object FlameGunActions {

    fun launchFlare(player: Player) {
        // 플레이어에게 사운드 재생
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.5f)
            playerall.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f)
        }

        val flamegun = NamespacedKey("EscapeShow", "flamegun")
        // 사용한 아이템의 메타데이터 변경
        val item = player.inventory.itemInMainHand
        val itemMeta = item.itemMeta ?: return
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.ITALIC}${ChatColor.STRIKETHROUGH}플레어건")
        itemMeta.persistentDataContainer.remove(flamegun)
        item.itemMeta = itemMeta


        val initialLoc = player.location.clone().add(0.0, 1.0, 0.0) // 플레이어 머리 위치 (약간 위로) 복제본 생성
        val particleData1 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f)
        val particleData2 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f)

        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd1 =
            "summon minecraft:armor_stand ${initialLoc.x} ${initialLoc.y} ${initialLoc.z} {Tags:[helicop],Invisible:1b}" // 실행할 명령어
        val cmd2 = "function server_datapack:flare"
        Bukkit.dispatchCommand(console, cmd1)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
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
                        Bukkit.dispatchCommand(console, cmd2)
                        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                            Helicopter.spawn(initialLoc.clone().add(0.0, 50.0, 0.0))
                            startEscape(player)
                        }, 4 * 20L)
                        cancel()


                    }
                }
            }.runTaskTimer(Loader.instance, 0L, 1L)
        }
    }

    fun startEscape(player: Player) {
        val startLocation = player.location.clone()  // 시작 시 위치 저장
        val escapeDuration = 12L// 3초간 대기 (20틱 = 1초, 3초 = 60틱)
        var taskCancelled = false

        // 파티클 소환 및 탈출 체크
        object : BukkitRunnable() {
            var tickCount = 0L

            override fun run() {
                if (taskCancelled) {
                    cancel()
                    return
                }


                // 플레이어가 움직였는지 확인
                if (player.location.distance(startLocation) > 0.5) {
                    try {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}탈출 실패!"))
                    } catch (e: Exception) {
                        Bukkit.getLogger().warning("액션바 전송 중 오류 발생: ${e.message}")
                    }
                    taskCancelled = true
                    cancel()
                    return
                }

                // 플레이어 위치에서 50칸 위까지 1자로 파티클 소환
                try {
                    for (i in 0..50) {
                        val particleLocation = player.location.clone().add(0.0, i.toDouble(), 0.0)
                        player.world.spawnParticle(Particle.END_ROD, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
                    }
                } catch (e: Exception) {
                    Bukkit.getLogger().warning("파티클 생성 중 오류 발생: ${e.message}")
                }

                // 3초가 지났으면 탈출 성공 처리
                tickCount += 1
                if (tickCount >= escapeDuration) {
                    try {
                        player.sendTitle("${ChatColor.GREEN}탈출 성공!", "", 10, 70, 20)
                        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                        player.addScoreboardTag("Escape")
                    } catch (e: Exception) {
                        Bukkit.getLogger().warning("타이틀 전송 중 오류 발생: ${e.message}")
                    }
                    taskCancelled = true
                    cancel()
                }
            }
        }.runTaskTimer(Loader.instance, 0L, 5L)  // 매 1초마다 실행
    }

}