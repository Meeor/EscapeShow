package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.persistence.PersistentDataType

class ItemUsageMission(
    private val targetMaterial: Material, // 아이템 타입
    private val customTagKey: String, // 커스텀 태그 이름
    private val requiredUses: Int, // 총 사용 횟수
    private val plugin: JavaPlugin // 플러그인 인스턴스
) : Mission {
    private val playerUsageMap = mutableMapOf<Player, Int>() // 플레이어별 사용 횟수 추적

    override fun missionStart(player: Player) {
        playerUsageMap[player] = 0 // 초기 사용 횟수 설정
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is PlayerInteractEvent) {
            val item = event.item ?: return false
            if (isValidItem(item)) { // 아이템 타입과 커스텀 태그 확인
                incrementUsage(player)
                return isMissionCompleted(player)
            }
        }
        return false
    }

    private fun isValidItem(item: ItemStack): Boolean {
        // 아이템 타입 확인
        if (item.type != targetMaterial) return false

        // 커스텀 태그 확인
        val meta = item.itemMeta ?: return false
        val gipsKey = NamespacedKey(plugin, customTagKey) // 커스텀 태그 키 생성
        return meta.persistentDataContainer.has(gipsKey, PersistentDataType.BYTE) // BYTE 타입 확인
    }

    private fun incrementUsage(player: Player) {
        val currentUsage = playerUsageMap.getOrDefault(player, 0) + 1
        playerUsageMap[player] = currentUsage
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            TextComponent("§d$targetMaterial §b아이템 사용 횟수: §e$currentUsage§b / §d$requiredUses")
        )
    }

    private fun isMissionCompleted(player: Player): Boolean {
        return (playerUsageMap[player] ?: 0) >= requiredUses
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerUsageMap.remove(player) // 플레이어 데이터 정리
    }

    override fun reset() {
        playerUsageMap.clear() // 모든 플레이어 데이터 초기화
    }
}
