package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType

class OpenChestMission(private val requiredOpens: Int) : Mission {
    private val playerOpens = mutableMapOf<Player, Int>()

    override fun missionStart(player: Player) {
        playerOpens[player] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryOpenEvent && event.inventory.type == InventoryType.BARREL) {
            val currentCount = playerOpens.getOrDefault(player, 0) + 1
            playerOpens[player] = currentCount
//            player.sendMessage("§b통을 열었습니다! (§e$currentCount§b/§d$requiredOpens§b)")
            player.spigot()
                .sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§b통을 열었습니다! (§e$currentCount§b/§d$requiredOpens§b)")
                )

            if (currentCount >= requiredOpens) {
                return true // 성공 조건 충족
            }
        }
        return false // 성공 조건 미충족
    }

    override fun onSuccess(player: Player) {
        player.sendMessage("${MISSIONPREFIX}축하합니다! 통을 §e${requiredOpens}§a번 열어 미션을 완료했습니다!")
        player.addScoreboardTag("MissionSuccess")
        playerOpens.remove(player) // 데이터 정리
    }

    override fun reset() {
        playerOpens.clear()
    }
}
