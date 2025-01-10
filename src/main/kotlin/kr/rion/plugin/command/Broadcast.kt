package kr.rion.plugin.command

import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object Broadcast {
    private val broadcastPlayers = mutableSetOf<String>()

    fun handleBroadcast(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            if (sender is Player) {
                togglePlayer(sender.name)
                sender.sendMessage("$prefix 방송 모드가 ${if (sender.name in broadcastPlayers) "활성화" else "비활성화"}되었습니다.")
            } else {
                sender.sendMessage("$prefix ${ChatColor.RED}콘솔에서는 사용할 수 없는 명령어입니다.")
            }
            return
        }

        val playerName = args[0]
        val player = Bukkit.getPlayer(playerName)

        if (player == null) {
            sender.sendMessage("$prefix ${ChatColor.RED}플레이어를 찾을 수 없습니다: $playerName")
            return
        }

        when {
            args.size == 1 -> {
                togglePlayer(playerName)
                sender.sendMessage("$prefix 플레이어 $playerName 의 방송 모드가 ${if (playerName in broadcastPlayers) "활성화" else "비활성화"}되었습니다.")
                player.sendMessage("$prefix 방송 모드가 ${if (playerName in broadcastPlayers) "활성화" else "비활성화"}되었습니다.")
            }

            args.size == 2 -> {
                val action = args[1].lowercase(Locale.getDefault())
                when (action) {
                    "true" -> {
                        broadcastPlayers.add(playerName)
                        sender.sendMessage("$prefix 플레이어 $playerName 이 방송 모드에 추가되었습니다.")
                        player.sendMessage("$prefix 방송모드가 활성화 되었습니다.")
                    }
                    "false" -> {
                        broadcastPlayers.remove(playerName)
                        sender.sendMessage("$prefix 플레이어 $playerName 이 방송 모드에서 제거되었습니다.")
                        player.sendMessage("$prefix 방송모드가 비활성화 되었습니다.")
                    }
                    else -> {
                        sender.sendMessage("$prefix ${ChatColor.RED}올바르지 않은 값입니다. true 또는 false 를 입력하세요.")
                    }
                }
            }

            else -> sender.sendMessage("$prefix 사용법: /방송 [플레이어] [true/false]")
        }
    }

    private fun togglePlayer(playerName: String) {
        if (playerName in broadcastPlayers) {
            broadcastPlayers.remove(playerName)
        } else {
            broadcastPlayers.add(playerName)
        }
    }
    fun isPlayerInBroadcast(playerName: String): Boolean {
        return playerName in broadcastPlayers
    }
}
