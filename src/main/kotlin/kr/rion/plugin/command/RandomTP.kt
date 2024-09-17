package kr.rion.plugin.command

import kr.rion.plugin.util.Teleport.initializeSafeLocations
import kr.rion.plugin.util.Teleport.safeLocations
import kr.rion.plugin.util.Teleport.setInitializedSafeLocations
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object RandomTP {
    fun handleRandomTP(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /랜덤티피 <목록/재설정>")
            return
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "목록" -> handleRandomTPList(sender)
            "재설정" -> {
                setInitializedSafeLocations(false)
                Bukkit.broadcastMessage("$prefix 랜덤이동좌표를 새롭게 정하고있습니다.\n$prefix${ChatColor.GOLD} 잠시 렉이 걸릴수 있습니다.")
                initializeSafeLocations()
                val coordinates = safeLocations
                sender.sendMessage("$prefix 랜덤이동좌표를 새롭게 설정하였습니다. ${ChatColor.YELLOW}총설정된 갯수 : ${coordinates.size}")

            }

            "초기화" -> handleRandomListClear(sender)

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. ")
        }
    }

    private fun handleRandomTPList(sender: CommandSender): Boolean {
        // 좌표 리스트를 문자열로 변환하여 출력
        val coordinates = safeLocations
        if (coordinates.isNotEmpty()) {
            coordinates.forEachIndexed { index, location ->
                sender.sendMessage("§e${index + 1}: ${location.x}, ${location.y}, ${location.z}")
            }
            sender.sendMessage("$prefix 현재 저장된 좌표 목록${ChatColor.GOLD}(${coordinates.size}개)${ChatColor.GREEN}:")
        } else {
            sender.sendMessage("$prefix 저장된 좌표가 없습니다.")
        }
        return true
    }

    private fun handleRandomListClear(sender: CommandSender) {
        if (safeLocations.isNotEmpty()) {
            safeLocations.clear()  // 리스트의 모든 요소를 제거
            sender.sendMessage("$prefix 저장된 좌표가 모두 지워졌습니다.")
        } else {
            sender.sendMessage("$prefix 저장된 좌표가 없습니다.")
        }
    }
}