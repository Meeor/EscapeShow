package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
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

    // 플레이어별 누적 아이템 수 관리
    private val collectedItemCounts = mutableMapOf<Player, Int>()

    override fun missionStart(player: Player) {
        collectedItemCounts[player] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryCloseEvent) {
            val newItemCount = getCurrentItemCount(player)
            val currentCount = collectedItemCounts.getOrDefault(player, 0)

            // 새롭게 추가된 아이템 개수만 계산
            val newlyCollected = newItemCount - currentCount
            if (newlyCollected > 0) {
                collectedItemCounts[player] = newItemCount // 누적 갱신

                // 아이템 이름 가져오기 및 메시지 변경
                val baseName = itemNameMap[targetItem] ?: targetItem.name
                val actionVerb = if (targetItem == Material.IRON_INGOT) "구운" else "수집한"
//                player.sendMessage("§b현재 $actionVerb §e$baseName§b: §e$newItemCount §b/ §d$requiredCount")
                player.spigot()
                    .sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("§b현재 $actionVerb §e$baseName§b: §e$newItemCount §b/ §d$requiredCount")
                    )
            }

            return newItemCount >= requiredCount // 요구 개수 충족 여부 확인
        }
        return false
    }

    override fun onSuccess(player: Player) {
        val baseName = itemNameMap[targetItem] ?: targetItem.name
        val actionVerb = if (targetItem == Material.IRON_INGOT) "구웠습니다" else "수집했습니다"

        player.sendMessage("$MISSIONPREFIX§a축하합니다! §e$baseName§a을(를) §e${requiredCount}§a개 $actionVerb! 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
        collectedItemCounts.remove(player) // 완료 후 데이터 정리
    }

    override fun reset() {
        collectedItemCounts.clear() // 모든 플레이어 데이터 초기화
    }

    private fun getCurrentItemCount(player: Player): Int {
        val inventory = player.inventory
        return inventory.contents.filterNotNull()
            .count { it.type == targetItem } // 현재 인벤토리에서 해당 아이템 개수 계산
    }
}
