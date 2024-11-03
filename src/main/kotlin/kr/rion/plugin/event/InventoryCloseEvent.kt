package kr.rion.plugin.event

import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.gameEvent.FlameGunSpawn.particleTask
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseEvent : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        // 플레어건 상자 위치와 닫힌 상자 위치가 일치하는지 확인
        if (chestLocation != null) {
            if (inventory.isEmpty) {
                chestLocation!!.block.type = Material.AIR // 상자 부수기
                particleTask?.cancel() // 파티클 반복 종료
                chestLocation = null // 상자 위치 초기화
                Bukkit.broadcastMessage("$prefix 누군가가 플레어건을 획득했습니다!")
            }
        }
    }
}