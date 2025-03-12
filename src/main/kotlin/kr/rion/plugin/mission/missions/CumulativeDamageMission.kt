package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.UUID

class CumulativeDamageMission(private val requiredDamage: Double) : Mission {
    private val playerDamageMap = mutableMapOf<UUID, Double>() // 플레이어별 누적 데미지 추적

    override fun missionStart(player: Player) {
        playerDamageMap[player.uniqueId] = 0.0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is EntityDamageByEntityEvent) {
            val damager = event.damager as? Player ?: return false

            if (damager == player) { // 미션 수행 중인 플레이어인지 확인
                if (event.entity is Player) { // 데미지를 받은 대상이 플레이어인지 확인
                    val currentDamage = playerDamageMap.getOrDefault(player.uniqueId, 0.0) + event.damage
                    playerDamageMap[player.uniqueId] = currentDamage
                    player.spigot()
                        .sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent("§b누적 데미지: §e$currentDamage§b / §d$requiredDamage")
                        )

                    if (currentDamage >= requiredDamage) {
                        return true // 성공 조건 충족
                    }
                }
                return false // 대상이 플레이어가 아닌 경우
            }
        }
        return false // 이벤트가 조건을 충족하지 않는 경우
    }


    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerDamageMap.remove(player.uniqueId) // 데이터 정리
    }

    override fun reset() {
        playerDamageMap.clear() // 모든 플레이어 데이터 초기화
    }
}
