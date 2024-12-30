package kr.rion.plugin.command

import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object Mission {
    fun HandleMission(sender: CommandSender, args: Array<out String>) {
        // 인수가 비었는지 확인
        if (args.isEmpty() || args.size < 2) {
            sender.sendMessage("$prefix 사용법: /미션 <부여/제거> [플레이어] [숫자]")
            return
        }

        val playerName = args[1]
        val player = Bukkit.getPlayer(playerName)

        if (player == null) {
            sender.sendMessage("$prefix ${ChatColor.RED}플레이어를 찾을 수 없습니다: $playerName")
            return
        }

        when (args[0].lowercase(Locale.getDefault())) {
            "부여" -> {
                if (args.size < 3) {
                    sender.sendMessage("$prefix 사용법: /미션 부여 <플레이어> <숫자>")
                    return
                }

                val missionId = args[2].toIntOrNull()
                if (missionId == null) {
                    sender.sendMessage("$prefix ${ChatColor.RED}미션 ID는 숫자여야 합니다.")
                    return
                }

                MissionManager.assignSpecificMission(sender,player, missionId)
            }

            "제거" -> {
                MissionManager.removeMission(sender,player)
            }

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. 사용법: /미션 <부여/제거> [플레이어] [숫자]")
        }
    }
}
