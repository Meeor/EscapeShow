package kr.rion.plugin.manager

import kr.rion.plugin.command.CommandHandler
import kr.rion.plugin.util.TabComplete
import org.bukkit.plugin.java.JavaPlugin

class CommandManager(private val plugin: JavaPlugin) {

    fun registerCommands() {
        val commandHandler = CommandHandler()

        plugin.getCommand("리셋")?.setExecutor(commandHandler)
        plugin.getCommand("좌표공개")?.setExecutor(commandHandler)
        plugin.getCommand("랜덤티피")?.setExecutor(commandHandler)
        plugin.getCommand("아이템지급")?.setExecutor(commandHandler)
        plugin.getCommand("계약서초기화")?.setExecutor(commandHandler)
        plugin.getCommand("탈출인원")?.setExecutor(commandHandler)
        plugin.getCommand("게임종료")?.setExecutor(commandHandler)
        plugin.getCommand("이벤트")?.setExecutor(commandHandler)
        plugin.getCommand("엔티티리스트")?.setExecutor(commandHandler)
        // TabCompleter
        plugin.getCommand("리셋")?.tabCompleter = TabComplete()
        plugin.getCommand("랜덤티피")?.tabCompleter = TabComplete()
        plugin.getCommand("아이템지급")?.tabCompleter = TabComplete()
        plugin.getCommand("탈출인원")?.tabCompleter = TabComplete()
        plugin.getCommand("이벤트")?.tabCompleter = TabComplete()
    }
}