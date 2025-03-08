package kr.rion.plugin.manager

import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.teamsMaxPlayers
import net.md_5.bungee.api.ChatColor

import org.bukkit.Bukkit

object TeamManager {
    private val teams = mutableMapOf<String, MutableList<String>>() // 팀 데이터 (코드 내부용, 플레이어 닉네임 저장)
    private var teamCounter = 1 // 자동 증가되는 팀 번호
    private var teamName = "Team$teamCounter"
    var teamPvpBoolean: Boolean = false

    /** 🔹 전체 플레이어를 랜덤 팀 배정 (RGB 색상 적용) */
    fun random() {
        val allPlayers = Bukkit.getOnlinePlayers()
            .filter { !it.scoreboardTags.contains("manager") }
            .shuffled() // ✅ 무작위 섞기

        teams.clear() // ✅ 기존 팀 초기화
        teamCounter = 1 // ✅ 팀 번호 초기화

        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        val usedColors: MutableSet<String> = mutableSetOf() // ✅ 사용된 색상 목록 (중복 방지)

        for (player in allPlayers) {
            val teamColorHex = getRandomTeamColor(usedColors) // ✅ 랜덤 RGB 색상 선택
            usedColors.add(teamColorHex) // ✅ 사용된 색상 저장

            val teamKey = "Team$teamCounter" // ✅ 색상 없이 팀 이름 저장
            teamName = "$teamColorHex[Team$teamKey]" // ✅ 팀 이름을 "색상코드[TeamX]"로 저장
            val teamCount = teams[teamName]?.size ?: 0

            Bukkit.getLogger().info("[DEBUG] 현재 $teamName 에 들어간 플레이어 수 : $teamCount")

            // ✅ 현재 팀 인원이 최대 인원을 초과하면 새로운 팀 생성
            if (teamCount >= teamsMaxPlayers) {
                teamCounter++ // 팀 번호 증가
                val newTeamColorHex = getRandomTeamColor(usedColors) // ✅ 새로운 팀 색상 선택
                usedColors.add(newTeamColorHex) // ✅ 사용된 색상 저장
                teamName = "$newTeamColorHex[Team$teamCounter]" // ✅ 새 팀 생성 시에도 색상 포함
            }

            // ✅ BungeeCord ChatColor (RGB) 생성
            val teamColorBungee = ChatColor.of(teamColorHex)

            var team = scoreboard?.getTeam(teamKey)
            if (team == null) {
                team = scoreboard?.registerNewTeam(teamKey)
                team?.prefix = "$teamColorBungee[$teamKey] " // ✅ 팀 이름에 RGB 색상 적용
            }

            team?.addEntry(player.name) // ✅ 플레이어를 팀에 추가
            teams.computeIfAbsent(teamName) { mutableListOf() }.add(player.name) // ✅ 로컬 변수에도 추가

            // ✅ 플레이어 머리 위 닉네임 (네임태그) RGB 색상 적용
            player.customName = "$teamColorBungee$$teamName${player.name}"
            player.isCustomNameVisible = true // ✅ 닉네임 항상 표시

            // ✅ Tab 리스트 닉네임 색상 적용
            player.setPlayerListName("$teamColorBungee$$teamName${player.name}")

            team?.let { Bukkit.getLogger().info("[DEBUG] $teamName 팀에 ${player.name} 을 추가하였습니다.") }
        }

        // ✅ 모든 플레이어에게 팀 배정 결과 전송
        Bukkit.getOnlinePlayers().forEach { player ->
            val teamName = teams.entries.find { it.value.contains(player.name) }?.key ?: "알 수 없음"
            val teamColor = scoreboard?.getTeam(teamName)?.color ?: ChatColor.WHITE
            player.sendMessage("$prefix ${ChatColor.GREEN}✅ 당신은 ${teamColor}$teamName${ChatColor.GREEN} 팀에 배정되었습니다!")
        }

        // ✅ 전체 공지 메시지 출력
        Bukkit.broadcastMessage("$prefix ${ChatColor.AQUA}✅ 랜덤 팀 배정 완료!")
    }

    /** 🔹 사용되지 않은 랜덤 RGB 색상을 가져오는 함수 */
    fun getRandomTeamColor(usedColors: MutableSet<String>): String {
        var randomColor: String
        do {
            // ✅ 랜덤한 RGB 색상 생성
            randomColor = String.format("#%02X%02X%02X", (0..255).random(), (0..255).random(), (0..255).random())
        } while (randomColor in usedColors) // ✅ 중복 방지

        return randomColor
    }




    /** 🔹 플레이어가 속한 팀 이름 가져오기 */
    fun getTeam(player: String): String? {
        return teams.entries.find { it.value.contains(player) }?.key
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

    /** 🔹 생존한 팀들의 목록 반환 */
    fun getSurvivorTeams(): List<String> {
        return teams.entries.filter { (_, members) ->
            members.any { playerName ->
                val player = Bukkit.getPlayer(playerName)
                player != null && excludedTags.none { player.scoreboardTags.contains(it) }
            }
        }.map { (teamName, _) -> teamName } // ✅ 단순 팀 이름 반환 (맵핑 제거)
    }


    /** 🔹 두 플레이어가 같은 팀인지 확인 */
    fun isSameTeam(player1: String, player2: String): Boolean {
        val team1 = getTeam(player1)
        val team2 = getTeam(player2)
        return team1 != null && team1 == team2
    }

    /**팀 정보 리셋 **/
    fun resetTeam() {
        teams.clear()
        teamCounter = 1
    }
}
