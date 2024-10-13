package kr.rion.plugin.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object MainMenu {
    fun openMainGUI(player: Player){
        val gui = Bukkit.createInventory(null,9,"")
        /*
        // 아이템 생성 및 배치
        val commandItem1 = ItemStack(Material.DIAMOND_SWORD)
        val commandItem2 = ItemStack(Material.APPLE)

        // 인벤토리 슬롯에 아이템 넣기
        gui.setItem(0, commandItem1)
        gui.setItem(1, commandItem2)
         */
        player.openInventory(gui) // 인벤토리열기
    }
}