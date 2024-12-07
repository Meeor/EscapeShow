package kr.rion.plugin.command


import kr.rion.plugin.command.EscapeSetting.HandleSetting
import kr.rion.plugin.command.GameEventCommand.handleEvent
import kr.rion.plugin.command.game.handlegame
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler : CommandExecutor {
    var location: Location? = null
    lateinit var player: Player


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            player = sender
            location = player.location // 플레이어의 위치 가져오기
        }
        when (command.name.lowercase()) {
            "인원설정" -> HandleSetting(sender, args)
            "이벤트" -> handleEvent(sender, args)
            "게임" -> handlegame(sender)

            else -> return false
        }
        return true
    }

}