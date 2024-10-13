package kr.rion.plugin.gui

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object PlayerTeleport {
    fun openTeleportGUI(player: Player) {
        val targetPlayers = Bukkit.getOnlinePlayers().filter {!it.scoreboardTags.contains("death") && !it.scoreboardTags.contains("manager") && !it.scoreboardTags.contains("EscapeComplete")}
        if (targetPlayers.isEmpty()) {
            player.sendMessage("${ChatColor.RED}텔레포트할 수 있는 플레이어가 없습니다.")
            return
        }

        // GUI 크기 계산 (플레이어 수에 맞게 9의 배수로)
        val inventorySize = ((targetPlayers.size / 9) + 1) * 9
        val gui = Bukkit.createInventory(null, inventorySize, "${ChatColor.DARK_GREEN}텔레포트할 플레이어 선택")

        // 플레이어의 머리 아이템을 GUI에 추가
        for (target in targetPlayers) {
            val skullItem = ItemStack(Material.PLAYER_HEAD, 1)
            val skullMeta = skullItem.itemMeta as SkullMeta
            skullMeta.owningPlayer = target
            skullMeta.setDisplayName("${ChatColor.GOLD}${target.name}님에게 텔레포트")
            skullItem.itemMeta = skullMeta
            gui.addItem(skullItem)
        }

        // 플레이어에게 GUI 열기
        player.openInventory(gui)
    }
}