package kr.rion.plugin.gameEvent

import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

object DeathCoin {
    // 데스코인 (40초간 적용)
    fun applyDeathCoinEvent() {
        // 스코어보드 objectives 추가
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "scoreboard objectives add deathCoin_killCount minecraft.custom:minecraft.player_kills"
        )

        // 데스코인 시작 사운드 및 메세지
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(player.location, "minecraft:custom.deathcoin", 1f, 1f)
            player.sendMessage("$prefix 데스코인이 시작됩니다!!\n죽일 때 마다 강해지죠")
        }

        // 스코어보드 점수 초기화 및 데스코인 효과 적용
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players set @a deathCoin_killCount 0")
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "scoreboard players add @a[scores={deathCoin_killCount=1}] damage_upgrade 1"
                )
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "scoreboard players set @a[scores={deathCoin_killCount=1}] deathCoin_killCount 0"
                )
            }
        }.runTaskTimer(JavaPlugin.getPlugin(JavaPlugin::class.java), 0L, 20L * 40) // 40초 동안 적용
    }

}