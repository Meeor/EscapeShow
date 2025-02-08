package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.entity.Player
import org.bukkit.event.Event

class LastSurvivorMission : Mission {

    override fun missionStart(player: Player) {
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        return false
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
    }
}
