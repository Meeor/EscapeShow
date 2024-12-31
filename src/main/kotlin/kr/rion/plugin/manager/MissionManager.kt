package kr.rion.plugin.manager

import kr.rion.plugin.mission.MissionRegistry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event

object MissionManager {
    private val activeMissions = mutableMapOf<Player, Int>()

    fun assignMission(player: Player) {
        // 1~50 사이의 랜덤 번호 생성
        val randomMissionId = (1..14).random()

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
    //테스트용 미션 수동부여 함수
    fun assignSpecificMission(sender: CommandSender,player: Player, missionId: Int) {
        // 유효한 미션 ID 범위 확인
        if (missionId !in 1..14) {
            sender.sendMessage("잘못된 미션 ID입니다. 유효한 범위는 1~14입니다.")
            return
        }

        // 태그 추가
        player.addScoreboardTag("MissionNo$missionId")
        activeMissions[player] = missionId

        val mission = MissionRegistry.getMission(missionId)
        if (mission != null) {
            activeMissions[player] = missionId
            mission.missionStart(player)
            player.sendMessage("미션이 성공적으로 부여되었습니다: ID $missionId")
            sender.sendMessage("미션이 성공적으로 부여되었습니다: ID $missionId")
        } else {
            player.sendMessage("해당 미션을 찾을 수 없습니다: ID $missionId")
            sender.sendMessage("해당 미션을 찾을 수 없습니다: ID $missionId")
        }
    }
    //테스트용 미션 수동 제거
    fun removeMission(sender: CommandSender,player: Player) {
        // 플레이어에게 활성화된 미션이 있는지 확인
        val missionId = activeMissions[player]
        if (missionId != null) {
            // 태그 제거
            player.removeScoreboardTag("MissionNo$missionId")
            activeMissions.remove(player)

            player.sendMessage("미션이 성공적으로 제거되었습니다: ID $missionId")
            sender.sendMessage("미션이 성공적으로 제거되었습니다: ID $missionId")
        } else {
            player.sendMessage("현재 할당된 미션이 없습니다.")
            sender.sendMessage("현재 할당된 미션이 없습니다.")
        }
    }

    fun handleEvent(player: Player, event: Event) {
        val missionId = activeMissions[player] ?: return
        val mission = MissionRegistry.getMission(missionId) ?: return

        if (mission.checkEventSuccess(player, event)) {
            mission.onSuccess(player)
            player.removeScoreboardTag("MissionNo$missionId")
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
    fun listMission(): MutableMap<Player, Int> {
        return activeMissions
    }
}