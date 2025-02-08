package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Event {
    fun eventGUI(player: Player) {

        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}이벤트")

        val sunName = "맑음"
        val rainName = "폭우"

        val sunLore = listOf("이벤트 ${ChatColor.YELLOW}맑음 ${ChatColor.RESET}을 발생시킵니다")
        val rainLore = listOf("이벤트 ${ChatColor.YELLOW}폭우 ${ChatColor.RESET}을 발생시킵니다")

        val sun = createCustomItem(sunName, sunLore, Material.MAGMA_CREAM, persistentDataKey = "event-sun")
        val rain = createCustomItem(rainName, rainLore, Material.LIGHTNING_ROD, persistentDataKey = "event-rain")
        val notitem = createCustomItem("", listOf(""), Material.BARRIER, persistentDataKey = "nothing")
        gui.setItem(0, sun)
        gui.setItem(1, sun)
        gui.setItem(2, sun)
        gui.setItem(3, sun)
        gui.setItem(4, notitem)
        gui.setItem(5, rain)
        gui.setItem(6, rain)
        gui.setItem(7, rain)
        gui.setItem(8, rain)

        player.openInventory(gui)
    }
}