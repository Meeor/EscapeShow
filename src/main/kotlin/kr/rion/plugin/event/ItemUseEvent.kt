package kr.rion.plugin.event

import kr.rion.plugin.item.ItemAction.handleBerries
import kr.rion.plugin.item.ItemAction.handleContract
import kr.rion.plugin.item.ItemAction.handleFlameGun
import kr.rion.plugin.item.ItemAction.handleHeal
import kr.rion.plugin.item.ItemAction.handleMap
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

class ItemUseEvent : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val itemMeta = item.itemMeta ?: return
        val player = event.player
        val flamegun = NamespacedKey("EscapeShow", "flamegun")
        val heal = NamespacedKey("EscapeShow", "heal")
        val berries = NamespacedKey("EscapeShow", "berries")
        val contract = NamespacedKey("EscapeShow", "contract")
        val map = NamespacedKey("EscapeShow", "map")
        if (event.hand == EquipmentSlot.HAND) {
            if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                when (item.type) {
                    Material.FLINT -> {
                        if (itemMeta.displayName == "${ChatColor.RED}플레어건" && itemMeta.persistentDataContainer.has(
                                flamegun,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleFlameGun(player)
                        }
                    }

                    Material.PAPER -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}붕대" && itemMeta.persistentDataContainer.has(
                                heal,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleHeal(player)
                        }
                    }

                    Material.GLOW_BERRIES -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}농축된 열매" && itemMeta.persistentDataContainer.has(
                                berries,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleBerries(player)
                        }
                    }

                    Material.SKULL_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.of("#FF4242")}계약서" && itemMeta.persistentDataContainer.has(
                                contract,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleContract(player)
                        }
                    }

                    Material.MOJANG_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.GRAY}지도" && itemMeta.persistentDataContainer.has(
                                map,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleMap(player)
                        }
                    }

                    else -> null
                }
            }
        }
    }
}