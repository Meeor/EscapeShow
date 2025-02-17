package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent

class CumulativeDamageTakenMission(private val requiredDamage: Double) : Mission {
    private val playerDamageTakenMap = mutableMapOf<Player, Double>() // 플레이어별 누적 받은 데미지 추적

    override fun missionStart(player: Player) {
        playerDamageTakenMap[player] = 0.0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is EntityDamageByEntityEvent) {
            val target = event.entity as? Player ?: return false // 데미지를 받은 대상이 플레이어인지 확인

            if (target == player) { // 미션 수행 중인 플레이어인지 확인
                val currentDamageTaken = playerDamageTakenMap.getOrDefault(player, 0.0) + event.damage
                playerDamageTakenMap[player] = currentDamageTaken
                player.spigot()
                    .sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("§b누적 받은 데미지: §e$currentDamageTaken§b / §d$requiredDamage")
                    )

                if (currentDamageTaken >= requiredDamage) {
                    return true // 성공 조건 충족
                }
            }
        }
        return false // 이벤트가 조건을 충족하지 않는 경우
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerDamageTakenMap.remove(player) // 데이터 정리
    }

    override fun reset() {
        playerDamageTakenMap.clear() // 모든 플레이어 데이터 초기화
    }
}
