package kr.rion.plugin.manager

import kr.rion.plugin.command.CommandHandler
import kr.rion.plugin.util.TabComplete
import org.bukkit.plugin.java.JavaPlugin

class CommandManager(private val plugin: JavaPlugin) {

    fun registerCommands() {
        val commandHandler = CommandHandler()


        plugin.getCommand("인원설정")?.setExecutor(commandHandler)
        plugin.getCommand("이벤트")?.setExecutor(commandHandler)
        plugin.getCommand("게임")?.setExecutor(commandHandler)
        plugin.getCommand("미션")?.setExecutor(commandHandler)
        plugin.getCommand("방송")?.setExecutor(commandHandler)
        plugin.getCommand("데미지")?.setExecutor(commandHandler)
        plugin.getCommand("부활불가")?.setExecutor(commandHandler)
        plugin.getCommand("버프데미지")?.setExecutor(commandHandler)
        // TabCompleter
        plugin.getCommand("인원설정")?.tabCompleter = TabComplete()
        plugin.getCommand("이벤트")?.tabCompleter = TabComplete()
        plugin.getCommand("미션")?.tabCompleter = TabComplete()
        plugin.getCommand("방송")?.tabCompleter = TabComplete()
    }
}