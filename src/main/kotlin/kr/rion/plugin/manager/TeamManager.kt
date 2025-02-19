package kr.rion.plugin.manager

import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.teamsMaxPlayers
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit

object TeamManager {
    private val teams = mutableMapOf<String, MutableList<String>>() // 팀 데이터 (코드 내부용, 플레이어 닉네임 저장)
    private var teamCounter = 1 // 자동 증가되는 팀 번호

    /** 🔹 전체 플레이어를 랜덤 팀 배정 (최대 인원 수 적용) */
    fun random() {
        val allPlayers = Bukkit.getOnlinePlayers()
            .filter { !it.scoreboardTags.contains("manager") }
            .map { it.name }
            .shuffled() // ✅ 무작위 섞기

        teams.clear() // ✅ 기존 팀 초기화
        var teamCounter = 1 // ✅ 팀 번호 초기화

        for (player in allPlayers) {
            val teamName = "Team$teamCounter"

            // ✅ 현재 팀의 인원이 최대값을 초과하면 새로운 팀 생성
            if ((teams[teamName]?.size ?: 0) >= teamsMaxPlayers) {
                teamCounter++
            }

            teams.computeIfAbsent(teamName) { mutableListOf() }.add(player)
        }

        // ✅ 모든 플레이어에게 팀 배정 결과 전송
        Bukkit.getOnlinePlayers().forEach { player ->
            val teamName = teams.entries.find { it.value.contains(player.name) }?.key ?: "알 수 없음"

            player.sendMessage("$prefix ${ChatColor.GREEN}✅ 당신은 ${ChatColor.YELLOW}$teamName${ChatColor.GREEN} 팀에 배정되었습니다!")
        }

        // ✅ 전체 공지 메시지 출력
        Bukkit.broadcastMessage("$prefix ${ChatColor.AQUA}✅ 랜덤 팀 배정 완료!")
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
