package kr.rion.plugin.item

import kr.rion.plugin.util.inventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object BerriesAction {
    fun lunchBerries(player: Player) {
        val loc = player.location.clone().add(0.0, 1.0, 0.0)

        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 0.0f)
        }
        val regenerationeffect = PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1, false, false)
        player.addPotionEffect(regenerationeffect)
        val currentHunger = player.foodLevel
        player.foodLevel = (currentHunger + 6).coerceAtMost(20)
        inventory.removeItemFromInventory(player, Material.GLOW_BERRIES, 1)

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

    }
}