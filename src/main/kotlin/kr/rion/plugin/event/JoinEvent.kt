package kr.rion.plugin.event

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
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
                event.player.sendMessage("")
                event.player.sendMessage(
                    "${ChatColor.YELLOW}왁타버스 마인크래프트 조공 컨텐츠\n" + "${ChatColor.GREEN}Escape Show ${ChatColor.YELLOW}에 오신것을 환영합니다."
                )
                event.player.sendMessage("")
                event.player.sendMessage(
                    "${ChatColor.AQUA}Esc 키 -> 설정 -> 비디오 설정 -> 쉐이더팩 설정에서\n" + "${ChatColor.LIGHT_PURPLE}Bliss_v2.0.4_(Chocapic13_Shaders_edit).zip\n" + "${ChatColor.AQUA}을 적용해주시길 바랍니다."
                )
                event.player.sendMessage("")
                event.player.sendMessage("${ChatColor.YELLOW}※TIP※ : 채팅창이 거슬린다면 \n           ${ChatColor.YELLOW}키보드 F3 키와 D 키를 동시에 눌러주세요")
                event.player.sendMessage("")
                event.player.sendMessage("${ChatColor.GOLD}$line")
            }
        }.runTask(Bukkit.getPluginManager().getPlugin("EscapeShow")!!) // YourPluginName을 실제 플러그인 이름으로 변경
    }
}