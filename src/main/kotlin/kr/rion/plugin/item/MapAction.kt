package kr.rion.plugin.item

import kr.rion.plugin.util.inventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MapAction {
    fun lunchMap(player: Player) {
        val loc = player.location.clone().add(0.0, 1.0, 0.0)

        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_GHAST_SHOOT, 0.3f, 1.3f)
        }
        // 현재 위치 기준으로 50블록 내에 있는 모든 플레이어 가져오기 (태그 필터링 포함)
        val nearbyPlayers = player.getNearbyEntities(50.0, 50.0, 50.0)
            .filterIsInstance<Player>() // 엔티티 중 플레이어만 필터링
            .filter { nearbyPlayer ->
                !nearbyPlayer.scoreboardTags.contains("manager") &&
                        !nearbyPlayer.scoreboardTags.contains("EscapeComplete")
            }

        // 각 플레이어에게 발광 효과 부여
        for (nearbyPlayer in nearbyPlayers) {
            nearbyPlayer.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 14*20, 1, false, false))
        }
        inventory.removeItemFromInventory(player, Material.MOJANG_BANNER_PATTERN, 1)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            // 파티클을 플레이어의 위치에 생성
            onlinePlayer.world.spawnParticle(
                Particle.END_ROD,
                loc,
                100,
                0.0,
                0.0,
                0.0,
                0.5
            )
        }
    }
}