package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class DamageFreeMission(
    private val durationSeconds: Int, // 미션 지속 시간 (초 단위)
    private val plugin: JavaPlugin
) : Mission {
    private val lastDamageTimeMap = mutableMapOf<UUID, Long>() // 마지막 데미지 시간
    private val activePlayers = mutableSetOf<UUID>() // 현재 미션 진행 중인 플레이어

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        activePlayers.add(uuid)
        lastDamageTimeMap[uuid] = System.currentTimeMillis() // 현재 시간으로 초기화

        object : BukkitRunnable() {
            override fun run() {
                if (!activePlayers.contains(uuid)) {
                    cancel() // 미션 진행 중이 아니면 타이머 종료
                    return
                }

                val lastDamageTime = lastDamageTimeMap[uuid] ?: System.currentTimeMillis()
                val timeSinceLastDamage = (System.currentTimeMillis() - lastDamageTime) / 1000

                if (timeSinceLastDamage >= durationSeconds) {
                    onSuccess(player) // 지정된 시간 동안 데미지를 안 받으면 성공 처리
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 20L, 20L) // 1초마다 실행
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is EntityDamageByEntityEvent) {
            val uuid = player.uniqueId
            if (activePlayers.contains(uuid)) { // 현재 미션 진행 중인 경우만 처리
                lastDamageTimeMap[uuid] = System.currentTimeMillis() // 데미지 받으면 시간 초기화
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("$MISSIONPREFIX §c데미지를 받아 미션 진행 시간이 초기화되었습니다!")
                )
            }
        }
        return false // 미션은 계속 진행됨
    }

    override fun onSuccess(player: Player) {
        val uuid = player.uniqueId
        if (!activePlayers.contains(uuid)) return // 이미 완료된 경우 중복 실행 방지

        player.addScoreboardTag("MissionSuccess")
        activePlayers.remove(uuid) // 미션 성공 후 목록에서 제거
        lastDamageTimeMap.remove(uuid)
    }

    override fun reset() {
        activePlayers.clear() // 모든 미션 데이터 초기화
        lastDamageTimeMap.clear()
    }
}
