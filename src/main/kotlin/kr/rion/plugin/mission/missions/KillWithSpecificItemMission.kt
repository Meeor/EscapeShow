package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.PlayerDeathEvent

class KillWithSpecificItemMission(private val requiredItem: Material) : Mission {

    // 사용자 정의 아이템 이름 매핑
    private val itemNameMap = mapOf(
        Material.COBBLESTONE to "조약돌",
        Material.AIR to "맨손"
    )

    override fun missionStart(player: Player) {
        // 별도 작업 필요 없음
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is PlayerDeathEvent) {
            val killer = event.entity.killer ?: return false // 킬러가 없는 경우 종료

            if (killer == player) {
                // 킬러가 특정 아이템을 들고 있었는지 확인
                val mainHandItem = killer.inventory.itemInMainHand.type
                if (mainHandItem == requiredItem) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        // 사용자 정의 이름을 가져오고, 없으면 기본 Material 이름 사용
        val itemName = itemNameMap[requiredItem] ?: requiredItem.name
        player.sendMessage("$MISSIONPREFIX§a축하합니다! §e$itemName§a으로 플레이어를 처치하여 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
        // 이 미션에서는 별도 상태 관리가 필요하지 않음
    }
}
