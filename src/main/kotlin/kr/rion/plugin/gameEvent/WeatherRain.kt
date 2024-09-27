package kr.rion.plugin.gameEvent

import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.Sound

object WeatherRain {
    // 폭우 (30초간 적용)
    fun applyRainWeather() {
        // 날씨를 폭우로 설정
        Bukkit.getWorlds().forEach { world -> world.setStorm(true); world.setThundering(true) }

        // 플레이어에게 어둠 효과 적용 (30초간)
        Bukkit.getOnlinePlayers().filter { player ->
            !player.scoreboardTags.contains("manager") && !player.scoreboardTags.contains("EscapeComplete")
        }.forEach { player ->
            player.addPotionEffect(
                org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.DARKNESS,
                    30 * 20,
                    0,
                    true,
                    true
                )
            )
            // 사운드 재생
            player.playSound(player.location, "minecraft:custom.rain", 1f, 1f)
            player.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f)
            player.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 0f)
            // 메세지 출력
            player.sendMessage("$prefix 폭우가 내립니다\n주위를 조심해주세요!")
        }

    }
}