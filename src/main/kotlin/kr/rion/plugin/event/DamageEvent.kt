package kr.rion.plugin.event

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageEvent : Listener {
    //탈출한 플레이어가 받는 모든데미지 무시
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 받는 플레이어공격및엔티티공격 전부무시
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true // 다른 플레이어의 공격도 무효화
            }
        }
    }
}