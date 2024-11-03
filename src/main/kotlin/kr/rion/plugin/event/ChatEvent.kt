package kr.rion.plugin.event

import kr.rion.plugin.util.Global.prefix
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEvent : Listener {
    //채팅금지
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!event.player.hasPermission("chat.on")) {
            event.isCancelled = true
            event.player.sendMessage("$prefix §l§c채팅이 금지되어있습니다.")
        }
    }
}