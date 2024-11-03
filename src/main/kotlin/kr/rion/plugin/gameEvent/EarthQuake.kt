package kr.rion.plugin.gameEvent

import kr.rion.plugin.Loader
import kr.rion.plugin.util.Global.prefix
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object EarthQuake {
    private var earthquakeRunning = false
    private val playerStages = mutableMapOf<Player, Int>()

    fun start(sender: CommandSender) {
        if (earthquakeRunning) {
            sender.sendMessage("$prefix 지진이 이미 발생 중입니다!")
            return
        }

        earthquakeRunning = true
        Bukkit.broadcastMessage("$prefix 지진이 발생하였습니다")

        object : BukkitRunnable() {
            private var elapsedTime = 0

            override fun run() {
                if (elapsedTime >= 20 * 20) { // 20초 후 멈춤 (20틱 * 20초 = 400틱)
                    earthquakeRunning = false
                    cancel() // Task 종료
                    Bukkit.broadcastMessage("$prefix 지진이 멈췄습니다!")
                    return
                }
                // manager 또는 EscapeComplete 태그가 없는 플레이어들에 대해서만 지진 효과 적용
                Bukkit.getOnlinePlayers().filter { player ->
                    !player.scoreboardTags.contains("manager") && !player.scoreboardTags.contains("EscapeComplete") && !player.scoreboardTags.contains("death")
                }.forEach { player ->
                    val currentStage = playerStages.getOrDefault(player, 0)
                    applyEarthquakeEffects(player, currentStage)
                    playerStages[player] = (currentStage + 1) % 8 // 0 ~ 7 스테이지 순환
                }

                // 루프가 끝난 후에 elapsedTime을 증가시킴
                elapsedTime++
            }
        }.runTaskTimer(Loader.instance, 0L, 1L)  // 매 1틱마다 실행
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
        player.location.yaw = location.yaw

        // 사운드 및 파티클 효과 적용
        playEarthquakeSounds(player, stage)
        spawnEarthquakeParticles(player, stage) // stage 값을 전달하여 파티클 생성
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

    private fun spawnEarthquakeParticles(player: Player, stage: Int) {
        val location = player.location.clone()

        // 파티클 소환 (코아스 더트 블록 파티클 생성, stage에 따른 범위 조절)
        val range = when (stage) {
            1, 3, 5, 7 -> 2.0 // 짝수 스테이지에서 파티클 범위 2
            2, 4, 6 -> 1.0 // 홀수 스테이지에서 파티클 범위 1
            else -> 0.0
        }

        if (range > 0) {
            player.world.spawnParticle(
                Particle.BLOCK_CRACK,
                location.add(0.0, 0.1, 0.0), // 파티클의 위치
                50, // 파티클의 양
                range, 0.0, range, // X, Y, Z 범위
                0.0, // 속도
                Material.COARSE_DIRT.createBlockData() // 블록 파티클 타입
            )
        }
    }
}