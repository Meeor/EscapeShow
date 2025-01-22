package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.PlayerDeathEvent

class LastSurvivorMission : Mission {
    private val missionPlayers = mutableSetOf<Player>() // 미션 받은 플레이어

    override fun missionStart(player: Player) {
        missionPlayers.add(player) // 미션에 참여한 플레이어 등록
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        // 사망 이벤트 처리
        if (event is PlayerDeathEvent) {
            val victim = event.entity
            if (victim == player && missionPlayers.contains(player)) {
                onFailure(player) // 사망 시 미션 실패 처리
            }
        }
        return false
    }

    fun onGameEnd() {
        // 게임 종료 시점에서 남아있는 미션 참여자 확인
        for (player in missionPlayers) {
            onSuccess(player) // 살아남은 플레이어는 미션 성공 처리
        }
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        missionPlayers.remove(player) // 미션 플레이어 리스트에서 제거
    }

    private fun onFailure(player: Player) {
        missionPlayers.remove(player) // 미션 플레이어 리스트에서 제거
        player.sendMessage("$MISSIONPREFIX 사망하여 미션에 실패했습니다.")
    }

    override fun reset() {
        missionPlayers.clear() // 모든 플레이어 초기화
    }
}
