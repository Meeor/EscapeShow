package kr.rion.plugin.command

import kr.rion.plugin.util.Global.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.endingPlayerMaxCount
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object EscapeSetting {
    fun HandleSetting(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /인원설정 <생존/탈출>")
            return
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "생존" -> {
                // 숫자인지 확인
                val playerCount = args[1].toIntOrNull()
                if (playerCount == null || playerCount <= 0) {
                    sender.sendMessage("$prefix §c유효한 숫자를 입력하세요. (1 이상의 숫자)")
                    return
                }

                // 인원수 설정
                endingPlayerMaxCount = playerCount
                sender.sendMessage("$prefix 게임종료에 필요한 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
            }

            "탈출" -> {
                // 숫자인지 확인
                val playerCount = args[1].toIntOrNull()
                if (playerCount == null || playerCount <= 0) {
                    sender.sendMessage("$prefix §c유효한 숫자를 입력하세요. (1 이상의 숫자)")
                    return
                }

                // 인원수 설정
                EscapePlayerMaxCount = playerCount
                sender.sendMessage("$prefix 탈출 가능 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
            }

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. ")
        }
    }

}