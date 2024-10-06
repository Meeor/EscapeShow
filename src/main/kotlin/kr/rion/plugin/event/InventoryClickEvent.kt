package kr.rion.plugin.event

import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class InventoryClickEvent : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        val clickedMeta = clickedItem.itemMeta as? SkullMeta ?: return

        // 클릭한 아이템이 플레이어 머리인 경우
        if (clickedItem.type == Material.PLAYER_HEAD) {
            val targetPlayer = Bukkit.getPlayer(clickedMeta.owningPlayer?.name ?: return)

            if (targetPlayer != null) {
                // 텔레포트 처리
                player.teleport(targetPlayer.location)
                player.sendMessage("$prefix ${ChatColor.GREEN}${targetPlayer.name}님에게 텔레포트되었습니다.")
            } else {
                player.sendMessage("$prefix ${ChatColor.RED}해당 플레이어는 서버에 접속한상태가 아닌것같습니다.")
            }

            event.isCancelled = true
            player.closeInventory()
        }
    }
}