package kr.rion.plugin.event

import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.gameEvent.FlameGunSpawn.particleTask
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseEvent : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory
        val holder = inventory.holder

        // 닫힌 상자가 플레어건 상자와 일치하지 않으면 리턴

        if (chestLocation == null || holder !is BlockState || holder.block.location != chestLocation) {
            return
        }

        // 인벤토리가 비었는지 확인
        if (inventory.isEmpty) {
            chestLocation!!.block.type = Material.AIR // 상자 제거
            particleTask?.cancel() // 파티클 반복 종료
            chestLocation = null // 위치 초기화
            Bukkit.broadcastMessage("$prefix 누군가가 플레어건을 획득했습니다!")
            bossbarEnable = 0
            for (player in Bukkit.getOnlinePlayers()) {
                removeDirectionBossBar(player)
            }
        }
    }

}