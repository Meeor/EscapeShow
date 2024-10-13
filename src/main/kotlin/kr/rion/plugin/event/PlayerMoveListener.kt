package kr.rion.plugin.event

import kr.rion.plugin.item.FlameGunActions.startEscape
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Helicopter.playerloc
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener: Listener {
    @EventHandler
    fun PlayerMoveEvent(event: PlayerMoveEvent){
        if(startEscape) {
            val player = event.player
            Bossbar.updateDirectionBossBar(player, playerloc!!)
        }else{
            return
        }
    }
}