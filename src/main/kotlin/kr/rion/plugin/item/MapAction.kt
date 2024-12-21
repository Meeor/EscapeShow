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

        // 모든 온라인 플레이어에게 사운드 재생
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_GHAST_SHOOT, 0.3f, 1.3f)
        }

        // 현재 위치 기준으로 50블록 내에 있는 모든 플레이어 가져오기 (태그 필터링 포함)
        val nearestPlayer = player.getNearbyEntities(50.0, 50.0, 50.0)
            .filterIsInstance<Player>() // 엔티티 중 플레이어만 필터링
            .filter { nearbyPlayer ->
                !nearbyPlayer.scoreboardTags.contains("manager") &&
                        !nearbyPlayer.scoreboardTags.contains("EscapeComplete")
            }
            .minByOrNull { nearbyPlayer -> player.location.distance(nearbyPlayer.location) } // 가장 가까운 플레이어 찾기

        // 가장 가까운 플레이어가 존재하면 발광 효과 부여
        nearestPlayer?.let {
            it.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 14 * 20, 1, false, false))
        }

        // 플레이어의 인벤토리에서 아이템 제거
        inventory.removeItemFromInventory(player, Material.MOJANG_BANNER_PATTERN, 1)

        // 모든 온라인 플레이어에게 파티클 생성
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
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