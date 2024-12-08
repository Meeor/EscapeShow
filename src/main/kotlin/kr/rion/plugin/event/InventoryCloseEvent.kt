package kr.rion.plugin.event

import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.gameEvent.FlameGunSpawn.particleTask
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseEvent : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        // 메인 메뉴일 경우 신호기 제거
        if (event.view.title == "${ChatColor.DARK_BLUE}메뉴") {
            inventory.contents.forEach { item ->
                if (item?.type == Material.BEACON) {
                    inventory.remove(item) // 신호기 아이템 제거
                }
            }
            inventory.clear()
            return
        }

        // 닫힌 상자가 플레어건 상자와 일치하지 않으면 리턴
        if (chestLocation == null || chestLocation!!.block != event.player.location.block) {
            return
        }

        // 인벤토리가 비었는지 확인
        if (inventory.isEmpty) {
            chestLocation!!.block.type = Material.AIR // 상자 제거
            particleTask?.cancel() // 파티클 반복 종료
            chestLocation = null // 위치 초기화
            Bukkit.broadcastMessage("$prefix 누군가가 플레어건을 획득했습니다!")
        }
    }

}