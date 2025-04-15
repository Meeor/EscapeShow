package kr.rion.plugin.gameEvent

import kr.rion.plugin.gameEvent.GameEvent.excludedTags
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Delay
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import java.util.*

object WeatherSun {
    // 햇살 (30초간 적용)
    fun applySunWeather() {
        // 시간을 아침으로 변경
        Bukkit.getWorlds().forEach { world ->
            world.time = 500L
        }

        Bukkit.getOnlinePlayers()
            .filter { player -> excludedTags.none { player.scoreboardTags.contains(it) } }
            .forEach { player ->
                applyAttackBoostFlat(player, 2.0)
                // 메세지 출력
                player.sendMessage("$prefix 날이 밝았습니다! 하루의 시작을 열심히 해볼까요?")
                Delay.delayRun(30 * 20) {
                    removeAttackBoost(player)
                }
            }

    }

    val ATTACK_BUFF_UUID: UUID = UUID.fromString("a1111111-b222-c333-d444-e55555555555")

    fun applyAttackBoostFlat(player: Player, amount: Double) {
        val attr = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return

        // 기존 버프 제거
        val existing = attr.modifiers.find { it.uniqueId == ATTACK_BUFF_UUID }
        if (existing != null) {
            attr.removeModifier(existing)
        }


        val modifier = AttributeModifier(
            ATTACK_BUFF_UUID,
            "attack_boost_flat",
            amount,
            AttributeModifier.Operation.ADD_NUMBER
        )
        attr.addModifier(modifier)
    }

    fun removeAttackBoost(player: Player) {
        val attr = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return
        val existing = attr.modifiers.find { it.uniqueId == ATTACK_BUFF_UUID }
        if (existing != null) {
            attr.removeModifier(existing)
        }

    }

}