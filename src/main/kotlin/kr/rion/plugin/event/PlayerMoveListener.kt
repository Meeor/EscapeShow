package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.startportal
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Helicopter.playerloc
import kr.rion.plugin.util.Teleport
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {
    @EventHandler
    fun playerMoveEvent(event: PlayerMoveEvent) {
        if (bossbarEnable) {
            val player = event.player
            Bossbar.updateDirectionBossBar(player, playerloc!!)
        } else {
            return
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!startportal) return // isStart가 true일 때만 실행

        val player = event.player
        val loc = player.location

        if (Teleport.isInDesignatedArea(loc)) {
            Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                Teleport.teleportToRandomLocation(player)
            })
        }
    }
}