package kr.rion.plugin.manager

import kr.rion.plugin.mission.MissionRegistry
import org.bukkit.entity.Player
import org.bukkit.event.Event

object MissionManager {
    private val activeMissions = mutableMapOf<Player, Int>()

    fun assignMission(player: Player) {
        // 1~50 사이의 랜덤 번호 생성
        val randomMissionId = (1..11).random()

        // 태그 추가
        player.addScoreboardTag("MissionNo$randomMissionId")
        activeMissions[player] = randomMissionId

        val mission = MissionRegistry.getMission(randomMissionId)
        if (mission != null) {
            activeMissions[player] = randomMissionId
            mission.missionStart(player)
        } else {
            player.sendMessage("해당 미션을 찾을 수 없습니다: $randomMissionId")
        }
    }

    fun handleEvent(player: Player, event: Event) {
        val missionId = activeMissions[player] ?: return
        val mission = MissionRegistry.getMission(missionId) ?: return

        if (mission.checkEventSuccess(player, event)) {
            mission.onSuccess(player)
            activeMissions.remove(player)
        }
    }
    fun resetMissions() {
        activeMissions.clear()
        // 모든 등록된 미션의 데이터를 초기화
        MissionRegistry.getAllMissions().values.forEach { mission ->
            mission.reset()
        }
    }
}