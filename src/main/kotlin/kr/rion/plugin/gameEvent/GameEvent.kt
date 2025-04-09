package kr.rion.plugin.gameEvent


object GameEvent {

    fun weatherClear() {
        WeatherClear.applyClearWeather()
    }

    fun weatherRain() {
        WeatherRain.applyRainWeather()
    }

    fun weatherThunder() {
        Weatherthunder.applyThunderWeather()
    }

    fun weatherWind() {
        WeatherWind.applyWindWeather()
    }

    fun weatherSun() {
        WeatherSun.applySunWeather()
    }

    // 확인할 태그 리스트
    val excludedTags = setOf("manager", "EscapeComplete", "MissionSuccessEscape", "death", "DeathAndAlive")
}