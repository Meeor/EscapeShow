package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent

class ItemCollectMission(private val targetItem: Material, private val requiredCount: Int) : Mission {

    override fun missionStart(player: Player) {
        //아무것도안해도됨.
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            return hasRequiredItems(player) // 아이템 수집 조건 확인
        }
        return false
    }

    override fun onSuccess(player: Player) {
        player.sendMessage("§a축하합니다! §e${targetItem.name}§a을(를) §e${requiredCount}§a개 수집하여 미션을 완료했습니다!")
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
