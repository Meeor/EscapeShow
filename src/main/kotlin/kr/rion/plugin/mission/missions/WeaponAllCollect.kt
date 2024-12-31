package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent

class WeaponAllCollect : Mission {
    // 필요한 아이템과 사용자 정의 이름 설정
    private val requiredItems = mapOf(
        Material.WOODEN_AXE to "사냥도끼",
        Material.DIAMOND_PICKAXE to "대형망치",
        Material.QUARTZ to "칼날",
        Material.FEATHER to "검날",
        Material.LIGHT_GRAY_DYE to "날",
        Material.STONE_HOE to "단검",
        Material.STONE_SWORD to "마체테"
    )

    private val collectedItemsMap = mutableMapOf<Player, MutableSet<Material>>()

    override fun missionStart(player: Player) {
        collectedItemsMap[player] = mutableSetOf() // 플레이어 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            val inventory = player.inventory

            // 플레이어가 현재까지 수집한 아이템을 가져옴
            val collectedItems = collectedItemsMap.getOrDefault(player, mutableSetOf())

            // 새로운 아이템만 추가
            requiredItems.keys.forEach { requiredItem ->
                if (inventory.contains(requiredItem) && !collectedItems.contains(requiredItem)) {
                    collectedItems.add(requiredItem) // 새롭게 발견된 아이템 추가
                }
            }
            collectedItemsMap[player] = collectedItems

            // 현재 상태를 채팅으로 표시
            sendCollectionStatus(player, collectedItems)

            // 모든 아이템이 수집되었는지 확인
            if (collectedItems.containsAll(requiredItems.keys)) {
                return true // 성공 조건 충족
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.sendMessage("${MISSIONPREFIX}축하합니다! 모든 무기를 수집하여 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
        collectedItemsMap.remove(player) // 완료 후 데이터 정리
    }

    override fun reset() {
        collectedItemsMap.clear() // 모든 플레이어 데이터 초기화
    }

    private fun sendCollectionStatus(player: Player, collectedItems: Set<Material>) {
        val statusMessage = requiredItems.entries.joinToString(", ") { (material, displayName) ->
            if (collectedItems.contains(material)) {
                "§a$displayName" // 수집된 아이템: 녹색
            } else {
                "§c$displayName" // 수집되지 않은 아이템: 빨간색
            }
        }
        player.sendMessage("$MISSIONPREFIX§b현재 무기 수집 상태 : $statusMessage")
    }
}
