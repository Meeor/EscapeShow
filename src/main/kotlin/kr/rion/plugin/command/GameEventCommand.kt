package kr.rion.plugin.command

import kr.rion.plugin.gameEvent.GameEvent
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object GameEventCommand {
    fun handleEvent(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /이벤트 <이벤트이름>")
            return
        }

        when (args[0].lowercase(Locale.getDefault())) {
            "맑음" -> GameEvent.weatherClear()
            "폭우" -> GameEvent.weatherRain()
            "폭풍" -> GameEvent.weatherThunder()
            "바람" -> GameEvent.weatherWind()
            "햇살" -> GameEvent.weatherSun()
            else -> sender.sendMessage("$prefix ${ChatColor.RED} 이벤트이름을 정확히 입력해주세요 (자동완성 지원됩니다.)")
        }
    }
}
