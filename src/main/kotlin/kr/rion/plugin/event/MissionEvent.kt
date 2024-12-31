package kr.rion.plugin.event

import kr.rion.plugin.manager.MissionManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerMoveEvent

class MissionEvent :Listener {
    @EventHandler(ignoreCancelled = false)
    fun berralOpen(event: InventoryOpenEvent){
        val player = event.player
        // Player로 캐스팅 가능한지 확인
        if (player is Player) {
            // MissionManager에 이벤트 처리 요청
            MissionManager.handleEvent(player, event)
        }
    }

    @EventHandler(ignoreCancelled = false)
    fun itemCheck(event: InventoryCloseEvent){
        val player = event.player
        // Player로 캐스팅 가능한지 확인
        if (player is Player) {
            // MissionManager에 이벤트 처리 요청
            MissionManager.handleEvent(player, event)
        }
    }

    @EventHandler(ignoreCancelled = false)
    fun blockBreak(event: BlockBreakEvent){
        val player = event.player
        MissionManager.handleEvent(player,event)
    }

    @EventHandler(ignoreCancelled = false)
    fun blockPlace(event: BlockPlaceEvent){
        val player = event.player
        MissionManager.handleEvent(player,event)
    }
    @EventHandler(ignoreCancelled = false)
    fun killer(event: PlayerDeathEvent){
        val killer = event.entity.killer ?: return // 킬러가 없는 경우 종료
        MissionManager.handleEvent(killer, event)
    }
    @EventHandler(ignoreCancelled = false)
    fun damager(event: EntityDamageByEntityEvent){
        val damager = event.damager
        if (damager is Player) {
            // 미션 시스템에 이벤트 전달
            MissionManager.handleEvent(damager, event)
        }
    }
    @EventHandler(ignoreCancelled = false)
    fun blockstep(event: PlayerMoveEvent){
        val player = event.player
        MissionManager.handleEvent(player, event)
    }
}