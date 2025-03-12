package kr.rion.plugin.mission.missions

import kr.rion.plugin.customEvent.IronMeltEvent
import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.UUID

class IronMeltMission(private val requiredCount: Int) : Mission {


    private val totalCollectedCounts = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        totalCollectedCounts[player.uniqueId] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is IronMeltEvent) {
            val item = event.item.type
            if (item == Material.IRON_INGOT) {
                val collectIron = totalCollectedCounts.getOrDefault(player.uniqueId, 0) + 1
                totalCollectedCounts[player.uniqueId] = collectIron
                player.sendMessage("$MISSIONPREFIX§b철을 구웠습니다 §a현재 구운 갯수 : §b(§e${collectIron}§b/§d$requiredCount§b)")

                if(collectIron >= requiredCount){
                    return true
                }
            }
        }
        return false
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        totalCollectedCounts.remove(player.uniqueId) // 완료 후 데이터 정리
    }

    override fun reset() {
        totalCollectedCounts.clear() // 누적 수집량 초기화
    }

}
