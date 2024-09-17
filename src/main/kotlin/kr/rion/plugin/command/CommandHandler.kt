package kr.rion.plugin.command


import kr.rion.plugin.command.Coordinates.handleCoordinatesCommand
import kr.rion.plugin.command.EscapeEndGame.handleEscapeEndGame
import kr.rion.plugin.command.EscapeSetting.HandleSetting
import kr.rion.plugin.command.Playeritem.handleItemReset
import kr.rion.plugin.command.Playeritem.handleitem
import kr.rion.plugin.command.RandomTP.handleRandomTP
import kr.rion.plugin.command.Reset.handleResetCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandHandler() : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label.equals("리셋", ignoreCase = true) -> handleResetCommand(sender, args)
            label.equals("좌표공개", ignoreCase = true) -> handleCoordinatesCommand(sender)
            label.equals("랜덤티피", ignoreCase = true) -> handleRandomTP(sender, args)
            label.equals("아이템지급", ignoreCase = true) -> handleitem(sender, args)
            label.equals("계약서초기화", ignoreCase = true) -> handleItemReset(sender)
            label.equals("게임종료", ignoreCase = true) -> handleEscapeEndGame(sender)
            label.equals("탈출인원",ignoreCase = true) -> HandleSetting(sender,args)
            else -> return false
        }
        return true
    }

}