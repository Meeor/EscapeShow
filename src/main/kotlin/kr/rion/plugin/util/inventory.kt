package kr.rion.plugin.util

import org.bukkit.Material
import org.bukkit.entity.Player

object inventory {
    /**
     * 플레이어의 인벤토리에 아이템을 추가합니다.
     * @param player 추가할 플레이어
     * @param item 추가할 아이템 종류
     * @param amount 추가할 수량
     */
    fun addItemToInventory(player: Player, item: Material, amount: Int) {
        val inventory = player.inventory
        val itemStack = inventory.getItem(inventory.firstEmpty())

        if (itemStack == null) {
            inventory.addItem(org.bukkit.inventory.ItemStack(item, amount))
        } else {
            val newAmount = itemStack.amount + amount
            itemStack.amount = newAmount
        }
    }

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
            if (itemStack != null && itemStack.type == item) {
                if (itemStack.amount <= remainingAmount) {
                    remainingAmount -= itemStack.amount
                    inventory.remove(itemStack) // 아이템 제거
                } else {
                    itemStack.amount -= remainingAmount
                    remainingAmount = 0
                    break
                }
            }
            if (remainingAmount <= 0) break
        }
    }

    /**
     * 플레이어의 인벤토리에서 특정 아이템의 수량을 확인합니다.
     * @param player 확인할 플레이어
     * @param item 확인할 아이템 종류
     * @return 해당 아이템의 총 수량
     */
    fun getItemAmountInInventory(player: Player, item: Material): Int {
        val inventory = player.inventory
        var totalAmount = 0

        for (itemStack in inventory.contents) {
            if (itemStack != null && itemStack.type == item) {
                totalAmount += itemStack.amount
            }
        }

        return totalAmount
    }

}