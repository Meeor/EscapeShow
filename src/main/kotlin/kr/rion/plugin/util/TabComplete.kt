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
            "리셋" -> MapReset(args)
            "랜덤티피" -> RandomTP(args)
            "아이템지급" -> GiveItem(args)
            else -> null
        }
    }

    private fun RandomTP(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("목록", "재설정").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    private fun MapReset(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("로비", "게임").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    private fun GiveItem(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("플레어건", "붕대","농축된열매","계약서","지도").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }
}