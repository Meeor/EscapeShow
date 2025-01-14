package kr.rion.plugin.mission.missions

import kr.rion.plugin.customEvent.RevivalEvent
import kr.rion.plugin.customEvent.RevivalEventType
import kr.rion.plugin.mission.Mission
import org.bukkit.entity.Player
import org.bukkit.event.Event

class ReviveOthersOnceMission : Mission {

    override fun missionStart(player: Player) {
        // 미션 시작 시 별도 초기화 없음
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is RevivalEvent && event.eventType == RevivalEventType.SUCCESS) {
            return event.relatedPlayer == player // 부활시킨 사람이 미션 수행자인 경우 성공
        }
        return false
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
        // 별도 상태 없음
    }
}
