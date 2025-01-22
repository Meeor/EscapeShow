package kr.rion.plugin.event

import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Global
import kr.rion.plugin.util.Global.endingPlayer
import kr.rion.plugin.util.Global.processedPlayers
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
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
        if (isEnding || !isStarting) return
        val player: Player = event.player
        val console = Bukkit.getConsoleSender()
        // 9번~35번 슬롯의 아이템 제거
        val inventory = player.inventory
        for (slot in 9..35) {
            inventory.setItem(slot, null) // 해당 슬롯의 아이템 제거
        }
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1, false, false))
        Bukkit.broadcastMessage("${ChatColor.YELLOW}누군가${ChatColor.RED}사망${ChatColor.RESET}하였습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${Global.survivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        console.sendMessage("${ChatColor.YELLOW}${event.player.name}${ChatColor.RESET}님이${ChatColor.RED}사망${ChatColor.RESET}하였습니다.")
        Bossbar.removeDirectionBossBar(player)
        processedPlayers.remove(player.name)
        if (ifEnding) {
            endingPlayer()
        }
    }

}