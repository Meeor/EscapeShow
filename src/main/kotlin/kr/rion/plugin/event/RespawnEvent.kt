package kr.rion.plugin.event

import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.manager.TeamManager
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Global.endingPlayer
import kr.rion.plugin.util.Global.playerItem
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Global.survivalPlayers
import kr.rion.plugin.util.Delay
import net.md_5.bungee.api.ChatColor
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
            if (isEnding) return
            Delay.delayRun(10) {
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 40, 1)) // 40틱(2초) 동안 실명 효과
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 3600, 1, false, false))
                if (!TeamGame) {
                    // 개인전일 경우 무조건 관전 모드
                    player.gameMode = GameMode.SPECTATOR
                    player.sendMessage("§c사망 하셨습니다.")
                    player.addScoreboardTag("death")
                    playerItem.remove(player.name)
                } else if (reviveFlags[player.name] == true || reviveFlags[player.name] == null) { //팀전이고 부활이 가능할경우
                    player.gameMode = GameMode.ADVENTURE // 모험 모드로 변경
                    player.sendMessage("§c사망하셨습니다.§a본인의 시체위에서 타인이 웅크리고 3초간 있을경우 부활할수있습니다.")
                    player.addScoreboardTag("DeathAndAlive")
                } else { //팀전이지만 부활이 불가능한상태일 경우
                    player.gameMode = GameMode.SPECTATOR // 관전 모드로 변경
                    player.sendMessage("§c사망 하셨습니다.")
                    player.addScoreboardTag("death")
                    playerItem.remove(player.name) // 데이터 삭제 (메모리 관리)
                }
                Bukkit.broadcastMessage(
                    "${ChatColor.YELLOW}누군가${ChatColor.RED}사망${ChatColor.RESET}하였습니다. ${ChatColor.LIGHT_PURPLE} \n" +
                            "${ChatColor.LIGHT_PURPLE}남은 플레이어 : ${ChatColor.YELLOW}${survivalPlayers().count}${ChatColor.LIGHT_PURPLE}명 ${ChatColor.GREEN}${if (TeamGame) "/ ${org.bukkit.ChatColor.AQUA}남은 팀 : ${org.bukkit.ChatColor.YELLOW}${TeamManager.getSurviverCount()}${org.bukkit.ChatColor.AQUA} 팀" else ""}"
                )
                endingPlayer()
            }//10틱이후 실행
        } else {
            event.respawnLocation = Location(Bukkit.getWorld("vip"), 15.5, 58.5, -44.5)
            Delay.delayRun(1) {
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 40, 1)) // 40틱(2초) 동안 실명 효과
            } // 1틱 후에 실행
        }
    }
}