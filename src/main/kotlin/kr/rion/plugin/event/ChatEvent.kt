package kr.rion.plugin.event

import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.manager.TeamManager.getPlayerList
import kr.rion.plugin.manager.TeamManager.getTeam
import kr.rion.plugin.manager.TeamManager.getTeamColorName
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEvent : Listener {
    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val message = event.message

        if (isStarting) {
            val teamName = getTeam(player.name)

            if (teamName == null) {
                event.isCancelled = true
                player.sendMessage("$prefix §c당신은 팀에 소속되어 있지 않습니다.")
                return
            }

            val teamPlayers = getPlayerList(teamName)
            val teamColorName = getTeamColorName(teamName)

            // 기본 채팅 취소
            event.isCancelled = true

            // 특정 태그가 있다면 메시지 전송 금지
            val restrictedTags = setOf("EscapeComplete", "death", "manager", "MissionSuccessEscape", "DeathAndAlive")

            if (player.scoreboardTags.any { it in restrictedTags }) {
                player.sendMessage("§c현재 상태에서는 팀 채팅을 사용할 수 없습니다.")
                return
            }

            // 팀원에게 메시지 전송
            teamPlayers.forEach { teammateName ->
                Bukkit.getPlayerExact(teammateName)?.sendMessage("§b팀: $teamColorName§f${player.name}: §7$message")
            }

            // "manager" 태그가 있는 관리자에게 팀 채팅을 보여줌
            Bukkit.getOnlinePlayers()
                .filter { it.scoreboardTags.contains("manager") && !teamPlayers.contains(it.name) }
                .forEach { manager ->
                    manager.sendMessage("§e[관리자] §b팀: $teamColorName§f${player.name}: §7$message")
                }

            // 콘솔에도 추가로 로그 표시
            Bukkit.getLogger().info("[팀채팅][$teamName]${player.name}: $message")

            event.isCancelled = true // 팀채팅 완료 후 이벤트 취소
        } else {
            if (!player.hasPermission("chat.on")) {
                event.isCancelled = true
                player.sendMessage("$prefix §l§c채팅이 금지되어 있습니다.")
            }
        }
    }
}