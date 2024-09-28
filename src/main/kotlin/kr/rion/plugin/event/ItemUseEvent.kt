package kr.rion.plugin.event

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.item.ItemAction.handleBerries
import kr.rion.plugin.item.ItemAction.handleContract
import kr.rion.plugin.item.ItemAction.handleFlameGun
import kr.rion.plugin.item.ItemAction.handleHeal
import kr.rion.plugin.item.ItemAction.handleMap
import kr.rion.plugin.util.global.prefix
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot


class ItemUseEvent : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val itemMeta = item.itemMeta ?: return
        val itemName = ChatColor.stripColor(itemMeta.displayName)  // 색 코드 제거
        val player = event.player

        if (event.hand == EquipmentSlot.HAND && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            val nbtItem = NBTItem(item)
            val tag = when (item.type) {
                Material.FLINT -> "flamegun"
                Material.PAPER -> "heal"
                Material.GLOW_BERRIES -> "berries"
                Material.SKULL_BANNER_PATTERN -> "contract"
                Material.MOJANG_BANNER_PATTERN -> "map"
                else -> return
            }

            if (nbtItem.getBoolean(tag)) {
                val expectedName = getExpectedNameForItem(tag) // 예상되는 아이템 이름을 가져옴
                if (itemName == expectedName) {
                    event.isCancelled = true
                    player.sendMessage("$prefix 아이템 ${itemMeta.displayName}${ChatColor.RESET}${ChatColor.GREEN} 을(를) 사용하셨습니다.")
                    handleAction(player, tag)
                }
            }
        }
    }

    private fun handleAction(player: Player, tag: String) {
        when (tag) {
            "flamegun" -> handleFlameGun(player)
            "heal" -> handleHeal(player)
            "berries" -> handleBerries(player)
            "contract" -> handleContract(player)
            "map" -> handleMap(player)
        }
    }
    private fun getExpectedNameForItem(tag: String): String {
        return when (tag) {
            "flamegun" -> "플레어건"
            "heal" -> "붕대"
            "berries" -> "농축된 열매"
            "contract" -> "계약서"
            "map" -> "지도"
            else -> ""
        }
    }
}