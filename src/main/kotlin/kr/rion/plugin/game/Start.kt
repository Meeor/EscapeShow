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
        for (player in Bukkit.getOnlinePlayers()) {
            player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f)
        }
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
            "${ChatColor.GREEN}조합아이템",
            listOf("${ChatColor.YELLOW}손에들고 우클릭시 조합창을 오픈합니다."),
            Material.SLIME_BALL
        )
        val map = createCustomItem(
            "${ChatColor.GREEN}지도",
            listOf("${ChatColor.YELLOW}클릭시 맵 전체 지도를 확인할수있습니다."),
            Material.MOJANG_BANNER_PATTERN
        )
        val barrier = createCustomItem("${ChatColor.RED}사용할수 없는칸", emptyList(), Material.BARRIER)
        Bukkit.getScheduler().runTask(Loader.instance, Runnable{
            for (player in Bukkit.getOnlinePlayers()) {
                removeDirectionBossBar(player)
                if (!player.scoreboardTags.contains("manager")) {
                    player.allowFlight = false
                    player.isFlying = false
                    player.gameMode = GameMode.SURVIVAL
                    // manager 태그가 없는 플레이어의 태그 모두 제거
                    MissionManager.assignMission(player) //플레이어에게 미션 부여
                    player.scoreboardTags.clear()
                    player.inventory.clear()
                    for (i in 8..35) {
                        when (i) {
                            24 -> player.inventory.setItem(i, map) // 24번 슬롯에 지도
                            8 -> player.inventory.setItem(i, craftingItem) // 8번 슬롯에 제작 아이템
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
                            for(player in Bukkit.getOnlinePlayers()){
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

                fillBlocksWithDestroyEffect(step)
                step += 5 // 5단계씩 증가
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 0틱 이후 실행, 1틱 간격으로 실행
    }

    // 특정 단계에서 블록을 공기로 설정하고 파괴 효과 적용
    private fun fillBlocksWithDestroyEffect(step: Int) {
        // 월드를 '로비'로 고정
        val lobbyWorld: World = Bukkit.getWorld("lobby") ?: run {
            Bukkit.getLogger().severe("로비 월드를 찾을 수 없습니다!")
            return
        }

        // 각 단계별 블록 좌표 처리
        when (step) {
            20 -> fillBlocksInRange(lobbyWorld, -23, 129, 0, -44, 134, 0)
            25 -> fillBlocksInRange(lobbyWorld, -44, 134, 1, -23, 129, -1)
            30 -> fillBlocksInRange(lobbyWorld, -23, 129, -2, -44, 134, 2)
            35 -> fillBlocksInRange(lobbyWorld, -44, 134, 3, -23, 129, -3)
            40 -> fillBlocksInRange(lobbyWorld, -44, 134, 4, -23, 129, -4)
            45 -> fillBlocksInRange(lobbyWorld, -23, 129, -5, -44, 134, 5)
            50 -> fillBlocksInRange(lobbyWorld, -44, 134, 6, -23, 129, -6)
            55 -> fillBlocksInRange(lobbyWorld, -22, 129, -7, -44, 134, 7)
            60 -> fillBlocksInRange(lobbyWorld, -22, 129, -8, -44, 134, 8)
            65 -> fillBlocksInRange(lobbyWorld, -44, 134, 9, -22, 129, -9)
            70 -> fillBlocksInRange(lobbyWorld, -22, 129, -10, -44, 134, 10)
            75 -> fillBlocksInRange(lobbyWorld, -43, 134, 11, -21, 129, -11)
            80 -> fillBlocksInRange(lobbyWorld, -21, 129, -12, -43, 134, 12)
            85 -> fillBlocksInRange(lobbyWorld, -43, 134, 13, -21, 129, -13)
            90 -> fillBlocksInRange(lobbyWorld, -43, 134, 14, -20, 129, -14)
            95 -> fillBlocksInRange(lobbyWorld, -20, 129, -15, -42, 134, 15)
            100 -> fillBlocksInRange(lobbyWorld, -42, 134, 16, -19, 129, -16)
            105 -> fillBlocksInRange(lobbyWorld, -20, 129, -17, -42, 134, 17)
            110 -> fillBlocksInRange(lobbyWorld, -41, 134, 18, -22, 129, -18)
            115 -> fillBlocksInRange(lobbyWorld, -24, 129, -19, -41, 134, 19)
            120 -> fillBlocksInRange(lobbyWorld, -41, 134, 20, -26, 129, -20)
            125 -> fillBlocksInRange(lobbyWorld, -28, 129, -21, -40, 134, 21)
            130 -> fillBlocksInRange(lobbyWorld, -39, 134, 22, -30, 129, -22)
            135 -> fillBlocksInRange(lobbyWorld, -32, 129, -23, -37, 134, 23)
            140 -> fillBlocksInRange(lobbyWorld, -41, 133, 18, -22, 129, -18)
            145 -> fillBlocksInRange(lobbyWorld, -41, 133, 19, -24, 129, -19)
            150 -> fillBlocksInRange(lobbyWorld, -26, 129, -20, -41, 133, 20)
            155 -> fillBlocksInRange(lobbyWorld, -40, 133, 21, -28, 129, -21)
        }
    }

    private fun fillBlocksInRange(world: World, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        val blocks = mutableListOf<Location>()
        val soundloc = Location(world, x1.toDouble(), y1.toDouble(), z1.toDouble())

        // 범위 내의 모든 블록을 리스트에 추가
        for (x in x1.coerceAtMost(x2)..x1.coerceAtLeast(x2)) {
            for (y in y1.coerceAtMost(y2)..y1.coerceAtLeast(y2)) {
                for (z in z1.coerceAtMost(z2)..z1.coerceAtLeast(z2)) {
                    blocks.add(world.getBlockAt(x, y, z).location)
                }
            }
        }

        // 일정 간격으로 블록을 처리
        object : BukkitRunnable() {
            var index = 0
            override fun run() {
                val batchSize = 50 // 한 번에 처리할 블록 수

                for (i in 0 until batchSize) {
                    if (index >= blocks.size) {
                        // 모든 블록 처리 완료 시, 소리 한 번 실행
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.playSound(soundloc, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f)
                        }
                        cancel() // 모든 블록 처리 후 반복 종료
                        return
                    }
                    val location = blocks[index]
                    val block = world.getBlockAt(location)
                    block.type = Material.AIR // 블록을 공기로 변경
                    index++
                }
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 1틱 간격으로 실행
    }

}