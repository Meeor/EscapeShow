package kr.rion.plugin.gameEvent

import kr.rion.plugin.Loader
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object EarthQuake {
    private var earthquakeRunning = false
    private val playerStages = mutableMapOf<Player, Int>()

    fun start() {
        if (earthquakeRunning) {
            Bukkit.broadcastMessage("$prefix 지진이 이미 발생 중입니다!")
            return
        }

        earthquakeRunning = true
        Bukkit.broadcastMessage("$prefix 지진 이벤트가 시작되었습니다!")

        object : BukkitRunnable() {
            private var elapsedTime = 0

            override fun run() {
                if (elapsedTime >= 20 * 20) { // 20초 후 멈춤 (20틱 * 20초 = 400틱)
                    earthquakeRunning = false
                    cancel() // Task 종료
                    Bukkit.broadcastMessage("$prefix 지진이 멈췄습니다!")
                    return
                }

                // 모든 온라인 플레이어에 대해 지진 효과 적용
                for (player in Bukkit.getOnlinePlayers()) {
                    val currentStage = playerStages.getOrDefault(player, 0)
                    applyEarthquakeEffects(player, currentStage)
                    playerStages[player] = (currentStage + 1) % 8 // 0 ~ 7 스테이지 순환
                }
                elapsedTime++
            }

            private fun applyEarthquakeEffects(player: Player, stage: Int) {
                val location: Location = player.location
                val pitchChange = when (stage) {
                    0 -> -2f
                    1 -> 2f
                    2 -> 0.9f
                    3 -> -0.9f
                    4 -> -0.9f
                    5 -> 0.9f
                    6 -> 0.9f
                    7 -> -0.9f
                    else -> 0f
                }
                // 플레이어 시야 변경
                location.yaw += pitchChange
                player.teleport(location)

                // 사운드 및 파티클 효과 적용
                playEarthquakeSounds(player, stage)
                spawnEarthquakeParticles(player)
            }

            private fun playEarthquakeSounds(player: Player, stage: Int) {
                val volume = 0.5f
                val pitch = when (stage) {
                    0 -> 1f
                    1 -> 0.9f
                    2 -> 1f
                    3 -> 1.1f
                    4 -> 1f
                    5 -> 0.9f
                    6 -> 1.1f
                    7 -> 1f
                    else -> 1f
                }
                player.playSound(player.location, Sound.BLOCK_STONE_BREAK, volume, pitch)
                player.playSound(player.location, Sound.BLOCK_GRASS_BREAK, volume, 0f)
            }

            private fun spawnEarthquakeParticles(player: Player) {
                player.world.spawnParticle(Particle.BLOCK_CRACK, player.location.add(0.0, 0.1, 0.0), 50)
            }
        }.runTaskTimer(Loader.instance, 0L, 20L) // 20틱(1초)에 한번씩 실행, 즉 20초 동안 실행
    }
}