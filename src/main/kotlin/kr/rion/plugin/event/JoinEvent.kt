package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.util.ChatUtil.centerText
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable

class JoinEvent : Listener {
    val line = "=".repeat(40)

    //접속 메세지
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onjoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                // 비동기 작업에서 플레이어에게 인사 메시지 전송


                event.player.sendMessage("${ChatColor.GOLD}$line\n\n")
                centerText(
                    """
                    |${ChatColor.YELLOW}왁타버스 마인크래프트 조공 컨텐츠
                    |${ChatColor.GREEN}Escape Show ${ChatColor.YELLOW}에 오신것을 환영합니다.
                    |
                    |${ChatColor.AQUA}Esc 키 -> 설정 -> 비디오 설정 -> 쉐이더팩 설정에서
                    |${ChatColor.LIGHT_PURPLE}Bliss.zip
                    |${ChatColor.AQUA}을 적용해주시길 바랍니다.
                    |
                    |${ChatColor.YELLOW}※TIP※ : 채팅창이 거슬린다면
                    |${ChatColor.YELLOW}키보드 F3 키와 D 키를 동시에 눌러주세요
                    """.trimMargin()
                    )
                event.player.sendMessage("${ChatColor.GOLD}$line")
            }
        }.runTask(Loader.instance)
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            if (event.player.scoreboardTags.contains("EscapeComplete") ||
                event.player.scoreboardTags.contains("death") ||
                event.player.scoreboardTags.contains("manager") ||
                event.player.scoreboardTags.contains("MissionSuccessEscape") ||
                event.player.scoreboardTags.contains("DeathAndAlive")
            ) {
                event.player.gameMode = GameMode.SPECTATOR
            }
        }, 30L)
    }
}