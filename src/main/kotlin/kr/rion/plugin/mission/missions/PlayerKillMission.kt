package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.UUID

class PlayerKillMission(private val requiredKills: Int) : Mission {
    private val playerKillCounts = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        playerKillCounts[player.uniqueId] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is PlayerDeathEvent) {
            val killer = event.entity.killer ?: return false

            if (killer == player) {
                val currentKills = playerKillCounts.getOrDefault(player.uniqueId, 0) + 1
                playerKillCounts[player.uniqueId] = currentKills
                player.spigot()
                    .sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("§b플레이어를 처치했습니다! (§e$currentKills§b/§d$requiredKills§b)")
                    )

                if (currentKills >= requiredKills) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerKillCounts.remove(player.uniqueId) // 데이터 정리
    }

    override fun reset() {
        playerKillCounts.clear() // 모든 플레이어 데이터 초기화
    }
}
