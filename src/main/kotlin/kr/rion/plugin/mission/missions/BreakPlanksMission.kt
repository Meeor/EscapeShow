package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent

class BreakPlanksMission(private val requiredCount: Int) : Mission {
    private val playerPlankCounts = mutableMapOf<Player, Int>()

    override fun missionStart(player: Player) {
        playerPlankCounts[player] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is BlockBreakEvent) {
            // 플레이어가 나무 판자를 캤는지 확인
            if (event.block.type == Material.OAK_PLANKS) {
                val currentCount = playerPlankCounts.getOrDefault(player, 0) + 1
                playerPlankCounts[player] = currentCount
                player.sendMessage("§b나무 판자를 캤습니다! (§e$currentCount§b/§d$requiredCount§b)")

                if (currentCount >= requiredCount) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.sendMessage("§a축하합니다! 나무 판자 §e${requiredCount}§a개를 캐서 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
        playerPlankCounts.remove(player) // 데이터 정리
    }

    override fun reset() {
        playerPlankCounts.clear() // 모든 플레이어 데이터 초기화
    }
}