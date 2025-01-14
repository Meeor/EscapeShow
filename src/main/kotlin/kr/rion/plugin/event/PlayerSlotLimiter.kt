package kr.rion.plugin.event

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerSlotLimiter : Listener {

    @EventHandler(ignoreCancelled = false)
    fun onInventoryClick(event: InventoryClickEvent) {
        val slot = event.slot // 클릭된 슬롯 번호
        val player = event.whoClicked as? Player ?: return

        // 플레이어 인벤토리에서만 처리
        if (event.clickedInventory == player.inventory) {
            if (slot in 9..35) { // 다른 슬롯은 클릭 비활성화
                event.isCancelled = true
            }
        }
    }
}
