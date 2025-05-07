package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.customEvent.IronMeltEvent
import kr.rion.plugin.customEvent.RevivalEvent
import kr.rion.plugin.manager.MissionManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.metadata.FixedMetadataValue

class MissionEvent : Listener {
    @EventHandler(ignoreCancelled = false)
    fun berralOpen(event: InventoryOpenEvent) {
        val player = event.player
        // Player로 캐스팅 가능한지 확인
        if (player is Player) {
            // MissionManager에 이벤트 처리 요청
            MissionManager.handleEvent(player, event)
        }
    }

    @EventHandler(ignoreCancelled = false)
    fun itemCheck(event: InventoryCloseEvent) {
        val player = event.player
        // Player로 캐스팅 가능한지 확인
        if (player is Player) {
            // MissionManager에 이벤트 처리 요청
            MissionManager.handleEvent(player, event)
        }
    }

    @EventHandler(ignoreCancelled = false)
    fun itemUse(event: PlayerInteractEvent) {
        val player = event.player

        // MissionManager에 이벤트 처리 요청
        MissionManager.handleEvent(player, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun blockBreak(event: BlockBreakEvent) {
        val player = event.player
        MissionManager.handleEvent(player, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun blockPlace(event: BlockPlaceEvent) {
        val player = event.player
        MissionManager.handleEvent(player, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun killer(event: PlayerDeathEvent) {
        val killer = event.entity.killer ?: return // 킬러가 없는 경우 종료
        MissionManager.handleEvent(killer, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun damager(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val victim = event.entity

        if (damager is Player) {
            MissionManager.handleEvent(damager, event) // 공격자 기준 미션
        }

        if (victim is Player) {
            MissionManager.handleEvent(victim, event) // 피해자 기준 미션 ← ⭐ 이거 추가해야 함
        }
    }


    @EventHandler(ignoreCancelled = false)
    fun itemDrop(event: PlayerDropItemEvent) {
        val player = event.player
        MissionManager.handleEvent(player, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun itemEat(event: PlayerItemConsumeEvent) {
        val player = event.player
        MissionManager.handleEvent(player, event)
    }

    @EventHandler(ignoreCancelled = false)
    fun itemPickup(event: EntityPickupItemEvent) {
        val pickupPlayer = event.entity
        if (pickupPlayer is Player) {
            // 미션 시스템에 이벤트 전달
            MissionManager.handleEvent(pickupPlayer, event)
        }
    }

    @EventHandler(ignoreCancelled = false)
    fun craft(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player is Player) {
            MissionManager.handleEvent(player, event)
        }
    }

    @EventHandler
    fun onRevivalEvent(event: RevivalEvent) {
        val player = event.relatedPlayer
        val targetPlayer = event.player

        // ✅ 이벤트 중복 실행 방지
        if (player?.hasMetadata("RevivalEventProcessed") == true) return
        if (targetPlayer.hasMetadata("RevivalEventProcessed")) return

        // ✅ 중복 실행 방지를 위한 태그 추가
        player?.setMetadata("RevivalEventProcessed", FixedMetadataValue(Loader.instance, true))
        targetPlayer.setMetadata("RevivalEventProcessed", FixedMetadataValue(Loader.instance, true))

        // ✅ 미션 처리
        MissionManager.handleEvent(player ?: return, event)
        MissionManager.handleEvent(targetPlayer, event)

        // 일정 시간 후 다시 부활 가능하도록 설정 (메모리 관리)
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            player.removeMetadata("RevivalEventProcessed", Loader.instance)
            targetPlayer.removeMetadata("RevivalEventProcessed", Loader.instance)
        }, 100L) // 5초 후 초기화 (원하는 시간으로 조정 가능)
    }


    @EventHandler
    fun ironMelt(event: IronMeltEvent) {
        val player = event.player
        MissionManager.handleEvent(player, event)
    }
}