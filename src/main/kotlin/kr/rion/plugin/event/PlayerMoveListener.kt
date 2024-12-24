package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.startportal
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.util.Bossbar
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Global.adjustToHighestValidLocation
import kr.rion.plugin.util.Helicopter.HelicopterLoc
import kr.rion.plugin.util.Teleport
import kr.rion.plugin.util.Teleport.stopPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
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
            val adjustedLocation = HelicopterLoc?.clone()?.apply {
                y -= 50 // Y 좌표를 50 줄임
            }?.let { baseLocation ->
                val world = baseLocation.world ?: run {
                    Bukkit.getLogger().warning("HelicopterLoc의 월드를 찾을 수 없습니다.")
                    return@let null
                }

                adjustToHighestValidLocation(world, baseLocation, setOf(
                    Material.AZALEA_LEAVES,
                    Material.FERN,
                    Material.LARGE_FERN,
                    Material.GRASS,
                    Material.CRIMSON_BUTTON,
                    Material.WATER,
                    Material.LAVA
                ))
            }

            if (adjustedLocation != null) {
                Bossbar.updateDirectionBossBar(player, adjustedLocation)
            } else {
                Bukkit.getLogger().warning("Bossbar를 업데이트할 유효한 위치를 찾을 수 없습니다.")
            }
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
    @EventHandler
    fun stopPlayerMove(event: PlayerMoveEvent) {
        if(stopPlayer[event.player] == true) event.isCancelled = true
    }
}