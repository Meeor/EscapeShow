package kr.rion.plugin.gameEvent


import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object Coordinates {
    private var UserCount: Int = 0

    // 좌표 공개 함수
    fun revealCoordinates(player: Player) {
        if (!player.isOp) {
            player.sendMessage("$prefix ${ChatColor.RED}이 명령어는 운영자만 사용 가능합니다.")
            return
        }

        val players = Bukkit.getOnlinePlayers().filter { it.gameMode == GameMode.SURVIVAL }
        if (players.isEmpty()) {
            player.sendMessage("$prefix ${ChatColor.RED}공개할 플레이어가 없습니다.")
        } else {
            val line = "=".repeat(30)
            UserCount = 0
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
            Bukkit.broadcastMessage(" ")
            Bukkit.broadcastMessage("   $prefix 플레이어 좌표 공개")
            Bukkit.broadcastMessage(" ")
            for (survivalPlayer in players) {
                val location = survivalPlayer.location
                val message =
                    "${ChatColor.GREEN}${survivalPlayer.name}: ${ChatColor.YELLOW}X: ${location.blockX}, Y: ${location.blockY}, Z: ${location.blockZ}"
                Bukkit.broadcastMessage(message)
                UserCount++
            }
            Bukkit.broadcastMessage(" ")
            Bukkit.broadcastMessage("${ChatColor.GREEN}생존자 수: ${ChatColor.GOLD}$UserCount")
            Bukkit.broadcastMessage(" ")
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
        }
    }
}
