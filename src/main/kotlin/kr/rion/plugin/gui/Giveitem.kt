package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Giveitem {
    fun ItemGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}아이템지급")

        val flamegunName = "${ChatColor.RED}플레어건"
        val berriesName = "${ChatColor.GREEN}농축된 열매"
        val teleportName = "${ChatColor.LIGHT_PURPLE}플레이어에게 텔레포트"
        val menuName = "${ChatColor.AQUA}메뉴창 열기"

        val flamegunLore = listOf("아이템 받으려면 클릭하세요")
        val berriesLore = listOf("아이템 받으려면 클릭하세요")
        val teleportLore = listOf("아이템 받으려면 클릭하세요")
        val menuLore = listOf("아이템 받으려면 클릭하세요")

        val flamegun = createCustomItem(flamegunName, flamegunLore, Material.FLINT, persistentDataKey = "give-flamegun")
        val berries =
            createCustomItem(berriesName, berriesLore, Material.GLOW_BERRIES, persistentDataKey = "give-berries")
        val teleportCompass = createCustomItem(
            teleportName,
            teleportLore,
            Material.RECOVERY_COMPASS,
            persistentDataKey = "give-teleportCompass"
        )
        val mainMenu = createCustomItem(menuName, menuLore, Material.CLOCK, persistentDataKey = "give-mainMenu")

        gui.setItem(1, flamegun)
        gui.setItem(3, berries)
        gui.setItem(5, teleportCompass)
        gui.setItem(7, mainMenu)

        player.openInventory(gui) // 인벤토리열기
    }
}