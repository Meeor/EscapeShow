package kr.rion.plugin.gui

import kr.rion.plugin.manager.TeamManager.teamPvpBoolean
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Item.createCustomItem
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SettingMenu {
    fun settingGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.DARK_BLUE}게임설정")

        val teampvpItem = createTeamPvpItem(teamPvpBoolean)
        val teamSettingItem = createTeamGame(TeamGame)


        gui.setItem(2, teamSettingItem)
        gui.setItem(6, teampvpItem)

        player.openInventory(gui)
    }

    private fun createTeamPvpItem(isPvpEnabled: Boolean): ItemStack {
        return if (TeamGame) {
            createCustomItem(
                "${org.bukkit.ChatColor.GREEN}팀 PVP 설정",
                listOf("팀 PVP 를 허용할지 금지할지 설정합니다", "", "현재 개인전 상태입니다. PVP설정을 하실수 없습니다."),
                Material.GRAY_WOOL,
                persistentDataKey = "team-pvp-noting"
            )
        } else if (isPvpEnabled) {
            createCustomItem(
                "${org.bukkit.ChatColor.GREEN}팀 PVP 허용",
                listOf("클릭 시 팀 PVP를 금지합니다."),
                Material.GREEN_WOOL,
                persistentDataKey = "team-pvp-true"
            )
        } else {
            createCustomItem(
                "${org.bukkit.ChatColor.RED}팀 PVP 금지",
                listOf("클릭 시 팀 PVP를 허용합니다."),
                Material.RED_WOOL,
                persistentDataKey = "team-pvp-false"
            )
        }
    }

    private fun createTeamGame(Teamgame: Boolean): ItemStack {
        return if (Teamgame) {
            createCustomItem(
                "${org.bukkit.ChatColor.GREEN}개인전으로 변경",
                listOf("클릭 시 게임방식을 개인전으로 변경합니다."),
                Material.GREEN_WOOL,
                persistentDataKey = "team-game"
            )
        } else {
            createCustomItem(
                "${org.bukkit.ChatColor.GREEN}팀전으로 변경",
                listOf("클릭 시 게임방식을 팀전으로 변경합니다."),
                Material.RED_WOOL,
                persistentDataKey = "solo-game"
            )
        }
    }
}