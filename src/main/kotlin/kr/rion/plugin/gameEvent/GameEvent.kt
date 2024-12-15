package kr.rion.plugin.gameEvent


import org.bukkit.Bukkit
import org.bukkit.command.CommandSender


object GameEvent {
    private val survivalPlayers = Bukkit.getOnlinePlayers().filter { player ->
        !player.scoreboardTags.contains("manager") && !player.scoreboardTags.contains("EscapeComplete") && !player.scoreboardTags.contains("death")
    }


    fun weatherClear() {
        WeatherClear.applyClearWeather()
    }

    fun weatherRain() {
        WeatherRain.applyRainWeather()
    }

    fun gravity() {
        Gravity.applyGravityAnomaly(survivalPlayers)
    }

    fun earthQuake(sender: CommandSender) {
        EarthQuake.start(sender)
    }

    fun donation() {
        Donation.applyDonationEvent()
    }

    fun deathCoin() {
        DeathCoin.applyDeathCoinEvent()
    }

    fun betting() {
        Betting.applyBettingEvent()
    }

    fun randomEvent(sender: CommandSender) {
        // 함수들을 리스트로 저장
        val eventFunctions = listOf(
            { weatherClear() },
            { weatherRain() },
            { gravity() },
            { earthQuake(sender) },
            { donation() },
            { deathCoin() },
            { betting() }
        )

        // 무작위로 하나의 함수를 선택하여 호출
        eventFunctions.random().invoke()
    }

}