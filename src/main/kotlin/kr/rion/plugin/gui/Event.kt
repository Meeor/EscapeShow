package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Event {
    fun eventGUI(player: Player) {

        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}이벤트")

        val clearName = "맑음"
        val rainName = "폭우"
        val sunName = "햇살"
        val windName = "바람"
        val thunderName = "폭풍"

        val clearLore = listOf("이벤트 ${ChatColor.YELLOW}맑음 ${ChatColor.RESET}을 발생시킵니다")
        val rainLore = listOf("이벤트 ${ChatColor.YELLOW}폭우 ${ChatColor.RESET}을 발생시킵니다")
        val sunLore = listOf("이벤트 ${ChatColor.YELLOW}햇살 ${ChatColor.RESET}을 발생시킵니다")
        val windLore = listOf("이벤트 ${ChatColor.YELLOW}바람 ${ChatColor.RESET}을 발생시킵니다")
        val thunderLore = listOf("이벤트 ${ChatColor.YELLOW}폭풍 ${ChatColor.RESET}을 발생시킵니다")

        val clear = createCustomItem(clearName, clearLore, Material.MAGMA_CREAM, persistentDataKey = "event-clear")
        val rain = createCustomItem(rainName, rainLore, Material.LINGERING_POTION, persistentDataKey = "event-rain")
        val sun = createCustomItem(sunName, sunLore, Material.FIRE_CHARGE, persistentDataKey = "event-sun")
        val wind = createCustomItem(windName, windLore, Material.WHITE_BANNER, persistentDataKey = "event-wind")
        val thunder =
            createCustomItem(thunderName, thunderLore, Material.LIGHTNING_ROD, persistentDataKey = "event-thunder")
        val notitem = createCustomItem("", listOf(""), Material.BARRIER, persistentDataKey = "nothing")
        gui.setItem(0, clear)
        gui.setItem(1, notitem)
        gui.setItem(2, sun)
        gui.setItem(3, notitem)
        gui.setItem(4, wind)
        gui.setItem(5, notitem)
        gui.setItem(6, thunder)
        gui.setItem(7, notitem)
        gui.setItem(8, rain)

        player.openInventory(gui)
    }
}