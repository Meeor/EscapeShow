package kr.rion.plugin.event

import kr.rion.plugin.Loader
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.persistence.PersistentDataType

class PickupEvent: Listener {
    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        val entity = event.entity
        if (entity is Player) {
            val item = event.item.itemStack
            val meta = item.itemMeta ?: return
            val key = NamespacedKey(Loader.instance, "owner")
            val owner = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

            // 해당 플레이어가 아니면 못 줍게 막기
            if (entity.uniqueId.toString() != owner) {
                event.isCancelled = true
            }
        }
    }
}