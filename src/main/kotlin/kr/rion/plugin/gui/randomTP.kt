package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object randomTP {
    fun RandomTpGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}랜덤티피")

        val listName = "${ChatColor.YELLOW}랜덤티피 목록 출력"
        val resetName = "${ChatColor.GREEN}랜덤티피 목록 재설정"
        val deleteName = "${ChatColor.RED}랜덤티피 목록 초기화"

        val listLore = listOf("클릭시 랜덤티피 목록을 채팅에 출력해줍니다.")
        val resetLore = listOf("클릭시 랜덤티피 목록을 재설정합니다.")
        val deleteLore = listOf("클릭시 랜덤티피 목록을 초기화합니다.")

        val list = createCustomItem(listName, listLore, Material.GRASS_BLOCK, persistentDataKey = "randomtp-list")
        val reset = createCustomItem(resetName, resetLore, Material.CHERRY_STAIRS, persistentDataKey = "randomtp-reset")
        val delete =
            createCustomItem(deleteName, deleteLore, Material.CHERRY_STAIRS, persistentDataKey = "randomtp-delete")


        gui.setItem(2, list)
        gui.setItem(4, reset)
        gui.setItem(6, delete)

        player.openInventory(gui)
    }
}