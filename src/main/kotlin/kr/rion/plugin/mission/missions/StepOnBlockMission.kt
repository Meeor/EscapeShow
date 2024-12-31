package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class StepOnBlockMission(
    private val targetBlock: Material, // 목표 블럭 종류
    private val requireSneaking: Boolean // 웅크리기 여부
) : Mission {

    override fun missionStart(player: Player) {
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is PlayerMoveEvent) {
            // 플레이어가 이동한 경우 발 아래 블록 확인
            val blockBelow = player.location.subtract(0.0, 1.0, 0.0).block

            // 블럭 종류와 웅크리기 여부 확인
            if (blockBelow.type == targetBlock) {
                if (requireSneaking && !player.isSneaking) {
                    return false
                }
                return true // 성공 조건 충족
            }
        }
        return false
    }

    override fun onSuccess(player: Player) {
        val sneakMessage = if (requireSneaking) "웅크린 상태로 " else ""
        player.sendMessage("§a축하합니다! §e$sneakMessage${targetBlock.name} §a블럭을 밟아 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
    }

    override fun reset() {
        // 별도 상태 관리 없음
    }
}
