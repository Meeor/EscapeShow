package kr.rion.plugin.event

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import kr.rion.plugin.gui.MainMenu.openMainGUI
import kr.rion.plugin.gui.PlayerTeleport.openTeleportGUI
import kr.rion.plugin.item.ItemAction.handleBerries
import kr.rion.plugin.item.ItemAction.handleFlameGun
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType


class ItemUseEvent : Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onPlayerUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val itemMeta = item.itemMeta ?: return
        val itemName = ChatColor.stripColor(itemMeta.displayName)  // 색 코드 제거
        val player = event.player

        if (event.hand == EquipmentSlot.HAND && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {

            // 나침반인지 확인
            if (item.type == Material.RECOVERY_COMPASS) {
                val meta = item.itemMeta
                if (meta != null && hasCustomTag(meta, "teleport")) {
                    openTeleportGUI(player)
                }
            }
            if (item.type == Material.CLOCK) {
                val meta = item.itemMeta
                if (meta != null && hasCustomTag(meta, "mainmenu")) {
                    if (player.hasPermission("EscapeShow.Game.GUI")) {
                        openMainGUI(player)
                    } else {
                        player.sendMessage("$prefix ${ChatColor.RED}권한이 없습니다.")
                    }
                }
            }

            val nbtItem = NBTItem(item)
            val tag = when (item.type) {
                Material.FLINT -> "flamegun"
                Material.GLOW_BERRIES -> "berries"
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
        try {
            when (tag) {
                "flamegun" -> {
                    println("🔥 handleFlameGun 호출됨") // 로그 추가
                    handleFlameGun(player)
                }
                "berries" -> {
                    println("🍓 handleBerries 호출됨") // 로그 추가
                    handleBerries(player)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            player.sendMessage("${ChatColor.RED}아이템 사용 중 오류가 발생했습니다.")
        }
    }


    private fun getExpectedNameForItem(tag: String): String {
        return when (tag) {
            "flamegun" -> "플레어건"
            "berries" -> "농축된 열매"
            else -> ""
        }
    }

    // 태그가 있는지 확인하는 함수
    private fun hasCustomTag(meta: ItemMeta, tag: String): Boolean {
        val key = NamespacedKey(Loader.instance, tag)
        return meta.persistentDataContainer.has(key, PersistentDataType.BOOLEAN)
    }
}