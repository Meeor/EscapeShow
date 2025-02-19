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
            else -> null
        }
    }


    private fun EscapeSettingTab(args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("미션", "탈출", "팀").filter { it.startsWith(args[0], ignoreCase = true) }
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

}