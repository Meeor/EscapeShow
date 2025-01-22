package kr.rion.plugin.event

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent

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

        // 서바이벌 또는 모험 모드에서 조합창 비활성화
        if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
            val inventory = event.clickedInventory

            // 플레이어의 크래프팅 그리드인지 확인
            if (inventory != null && inventory.type == InventoryType.CRAFTING) {
                val slotType = event.slotType

                // 크래프팅 슬롯에 접근했는지 확인
                if (slotType == InventoryType.SlotType.CRAFTING || slotType == InventoryType.SlotType.RESULT) {
                    // 클릭 이벤트 취소
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        event.inventory.result = null // 조합 결과 비활성화
    }


}
