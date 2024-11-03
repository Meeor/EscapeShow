package kr.rion.plugin.manager

import kr.rion.plugin.command.CommandHandler
import kr.rion.plugin.util.TabComplete
import org.bukkit.plugin.java.JavaPlugin

class CommandManager(private val plugin: JavaPlugin) {

    fun registerCommands() {
        val commandHandler = CommandHandler()


        plugin.getCommand("탈출인원")?.setExecutor(commandHandler)
        plugin.getCommand("이벤트")?.setExecutor(commandHandler)
        plugin.getCommand("게임")?.setExecutor(commandHandler)
        // TabCompleter
        plugin.getCommand("탈출인원")?.tabCompleter = TabComplete()
        plugin.getCommand("이벤트")?.tabCompleter = TabComplete()
    }
}