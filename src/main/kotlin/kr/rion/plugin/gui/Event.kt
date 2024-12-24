package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Event {
    fun eventGUI(player: Player){

        val gui = Bukkit.createInventory(null,27,"${ChatColor.DARK_BLUE}이벤트")

        val sunName = "맑음"
        val rainName = "폭우"
        val gravityName = "중력이상"
        val earthQuakeName = "지진"
        val donationName = "후원"
        val deathCoinName = "데스코인"
        val bettingName = "베팅"
        val randomName = "랜덤"

        val sunLore = listOf("이벤트 ${ChatColor.YELLOW}맑음 ${ChatColor.RESET}을 발생시킵니다")
        val rainLore = listOf("이벤트 ${ChatColor.YELLOW}폭우 ${ChatColor.RESET}을 발생시킵니다")
        val gravityLore = listOf("이벤트 ${ChatColor.YELLOW}중력이상 ${ChatColor.RESET}을 발생시킵니다")
        val earthQuakeLore = listOf("이벤트 ${ChatColor.YELLOW}지진 ${ChatColor.RESET}을 발생시킵니다")
        val donationLore = listOf("이벤트 ${ChatColor.YELLOW}후원 ${ChatColor.RESET}을 발생시킵니다")
        val deathCoinLore = listOf("이벤트 ${ChatColor.YELLOW}데스코인 ${ChatColor.RESET}을 발생시킵니다")
        val bettingLore = listOf("이벤트 ${ChatColor.YELLOW}베팅 ${ChatColor.RESET}을 발생시킵니다")
        val randomLore = listOf("이벤트를 플러그인에서 하나를 지정하여 랜덤으로 발생시킵니다.")

        val sun = createCustomItem(sunName,sunLore,Material.MAGMA_CREAM, persistentDataKey = "event-sun")
        val rain = createCustomItem(rainName,rainLore,Material.LIGHTNING_ROD, persistentDataKey = "event-rain")
        val gravity = createCustomItem(gravityName,gravityLore,Material.FEATHER, persistentDataKey = "event-gravity")
        val earthQuake =
            createCustomItem(earthQuakeName,earthQuakeLore,Material.COARSE_DIRT, persistentDataKey = "event-earthQuake")
        val donation = createCustomItem(donationName,donationLore,Material.GOLDEN_CARROT, persistentDataKey = "event-donation")
        val deathCoin = createCustomItem(deathCoinName,deathCoinLore,Material.BONE, persistentDataKey = "event-deathCoin")
        val betting = createCustomItem(bettingName,bettingLore,Material.GOLD_NUGGET, persistentDataKey = "event-betting")
        val random = createCustomItem(randomName,randomLore,Material.BLAZE_ROD, persistentDataKey = "event-random")

        val notitem = createCustomItem("", listOf(""),Material.BARRIER, persistentDataKey = "nothing")
        gui.setItem(13,sun)
        gui.setItem(15,rain)
        gui.setItem(17,gravity)
        gui.setItem(7,earthQuake)
        gui.setItem(11,donation)
        gui.setItem(9,deathCoin)
        gui.setItem(1,betting)
        gui.setItem(22,random)
        gui.setItem(3, notitem)
        gui.setItem(5,notitem)

        player.openInventory(gui)
    }
}