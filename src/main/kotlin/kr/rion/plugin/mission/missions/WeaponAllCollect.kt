package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent

class WeaponAllCollect : Mission {
    private val requiredItems = listOf(
        Material.WOODEN_AXE,       // 나무도끼
        Material.DIAMOND_PICKAXE, // 다이아곡괭이
        Material.QUARTZ,   // 네더석영
        Material.FEATHER,         // Feather
        Material.LIGHT_GRAY_DYE,  // 회백색염료
        Material.STONE_PICKAXE,   // 돌괭이
        Material.STONE_SWORD      // 돌검
    )

    override fun missionStart(player: Player) {
        //딱히 작업할것 없음.
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            val inventory = player.inventory

            // 모든 아이템이 인벤토리에 있는지 확인
            val hasAllItems = requiredItems.all { requiredItem ->
                inventory.contains(requiredItem)
            }

            if (hasAllItems) {
                return true // 성공 조건 충족
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.sendMessage("§a축하합니다! 모든 무기를 수집하여 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
        // 이 미션에서는 별도 상태가 없으므로 초기화할 데이터가 없습니다.
    }
}
