package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Global.reviveFlags
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class RespawnEvent : Listener {
    //사망시 리스폰위치변경
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        // 리스폰 위치를 변경하지 않거나 특정 위치로 설정할 수 있습니다.
        // 예를 들어, 원래 리스폰 위치를 설정하지 않도록 하고 싶다면 이 코드를 추가합니다:
        if (isStarting) {
            event.respawnLocation = player.location // 플레이어의 사망 위치로 리스폰 설정
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 40, 1)) // 40틱(2초) 동안 실명 효과
            }, 1L) // 1틱 후에 실행
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
        } else {
            event.respawnLocation = Location(Bukkit.getWorld("vip"), 15.5, 58.5, -44.5)
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 40, 1)) // 40틱(2초) 동안 실명 효과
            }, 1L) // 1틱 후에 실행
        }
    }
}