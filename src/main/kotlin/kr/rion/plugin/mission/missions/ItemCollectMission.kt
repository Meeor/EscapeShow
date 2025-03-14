package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import java.util.*

class ItemCollectMission(private val targetItem: Material, private val requiredCount: Int) : Mission {

    // 플레이어별 현재 인벤토리 상태 및 누적 아이템 수 관리
    private val currentInventoryCounts = mutableMapOf<UUID, Int>()
    private val totalCollectedCounts = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        currentInventoryCounts[uuid] = 0 // 초기화
        totalCollectedCounts[uuid] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        when (event) {
            is InventoryCloseEvent -> handleInventoryClose(player)
            is PlayerDropItemEvent -> handleItemDrop(player, event)
            is PlayerItemConsumeEvent -> handleItemConsume(player, event)
            is EntityPickupItemEvent -> handleItemPickup(player, event)
        }
        val uuid = player.uniqueId

        // 누적된 수집량이 조건을 충족했는지 확인
        return (totalCollectedCounts[uuid] ?: 0) >= requiredCount
    }

    override fun onSuccess(player: Player) {
        val uuid = player.uniqueId
        player.addScoreboardTag("MissionSuccess")
        currentInventoryCounts.remove(uuid) // 완료 후 데이터 정리
        totalCollectedCounts.remove(uuid) // 완료 후 데이터 정리
    }

    override fun reset() {
        currentInventoryCounts.clear() // 현재 인벤토리 상태 초기화
        totalCollectedCounts.clear() // 누적 수집량 초기화
    }

    private fun handleInventoryClose(player: Player) {
        val uuid = player.uniqueId
        // 0~8번 슬롯에서 목표 아이템 개수 계산
        val currentInventoryCount = (0..8)
            .mapNotNull { player.inventory.getItem(it) } // 해당 슬롯에서 아이템 가져오기 (null은 제외)
            .filter { it.type == targetItem } // 목표 아이템 타입 필터링
            .sumOf { it.amount } // 목표 아이템의 총 개수 계산


        val previousInventoryCount = currentInventoryCounts.getOrDefault(uuid, 0)

        if (currentInventoryCount > previousInventoryCount) {
            // 새로 추가된 아이템 계산
            val newlyCollected = currentInventoryCount - previousInventoryCount

            // 누적 수집량 업데이트
            val previousTotal = totalCollectedCounts.getOrDefault(uuid, 0)
            totalCollectedCounts[uuid] = previousTotal + newlyCollected
        }

        // 현재 상태 업데이트 (증가/감소 모두 반영)
        currentInventoryCounts[uuid] = currentInventoryCount

    }


    private fun handleItemDrop(player: Player, event: PlayerDropItemEvent) {
        val uuid = player.uniqueId
        val item = event.itemDrop.itemStack

        // 버린 아이템이 타겟 아이템인지 확인
        if (item.type == targetItem) {
            val droppedAmount = item.amount
            val currentInventoryCount = currentInventoryCounts.getOrDefault(uuid, 0)

            // 현재 인벤토리 상태에서 차감
            currentInventoryCounts[uuid] = kotlin.math.max(0, currentInventoryCount - droppedAmount)
        }
    }

    private fun handleItemConsume(player: Player, event: PlayerItemConsumeEvent) {
        val uuid = player.uniqueId
        val item = event.item

        // 섭취한 아이템이 타겟 아이템인지 확인
        if (item.type == targetItem) {
            val currentInventoryCount = currentInventoryCounts.getOrDefault(uuid, 0)

            // 섭취한 아이템 개수를 현재 상태에서 차감
            currentInventoryCounts[uuid] = kotlin.math.max(0, currentInventoryCount - item.amount)
        }
    }

    private fun handleItemPickup(player: Player, event: EntityPickupItemEvent) {
        val uuid = player.uniqueId
        // 아이템을 주운 엔티티가 플레이어인지 확인
        if (event.entity is Player && event.entity == player) {
            val item = event.item.itemStack

            // 주운 아이템이 타겟 아이템인지 확인
            if (item.type == targetItem) {
                val pickedAmount = item.amount
                val currentInventoryCount = currentInventoryCounts.getOrDefault(uuid, 0)

                // 현재 인벤토리 상태 업데이트
                currentInventoryCounts[uuid] = currentInventoryCount + pickedAmount
            }
        }
    }
}
