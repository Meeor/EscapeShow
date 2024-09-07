package kr.rion.plugin.util

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object global {
    val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"

    var playerCheckTask: BukkitTask? = null

    fun checkPlayersWithTag(tag: String) {
        // 모든 플레이어를 순회하며 태그 확인
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.scoreboardTags.contains(tag)) { // 태그가 권한으로 설정되어 있다고 가정
                performAction(player)
            }
            if (!player.scoreboardTags.contains("EscapeComplete")) {
                if (player.gameMode == GameMode.ADVENTURE || player.gameMode == GameMode.SURVIVAL) {
                    noFly(player)
                }
            }
        }
    }

    //탈출시 작업
    fun performAction(player: Player) {
        // 게임 모드 변경
        player.gameMode = GameMode.ADVENTURE

        // 플라이 허용
        player.allowFlight = true
        player.isFlying = true

        // 투명화 버프 부여 (무한지속시간)
        val invisibilityEffect = PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false)
        player.addPotionEffect(invisibilityEffect)

        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다.")
        player.addScoreboardTag("EscapeComplete")
        player.removeScoreboardTag("Escape")
        player.sendMessage("${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN} 플라이,무적및 투명화가 활성화 되었습니다!")
    }

    //탈출완료태그없을때 작업
    fun noFly(player: Player) {
        player.allowFlight = false
        player.isFlying = false
    }

    fun startPlayerCheckTask(plugin: JavaPlugin) {
        playerCheckTask?.cancel()
        playerCheckTask = object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { player ->
                    val loc = player.location
                    if (Teleport.isInDesignatedArea(loc)) {
                        Bukkit.getLogger().warning("지금 텔레포트합니다.")
                        Teleport.teleportToRandomLocation(player)
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 20L은 주기, 1초마다 실행
    }
}