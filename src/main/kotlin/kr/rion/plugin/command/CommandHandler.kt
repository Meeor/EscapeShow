package kr.rion.plugin.command


import kr.rion.plugin.command.Coordinates.handleCoordinatesCommand
import kr.rion.plugin.command.EntityList.sendEntityList
import kr.rion.plugin.command.EscapeEndGame.handleEscapeEndGame
import kr.rion.plugin.command.EscapeSetting.HandleSetting
import kr.rion.plugin.command.GameEventCommand.handleEvent
import kr.rion.plugin.command.RandomTP.handleRandomTP
import kr.rion.plugin.command.Reset.handleResetCommand
import kr.rion.plugin.command.item.handleItemReset
import kr.rion.plugin.command.item.handleitem
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler : CommandExecutor {
    var location: Location? = null


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val player: Player = sender
            location = player.location // 플레이어의 위치 가져오기
        }
            when (label.lowercase()) {
                "리셋" -> handleResetCommand(sender, args)
                "좌표공개" -> handleCoordinatesCommand(sender)
                "랜덤티피" -> handleRandomTP(sender, args)
                "아이템지급" -> handleitem(sender, args)
                "계약서초기화" -> handleItemReset(sender)
                "게임종료" -> handleEscapeEndGame(sender)
                "탈출인원" -> HandleSetting(sender, args)
                "이벤트" -> handleEvent(sender, args)
                "엔티티리스트" -> sendEntityList(sender)
                "플레어건소환" -> location?.let { FlameGunSpawn.spawnFlareGunChest(it) }
                else -> return false
            }
        return true
    }

}