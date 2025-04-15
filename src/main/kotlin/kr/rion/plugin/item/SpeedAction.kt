package kr.rion.plugin.item

import kr.rion.plugin.util.Delay
import kr.rion.plugin.util.inventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object SpeedAction {
    fun lunchSpeed(player: Player) {
        val loc = player.location.clone().add(0.0, 0.5, 0.0)
        inventory.removeItemFromInventory(player, Material.BEETROOT, 1)
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f)
        }
        val speedeffect = PotionEffect(PotionEffectType.SPEED, 5 * 20, 1, false, false)
        player.addPotionEffect(speedeffect)
        Delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { player -> // 파티클을 플레이어의 위치에 생성
                player.world.spawnParticle(
                    Particle.CLOUD,
                    loc,
                    2,
                    0.3,
                    0.3,
                    0.3,
                    1.0
                )

            }
        )
    }
}