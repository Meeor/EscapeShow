package kr.rion.plugin.game

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Reset.handleGameReset
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.manager.ChunkyManager.loadChunkyForWorld
import kr.rion.plugin.manager.TeamManager.getSurvivorTeams
import kr.rion.plugin.manager.TeamManager.resetTeam
import kr.rion.plugin.util.Global.GameAllReset
import kr.rion.plugin.util.Global.GameAllReset2
import kr.rion.plugin.util.Global.PlayerAllReset
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Helicopter
import kr.rion.plugin.util.Helicopter.fillBlocks
import kr.rion.plugin.util.Helicopter.setBlockWithAttributes
import kr.rion.plugin.util.delay
import org.bukkit.*

object End {
    var isEnding: Boolean = true

    val EscapePlayers: MutableSet<String> = mutableSetOf()
    val MissionSuccessPlayers: MutableSet<String> = mutableSetOf() // ✅ 미션 성공 후 탈출한 플레이어
    var LastSurvivor: String? = null // ✅ 마지막 생존자
    private val soundName = "custom.bye"

    fun EndAction() {
        val world = Bukkit.getWorld("game")
        val worldWait = Bukkit.getWorld("vip")

        if (world == null) {
            Bukkit.getLogger().warning("game 월드를 가져오지 못했습니다.")
        }
        if (worldWait == null) {
            Bukkit.getLogger().warning("vip 월드를 가져오지 못했습니다.")
        }

        // 게임 종료 후 맵 초기화
        fillBlocks(Location(worldWait, 23.0, 60.0, -46.0), Location(worldWait, 23.0, 57.0, -44.0), Material.OAK_FENCE)
        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.OAK_FENCE)
        GameAllReset()
        Helicopter.remove()

        isEnding = true
        Bukkit.broadcastMessage("")
        Bukkit.broadcastMessage("$prefix 게임이 종료되었습니다.")

        delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { player ->
                player.playSound(player, soundName, SoundCategory.MASTER, 1.0f, 1.0f)
                reviveFlags[player.name] = false

                // ✅ 기존 사망자 (DeathAndAlive) 처리 → 추가 사망 방지
                if (player.scoreboardTags.contains("DeathAndAlive")) {
                    player.inventory.clear()
                    player.gameMode = GameMode.SPECTATOR
                    player.removeScoreboardTag("DeathAndAlive")
                    player.addScoreboardTag("death")
                }
            }
        )

        // 청크 강제 로드
        val chunk = world?.getChunkAt(Location(worldWait, 15.5, 58.5, -44.5))
        chunk?.let { world.loadChunk(it) }

        // 게임 종료 시 플레이어 상태 확인
        world?.players?.forEach { player ->
            if (!player.scoreboardTags.contains("manager")) { // 운영자는 제외
                when {
                    player.scoreboardTags.contains("MissionSuccessEscape") -> {
                        //추가 처리 하지 않음.
                    }

                    player.scoreboardTags.contains("EscapeComplete") -> {
                        //추가 처리 하지 않음.
                    }

                    player.scoreboardTags.contains("lastsuriver") -> { // ✅ 마지막 생존자 설정
                        LastSurvivor = player.name
                    }

                    player.scoreboardTags.contains("death") -> {
                        // ✅ 이미 사망한 플레이어는 추가 사망 처리하지 않음
                    }

                    else -> {
                        //생존자 팀 반환할수있게 남은사람 처리 X
                    }
                }
            }
        }

        // 게임 종료 후 모든 플레이어 이동 및 초기화
        delay.delayRun(20 * 20) {
            Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                loadChunkyForWorld("vip")
                PlayerAllReset()
            })
            delay.delayForEachPlayer(
                Bukkit.getOnlinePlayers(),
                action = { player ->
                    if (worldWait != null) {
                        player.teleport(Location(worldWait, 15.5, 58.5, -44.5))
                    } else {
                        player.sendMessage("${prefix}${ChatColor.RED}운영자에게 이동을 요청하세요.")
                    }
                }
            )
            // 탈출한 플레이어 출력
            val escapePlayersMessage = if (EscapePlayers.isNotEmpty()) {
                EscapePlayers.joinToString(", ") { "${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}$it${ChatColor.RESET}" }
            } else {
                "없음"
            }

            val line = "=".repeat(40)

            var message = ""
            if (TeamGame) {

                // 생존한 팀 목록 가져오기
                val survivingTeams = getSurvivorTeams()?.joinToString(", ") { team ->
                    "${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}$team${ChatColor.RESET}"
                } ?: "없음"

                // 종료 메시지
                message = """
                    ${ChatColor.GOLD}$line
                    ${ChatColor.GREEN}탈출한 플레이어 : $escapePlayersMessage
                    ${ChatColor.YELLOW}생존한 팀 : ${if (survivingTeams.isNotEmpty()) survivingTeams else "없음"}
                    ${ChatColor.GOLD}$line
                """.trimIndent()
            } else {

                // 마지막 생존자 출력
                val lastSurvivorMessage = if (!LastSurvivor.isNullOrEmpty()) {
                    "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}$LastSurvivor${ChatColor.RESET}"
                } else {
                    "없음"
                }

                // 미션 성공 후 탈출한 플레이어 출력
                val missionEscapePlayersMessage = if (MissionSuccessPlayers.isNotEmpty()) {
                    MissionSuccessPlayers.joinToString(", ") { "${ChatColor.AQUA}${ChatColor.BOLD}$it${ChatColor.RESET}" }
                } else {
                    "없음"
                }

                // 종료 메시지
                message = """
                    ${ChatColor.GOLD}$line
                    ${ChatColor.GREEN}탈출한 플레이어 : $escapePlayersMessage
                    ${ChatColor.AQUA}미션 성공 후 탈출한 플레이어 : $missionEscapePlayersMessage
                    ${ChatColor.LIGHT_PURPLE}마지막 생존자 : $lastSurvivorMessage
                    ${ChatColor.GOLD}$line
                """.trimIndent()
            }

            // 메시지를 모든 플레이어에게 브로드캐스트
            Bukkit.broadcastMessage(message)

            // 최종 초기화
            delay.delayRun(20 * 10) {
                GameAllReset2()
                resetTeam()
                handleGameReset()
                isStarting = false
            }
        }
    }
}
