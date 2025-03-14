package kr.rion.plugin.event

import kr.rion.plugin.manager.TeamManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangeWorldEvent : Listener {
    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val player = event.player
        val teamname = TeamManager.getTeam(player.toString())
        val teamDisplayName = teamname?.let { TeamManager.getTeamColorName(it) } ?: ""

        // ✅ 기존 팀 유지 및 색상 복원
        player.setPlayerListName("${teamDisplayName}${player.name}")
        player.customName = "${teamDisplayName}${player.name}"
        player.isCustomNameVisible = true // ✅ 닉네임 항상 표시
    }
}