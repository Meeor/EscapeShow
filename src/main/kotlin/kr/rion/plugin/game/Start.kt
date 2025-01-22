package kr.rion.plugin.game

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End.EscapePlayers
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.game.Reset.resetplayerAttribute
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestEnable
import kr.rion.plugin.item.FlameGunActions.playersAtParticle
import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.manager.MissionManager.listMission
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Global.EscapePlayerCount
import kr.rion.plugin.util.Global.door
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Helicopter.fillBlocks
import kr.rion.plugin.util.Helicopter.setBlockWithAttributes
import kr.rion.plugin.util.Item.createCustomItem
import kr.rion.plugin.util.Teleport.stopPlayer
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable


object Start {
    var isStart = false //시작작업상태 플래그
    var isStarting = false //게임 상태 플래그
    var startportal = false //텔레포트 플래그
    private val worldWait = Bukkit.getWorld("vip")

    fun startAction() {
        stopPlayer.clear()
        MissionManager.resetMissions()
        isStart = true
        startportal = true
        executeBlockFillingAndEffect()
        isEnding = false
        chestEnable = false
        bossbarEnable = 0 // 보스바 업데이트 종료
        EscapePlayerCount = 0
        fillBlocks(
            Location(worldWait, 23.0, 60.0, -46.0),
            Location(worldWait, 23.0, 57.0, -44.0),
            Material.AIR
        )
        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.AIR)
        door = true

        val craftingItem = createCustomItem(
            "${ChatColor.GREEN}아이템 조합",
            listOf("${ChatColor.YELLOW}클릭시 조합창이 오픈됩니다."),
            Material.SLIME_BALL
        )
        val bookAndQuill = createCustomItem(
            "${ChatColor.GREEN}미션",
            listOf("${ChatColor.YELLOW}현재 본인이 받은 미션을 확인합니다.", "", "${ChatColor.RED}진행상황은 표시되지 않습니다!"),
            Material.WRITABLE_BOOK
        )
        val barrier = createCustomItem("${ChatColor.RED}사용할수 없는칸", emptyList(), Material.BARRIER)
        Bukkit.getScheduler().runTask(Loader.instance, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                removeDirectionBossBar(player)
                if (!player.scoreboardTags.contains("manager")) {
                    player.allowFlight = false
                    player.isFlying = false
                    player.gameMode = GameMode.SURVIVAL
                    // manager 태그가 없는 플레이어의 태그 모두 제거
                    player.scoreboardTags.clear()
                    MissionManager.assignMission(player) //플레이어에게 미션 부여
                    player.inventory.clear()
                    for (i in 9..35) {
                        when (i) {
                            20 -> player.inventory.setItem(i, bookAndQuill) // 20번 슬룻에 미션책
                            24 -> player.inventory.setItem(i, craftingItem) // 24번 슬롯에 제작아이템
                            else -> player.inventory.setItem(i, barrier) // 나머지 슬롯에 방벽
                        }
                    }
                }
            }
        })
        resetplayerAttribute()
        playersAtParticle.clear()
        EscapePlayers.clear()
        reviveFlags.clear()
        Bukkit.getLogger().warning("${listMission()}")
    }


    private fun executeBlockFillingAndEffect() {
        object : BukkitRunnable() {
            var step = 20

            override fun run() {
                // 월드를 '로비'로 고정
                val lobbyWorld: World = Bukkit.getWorld("lobby") ?: run {
                    Bukkit.getLogger().severe("로비 월드를 찾을 수 없습니다!")
                    return
                }
                if (step > 155) {
                    // 작업 완료 시 추가 작업 실행
                    isStarting = true
                    isStart = false
                    for (player: Player in Bukkit.getOnlinePlayers()) {
                        if (player.scoreboardTags.contains("manager")) {
                            // 콘솔 명령어 실행하여 해당 플레이어를 game 월드로 이동
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${player.name} game")
                        }
                    }
                    object : BukkitRunnable() {
                        override fun run() {
                            for (player in Bukkit.getOnlinePlayers()) {
                                stopPlayer[player] = false
                                player.removePotionEffect(PotionEffectType.BLINDNESS)
                            }
                        }
                    }.runTaskLater(Loader.instance, 20L * 7)
                    // 3분 후 isStart를 false로 설정하는 작업 추가
                    object : BukkitRunnable() {
                        override fun run() {
                            startportal = false
                            stopPlayer.clear()
                        }
                    }.runTaskLater(Loader.instance, 20L * 180) // 3분 (180초) 후 실행
                    cancel() // 반복 종료
                    return
                }

                fillBlocksWithDestroyEffect(step, lobbyWorld)
                step += 5 // 5단계씩 증가
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 0틱 이후 실행, 1틱 간격으로 실행
    }

    private fun fillBlocksWithDestroyEffect(step: Int, world: World) {
        // 단계별 블록 범위 매핑
        // 단계별 블록 범위 매핑
        val blockRanges = mapOf(
            20 to Triple(-23 to -44, 129 to 134, 0 to 0),
            25 to Triple(-44 to -23, 134 to 129, 1 to -1),
            30 to Triple(-23 to -44, 129 to 134, -2 to 2),
            35 to Triple(-44 to -23, 134 to 129, 3 to -3),
            40 to Triple(-44 to -23, 134 to 129, 4 to -4),
            45 to Triple(-23 to -44, 129 to 134, -5 to 5),
            50 to Triple(-44 to -23, 134 to 129, 6 to -6),
            55 to Triple(-22 to -44, 129 to 134, -7 to 7),
            60 to Triple(-22 to -44, 129 to 134, -8 to 8),
            65 to Triple(-44 to -22, 134 to 129, 9 to -9),
            70 to Triple(-22 to -44, 129 to 134, -10 to 10),
            75 to Triple(-43 to -21, 134 to 129, 11 to -11),
            80 to Triple(-21 to -43, 129 to 134, -12 to 12),
            85 to Triple(-43 to -21, 134 to 129, 13 to -13),
            90 to Triple(-43 to -20, 134 to 129, 14 to -14),
            95 to Triple(-20 to -42, 129 to 134, -15 to 15),
            100 to Triple(-42 to -19, 134 to 129, -16 to 16),
            105 to Triple(-20 to -42, 129 to 134, -17 to 17),
            110 to Triple(-41 to -22, 134 to 129, 18 to -18),
            115 to Triple(-24 to -41, 129 to 134, -19 to 19),
            120 to Triple(-41 to -26, 134 to 129, 20 to -20),
            125 to Triple(-28 to -40, 129 to 134, -21 to 21),
            130 to Triple(-39 to -30, 134 to 129, 22 to -22),
            135 to Triple(-32 to -37, 129 to 134, -23 to 23),
            140 to Triple(-41 to -22, 133 to 129, 18 to -18),
            145 to Triple(-41 to -24, 133 to 129, 19 to -19),
            150 to Triple(-26 to -41, 129 to 133, -20 to 20),
            155 to Triple(-40 to -28, 133 to 129, 21 to -21)
        )


        // 현재 단계에 해당하는 범위를 가져옴
        val ranges = blockRanges[step] ?: return
        val (xRange, yRange, zRange) = ranges

        // 범위 내의 모든 블록을 리스트에 추가
        val blocks = mutableListOf<Location>()
        for (x in xRange.first..xRange.second) {
            for (y in yRange.first..yRange.second) {
                for (z in zRange.first..zRange.second) {
                    blocks.add(world.getBlockAt(x, y, z).location)
                }
            }
        }

        // 블록 파괴와 소리 출력 (범위당 소리 1회 출력)
        object : BukkitRunnable() {
            var index = 0
            var soundPlayed = false // 소리 출력 여부
            override fun run() {
                val batchSize = 50 // 한 번에 처리할 블록 수
                for (i in 0 until batchSize) {
                    if (index >= blocks.size) {
                        cancel() // 모든 블록 처리가 완료되면 반복 종료
                        return
                    }

                    val location = blocks[index]
                    location.block.type = Material.AIR // 블록 파괴

                    // 첫 번째 블록 처리 시 소리 출력
                    if (!soundPlayed) {
                        playSoundToAllPlayers(location, Sound.BLOCK_WOOD_BREAK)
                        soundPlayed = true // 소리 출력 완료로 설정
                    }

                    index++
                }
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 1틱 간격으로 실행
    }
    private fun playSoundToAllPlayers(location: Location, sound: Sound) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(location, sound, 1.0f, 1.0f)
        }
    }


}