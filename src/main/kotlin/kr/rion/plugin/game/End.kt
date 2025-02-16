package kr.rion.plugin.game

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Reset.handleGameReset
import kr.rion.plugin.game.Reset.resetplayerAttribute
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.game.Start.startportal
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestEnable
import kr.rion.plugin.item.FlameGunActions.playersAtParticle
import kr.rion.plugin.manager.ChunkyManager.loadChunkyForWorld
import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Global
import kr.rion.plugin.util.Global.EscapePlayerCount
import kr.rion.plugin.util.Global.cancelAllTasks
import kr.rion.plugin.util.Global.door
import kr.rion.plugin.util.Global.playerItem
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Global.survivalPlayers
import kr.rion.plugin.util.Helicopter
import kr.rion.plugin.util.Helicopter.HelicopterisSpawn
import kr.rion.plugin.util.Helicopter.fillBlocks
import kr.rion.plugin.util.Helicopter.setBlockWithAttributes
import kr.rion.plugin.util.Teleport.stopPlayer
import kr.rion.plugin.util.Teleport.tpstopPlayer
import org.bukkit.*

object End {
    var isEnding: Boolean = true
    var ifEnding: Boolean = false

    var EscapePlayers: MutableList<String> = mutableListOf()
    private var SurvivalPlayers: MutableList<String> = mutableListOf()
    private val soundName = "custom.bye"
    fun EndAction() {
        val world = Bukkit.getWorld("game")  // Multiverse 대신 Bukkit API로 월드 가져오기
        val worldWait = Bukkit.getWorld("vip") // vip 월드도 동일하게 Bukkit API로 가져옴

        // 월드가 null인 경우 로그 출력
        if (world == null) {
            Bukkit.getLogger().warning("game 월드를 가져오지 못했습니다. worldManager 또는 해당 월드 확인 필요.")
        }
        if (worldWait == null) {
            Bukkit.getLogger().warning("vip 월드를 가져오지 못했습니다. worldManager 또는 해당 월드 확인 필요.")
        }
        fillBlocks(Location(worldWait, 23.0, 60.0, -46.0), Location(worldWait, 23.0, 57.0, -44.0), Material.OAK_FENCE)
        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.OAK_FENCE)
        //게임종료후 각종변수및 정보들 리셋작업.
        EscapePlayerCount = 0
        Helicopter.remove()
        HelicopterisSpawn = false
        //플레어건 탈출파티클 끝내기
        ifEnding = false
        startportal = false
        isEnding = true
        chestEnable = false
        door = true
        bossbarEnable = 0
        stopPlayer.clear()
        tpstopPlayer.clear()
        playerItem.clear() //플레이어 핫바및 갑옷슬룻 저장값 초기화
        MissionManager.resetMissions()
        cancelAllTasks()
        Bukkit.broadcastMessage("")
        Bukkit.broadcastMessage("${Global.prefix} 게임이 종료되었습니다.")

        //여기까지.종료직후리셋.
        Bukkit.getScheduler().runTask(Loader.instance, Runnable {
            //모든플레이어에게 게임종료사운드보내기.
            for (player in Bukkit.getOnlinePlayers()) {
                removeDirectionBossBar(player)
                player.playSound(player, soundName, SoundCategory.MASTER, 1.0f, 1.0f)
                reviveFlags[player.name] = false
                // ✅ DeathAndAlive 태그가 있으면 무조건 사망 처리
                if (player.scoreboardTags.contains("DeathAndAlive")) {
                    player.inventory.clear() // 인벤토리 비우기
                    player.gameMode = GameMode.SPECTATOR // 관전 모드로 변경
                    player.removeScoreboardTag("DeathAndAlive")
                    player.addScoreboardTag("death") // Death 태그 추가
                }
            }
            val chunk = world?.getChunkAt(Location(worldWait, 15.5, 58.5, -44.5))

            if (world != null) {
                chunk?.let { world.loadChunk(it) }
            } // 청크강제 로드

            // 사망자, 탈출자, 운영자를 제외한 남은 플레이어 처리
            world?.players?.forEach { player ->
                if (!player.scoreboardTags.contains("manager")) { // 운영자는 제외
                    if (!player.scoreboardTags.contains("EscapeComplete") && !player.scoreboardTags.contains("death")) {
                        // lastsuriver태그가 있는경우 생존
                        if (player.scoreboardTags.contains("lastsuriver")) {
                            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                                player.sendTitle("${ChatColor.LIGHT_PURPLE}마지막까지 생존하였습니다!", "")
                            }, 20L * 10)
                        }
                        // ✅ MissionSuccess 태그가 있는 경우 생존 처리
                        else if (player.scoreboardTags.contains("MissionSuccess")) {
                            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                                player.sendTitle("${ChatColor.GREEN}생존하였습니다!", "")
                                SurvivalPlayers.add(player.name)
                            }, 20L * 10)
                        }
                        // ✅ 일반 사망 처리 (MissionSuccess도 없고 lastsuriver도 없는 경우)
                        else {
                            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                                player.health = 0.0 // 치명적인 데미지를 줘서 죽임
                            }, 20L * 10)
                        }
                    }
                }
            }
        })

        //모든플레이어들.. 게임모드나 플라이등의 설정변경.
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                loadChunkyForWorld("vip")
                for (player in Bukkit.getOnlinePlayers()) {

                    removeDirectionBossBar(player)
                    if (!player.scoreboardTags.contains("manager")) {
                        // manager 태그가 없는 플레이어작업
                        player.scoreboardTags.clear()
                        player.allowFlight = false
                        player.isFlying = false
                    }
                    player.inventory.clear()
                    for (effect in player.activePotionEffects) {
                        player.removePotionEffect(effect.type)
                    }

                    if (worldWait != null) {
                        player.teleport(Location(worldWait, 15.5, 58.5, -44.5))
                    } else {
                        player.sendMessage("${Global.prefix}${ChatColor.RED}플러그인에 버그가 발생하였습니다. 운영자에게 이동을 요청하시길 바랍니다.")
                    }
                }
            })
            val line = "=".repeat(40)
            val escapePlayersMessage = if (EscapePlayers.isNotEmpty()) {
                EscapePlayers.joinToString(", ") { "${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}$it${ChatColor.RESET}" }
            } else {
                "없음"
            }

            val survivalPlayersMessage = if (SurvivalPlayers.isNotEmpty()) {
                SurvivalPlayers.joinToString(", ") { "${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}$it${ChatColor.RESET}" }
            } else {
                "없음"
            }
            val lastsuriver = survivalPlayers().names.firstOrNull() ?: "없음"

            val message = "${ChatColor.GREEN}탈출한 플레이어 : $escapePlayersMessage\n" +
                    "${ChatColor.GREEN}생존한 플레이어 : $survivalPlayersMessage" + (if (survivalPlayers().count == 1) "\n${ChatColor.LIGHT_PURPLE}마지막 생존자 :${ChatColor.AQUA}${ChatColor.BOLD} $lastsuriver ${ChatColor.RESET}" else "")

            // 메시지를 모든 플레이어에게 브로드캐스트
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
            Bukkit.broadcastMessage(message)
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")

            // EscapePlayers 리스트를 초기화 (비우기)
            EscapePlayers.clear()
            reviveFlags.clear()
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                //게임종료 마지막부분에 리셋되어야할것들
                playersAtParticle.clear()
                handleGameReset()
                resetplayerAttribute()
                isStarting = false
            }, 20L * 10)
        }, 20L * 15)
    }
}