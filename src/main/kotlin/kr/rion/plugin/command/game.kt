package kr.rion.plugin.command

import kr.rion.plugin.gui.MainMenu.openMainGUI
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object game {
    fun handlegame(sender: CommandSender) {
        if (sender is Player) {
            if (sender.hasPermission("EscapeShow.Game.GUI")) {
                openMainGUI(sender)
            } else {
                sender.sendMessage("$prefix ${ChatColor.RED}권한이 없습니다.")
            }
        } else {
            sender.sendMessage("$prefix ${ChatColor.RED}플레이어만 사용가능합니다.")
        }
    }
}