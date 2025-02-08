package kr.rion.plugin.util


import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


class TabComplete : TabCompleter {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        return when (command.name.lowercase()) {
            "인원설정" -> EscapeSettingTab(args)
            "이벤트" -> GameEvent(args)
            "미션" -> HandleMissionTab(args)
            "방송" -> HandleBroadcastTab(args)
            "부활불가" -> HandleReviveTab(args)
            else -> null
        }
    }


    private fun EscapeSettingTab(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("게임종료", "탈출").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    private fun GameEvent(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("맑음", "폭우").filter {
                it.startsWith(
                    args[0],
                    ignoreCase = true
                )
            }
        } else {
            emptyList()
        }
    }

    fun HandleMissionTab(args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> listOf("부여", "제거").filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
            else -> emptyList()
        }
    }

    private fun HandleBroadcastTab(args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> listOf("true", "false").filter { it.startsWith(args[1], ignoreCase = true) }
            else -> emptyList()
        }
    }
    private fun HandleReviveTab(args: Array<out String>): List<String> {
        // 명령어 입력 중 첫 번째 인수라면 플레이어 이름을 자동완성으로 반환
        if (args.size == 1) {
            val input = args[0].lowercase() // 사용자가 입력한 값을 소문자로 변환
            return Bukkit.getOnlinePlayers()
                .map { it.name } // 모든 온라인 플레이어의 이름을 가져옴
                .filter { it.lowercase().startsWith(input) } // 입력값과 일치하는 이름만 필터링
        }
        return emptyList() // 그 외에는 자동완성 결과 없음
    }

}