package kr.rion.plugin.event

import kr.rion.plugin.item.ItemAction.handleBerries
import kr.rion.plugin.item.ItemAction.handleContract
import kr.rion.plugin.item.ItemAction.handleFlameGun
import kr.rion.plugin.item.ItemAction.handleHeal
import kr.rion.plugin.item.ItemAction.handleMap
import kr.rion.plugin.util.global.prefix
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
                            player.sendMessage("$prefix 아이템 ${item.itemMeta.displayName} 을 사용하셨습니다.")
                            handleFlameGun(player)
                        }
                    }

                    Material.PAPER -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}붕대" && itemMeta.persistentDataContainer.has(
                                heal,
                                PersistentDataType.STRING
                            )
                        ) {
                            player.sendMessage("$prefix 아이템 ${item.itemMeta.displayName} 을 사용하셨습니다.")
                            handleHeal(player)
                        }
                    }

                    Material.GLOW_BERRIES -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}농축된 열매" && itemMeta.persistentDataContainer.has(
                                berries,
                                PersistentDataType.STRING
                            )
                        ) {
                            player.sendMessage("$prefix 아이템 ${item.itemMeta.displayName} 을 사용하셨습니다.")
                            handleBerries(player)
                        }
                    }

                    Material.SKULL_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.of("#FF4242")}계약서" && itemMeta.persistentDataContainer.has(
                                contract,
                                PersistentDataType.STRING
                            )
                        ) {
                            player.sendMessage("$prefix 아이템 ${item.itemMeta.displayName} 을 사용하셨습니다.")
                            handleContract(player)
                        }
                    }

                    Material.MOJANG_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.GRAY}지도" && itemMeta.persistentDataContainer.has(
                                map,
                                PersistentDataType.STRING
                            )
                        ) {
                            player.sendMessage("$prefix 아이템 ${item.itemMeta.displayName} 을 사용하셨습니다.")
                            handleMap(player)
                        }
                    }

                    else -> null
                }
            }
        }
    }
}