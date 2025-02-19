package kr.rion.plugin.util

import org.bukkit.Material
import org.bukkit.entity.Player

object inventory {
    /**
     * 플레이어의 인벤토리에서 아이템을 제거합니다.
     * @param player 제거할 플레이어
     * @param item 제거할 아이템 종류
     * @param amount 제거할 수량
     */
    fun removeItemFromInventory(player: Player, item: Material, amount: Int) {
        val inventory = player.inventory
        var remainingAmount = amount

        for (itemStack in inventory.contents) {
            if (itemStack?.type == item) { // 🔹 null 체크를 안전하게 유지
                if (itemStack.amount <= remainingAmount) {
                    remainingAmount -= itemStack.amount
                    inventory.remove(itemStack) // 아이템 제거
                } else {
                    itemStack.amount -= remainingAmount
                    break
                }
            }
            if (remainingAmount <= 0) break
        }
    }
}