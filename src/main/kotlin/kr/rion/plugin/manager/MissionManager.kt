package kr.rion.plugin.manager

import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import kr.rion.plugin.mission.MissionRegistry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event

object MissionManager {
    val activeMissions = mutableMapOf<Player, Int>()

    fun assignMission(player: Player) {
        // 1~50 사이의 랜덤 번호 생성
        val randomMissionId = (1..15).random()

        // 태그 추가
        player.addScoreboardTag("MissionNo$randomMissionId")
        activeMissions[player] = randomMissionId
        player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 부여되었습니다: ID $randomMissionId  (테스트용로그)")

        val mission = MissionRegistry.getMission(randomMissionId)
        if (mission != null) {
            activeMissions[player] = randomMissionId
            mission.missionStart(player)
        } else {
            player.sendMessage("${MISSIONPREFIX}등록되지 않은 미션이 확인되어 미션 지급이 취소되었습니다.")
        }
    }

    //테스트용 미션 수동부여 함수
    fun assignSpecificMission(sender: CommandSender, player: Player, missionId: Int) {
        // 유효한 미션 ID 범위 확인
        if (missionId !in 1..15) {
            sender.sendMessage("${MISSIONPREFIX}잘못된 미션 ID입니다. 유효한 범위는 1~15입니다.")
            return
        }

        // 태그 추가
        player.addScoreboardTag("MissionNo$missionId")
        activeMissions[player] = missionId

        val mission = MissionRegistry.getMission(missionId)
        if (mission != null) {
            activeMissions[player] = missionId
            mission.missionStart(player)
            player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 부여되었습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}미션이 성공적으로 부여되었습니다: ID $missionId")
        } else {
            player.sendMessage("${MISSIONPREFIX}해당 미션을 찾을 수 없습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}해당 미션을 찾을 수 없습니다: ID $missionId")
        }
    }

    //테스트용 미션 수동 제거
    fun removeMission(sender: CommandSender, player: Player) {
        // 플레이어에게 활성화된 미션이 있는지 확인
        val missionId = activeMissions[player]
        player.removeScoreboardTag("MissionNo$missionId")
        player.removeScoreboardTag("MissionSuccess")
        if (missionId != null) {
            activeMissions.remove(player)

            player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
        } else {
            player.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
            sender.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
        }
    }
    // ID와 메시지를 매핑
    private val missionMessages = mapOf(
        1 to "축하합니다! §b상자 §e50§b번 §d열기 §b미션을 완료하셨습니다!",
        2 to "축하합니다! §b상자 §e100§b번 §d열기 §b미션을 완료하셨습니다!",
        3 to "축하합니다! §b단검 §e2§b개 §d얻기 §b미션을 완료하셨습니다!",
        4 to "축하합니다! §b철 §e30§b개 §d굽기 §b미션을 완료하셨습니다!",
        5 to "축하합니다! §b달콤한 열매 §e30§b개 §d얻기 §b미션을 완료하셨습니다!",
        6 to "축하합니다! §b모든무기종류 §d얻기 §b미션을 완료하셨습니다!",
        7 to "축하합니다! §b나무판자 §e5§b개 §d부수기 §b미션을 완료하셨습니다!",
        8 to "축하합니다! §b나무판자 §e10§b개 §d부수기 §b미션을 완료하셨습니다!",
        9 to "축하합니다! §b나무판자 §e10§b개 §d설치하기 §b미션을 완료하셨습니다!",
        10 to "축하합니다! §b플레이어 §e3§b명 §d처치하기 §b미션을 완료하셨습니다!",
        11 to "축하합니다! §b플레이어 §e5§b명 §d처치하기 §b미션을 완료하셨습니다!",
        12 to "축하합니다! §b플레이어 §e10§b명 §d처치하기 §b미션을 완료하셨습니다!",
        13 to "축하합니다! §b맨손으로 플레이어 §e1§b명 §d처치하기 §b미션을 완료하셨습니다!",
        14 to "축하합니다! §b조약돌로 플레이어 §e1§b명 §d처치하기 §b미션을 완료하셨습니다!",
        15 to "축하합니다! §b누적 §e20§b데미지 §d넣기 §b미션을 완료하셨습니다!",

    )

    fun handleEvent(player: Player, event: Event) {
        // 플레이어의 활성화된 미션 ID 확인
        val missionId = activeMissions[player] ?: return
        val mission = MissionRegistry.getMission(missionId) ?: return

        // 미션 성공 여부 확인
        if (mission.checkEventSuccess(player, event)) {
            mission.onSuccess(player)

            // ID에 따른 메시지 출력
            val successMessage = missionMessages[missionId]
                ?: "축하합니다! 미션을 완료했습니다!" // 기본 메시지
            player.sendMessage("$MISSIONPREFIX $successMessage")

            activeMissions.remove(player) // 미션 완료 후 제거
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