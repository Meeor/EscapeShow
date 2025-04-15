package kr.rion.plugin.manager

import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.teamsMaxPlayers
import kr.rion.plugin.util.Delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.scoreboard.Team

object TeamManager {
    private val teams = mutableMapOf<String, MutableList<String>>() // 팀 데이터 (코드 내부용, 플레이어 닉네임 저장)
    private var teamCounter = 1 // 자동 증가되는 팀 번호
    private var teamKey = "Team$teamCounter"
    var teamPvpBoolean: Boolean = false

    // ✅ 팀별 색상을 저장하는 맵
    private val teamColors: MutableMap<String, String> = mutableMapOf()

    // ✅ 이미 사용된 색상 목록 (중복 방지)
    private val usedColors: MutableSet<String> = mutableSetOf()

    /** 🔹 전체 플레이어를 랜덤 팀 배정 (팀별 색상 적용 + 색상 중복 방지) */
    fun random() {
        val allPlayers = Bukkit.getOnlinePlayers()
            .filter { !it.scoreboardTags.contains("manager") }
            .shuffled() // ✅ 무작위 섞기

        teams.clear() // ✅ 기존 팀 초기화
        teamColors.clear() // ✅ 팀 색상 초기화
        usedColors.clear() // ✅ 사용된 색상 목록 초기화
        teamCounter = 1 // ✅ 팀 번호 초기화

        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard

        for (player in allPlayers) {
            val teamCount = teams[teamKey]?.size ?: 0
            if (teamCount >= teamsMaxPlayers) {
                teamCounter++ // ✅ 팀 번호 증가
                teamKey = "Team$teamCounter"
            }

            // ✅ 기존 팀이 없다면 새로운 색상 생성 & 저장 (중복 방지)
            val teamColorHex = teamColors.getOrPut(teamKey) { getUniqueTeamColor() }
            val teamColorBungee = ChatColor.of(teamColorHex) // ✅ RGB 색상 적용

            var team = scoreboard?.getTeam(teamKey)
            if (team == null) {
                team = scoreboard?.registerNewTeam(teamKey)
                team?.prefix = "$teamColorBungee[Team$teamCounter] " // ✅ Tab 목록 팀 이름에 색상 적용
                team?.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            }

            team?.addEntry(player.name) // ✅ 플레이어를 팀에 추가
            teams.computeIfAbsent(teamKey) { mutableListOf() }.add(player.name) // ✅ 로컬 변수에도 추가

            team?.let { Bukkit.getLogger().info("[DEBUG] $teamKey 팀에 ${player.name} 을 추가하였습니다.") }
        }

        // ✅ 모든 플레이어에게 팀 배정 결과 전송
        Bukkit.getOnlinePlayers().forEach { player ->
            val teamName = teams.entries.find { it.value.contains(player.name) }?.key ?: "알 수 없음"
            val teamColor = scoreboard?.getTeam(teamName)?.prefix ?: ChatColor.WHITE
            player.sendMessage("$prefix ${ChatColor.GREEN}✅ 당신은 ${teamColor}${ChatColor.GREEN} 팀에 배정되었습니다!")
        }

        // ✅ 전체 공지 메시지 출력
        Bukkit.broadcastMessage("$prefix ${ChatColor.AQUA}✅ 랜덤 팀 배정 완료!")
    }

    /** 🔹 중복되지 않은 랜덤 RGB 색상을 가져오는 함수 */
    fun getUniqueTeamColor(): String {
        var randomColor: String
        do {
            // ✅ 랜덤한 RGB 색상 생성
            randomColor = String.format("#%02X%02X%02X", (0..255).random(), (0..255).random(), (0..255).random())
        } while (randomColor in usedColors) // ✅ 중복된 색상인지 확인

        usedColors.add(randomColor) // ✅ 사용된 색상 목록에 추가
        return randomColor
    }


    /** 🔹 플레이어가 속한 팀 이름 가져오기 */
    fun getTeam(player: String): String? {
        return teams.entries.find { it.value.contains(player) }?.key
    }

    /** 🔹 색상코드가 포함된 팀 이름 가져오기 */
    fun getTeamColorName(teamName: String): String {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        val teamColor = scoreboard?.getTeam(teamName)?.prefix ?: "§f"  // 없으면 흰색 적용
        return teamColor
    }

    /** 🔹 특정 팀의 플레이어 목록 가져오기 */
    fun getPlayerList(teamName: String): List<String> {
        return teams[teamName] ?: emptyList()
    }

    /** 🔹 현재 존재하는 팀 목록 반환 */
    fun getTeamList(): List<String> {
        return teams.keys.toList()
    }

    /** 🔹 현재 존재하는 팀 갯수 반환 */
    fun getTeamCount(): Int {
        return teams.count()
    }

    /** ✅ 생존 팀을 판단할 때 제외할 태그 리스트 */
    private val excludedTags = setOf(
        "manager",
        "EscapeComplete",
        "death",
        "DeathAndAlive",
        "MissionSuccessEscape"
    )

    /** 🔹 생존 팀 수 반환 */
    fun getSurviverCount(): Int {
        return teams.entries.count { (_, members) ->
            members.any { playerName ->
                val player = Bukkit.getPlayer(playerName)
                player != null && excludedTags.none { player.scoreboardTags.contains(it) }
            }
        }
    }

    fun getSurvivorTeams(): List<String>? {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard

        return teams.entries.filter { (_, members) ->
            members.any { playerName ->
                val player = Bukkit.getPlayer(playerName)
                player != null && excludedTags.none { player.scoreboardTags.contains(it) }
            }
        }.map { (teamName, _) ->
            val team = scoreboard?.getTeam(teamName) // ✅ Scoreboard에서 팀 정보 가져오기
            val teamColor = team?.prefix ?: "§f" // ✅ 팀 색상 적용 (없으면 기본 흰색)
            "$teamColor$teamName" // ✅ 색상 적용된 팀 이름 반환
        }.takeIf { it.isNotEmpty() } // ✅ 리스트가 비어 있으면 null 반환
    }


    /** 🔹 두 플레이어가 같은 팀인지 확인 */
    fun isSameTeam(player1: String, player2: String): Boolean {
        val team1 = getTeam(player1)
        val team2 = getTeam(player2)
        return team1 != null && team1 == team2
    }

    /**팀 정보 리셋 **/
    fun resetTeam() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard

        // ✅ 팀 데이터 초기화
        teams.clear()
        teamCounter = 1
        teamColors.clear() // ✅ 저장된 팀 색상 초기화
        usedColors.clear() // ✅ 사용된 색상 목록 초기화

        // ✅ Scoreboard에서 모든 팀 제거
        scoreboard?.teams?.forEach { team ->
            team.unregister() // 스코어보드에서 팀 삭제
        }

        // ✅ 모든 플레이어의 머리 위 닉네임, 탭 리스트 닉네임, 채팅 닉네임 초기화
        Delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { player ->
                player.setPlayerListName(player.name) // ✅ 탭 리스트 기본값 (플레이어 이름만 표시)
                player.customName = player.name // ✅ 머리 위 닉네임 기본값
                player.isCustomNameVisible = true // ✅ 기본적으로 머리 위 닉네임 숨김 (필요시 true로 변경)
            }
        )
    }


}
