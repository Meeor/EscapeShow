package kr.rion.plugin.item

import kr.rion.plugin.util.Delay
import kr.rion.plugin.util.inventory
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import java.util.UUID

object UpgradeAction {
    fun lunchUpgrade(player: Player) {
        inventory.removeItemFromInventory(player, Material.PUFFERFISH, 1)
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
    }
}