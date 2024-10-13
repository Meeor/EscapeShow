package kr.rion.plugin.event

import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.game.End
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.util.global
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathEvent : Listener {
    //사망시 관전모드
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.SPECTATOR
        player.addScoreboardTag("death")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}누군가${ChatColor.RED}사망${ChatColor.RESET}하였습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${global.SurvivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        Bossbar.removeDirectionBossBar(player)
        if (global.SurvivalPlayers() == 0 && !isEnding) {
            isEnding = true
            End.EndAction()
        }
    }
}