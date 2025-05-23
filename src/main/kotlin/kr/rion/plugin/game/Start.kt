package kr.rion.plugin.game

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.manager.TeamManager
import kr.rion.plugin.util.Delay
import kr.rion.plugin.util.Global.GameAllReset
import kr.rion.plugin.util.Global.GameAllReset2
import kr.rion.plugin.util.Global.PlayerAllReset
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Global.allUnStopPlayer
import kr.rion.plugin.util.Global.playerInventorySetting
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.stopPlayer
import kr.rion.plugin.util.Helicopter.fillBlocks
import kr.rion.plugin.util.Helicopter.setBlockWithAttributes
import kr.rion.plugin.util.Item.ItemGuideBook
import kr.rion.plugin.util.TPSManager
import kr.rion.plugin.util.Teleport.immunePlayers
import kr.rion.plugin.util.Teleport.initializeSafeLocations
import kr.rion.plugin.util.Teleport.teleportSoleToRandomLocation
import kr.rion.plugin.util.Teleport.teleportTeamToRandomLocation
import org.bukkit.*
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable


object Start {
    var isStart = false //시작작업상태 플래그
    var isStarting = false //게임 상태 플래그
    private val worldWait = Bukkit.getWorld("vip")

    fun startAction() {
        if (TeamGame) {
            Bukkit.broadcastMessage("$prefix §a설정된 게임방식 : §e팀전")
            Delay.delayRun(20) {
                startTeamAction()
            }
        } else {
            Bukkit.broadcastMessage("$prefix §a설정된 게임방식 : §e개인전")
            Delay.delayRun(20) {
                startSoleAction()
            }
        }
    }

    fun startSoleAction() {
        GameAllReset()
        Delay.delayRun(20) {
            initializeSafeLocations(Bukkit.getOnlinePlayers().size)//랜덤좌표 설정
            Delay.delayRun(30) {
                isStart = true
                Delay.delayForEachPlayer(
                    Bukkit.getOnlinePlayers(),
                    action = { player ->
                        player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f)
                        if (!player.scoreboardTags.contains("manager")) {
                            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 300, 1, false, false))
                            stopPlayer(player)
                            immunePlayers.add(player)
                        }
                    },
                    onComplete = {
                        executeBlockFillingAndEffect()
                    }
                )
            }
        }
    }


    fun startTeamAction() {
        GameAllReset()
        TeamManager.resetTeam()
        Delay.delayRun(20) {
            //팀설정시작전 안내
            Bukkit.broadcastMessage("$prefix 랜덤 팀설정을 시작합니다.")
            Delay.delayRun(5) {
                //팀설정시작
                TeamManager.random()
                Delay.delayRun(5) {
                    //팀설정 완료
                    Bukkit.broadcastMessage("$prefix 랜덤 팀설정을 끝냈습니다. 게임맵에서 각팀이 이동될 좌표 선정을 시작합니다.")
                    initializeSafeLocations(TeamManager.getTeamCount())//랜덤좌표 설정
                    Delay.delayRun(30) {
                        isStart = true
                        Delay.delayForEachPlayer(
                            Bukkit.getOnlinePlayers(),
                            action = { player ->
                                player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f)
                                if (!player.scoreboardTags.contains("manager")) {
                                    player.addPotionEffect(
                                        PotionEffect(
                                            PotionEffectType.BLINDNESS,
                                            500,
                                            1,
                                            false,
                                            false
                                        )
                                    )
                                    stopPlayer(player)
                                    immunePlayers.add(player)
                                }
                            },
                            onComplete = {
                                executeBlockFillingAndEffect()
                            },
                            tickGap = 1L
                        )
                    }//랜덤이동좌표선정이후 연출 시작.
                }
            }
        }//게임시작 요청 받은후 1초뒤 팀선정 및 랜덤이동좌표선정 시작
    }


    private fun executeBlockFillingAndEffect() {
        object : BukkitRunnable() {
            var step = 20

            override fun run() {
                if (step > 290) {
                    // 작업 완료 시 추가 작업 실행
                    isStarting = true
                    isStart = false
                    // 블록 처리 완료 후 텔레포트 및 게임 시작 메시지 출력
                    Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                        if (TeamGame) {
                            startTeamAction2()
                        } else {
                            startSoleAction2()
                        }
                    })
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
            30 -> fillBlocksInRange(lobbyWorld, -44, 134, 1, -23, 129, -1)
            40 -> fillBlocksInRange(lobbyWorld, -23, 129, -2, -44, 134, 2)
            50 -> fillBlocksInRange(lobbyWorld, -44, 134, 3, -23, 129, -3)
            60 -> fillBlocksInRange(lobbyWorld, -44, 134, 4, -23, 129, -4)
            70 -> fillBlocksInRange(lobbyWorld, -23, 129, -5, -44, 134, 5)
            80 -> fillBlocksInRange(lobbyWorld, -44, 134, 6, -23, 129, -6)
            90 -> fillBlocksInRange(lobbyWorld, -22, 129, -7, -44, 134, 7)
            100 -> fillBlocksInRange(lobbyWorld, -22, 129, -8, -44, 134, 8)
            110 -> fillBlocksInRange(lobbyWorld, -44, 134, 9, -22, 129, -9)
            120 -> fillBlocksInRange(lobbyWorld, -22, 129, -10, -44, 134, 10)
            130 -> fillBlocksInRange(lobbyWorld, -43, 134, 11, -21, 129, -11)
            140 -> fillBlocksInRange(lobbyWorld, -21, 129, -12, -43, 134, 12)
            150 -> fillBlocksInRange(lobbyWorld, -43, 134, 13, -21, 129, -13)
            160 -> fillBlocksInRange(lobbyWorld, -43, 134, 14, -20, 129, -14)
            170 -> fillBlocksInRange(lobbyWorld, -20, 129, -15, -42, 134, 15)
            180 -> fillBlocksInRange(lobbyWorld, -42, 134, 16, -19, 129, -16)
            190 -> fillBlocksInRange(lobbyWorld, -20, 129, -17, -42, 134, 17)
            200 -> fillBlocksInRange(lobbyWorld, -41, 134, 18, -22, 129, -18)
            210 -> fillBlocksInRange(lobbyWorld, -24, 129, -19, -41, 134, 19)
            220 -> fillBlocksInRange(lobbyWorld, -41, 134, 20, -26, 129, -20)
            230 -> fillBlocksInRange(lobbyWorld, -28, 129, -21, -40, 134, 21)
            240 -> fillBlocksInRange(lobbyWorld, -39, 134, 22, -30, 129, -22)
            250 -> fillBlocksInRange(lobbyWorld, -32, 129, -23, -37, 134, 23)
            260 -> fillBlocksInRange(lobbyWorld, -41, 133, 18, -22, 129, -18)
            270 -> fillBlocksInRange(lobbyWorld, -41, 133, 19, -24, 129, -19)
            280 -> fillBlocksInRange(lobbyWorld, -26, 129, -20, -41, 133, 20)
            290 -> fillBlocksInRange(lobbyWorld, -40, 133, 21, -28, 129, -21)
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
                val batchSize = when {
                    TPSManager.tps > 19.5 -> 100 // TPS 19.5 이상 → 최대 속도로 처리
                    TPSManager.tps > 18 -> 75   // TPS 18~19.5 → 빠른 처리
                    TPSManager.tps > 16 -> 50   // TPS 16~18 → 중간 속도
                    TPSManager.tps > 10 -> 25   // TPS 10~16 → 부하 감소
                    else -> 10  // TPS 10 이하 → 최소 부하 (서버 안정화)
                }



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


    //대기실 문열고 게임시작전 리셋
    fun startSetting() {
        isEnding = false


        fillBlocks(
            Location(worldWait, 23.0, 60.0, -46.0),
            Location(worldWait, 23.0, 57.0, -44.0),
            Material.AIR
        )
        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.AIR)
        GameAllReset2()
    }

    private fun startTeamAction2() {

        startSetting()

        Delay.delayRun(30) {
            PlayerAllReset()
            Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                Bukkit.getLogger().warning("[DEBUG] 팀 갯수 : ${TeamManager.getTeamCount()}")
                TeamManager.getTeamList().forEach { team ->
                    val teamplayers = TeamManager.getPlayerList(team).mapNotNull { playerName ->
                        // 플레이어 이름을 통해 실제 플레이어 객체를 가져옴
                        Bukkit.getPlayer(playerName)
                    }
                    Bukkit.getLogger()
                        .info("[DEBUG] 팀: $team, 팀 플레이어 목록: ${teamplayers.joinToString(", ") { it.name }}")
                    teleportTeamToRandomLocation(teamplayers)
                    Bukkit.getLogger().warning("[DEBUG] $team 이동 완료")
                }

                Delay.delayRun(40) {
                    Delay.delayForEachPlayer(
                        Bukkit.getOnlinePlayers(),
                        action = { player ->
                            if (!player.scoreboardTags.contains("manager")) {
                                MissionManager.assignMission(player) //플레이어에게 미션 부여
                                playerInventorySetting(player)
                                player.inventory.addItem(ItemGuideBook())
                                clearBlindnessProperly(player)
                                player.walkSpeed = 0f
                                player.sendTitle(
                                    "${ChatColor.GREEN}게임을 시작합니다.",
                                    "${ChatColor.YELLOW}상대를 죽이고 탈출수단을 이용해서 이곳을 탈출하세요."
                                )
                                player.playSound(player, "custom.start", SoundCategory.MASTER, 1.0f, 1.0f)
                            } else {
                                // 콘솔 명령어 실행하여 해당 플레이어를 game 월드로 이동
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${player.name} game")
                            }
                        },
                        onComplete = {
                            immunePlayers.clear()
                            allUnStopPlayer()
                        })
                    Delay.delayRun(100) {
                        val world = Bukkit.getWorld("game") ?: return@delayRun
                        val targetLocations = listOf(
                            Location(world, 227.0, 51.0, -669.0),
                            Location(world, -19.0, 61.0, -460.0),
                            Location(world, -38.0, 73.0, -13.0),
                            Location(world, 317.0, 57.0, -213.0)
                        )
                        targetLocations.forEach { loc ->
                            val block = loc.block
                            block.type = Material.LIME_SHULKER_BOX
                            val state = block.state
                            if (state is ShulkerBox) {
                                state.customName = "§e[§6MISSION §aCLEAR §b인증 상자§e]"
                                state.update(true)
                            }
                        }
                    }
                }
            })
        }//연출이 끝난후 플레이어 세팅및 이동시작.
    }

    private fun startSoleAction2() {

        startSetting()

        Delay.delayRun(30) {
            PlayerAllReset()
            Delay.delayForEachPlayer(
                Bukkit.getOnlinePlayers(),
                action = { player ->
                    teleportSoleToRandomLocation(player)
                }
            )

            Delay.delayRun(40) {
                Delay.delayForEachPlayer(
                    Bukkit.getOnlinePlayers(),
                    action = { player ->
                        if (!player.scoreboardTags.contains("manager")) {
                            MissionManager.assignMission(player) //플레이어에게 미션 부여
                            playerInventorySetting(player)
                            player.inventory.addItem(ItemGuideBook())
                            clearBlindnessProperly(player)
                            player.walkSpeed = 0f
                            player.sendTitle(
                                "${ChatColor.GREEN}게임을 시작합니다.",
                                "${ChatColor.YELLOW}상대를 죽이고 탈출수단을 이용해서 이곳을 탈출하세요."
                            )
                            player.playSound(player, "custom.start", SoundCategory.MASTER, 1.0f, 1.0f)
                        } else {
                            // 콘솔 명령어 실행하여 해당 플레이어를 game 월드로 이동
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${player.name} game")
                        }
                    },
                    onComplete = {
                        immunePlayers.clear()
                        allUnStopPlayer()
                    })
                Delay.delayRun(100) {
                    val world = Bukkit.getWorld("game") ?: return@delayRun
                    val targetLocations = listOf(
                        Location(world, 227.0, 51.0, -669.0),
                        Location(world, -19.0, 61.0, -460.0),
                        Location(world, -38.0, 73.0, -13.0),
                        Location(world, 317.0, 57.0, -213.0)
                    )
                    targetLocations.forEach { loc ->
                        val block = loc.block
                        block.type = Material.LIME_SHULKER_BOX
                        val state = block.state
                        if (state is ShulkerBox) {
                            state.customName = "§e[§6MISSION §aCLEAR §b인증 상자§e]"
                            state.update(true)
                        }
                    }
                }
            }
        }//연출이 끝난후 플레이어 세팅및 이동시작.
    }

    fun clearBlindnessProperly(player: Player) {
        // 1. 실명 제거 시도
        player.removePotionEffect(PotionEffectType.BLINDNESS)

        // 2. 억지로 실명 1틱 넣고 다시 제거
        player.addPotionEffect(
            PotionEffect(PotionEffectType.BLINDNESS, 1, 0, false, false)
        )

        // 3. delayRun: 실명 재제거 + 이동속도/스프린트 초기화
        Delay.delayRun(2L) {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            player.walkSpeed = 0.2f
            player.isSprinting = false

            // 4. 클라 스프린트 상태 다시 활성화
            Delay.delayRun(2L) {
                player.isSprinting = true
            }
        }
    }

}