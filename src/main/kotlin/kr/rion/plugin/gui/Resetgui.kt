package kr.rion.plugin.gui

import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object Resetgui {
    fun ResetGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.DARK_BLUE}리셋")

        val gameName = "게임맵 리셋"
        val lobbyName = "로비맵 리셋"

        val gameLore = listOf("클릭시 게임맵 리셋작업을 시작합니다.")
        val lobbyLore = listOf("클릭시 로비맵 리셋작업을 시작합니다.")

        val game = createCustomItem(gameName, gameLore, Material.GRASS_BLOCK, persistentDataKey = "reset-game")
        val lobby = createCustomItem(lobbyName, lobbyLore, Material.CHERRY_STAIRS, persistentDataKey = "reset-lobby")


        gui.setItem(11, game)
        gui.setItem(15, lobby)

        player.openInventory(gui)
    }
}