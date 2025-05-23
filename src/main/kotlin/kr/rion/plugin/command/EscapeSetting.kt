package kr.rion.plugin.command

import kr.rion.plugin.Loader.Companion.instance
import kr.rion.plugin.util.Global.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.MissionEscapeMaxCount
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.teamsMaxPlayers
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.spigotmc.SpigotConfig.config
import java.util.*

object EscapeSetting {

    fun HandleSetting(sender: CommandSender, args: Array<out String>) {
        // 인수가 비었는지 확인
        if (args.isEmpty() || args.size < 2) {
            sender.sendMessage("$prefix 사용법: /인원설정 <미션/탈출/팀> <숫자>")
            return
        }

        when (args[0].lowercase(Locale.getDefault())) {
            "미션" -> {
                val playerCount = parsePlayerCount(sender, args[1]) ?: return
                config.set("MissionEscapeMaxCount", playerCount)
                MissionEscapeMaxCount = playerCount
                sender.sendMessage("$prefix ${ChatColor.GREEN}미션 탈출 가능 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
                instance.saveConfig()
            }

            "탈출" -> {
                val playerCount = parsePlayerCount(sender, args[1]) ?: return
                config.set("EscapePlayerMaxCount", playerCount)
                EscapePlayerMaxCount = playerCount
                sender.sendMessage("$prefix ${ChatColor.GREEN}탈출 가능 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
                instance.saveConfig()
            }

            "팀" -> {
                val playerCount = parsePlayerCount(sender, args[1]) ?: return
                config.set("teamsMaxPlayers", playerCount)
                teamsMaxPlayers = playerCount
                sender.sendMessage("$prefix ${ChatColor.GREEN}팀 최대 인원 수가 ${ChatColor.LIGHT_PURPLE}${playerCount}${ChatColor.GREEN}명으로 설정되었습니다.")
                instance.saveConfig()
            }

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. 사용법: /인원설정 <미션/탈출/팀> <숫자>")
        }
    }

    // 숫자 유효성 검사 및 반환 함수
    private fun parsePlayerCount(sender: CommandSender, input: String): Int? {
        val playerCount = input.toIntOrNull()
        if (playerCount == null || playerCount <= 0) {
            sender.sendMessage("$prefix ${ChatColor.RED}유효한 숫자를 입력하세요. (1 이상의 숫자)")
            return null
        }
        return playerCount
    }
}
