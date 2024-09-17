package kr.rion.plugin.command

import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Coordinates {
    private var UserCount: Int = 0

    fun handleCoordinatesCommand(sender: CommandSender) {
        if (sender is Player) {
            if (sender.isOp) {
                val players = Bukkit.getOnlinePlayers().filter {
                    it.gameMode == GameMode.SURVIVAL
                }
                if (players.isEmpty()) {
                    sender.sendMessage("$prefix ${ChatColor.RED}공개할 플레이어가 없습니다.")
                } else {
                    val line = "=".repeat(30)
                    UserCount = 0
                    Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("   $prefix 플레이어 좌표공개")
                    Bukkit.broadcastMessage(" ")
                    for (player in players) {
                        val location = player.location
                        val message =
                            "${ChatColor.GREEN}${player.name}: ${ChatColor.YELLOW}X: ${location.blockX}, Y: ${location.blockY}, Z: ${location.blockZ}"
                        Bukkit.broadcastMessage(message)
                        UserCount++
                    }
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("${ChatColor.GREEN}생존자 수 : ${ChatColor.GOLD}$UserCount")
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
                }
                return // 명령어 처리 완료
            } else {
                sender.sendMessage("$prefix ${ChatColor.RED}이 명령어는 운영자만 사용가능합니다.")
                return // 명령어 처리 완료
            }
        } else {
            sender.sendMessage("$prefix ${ChatColor.RED}이 명령어는 플레이어만 사용할 수 있습니다.")
            return // 명령어 처리 완료
        }
    }
}