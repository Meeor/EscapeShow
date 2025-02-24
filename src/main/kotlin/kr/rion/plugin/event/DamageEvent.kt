package kr.rion.plugin.event

import kr.rion.plugin.manager.TeamManager
import kr.rion.plugin.manager.TeamManager.teamPvpBoolean
import kr.rion.plugin.util.Teleport.immunePlayers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class DamageEvent : Listener {
    // 텔레포트 시 잠시동안 무적 상태 적용 (startAction()에서 관리됨)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (immunePlayers.contains(player)) { // ✅ 면역 상태 확인
                event.isCancelled = true // ✅ 데미지 무효화
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val victim = event.entity

        // ✅ 공격자와 피해자가 모두 플레이어인지 확인
        if (damager is Player && victim is Player) {
            // ✅ teamPvpBoolean이 false일 때, 같은 팀이면 PVP 차단
            if (!teamPvpBoolean && TeamManager.isSameTeam(damager.name, victim.name)) {
                event.isCancelled = true
            }
        }
    }

}