package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockPlaceEvent
import java.util.*

class PlacePlanksMission(private val requiredCount: Int) : Mission {
    private val playerPlankPlacements = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        playerPlankPlacements[uuid] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is BlockPlaceEvent) {
            // 설치된 블록이 나무 판자인지 확인
            if (event.block.type in listOf(
                    Material.OAK_PLANKS,
                    Material.BIRCH_PLANKS,
                    Material.SPRUCE_PLANKS,
                    Material.JUNGLE_PLANKS,
                    Material.ACACIA_PLANKS,
                    Material.DARK_OAK_PLANKS,
                    Material.MANGROVE_PLANKS
                )
            ) {
                val uuid = player.uniqueId
                val currentCount = playerPlankPlacements.getOrDefault(uuid, 0) + 1
                playerPlankPlacements[uuid] = currentCount
                player.spigot()
                    .sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("§b나무 판자를 설치했습니다! (§e$currentCount§b/§d$requiredCount§b)")
                    )
                player.playSound(
                    player.location, // 플레이어 위치
                    Sound.BLOCK_NOTE_BLOCK_BELL, // 사운드 이름
                    1.0f, // 볼륨
                    1.7f // 피치
                )

                if (currentCount >= requiredCount) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        val uuid = player.uniqueId
        player.addScoreboardTag("MissionSuccess")
        playerPlankPlacements.remove(uuid) // 데이터 정리
    }

    override fun reset() {
        playerPlankPlacements.clear() // 모든 플레이어 데이터 초기화
    }
}
