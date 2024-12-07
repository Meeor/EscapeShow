package kr.rion.plugin.event

import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Global
import kr.rion.plugin.util.Global.endingPlayer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class DeathEvent : Listener {
    //사망시 관전모드
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player: Player = event.player
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1, false, false))
        player.gameMode = GameMode.SPECTATOR
        player.addScoreboardTag("death")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}누군가${ChatColor.RED}사망${ChatColor.RESET}하였습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${Global.SurvivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        Bossbar.removeDirectionBossBar(player)
        if (ifEnding) {
            endingPlayer()
        }
    }
}