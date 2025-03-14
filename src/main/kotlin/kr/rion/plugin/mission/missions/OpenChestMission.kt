package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import java.util.*

class OpenChestMission(private val requiredOpens: Int) : Mission {
    private val playerOpens = mutableMapOf<UUID, Int>()

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        playerOpens[uuid] = 0 // 초기화
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is InventoryOpenEvent && event.inventory.type == InventoryType.BARREL) {
            val uuid = player.uniqueId
            val currentCount = playerOpens.getOrDefault(uuid, 0) + 1
            playerOpens[uuid] = currentCount
            player.spigot()
                .sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§b통을 열었습니다! (§e$currentCount§b/§d$requiredOpens§b)")
                )
            player.playSound(
                player.location, // 플레이어 위치
                Sound.BLOCK_NOTE_BLOCK_BELL, // 사운드 이름
                1.0f, // 볼륨
                1.7f // 피치
            )

            // 성공 조건 충족 확인
            return currentCount >= requiredOpens
        }
        return false
    }


    override fun onSuccess(player: Player) {
        val uuid = player.uniqueId
        player.addScoreboardTag("MissionSuccess")
        playerOpens.remove(uuid) // 데이터 정리
    }

    override fun reset() {
        playerOpens.clear()
    }
}
