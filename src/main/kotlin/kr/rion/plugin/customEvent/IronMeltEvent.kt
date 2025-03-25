package kr.rion.plugin.customEvent

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class IronMeltEvent(
    val player: Player,
    val item: ItemStack
) : Event() {
    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

    override fun getHandlers(): HandlerList {
        return handlerList // ✅ 올바른 방식
    }

}