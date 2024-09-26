package kr.rion.plugin.gameEvent

import org.bukkit.Bukkit

object Donation {
    fun applyDonationEvent() {
        // 후원 이벤트 랜덤 실행
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "execute as @r[tag=!weather_DonationSettingSuccess] at @s run function server_datapack:donation_random"
        )
    }
}