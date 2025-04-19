package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Giveitem {
    fun itemGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}아이템지급")

        val flamegunName = "${ChatColor.RED}플레어건"
        val berriesName = "${ChatColor.GREEN}농축된 열매"
        val speedName = "${ChatColor.GREEN}복숭아"
        val upgradeName = "${ChatColor.GREEN}강화제"
        val teleportName = "${ChatColor.LIGHT_PURPLE}플레이어에게 텔레포트"
        val menuName = "${ChatColor.AQUA}메뉴창 열기"

        val flamegunLore = listOf("아이템 받으려면 클릭하세요")
        val berriesLore = listOf("아이템 받으려면 클릭하세요")
        val speedLore = listOf("아이템 받으려면 클릭하세요")
        val upgradeLore = listOf("아이템 받으려면 클릭하세요")
        val teleportLore = listOf("아이템 받으려면 클릭하세요")
        val menuLore = listOf("아이템 받으려면 클릭하세요")

        val flamegun = createCustomItem(flamegunName, flamegunLore, Material.FLINT, persistentDataKey = "give-flamegun")
        val berries =
            createCustomItem(berriesName, berriesLore, Material.GLOW_BERRIES, persistentDataKey = "give-berries")
        val speed =
            createCustomItem(speedName, speedLore, Material.BEETROOT, persistentDataKey = "give-speed")
        val upgrade =
            createCustomItem(upgradeName, upgradeLore, Material.PUFFERFISH, persistentDataKey = "give-upgrade")
        val teleportCompass = createCustomItem(
            teleportName,
            teleportLore,
            Material.RECOVERY_COMPASS,
            persistentDataKey = "give-teleportCompass"
        )
        val mainMenu = createCustomItem(menuName, menuLore, Material.CLOCK, persistentDataKey = "give-mainMenu")

        gui.setItem(0, flamegun)
        gui.setItem(1, berries)
        gui.setItem(2, speed)
        gui.setItem(3, upgrade)
        gui.setItem(4, teleportCompass)
        gui.setItem(5, mainMenu)

        player.openInventory(gui) // 인벤토리열기
    }
}