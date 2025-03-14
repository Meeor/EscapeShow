package kr.rion.plugin.command

import kr.rion.plugin.Loader
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.File

object Gameinfo {
    fun handleGameInfo(sender: CommandSender) {
        val file = File(Loader.instance.dataFolder, "message.txt")

        if (!file.exists()) {
            Bukkit.getLogger().warning("message.txt 파일을 찾을 수 없습니다!")
            sender.sendMessage("내용을 찾을수 없습니다. 운영자에게 문의하세요")
            return
        }

        val messages = file.readText()
            .replace("\\n", "\n")
            .split("\n")
        sender.sendMessage("message.txt 파일을 찾았습니다. 순차적으로 안내 메세지 출력을 시작합니다.")
        broadcastSequentialMessages(sender, messages, 30L) // 1.5초 간격으로 출력
    }

    fun handleItemInfo(sender: CommandSender) {
        val file = File(Loader.instance.dataFolder, "Items.txt")

        if (!file.exists()) {
            Bukkit.getLogger().warning("Items.txt 파일을 찾을 수 없습니다!")
            sender.sendMessage("내용을 찾을수 없습니다. 운영자에게 문의하세요")
            return
        }

        val messages = file.readText()
            .replace("\\n", "\n")
            .split("\n")
        messages.forEach { message ->
            sender.sendMessage(message)
        }
    }

    fun handleCraftInfo(sender: CommandSender) {
        val file = File(Loader.instance.dataFolder, "Crafting.txt")

        if (!file.exists()) {
            Bukkit.getLogger().warning("Crafting.txt 파일을 찾을 수 없습니다!")
            sender.sendMessage("내용을 찾을수 없습니다. 운영자에게 문의하세요")
            return
        }

        val messages = file.readText()
            .replace("\\n", "\n")
            .split("\n")
        messages.forEach { message ->
            sender.sendMessage(message)
        }
    }

    fun broadcastSequentialMessages(sender: CommandSender, messages: List<String>, intervalTicks: Long = 20L) {
        messages.forEachIndexed { index, message ->
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                sender.sendMessage(message)
            }, index * intervalTicks)
        }
    }

}