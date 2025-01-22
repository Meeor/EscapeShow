package kr.rion.plugin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent

class GamemodeTest : Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        val player = event.player
        val newGameMode = event.newGameMode.toString()
        var reason = "알 수 없음" // 기본 변경 이유
        var stackTraceFirstLine = ""

        // 스택 트레이스 첫 줄 추출
        try {
            throw Exception("GameMode Change Trace")
        } catch (e: Exception) {
            stackTraceFirstLine = e.stackTrace.firstOrNull()?.toString() ?: "스택 트레이스를 가져올 수 없습니다."
        }

        // 변경 이유 추적
        if (player.lastDamageCause != null) {
            reason = "사망으로 인해 변경"
        }

        // 변경 알림 메시지 전송
        sendAdminMessage("§l§b스택 트레이스 첫 줄: §c$stackTraceFirstLine")
        sendAdminMessage("§d[${player.name}]§b 게임 모드 변경됨: $newGameMode")
        sendAdminMessage("§b변경 이유: §y$reason")
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        sendAdminMessage("[${player.name}] 사망으로 게임 모드 변경 가능성 있음.")
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val player = event.player
        sendAdminMessage("[${player.name}] 월드 이동 감지: ${event.from.name} -> ${player.world.name}")
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val command = event.message.lowercase()
        if (command.startsWith("/gamemode")) {
            sendAdminMessage("[${event.player.name}] 명령어로 게임 모드 변경 시도: $command")
        }
    }

    // 관리자 메시지 전송 메서드
    private fun sendAdminMessage(message: String) {
        val admin: Player? = Bukkit.getPlayer("Meor_")
        admin?.takeIf { it.isOnline }?.sendMessage(message)
    }
}