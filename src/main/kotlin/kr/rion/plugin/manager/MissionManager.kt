package kr.rion.plugin.manager

import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import kr.rion.plugin.mission.MissionRegistry
import kr.rion.plugin.mission.missions.LastSurvivorMission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event

object MissionManager {
    val activeMissions = mutableMapOf<String, Int>()

    fun assignMission(player: Player) {
        // 1~50 사이의 랜덤 번호 생성
        val randomMissionId = (1..31).random()

        val mission = MissionRegistry.getMission(randomMissionId)
        if (mission != null) {
            // 태그 추가
            player.addScoreboardTag("MissionNo$randomMissionId")
            activeMissions[player.name] = randomMissionId
            mission.missionStart(player)
        } else {
            player.sendMessage("${MISSIONPREFIX}등록되지 않은 미션이 확인되어 미션 지급이 취소되었습니다.")
        }
    }

    //테스트용 미션 수동부여 함수
    fun assignSpecificMission(sender: CommandSender, player: Player, missionId: Int) {
        // 유효한 미션 ID 범위 확인
        if (missionId !in 1..31) {
            sender.sendMessage("${MISSIONPREFIX}잘못된 미션 ID입니다. 유효한 범위는 1~15입니다.")
            return
        }
        val mission = MissionRegistry.getMission(missionId)
        if (mission != null) {
            // 태그 추가
            player.addScoreboardTag("MissionNo$missionId")
            activeMissions[player.name] = missionId
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
        val missionId = activeMissions[player.name]
        player.removeScoreboardTag("MissionNo$missionId")
        player.removeScoreboardTag("MissionSuccess")
        if (missionId != null) {
            activeMissions.remove(player.name)
            player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
        } else {
            player.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
            sender.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
        }
    }

    // ID와 메시지를 매핑
    private val missionMessages = mapOf(
        1 to "축하합니다! §d상자 §e50§b번 열기 미션을 완료하셨습니다!",
        2 to "축하합니다! §d상자 §e100§b번 열기 미션을 완료하셨습니다!",
        3 to "축하합니다! §d단검 §e2§b개 얻기 미션을 완료하셨습니다!",
        4 to "축하합니다! §d철 §e30§b개 굽기 미션을 완료하셨습니다!",
        5 to "축하합니다! §d달콤한 열매 §e30§b개 얻기 미션을 완료하셨습니다!",
        6 to "축하합니다! §d모든무기종류 §b얻기 미션을 완료하셨습니다!",
        7 to "축하합니다! §d판자더미 §e5§b개 부수기 미션을 완료하셨습니다!",
        8 to "축하합니다! §d판자더미 §e10§b개 부수기 미션을 완료하셨습니다!",
        9 to "축하합니다! §d판자더미 §e10§b개 설치하기 미션을 완료하셨습니다!",
        10 to "축하합니다! §d플레이어 §e3§b명 처치하기 미션을 완료하셨습니다!",
        11 to "축하합니다! §d플레이어 §e5§b명 처치하기 미션을 완료하셨습니다!",
        12 to "축하합니다! §d플레이어 §e10§b명 처치하기 미션을 완료하셨습니다!",
        13 to "축하합니다! §d맨손으로 플레이어 §e1§b명 처치하기 미션을 완료하셨습니다!",
        14 to "축하합니다! §d조약돌로 플레이어 §e1§b명 처치하기 미션을 완료하셨습니다!",
        15 to "축하합니다! §b누적 §e20§d데미지§b 넣기 미션을 완료하셨습니다!",
        16 to "축하합니다! 사슬갑옷 §b얻기 미션을 완료하셨습니다!",
        17 to "축하합니다! 갑옷§b 얻기 미션을 완료하셨습니다!",
        18 to "축하합니다! 강철갑옷 §b얻기 미션을 완료하셨습니다!",
        19 to "축하합니다! §b부활 하기 미션을 완료하셨습니다!",
        20 to "축하합니다! §b시체 털기 미션을 완료하셨습니다!",
        21 to "축하합니다! §d가죽 §e30§b개 얻기 미션을 완료하셨습니다!",
        22 to "축하합니다! §b누적 §d회복§e10§b 받기 미션을 완료하셨습니다!",
        23 to "축하합니다! §d계약서 §b사용하기 미션을 완료하셨습니다!",
        24 to "축하합니다! §d지도 §e3§b번 사용하기 미션을 완료하셨습니다!",
        25 to "축하합니다! §d부목 §b사용하기 미션을 완료하셨습니다!",
        26 to "축하합니다! §d붕대 §e10§b개 사용하기 미션을 완료하셨습니다!",
        27 to "축하합니다! §d붕대 §e15§b개 사용하기 미션을 완료하셨습니다!",
        28 to "축하합니다! §d붕대 §e20§b개 사용하기 미션을 완료하셨습니다!",
        29 to "축하합니다! §b누적 §e10§d데미지 §b받기 미션을 완료하셨습니다!",
        30 to "축하합니다! §e10분§b동안 데미지 안받기 미션을 완료 하셨습니다!",
        31 to "축하합니다! §b마지막까지 살아남으셔서 미션에 성공하셨습니다!"

    )

    fun handleEvent(player: Player, event: Event) {
        // 플레이어의 활성화된 미션 ID 확인
        val missionId = activeMissions[player.name] ?: return
        val mission = MissionRegistry.getMission(missionId) ?: return

        // 미션 성공 여부 확인
        if (mission.checkEventSuccess(player, event)) {
            mission.onSuccess(player)

            // ID에 따른 메시지 출력
            val successMessage = missionMessages[missionId]
                ?: "축하합니다! 미션을 완료했습니다!" // 기본 메시지
            player.sendMessage("$MISSIONPREFIX$successMessage")
            player.playSound(
                player.location, // 플레이어 위치
                "minecraft:ui.toast.challenge_complete", // 사운드 이름
                1.0f, // 볼륨
                1.0f // 피치
            )

            activeMissions.remove(player.name) // 미션 완료 후 제거
        }
    }

    fun resetMissions() {
        activeMissions.clear()
        // 모든 등록된 미션의 데이터를 초기화
        MissionRegistry.getAllMissions().values.forEach { mission ->
            mission.reset()
        }
    }

    fun listMission(): MutableMap<String, Int> {
        return activeMissions
    }

    fun endGame() {
        // 모든 활성 미션 플레이어 확인
        activeMissions.forEach { (playerName, missionId) ->
            val player = Bukkit.getPlayer(playerName) ?: return@forEach // 플레이어 객체 가져오기
            val mission = MissionRegistry.getMission(missionId) ?: return@forEach // 미션 객체 가져오기

            if (mission is LastSurvivorMission) {
                // LastSurvivorMission의 게임 종료 처리
                mission.onGameEnd()
                player.sendMessage("$MISSIONPREFIX 마지막까지 생존하여 미션을 완료했습니다!")
                player.playSound(
                    player.location, // 플레이어 위치
                    "minecraft:ui.toast.challenge_complete", // 사운드 이름
                    1.0f, // 볼륨
                    1.0f // 피치
                )
            }
        }
    }

}