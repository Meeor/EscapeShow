package kr.rion.plugin.gameEvent

import kr.rion.plugin.gameEvent.GameEvent.excludedTags
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.delay
import org.bukkit.Bukkit
import org.bukkit.Particle

object WeatherWind {
    // 바람 (30초간 적용)
    fun applyWindWeather() {
        // 시간을 오전으로 변경
        Bukkit.getWorlds().forEach { world ->
            world.time = 3000L
        }
        val defaultWalkSpeed = 0.2f  // 기본 이동 속도
        val walkSpeed = 1.2 //이속수정 퍼센트
        Bukkit.getOnlinePlayers()
            .filter { player -> excludedTags.none { player.scoreboardTags.contains(it) } }
            .forEach { player ->
                val clampedPercent = walkSpeed.coerceIn(0.0, 5.0)  // 0% ~ 500% 사이로 제한
                val newSpeed = defaultWalkSpeed * clampedPercent
                player.walkSpeed = newSpeed.toFloat()
                player.world.spawnParticle(
                    Particle.CLOUD,               // 파티클 종류
                    player.location,              // 위치 (~ ~ ~)
                    100,                          // 수량
                    5.0, 5.0, 5.0,                // x, y, z 퍼짐 범위
                    0.0                           // 속도
                )

                // 메세지 출력
                player.sendMessage("$prefix 바람이 불기 시작합니다. 바람에 몸을 맡겨봅시다~")
                delay.delayRun(30 * 20) {
                    player.walkSpeed = defaultWalkSpeed
                }
            }

    }
}