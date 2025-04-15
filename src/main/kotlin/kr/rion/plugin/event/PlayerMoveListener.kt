package kr.rion.plugin.event

import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.item.FlameGunActions.EscapeLocation
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Bossbar.bossbarEnable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {
    @EventHandler
    fun playerMoveEvent(event: PlayerMoveEvent) {
        if (bossbarEnable == 1) { //플레어건 위치로 보스바 조절
            val player = event.player
            Bossbar.updateDirectionBossBar(player, chestLocation!!)
        } else if (bossbarEnable == 2) { // 헬기 위치로 보스바 조절
            val player = event.player
            Bossbar.updateDirectionBossBar(player, EscapeLocation)
        } else {
            return
        }
    }
}