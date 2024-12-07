package kr.rion.plugin.event

import kr.rion.plugin.util.Teleport.immunePlayers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DamageEvent : Listener {
    //텔레포트시 잠시동안 무적상태
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val currentTime = System.currentTimeMillis()
            // 플레이어가 면역 상태인지 확인
            if (immunePlayers[player]?.let { it > currentTime } == true) {
                event.isCancelled = true // 면역 상태일 경우 데미지 무시
            }
        }
    }
}