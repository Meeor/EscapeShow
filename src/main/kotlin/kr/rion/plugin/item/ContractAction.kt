package kr.rion.plugin.item

import kr.rion.plugin.util.inventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object ContractAction {
    fun lunchContract(player: Player) {

        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.4f)
        }
        // 현재 체력과 최대 체력 감소
        val currentHealth = player.health
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

        // 최대 체력 감소 (최대 체력을 50%로 줄임)
        val newMaxHealth = maxHealth / 2
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = newMaxHealth

        // 현재 체력이 새로운 최대 체력을 초과하지 않도록 조정
        player.health = minOf(currentHealth, newMaxHealth)

        // 데미지 증가 (데미지를 2배로 설정)
        val currentAttackDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue ?: 1.0
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = currentAttackDamage * 2
        player.damage(0.1)
        inventory.removeItemFromInventory(player, Material.SKULL_BANNER_PATTERN, 1)

        player.addScoreboardTag("Contract_Activate")
    }

    fun resetContract() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.scoreboardTags.contains("Contract_Activate")) {
                val playerhealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                playerhealth?.baseValue = 20.0 // 최대 체력을 설정합니다.
                player.health = 20.0 // 현재 체력도 최대 체력에 맞춰줍니다.
                player.removeScoreboardTag("Contract_Activate")

                val playerdamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                if (playerdamage != null) {
                    val currentDamage = playerdamage.baseValue
                    playerdamage.baseValue = currentDamage / 2
                }
            }
        }
    }
}