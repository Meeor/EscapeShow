package kr.rion.plugin.event

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PlayerAttackEvent: Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onAttack(event: EntityDamageByEntityEvent){
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true
            }
        }
    }
}