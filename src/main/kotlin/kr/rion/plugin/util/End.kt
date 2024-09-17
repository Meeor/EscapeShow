package kr.rion.plugin.util

import kr.rion.plugin.Loader
import org.bukkit.*

object End {
    val world = worldManager?.getMultiverseWorld("game")
    val worldWait = worldManager?.getMultiverseWorld("vip")
    var EscapePlayerCount: Int = 0
    var EscapePlayers: MutableList<String> = mutableListOf()
    val soundName = "custom.bye"
    fun EndAction() {
        EscapePlayerCount = 0
        Helicopter.remove()
        Bukkit.broadcastMessage("${global.prefix} 게임이 종료되었습니다.")
        for (player in Bukkit.getOnlinePlayers()) {
            player.playSound(player.location, soundName, SoundCategory.MASTER, 1.0f, 1.0f)
        }
        world?.players?.forEach { player ->
            // 플레이어가 서바이벌 모드인 경우
            if (player.gameMode == GameMode.SURVIVAL) {
                Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                    player.health = 0.0 // 플레이어의 체력을 0으로 설정하여 죽이기
                }, 20L * 3)
            }
        }

        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                player.allowFlight = false
                player.isFlying = false
                player.gameMode = GameMode.SURVIVAL
                for (effect in player.activePotionEffects) {
                    player.removePotionEffect(effect.type)
                }
                player.removeScoreboardTag("EscapeComplete")
                player.teleport(Location(worldWait, 15.5, 58.5, -44.5))
            }
            val line = "=".repeat(40)
            val message =
                "${global.prefix} 탈출한 플레이어 \n${ChatColor.YELLOW} ${EscapePlayers.joinToString(" \n${ChatColor.YELLOW}")}"

            // 메시지를 모든 플레이어에게 브로드캐스트
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
            Bukkit.broadcastMessage(message)
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")

            // EscapePlayers 리스트를 초기화 (비우기)
            EscapePlayers.clear()
        }, 20L * 8)
    }
}