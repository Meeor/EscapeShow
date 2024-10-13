package kr.rion.plugin.util


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
            "탈출인원" -> EscapeSettingTab(args)
            "이벤트" -> GameEvent(args)
            else -> null
        }
    }


    private fun EscapeSettingTab(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("설정", "확인").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    private fun GameEvent(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("맑음", "폭우", "중력이상", "지진", "후원", "데스코인", "베팅", "랜덤").filter {
                it.startsWith(
                    args[0],
                    ignoreCase = true
                )
            }
        } else {
            emptyList()
        }
    }
}