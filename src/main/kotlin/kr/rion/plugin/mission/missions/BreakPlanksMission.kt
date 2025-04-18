package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

class BreakPlanksMission(private val requiredCount: Int) : Mission {
    private val playerPlankCounts = mutableMapOf<UUID, Int>()

    // 나무 판자 종류를 정의
    private val plankMaterials = setOf(
        Material.OAK_PLANKS,
        Material.SPRUCE_PLANKS,
        Material.BIRCH_PLANKS,
        Material.JUNGLE_PLANKS,
        Material.ACACIA_PLANKS,
        Material.DARK_OAK_PLANKS,
        Material.MANGROVE_PLANKS,
        Material.BAMBOO_PLANKS,
        Material.CHERRY_PLANKS
    )

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        playerPlankCounts[uuid] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is BlockBreakEvent) {
            // 플레이어가 나무 판자 종류 중 하나를 캤는지 확인
            if (event.block.type in plankMaterials) {
                val uuid = player.uniqueId
                val currentCount = playerPlankCounts.getOrDefault(player.uniqueId, 0) + 1
                playerPlankCounts[uuid] = currentCount
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§b나무 판자를 캤습니다! (§e$currentCount§b/§d$requiredCount§b)")
                )

                if (currentCount >= requiredCount) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        val uuid = player.uniqueId
        playerPlankCounts.remove(uuid) // 데이터 정리
    }

    override fun reset() {
        playerPlankCounts.clear() // 모든 플레이어 데이터 초기화
    }
}
