package kr.rion.plugin.gameEvent

import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit

object Betting {
    // 베팅 (1번만 적용)
    fun applyBettingEvent() {
        // 베팅 사운드 재생 및 메세지 출력
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(player.location, "minecraft:custom.betting", 1f, 1f)
            player.sendMessage("$prefix 베팅이 시작됩니다\n누군가가 강해졌습니다")
        }

        // 스코어보드 objectives 추가
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives add weather_BettingNumber dummy")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives add weather_BettingNumberResult dummy")

        // 베팅 함수 실행
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function server_datapack:betting")
    }
}
