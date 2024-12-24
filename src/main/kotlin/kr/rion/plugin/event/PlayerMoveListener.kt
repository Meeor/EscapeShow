package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.startportal
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Helicopter.HelicopterLoc
import kr.rion.plugin.util.Teleport
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {
    @EventHandler
    fun playerMoveEvent(event: PlayerMoveEvent) {
        if (bossbarEnable == 1) { //플레어건 위치로 보스바 조절
            val player = event.player
            Bossbar.updateDirectionBossBar(player, chestLocation!!)
        } else if(bossbarEnable == 2) { // 헬기 위치로 보스바 조절
            val player = event.player
            Bossbar.updateDirectionBossBar(player, HelicopterLoc!!.clone().add(0.0,-50.0,0.0))
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

    //////플라이 풀림 원인 디버깅
    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        val player = event.player
        if (event.newGameMode != GameMode.CREATIVE && event.newGameMode != GameMode.SPECTATOR) {
            Bukkit.broadcastMessage("${player.name}님의 플라이가 해제되었습니다. ${ChatColor.YELLOW}사유 : 게임 모드 변경됨")
            Exception().printStackTrace() // 스택 트레이스를 바로 콘솔에 출력
        }
    }

    private val notifiedPlayers = mutableSetOf<Player>() // 이미 메시지를 출력한 플레이어 목록

    @EventHandler
    fun onPlayerFlyDisableCheck(event: PlayerMoveEvent) {
        val player = event.player

        // EscapeComplete 태그가 있는 플레이어만 체크
        if (player.scoreboardTags.contains("EscapeComplete")) {
            // allowFlight 상태가 false이고 아직 메시지를 출력하지 않은 경우
            if (!player.allowFlight && !notifiedPlayers.contains(player)) {
                Bukkit.getLogger().info("${player.name}의 플라이 상태가 해제되었습니다. ${ChatColor.YELLOW}MoveEvent 에서 확인됨.")
                notifiedPlayers.add(player) // 메시지를 출력한 플레이어로 추가
            }

            // allowFlight가 다시 true로 설정되면 목록에서 제거
            if (player.allowFlight) {
                notifiedPlayers.remove(player)
            }
        }
    }
    /////플라이 풀림 원인 디버깅
}