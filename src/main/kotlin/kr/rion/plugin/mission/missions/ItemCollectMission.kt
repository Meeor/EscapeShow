package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent

class ItemCollectMission(private val targetItem: Material, private val requiredCount: Int) : Mission {

    // 사용자 정의 아이템 이름 매핑
    private val itemNameMap = mapOf(
        Material.STONE_HOE to "단검",
        Material.SWEET_BERRIES to "달콤한 열매",
        Material.IRON_INGOT to "철"
    )

    override fun missionStart(player: Player) {
        // 별도 작업 필요 없음
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            return hasRequiredItems(player) // 아이템 수집 조건 확인
        }
        return false
    }

    override fun onSuccess(player: Player) {
        // 사용자 정의 이름을 가져오고 없으면 기본 Material 이름 사용
        val baseName = itemNameMap[targetItem] ?: targetItem.name
        val actionVerb = if (targetItem == Material.IRON_INGOT) "구웠습니다" else "수집했습니다"

        player.sendMessage("§a축하합니다! §e$baseName§a을(를) §e${requiredCount}§a개 $actionVerb! 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
        // 별도 초기화 필요 없음
    }

    private fun hasRequiredItems(player: Player): Boolean {
        val inventory = player.inventory
        val itemCount = inventory.contents.filterNotNull() // null 제거
            .count { it.type == targetItem } // 대상 아이템 개수 세기
        return itemCount >= requiredCount // 요구 갯수 이상인지 확인
    }
}
