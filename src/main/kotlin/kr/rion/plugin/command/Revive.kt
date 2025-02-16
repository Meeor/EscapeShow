package kr.rion.plugin.command

import kr.rion.plugin.util.Global.cancelAllTasks
import kr.rion.plugin.util.Global.endingPlayer
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Global.survivalPlayers
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

object Revive {
    fun handleRevive(sender: CommandSender) {
        // 전체 플레이어 가져오기
        val players = Bukkit.getOnlinePlayers()

        if (players.isEmpty()) {
            sender.sendMessage("§c현재 온라인 상태인 플레이어가 없습니다!")
            return
        }

        // 모든 플레이어 처리
        cancelAllTasks() // 모든 작업 초기화
        players.forEach { player ->
            reviveFlags[player.name] = false // 부활 불가 플래그 설정

            // DeathAndAlive 태그가 있는 플레이어 추가 처리
            if (player.scoreboardTags.contains("DeathAndAlive")) {
                player.inventory.clear() // 인벤토리 비우기
                player.gameMode = GameMode.SPECTATOR // 관전 모드로 변경
                player.removeScoreboardTag("DeathAndAlive")
                player.addScoreboardTag("death") // Death 태그 추가
                player.sendMessage("부활금지모드가 작동하여 시도중이던 부활이 취소되었습니다.")
            }
        }
        if (survivalPlayers().count == 1) {
            endingPlayer()
        }
        Bukkit.broadcastMessage("$prefix 지금부터 부활이 금지됩니다!")
        sender.sendMessage("§a전체 플레이어의 부활 불가 처리가 완료되었습니다!")
    }


}