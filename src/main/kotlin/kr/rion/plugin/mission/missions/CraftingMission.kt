package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class CraftingMission(
    private val targetItems: List<Pair<Material, Int>> // 미션 아이템(Material)과 목표 수량(Int)
) : Mission {

    // 사용자 정의 아이템 이름 매핑
    private val itemNameMap = mapOf(
        Material.DIAMOND to "다이아몬드",
        Material.EMERALD to "에메랄드",
        Material.IRON_INGOT to "철"
    )

    // 플레이어별 누적 거래 아이템 수 관리
    private val collectedItemCounts = mutableMapOf<Player, MutableMap<Material, Int>>()

    override fun missionStart(player: Player) {
        collectedItemCounts[player] = mutableMapOf() // 초기화
        targetItems.forEach { (material, _) ->
            collectedItemCounts[player]?.put(material, 0) // 목표 아이템 초기화
        }
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryClickEvent) {
            handleTradeClick(player, event) // 거래 클릭 처리
        }

        // 모든 목표 아이템 수량을 충족했는지 확인
        return targetItems.all { (material, requiredCount) ->
            (collectedItemCounts[player]?.get(material) ?: 0) >= requiredCount
        }
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        collectedItemCounts.remove(player) // 완료 후 데이터 정리
    }

    override fun reset() {
        collectedItemCounts.clear() // 데이터 초기화
    }

    private fun handleTradeClick(player: Player, event: InventoryClickEvent) {
        // 주민 거래 GUI인지 확인
        if (event.view.topInventory.type != InventoryType.MERCHANT) return

        val clickedSlot = event.slot // 클릭한 슬롯 번호
        val clickedItem = event.currentItem ?: return // 클릭한 아이템

        // 거래 완료 슬롯 확인 (테스트 후 정확한 슬롯 번호로 수정)
        if (clickedSlot != 2) return

        val material = clickedItem.type

        // 클릭한 아이템이 목표 아이템인지 확인
        if (targetItems.any { it.first == material }) {
            val addedAmount = clickedItem.amount
            val previousCount = collectedItemCounts[player]?.get(material) ?: 0

            // 누적 수집량 업데이트
            collectedItemCounts[player]?.put(material, previousCount + addedAmount)

            // 진행도 액션바 출력
            val koreanName = itemNameMap[material] ?: material.name
            val currentProgress = collectedItemCounts[player]?.get(material) ?: 0
            val requiredCount = targetItems.first { it.first == material }.second
            player.sendMessage("§b${koreanName} 조합 : §e${currentProgress}§b/§d${requiredCount}")
        }
    }
}
