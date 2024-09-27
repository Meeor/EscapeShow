package kr.rion.plugin.gameEvent

import org.bukkit.Bukkit

object WeatherClear {
    fun applyClearWeather() {
        // 날씨를 맑게 설정
        Bukkit.getWorlds().forEach { world -> world.setStorm(false); world.isThundering = false }

        // 스코어보드에 dummy objective 추가
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives add weather_GravityAbnormality dummy")

        // Action bar 초기화
        Bukkit.getOnlinePlayers().forEach { player -> player.sendActionBar("") }
    }
}