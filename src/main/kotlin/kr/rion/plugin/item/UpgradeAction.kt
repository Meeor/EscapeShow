package kr.rion.plugin.item

import kr.rion.plugin.util.Delay
import kr.rion.plugin.util.inventory
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import java.util.*

object UpgradeAction {
    fun lunchUpgrade(player: Player) {
        val loc = player.location.clone().add(0.0, 0.5, 0.0)
        inventory.removeItemFromInventory(player, Material.PUFFERFISH, 1)
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, SoundCategory.AMBIENT, 1.0f, 0.5f)
        }
        val attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return

        val uuid = UUID.randomUUID()

        // 공격력 2배 Modifier
        val buffModifier = AttributeModifier(
            uuid,
            "damage",
            1.0, // 100% 증가 (곱하기 2배)
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        attribute.addModifier(buffModifier)

        // 10초 후 디버프로 교체
        Delay.delayRun(10 * 20L) {
            attribute.removeModifier(buffModifier)

            val debuffModifier = AttributeModifier(
                uuid,
                "damage",
                -0.5, // 50% 감소 (곱하기 0.5배)
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
            attribute.addModifier(debuffModifier)


            // 5초 후 원래대로 복구
            Delay.delayRun(5 * 20L) {
                attribute.removeModifier(debuffModifier)
            }
        }
        Delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { player -> // 파티클을 플레이어의 위치에 생성
                player.world.spawnParticle(
                    Particle.SOUL,
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