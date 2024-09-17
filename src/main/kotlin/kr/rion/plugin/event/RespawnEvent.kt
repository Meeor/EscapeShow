package kr.rion.plugin.event

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class RespawnEvent : Listener {
    //사망시 리스폰위치변경
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        // 리스폰 위치를 변경하지 않거나 특정 위치로 설정할 수 있습니다.
        // 예를 들어, 원래 리스폰 위치를 설정하지 않도록 하고 싶다면 이 코드를 추가합니다:
        event.respawnLocation = player.location // 플레이어의 사망 위치로 리스폰 설정
    }
}