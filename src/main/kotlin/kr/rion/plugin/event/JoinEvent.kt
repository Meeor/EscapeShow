package kr.rion.plugin.event

import kr.rion.plugin.Loader
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinEvent : Listener {

    //접속 메세지
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onjoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            if (event.player.scoreboardTags.contains("EscapeComplete") ||
                event.player.scoreboardTags.contains("death") ||
                event.player.scoreboardTags.contains("manager") ||
                event.player.scoreboardTags.contains("MissionSuccessEscape") ||
                event.player.scoreboardTags.contains("DeathAndAlive")
            ) {
                event.player.gameMode = GameMode.SPECTATOR
            }
        }, 30L)
    }
}