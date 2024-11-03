package kr.rion.plugin.command

import kr.rion.plugin.game.End.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object EscapeSetting {
    fun HandleSetting(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /탈출인원 <설정/확인>")
            return
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "설정" -> {
                if (args.size < 2) {
                    sender.sendMessage("$prefix 사용법: /탈출인원 설정 <인원수>")
                    return
                }

                // 숫자인지 확인
                val playerCount = args[1].toIntOrNull()
                if (playerCount == null || playerCount <= 0) {
                    sender.sendMessage("$prefix §c유효한 숫자를 입력하세요. (1 이상의 숫자)")
                    return
                }

                // 인원수 설정
                EscapePlayerMaxCount = playerCount
                sender.sendMessage("$prefix 탈출 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
            }

            "확인" -> {
                sender.sendMessage("$prefix 현재 설정된 탈출 인원 수: ${ChatColor.LIGHT_PURPLE}${EscapePlayerMaxCount}${ChatColor.GREEN}명")
            }

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. ")
        }
    }

}