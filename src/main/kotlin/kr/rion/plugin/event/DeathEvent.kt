package kr.rion.plugin.event

import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Global
import kr.rion.plugin.util.Global.endingPlayer
import kr.rion.plugin.util.Global.reviveFlags
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
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    fun onDeath(event: PlayerDeathEvent) {
        val player: Player = event.player
        val console = Bukkit.getConsoleSender()
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1, false, false))
        Bukkit.broadcastMessage("${ChatColor.YELLOW}누군가${ChatColor.RED}사망${ChatColor.RESET}하였습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${Global.SurvivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        console.sendMessage("${ChatColor.YELLOW}${event.player.name}${ChatColor.RESET}님이${ChatColor.RED}사망${ChatColor.RESET}하였습니다.")
        Bossbar.removeDirectionBossBar(player)
        if (ifEnding) {
            endingPlayer()
        }
        if (reviveFlags[player.name] == true || reviveFlags[player.name] == null) {
            player.gameMode = GameMode.ADVENTURE // 모험 모드로 변경
            player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false))
            player.sendMessage("§c사망하셨습니다.§a본인의 시체위에서 웅크리고 3초간 있을경우 부활할수있습니다.")
            player.addScoreboardTag("DeathAndAlive")
        } else {
            player.gameMode = GameMode.SPECTATOR // 관전 모드로 변경
            player.sendMessage("§e사망 하셨습니다.")
            player.addScoreboardTag("death")
        }
    }
}