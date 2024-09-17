package kr.rion.plugin.command

import kr.rion.plugin.util.End
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object EscapeEndGame {
    fun handleEscapeEndGame(sender: CommandSender){
        if(sender.isOp){
            Bukkit.broadcastMessage("$prefix 관리자에 의해 게임이 강제종료됩니다.")
            End.EndAction()
        }
    }
}