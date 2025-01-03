package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

class ItemCollectMission(private val targetItem: Material, private val requiredCount: Int) : Mission {

    // 사용자 정의 아이템 이름 매핑
    private val itemNameMap = mapOf(
        Material.STONE_HOE to "단검",
        Material.SWEET_BERRIES to "달콤한 열매",
        Material.IRON_INGOT to "철"
    )

    // 플레이어별 현재 인벤토리 상태 및 누적 아이템 수 관리
    private val currentInventoryCounts = mutableMapOf<Player, Int>()
    private val totalCollectedCounts = mutableMapOf<Player, Int>()

    override fun missionStart(player: Player) {
        currentInventoryCounts[player] = 0 // 초기화
        totalCollectedCounts[player] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        when (event) {
            is InventoryCloseEvent -> handleInventoryClose(player)
            is PlayerDropItemEvent -> handleItemDrop(player, event)
            is PlayerItemConsumeEvent -> handleItemConsume(player, event)
        }

        // 누적된 수집량이 조건을 충족했는지 확인
        return (totalCollectedCounts[player] ?: 0) >= requiredCount
    }

    override fun onSuccess(player: Player) {
        val baseName = itemNameMap[targetItem] ?: targetItem.name
        val actionVerb = if (targetItem == Material.IRON_INGOT) "구웠습니다" else "수집했습니다"

        player.sendMessage("${MISSIONPREFIX}축하합니다! §e$baseName§a을(를) §e${requiredCount}§a개 $actionVerb! 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
        currentInventoryCounts.remove(player) // 완료 후 데이터 정리
        totalCollectedCounts.remove(player) // 완료 후 데이터 정리
    }

    override fun reset() {
        currentInventoryCounts.clear() // 현재 인벤토리 상태 초기화
        totalCollectedCounts.clear() // 누적 수집량 초기화
    }

    private fun handleInventoryClose(player: Player) {
        val currentInventoryCount = getCurrentItemCount(player) // 현재 인벤토리 상태
        val previousInventoryCount = currentInventoryCounts.getOrDefault(player, 0)

        if (currentInventoryCount > previousInventoryCount) {
            // 새로 추가된 아이템 계산
            val newlyCollected = currentInventoryCount - previousInventoryCount

            // 누적 수집량 업데이트
            val previousTotal = totalCollectedCounts.getOrDefault(player, 0)
            totalCollectedCounts[player] = previousTotal + newlyCollected
        }

        // 현재 상태 업데이트 (증가/감소 모두 반영)
        currentInventoryCounts[player] = currentInventoryCount
    }

    private fun handleItemDrop(player: Player, event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack

        // 버린 아이템이 타겟 아이템인지 확인
        if (item.type == targetItem) {
            val droppedAmount = item.amount
            val currentInventoryCount = currentInventoryCounts.getOrDefault(player, 0)

            // 현재 인벤토리 상태에서 차감
            currentInventoryCounts[player] = kotlin.math.max(0, currentInventoryCount - droppedAmount)

            // 누적 수집량에서도 차감
            val currentTotal = totalCollectedCounts.getOrDefault(player, 0)
            totalCollectedCounts[player] = kotlin.math.max(0, currentTotal - droppedAmount)
        }
    }

    private fun handleItemConsume(player: Player, event: PlayerItemConsumeEvent) {
        val item = event.item

        // 섭취한 아이템이 타겟 아이템인지 확인
        if (item.type == targetItem) {
            val currentTotal = totalCollectedCounts.getOrDefault(player, 0)

            // 섭취한 아이템 개수를 누적 수집량에서 차감
            totalCollectedCounts[player] = kotlin.math.max(0, currentTotal - item.amount)
        }
    }

    private fun getCurrentItemCount(player: Player): Int {
        val inventory = player.inventory
        return inventory.contents.filterNotNull()
            .count { it.type == targetItem } // 현재 인벤토리에서 해당 아이템 개수 계산
    }
}
