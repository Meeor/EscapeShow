package kr.rion.plugin.item

import kr.rion.plugin.Loader
import kr.rion.plugin.util.inventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable


object HealAction {
    fun lunchHeal(player: Player) {

        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.BLOCK_WOOL_FALL, 0.3f, 0.0f)
        }

        val loc = player.location.clone().add(0.0, 1.0, 0.0)
        val count = 4 // 반복 횟수

        object : BukkitRunnable() {
            var currentCount = 0 // 현재 반복 횟수

            override fun run() {
                if (currentCount >= count) {
                    val effect = PotionEffect(PotionEffectType.SLOW, 3, 1, false, false)
                    player.addPotionEffect(effect)
                    inventory.removeItemFromInventory(player, Material.PAPER,1)
                    val maxHealth = player.maxHealth
                    val newHealth = (player.health + 5).coerceAtMost(maxHealth)
                    player.health = newHealth
                    this.cancel() // 반복 횟수에 도달하면 작업 종료
                    return
                }

                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    // 파티클을 플레이어의 위치에 생성
                    onlinePlayer.world.spawnParticle(
                        Particle.HEART,
                        loc,
                        2,
                        0.3,
                        0.3,
                        0.3,
                        1.0
                    )
                }

                currentCount++ // 반복 횟수 증가
            }
        }.runTaskTimer(Loader.instance, 0L, 20L)
    }
}