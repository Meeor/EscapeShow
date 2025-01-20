package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryCloseEvent
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent

class WeaponAllCollect : Mission {
    // 필요한 아이템 목록
    private val requiredItems = setOf(
        Material.IRON_SWORD, // 사냥도끼
        Material.WOODEN_AXE, // 대형망치
        Material.QUARTZ, // 칼날
        Material.FEATHER, // 검날
        Material.LIGHT_GRAY_DYE, // 날
        Material.WOODEN_SWORD, // 단검
        Material.STONE_SWORD // 마체테
    )

    private val collectedItemsMap = mutableMapOf<Player, MutableMap<Material, Int>>()

    override fun missionStart(player: Player) {
        collectedItemsMap[player] = mutableMapOf() // 플레이어 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            val inventory = player.inventory

            // 플레이어가 현재까지 수집한 아이템을 가져옴
            val collectedItems = collectedItemsMap.getOrPut(player) { mutableMapOf() }

            // 아이템을 확인하고 수량을 업데이트
            requiredItems.forEach { requiredItem ->
                val count = inventory.all(requiredItem).values.sumOf { it.amount }
                if (count > 0) {
                    collectedItems[requiredItem] = (collectedItems[requiredItem] ?: 0) + count
                }
            }

            collectedItemsMap[player] = collectedItems

            // 현재 상태를 액션바로 표시
            sendCollectionStatus(player, collectedItems.values.sum())

            // 총 수집한 아이템 수량 확인
            val totalCollected = collectedItems.values.sum()
            if (totalCollected >= 3) {
                return true // 성공 조건 충족
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        collectedItemsMap.remove(player) // 완료 후 데이터 정리
    }

    override fun reset() {
        collectedItemsMap.clear() // 모든 플레이어 데이터 초기화
    }

    private fun sendCollectionStatus(player: Player, totalCollected: Int) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("$MISSIONPREFIX§b무기 수집량 : §e$totalCollected 개"))
    }
}
