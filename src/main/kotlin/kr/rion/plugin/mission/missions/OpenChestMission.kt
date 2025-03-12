package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import java.util.UUID

class OpenChestMission(private val requiredOpens: Int) : Mission {
    private val playerOpens = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        playerOpens[player.uniqueId] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryOpenEvent && event.inventory.type == InventoryType.BARREL) {
            val currentCount = playerOpens.getOrDefault(player.uniqueId, 0) + 1
            playerOpens[player.uniqueId] = currentCount
            player.spigot()
                .sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§b통을 열었습니다! (§e$currentCount§b/§d$requiredOpens§b)")
                )

            // 10단위로 사운드 재생
            if (currentCount % 10 == 0) {
                player.playSound(
                    player.location, // 플레이어 위치
                    "minecraft:block.note_block.bell ", // 사운드 이름
                    1.0f, // 볼륨
                    1.4f // 피치
                )
            }

            // 성공 조건 충족 확인
            return currentCount >= requiredOpens
        }
        return false
    }


    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerOpens.remove(player.uniqueId) // 데이터 정리
    }

    override fun reset() {
        playerOpens.clear()
    }
}
