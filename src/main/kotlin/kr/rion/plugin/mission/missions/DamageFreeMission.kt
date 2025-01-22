package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.plugin.java.JavaPlugin

class DamageFreeMission(
    private val durationSeconds: Int, // 미션 지속 시간 (초 단위)
    private val plugin: JavaPlugin
) : Mission {
    private val lastDamageTimeMap = mutableMapOf<Player, Long>() // 플레이어별 마지막 데미지 시간
    private val activePlayers = mutableSetOf<Player>() // 미션 활성화 중인 플레이어

    override fun missionStart(player: Player) {
        activePlayers.add(player)
        lastDamageTimeMap[player] = System.currentTimeMillis() // 현재 시간을 기록

        // 1초 간격으로 10분 동안 감시
        object : BukkitRunnable() {
            private var elapsedSeconds = 0

            override fun run() {
                if (!activePlayers.contains(player)) {
                    cancel() // 플레이어가 미션에서 제외되면 타이머 취소
                    return
                }

                elapsedSeconds++

                val lastDamageTime = lastDamageTimeMap[player] ?: System.currentTimeMillis()
                val timeSinceLastDamage = (System.currentTimeMillis() - lastDamageTime) / 1000

                if (timeSinceLastDamage >= durationSeconds) {
                    onSuccess(player) // 10분 동안 데미지를 받지 않았으면 성공 처리
                    cancel()
                }

                if (elapsedSeconds >= durationSeconds) {
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 20L, 20L) // 1초마다 실행
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is EntityDamageEvent) {
            val entity = event.entity as? Player ?: return false
            if (entity == player) {
                // 데미지를 받으면 마지막 시간 업데이트
                lastDamageTimeMap[player] = System.currentTimeMillis()
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("$MISSIONPREFIX §c데미지를 받아 미션진행시간이 초기화 되었습니다!")
                )
            }
        }
        return false
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        activePlayers.remove(player)
        lastDamageTimeMap.remove(player)
    }

    override fun reset() {
        // 모든 플레이어의 데이터 초기화
        activePlayers.clear()
        lastDamageTimeMap.clear()
    }
}
