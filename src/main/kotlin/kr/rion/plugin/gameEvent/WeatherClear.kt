package kr.rion.plugin.gameEvent

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType

object WeatherClear {
    fun applyClearWeather() {
        // 날씨를 맑게 설정
        Bukkit.getWorlds().forEach { world -> world.setStorm(false); world.isThundering = false }

        Bukkit.getOnlinePlayers().forEach { player ->
            for (effect in PotionEffectType.values()) {
                player.removePotionEffect(effect)
            }
        }
        // Action bar 초기화
        Bukkit.getOnlinePlayers()
            .forEach { player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("")) }
    }
}