package kr.rion.plugin.gameEvent

import kr.rion.plugin.Loader
import kr.rion.plugin.util.global.prefix
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object Gravity {

    // 중력 이상 상태를 플레이어에게 30초 동안 적용하는 함수
    fun applyGravityAnomaly(players: Collection<Player>) {
        // 1번만 적용할 효과
        for (player in players) {
            // 엔드 포탈 스폰 사운드 (1번만 적용)
            player.playSound(player.location, Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 1.0f)
            // 텍스트 출력 (1번만 적용)
            player.sendMessage("\n\n\n\n\n$prefix ${ChatColor.BOLD}중력이상이 발생합니다!") // bold 효과
            // 포탈 소리 재생 (1번만 적용)
            player.playSound(player.location, Sound.BLOCK_PORTAL_AMBIENT, 0.2f, 1.0f)
        }

        // 5초마다 재생할 커스텀 사운드와 위치에 따른 중력 효과를 적용
        object : BukkitRunnable() {
            private var count = 0

            override fun run() {
                // 30초 동안(5초 간격으로 총 6회) 사운드 재생
                if (count >= 6) {
                    cancel() // 30초 후 종료
                    return
                }

                for (player in players) {
                    // 커스텀 중력 사운드 (5초마다 적용)
                    player.playSound(player.location, "minecraft:custom.gravity", 1f, 1f)
                }

                count++
            }
        }.runTaskTimer(Loader.instance, 0L, 100L) // 100틱 = 5초 (30초 동안 6번 실행)

        // 1틱마다 중력 효과를 플레이어 위치에 따라 적용
        object : BukkitRunnable() {
            private var tickCount = 0

            override fun run() {
                if (tickCount >= 600) { // 30초 후 종료 (1틱 = 0.05초, 600틱 = 30초)
                    cancel()
                    return
                }

                for (player in players) {
                    // 1틱마다 플레이어 위치에 따른 중력 효과 적용
                    applyGravityEffectBasedOnLocation(player, player.location)
                }

                tickCount++
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 1틱마다 실행
    }

    // 중력 강화 (중력 이상 상태일 때 적용)
    private fun applyGravityStrength(player: Player) {
        // 플레이어에게 효과 적용
        player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 100, 128, true, true)) // 5초간 유지
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 100, 1, true, true)) // 5초간 유지

        // 파티클 효과 (강화 시 붉은색)
        player.world.spawnParticle(Particle.REDSTONE, player.location, 10, 0.3, 0.0, 0.3new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0)

        // Action bar 메시지
        player.sendActionBar("$prefix ${ChatColor.BOLD}${ChatColor.RED}중력이 강화됩니다")
    }

    // 중력 약화 (중력 이상 상태일 때 적용)
    private fun applyGravityWeakness(player: Player) {
        // 플레이어에게 효과 적용
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 100, 3, true, true)) // 5초간 유지
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 100, 3, true, true)) // 5초간 유지
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100, 1, true, true)) // 5초간 유지

        // 파티클 효과 (약화 시 연두색)
        player.world.spawnParticle(Particle.REDSTONE, player.location, 10, 0.3, 0.0, 0.3,new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.0)

        // Action bar 메시지
        player.sendActionBar("$prefix ${ChatColor.BOLD}중력이 약화됩니다")
    }

    // 플레이어의 위치에 따라 중력 강화/약화 효과를 적용
    private fun applyGravityEffectBasedOnLocation(player: Player, location: Location) {
        val x = location.x
        val z = location.z

        // 중력 강화 조건
        if (x > 118 && z > -341) {
            applyGravityStrength(player)
        }
        // 중력 약화 조건
        else if (x < 118 && z < -341) {
            applyGravityWeakness(player)
        }
    }
}

