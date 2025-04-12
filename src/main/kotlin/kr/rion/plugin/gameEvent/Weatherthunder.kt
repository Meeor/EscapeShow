package kr.rion.plugin.gameEvent

import kr.rion.plugin.gameEvent.GameEvent.excludedTags
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Weatherthunder {
    // 폭풍 (30초간 적용)
    fun applyThunderWeather() {
        // 날씨를 폭풍으로 설정
        Bukkit.getWorlds().forEach { world ->
            world.time = 23000L
        }

        // 플레이어에게 구속 효과 적용 (30초간)
        Bukkit.getOnlinePlayers()
            .filter { player -> excludedTags.none { player.scoreboardTags.contains(it) } }
            .forEach { player ->
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.SLOW,
                        30 * 20,
                        0,
                        true,
                        true
                    )
                )
                // 메세지 출력
                player.sendMessage("$prefix 폭풍이 칩니다! 움직이기 힘들군요...")
            }

    }
}