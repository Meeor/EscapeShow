package kr.rion.plugin.command


import kr.rion.plugin.command.Coordinates.handleCoordinatesCommand
import kr.rion.plugin.command.EntityList.sendEntityList
import kr.rion.plugin.command.EscapeEndGame.handleEscapeEndGame
import kr.rion.plugin.command.EscapeSetting.HandleSetting
import kr.rion.plugin.command.FlameGunSpawn.spawnFlareGunChest
import kr.rion.plugin.command.GameEventCommand.handleEvent
import kr.rion.plugin.command.RandomTP.handleRandomTP
import kr.rion.plugin.command.Reset.handleResetCommand
import kr.rion.plugin.command.item.handleItemReset
import kr.rion.plugin.command.item.handleitem
import kr.rion.plugin.util.global.prefix
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jline.utils.Log

class CommandHandler : CommandExecutor {
    var location: Location? = null


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val player: Player = sender
            location = player.location // 플레이어의 위치 가져오기
        }
            when (command.name.lowercase()) {
                "리셋" -> handleResetCommand(sender, args)
                "좌표공개" -> handleCoordinatesCommand(sender)
                "랜덤티피" -> handleRandomTP(sender, args)
                "아이템지급" -> handleitem(sender, args)
                "계약서초기화" -> handleItemReset(sender)
                "게임종료" -> handleEscapeEndGame(sender)
                "탈출인원" -> HandleSetting(sender, args)
                "이벤트" -> handleEvent(sender, args)
                "엔티티리스트" -> sendEntityList(sender)
                "플레어건소환" -> {
                    if (sender !is Player) {
                        // sender가 플레이어가 아니면 메시지 출력 후 종료
                        sender.sendMessage("$prefix ${ChatColor.RED}이 명령어는 플레이어만 사용할 수 있습니다.")
                        return true
                    } else {
                        // sender가 Player인 경우 위치 가져오기
                        val player: Player = sender // Player로 캐스팅
                        val location = player.location // 플레이어 위치 가져오기
                        spawnFlareGunChest(sender, location)
                    }
                }

                else -> {
                    sender.sendMessage("${ChatColor.RED}알 수 없는 명령어입니다.") // 디버그 메시지 추가
                    return false
                }
            }
        return true
    }

}