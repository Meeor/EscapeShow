package kr.rion.plugin.game

import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Teleport.initializeSafeLocations
import kr.rion.plugin.util.Teleport.safeLocations
import kr.rion.plugin.util.Teleport.setInitializedSafeLocations
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RandomTp {
    fun handleRandomTPList(player: Player): Boolean {
        // 좌표 리스트를 문자열로 변환하여 출력
        val coordinates = safeLocations
        if (coordinates.isNotEmpty()) {
            coordinates.forEachIndexed { index, location ->
                player.sendMessage("§e${index + 1}: ${location.x}, ${location.y}, ${location.z}")
            }
            player.sendMessage("$prefix 현재 저장된 좌표 목록${ChatColor.GOLD}(${coordinates.size}개)${ChatColor.GREEN}:")
        } else {
            player.sendMessage("$prefix 저장된 좌표가 없습니다.")
        }
        return true
    }

    fun handleRandomListClear(player: Player) {
        if (safeLocations.isNotEmpty()) {
            safeLocations.clear()  // 리스트의 모든 요소를 제거
            player.sendMessage("$prefix 저장된 좌표가 모두 지워졌습니다.")
        } else {
            player.sendMessage("$prefix 저장된 좌표가 없습니다.")
        }
    }

    fun handleRandomReset(player: Player) {
        setInitializedSafeLocations(false)
        Bukkit.broadcastMessage("$prefix 랜덤이동좌표를 새롭게 정하고있습니다.\n$prefix${ChatColor.GOLD} 잠시 렉이 걸릴수 있습니다.")
        initializeSafeLocations()
        val coordinates = safeLocations
        player.sendMessage("$prefix 랜덤이동좌표를 새롭게 설정하였습니다. ${ChatColor.YELLOW}총설정된 갯수 : ${coordinates.size}")

    }
}