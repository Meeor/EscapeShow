package kr.rion.plugin.gameEvent


object GameEvent {

    fun weatherClear() {
        WeatherClear.applyClearWeather()
    }

    fun weatherRain() {
        WeatherRain.applyRainWeather()
    }

}