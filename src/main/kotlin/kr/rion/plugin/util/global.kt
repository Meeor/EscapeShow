package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.End.isEnding
import kr.rion.plugin.util.Teleport.console
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

private var worldManager: WorldManager? = null

object global {
    val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"

    var playerCheckTask: BukkitTask? = null

    fun checkPlayersWithTag() {
        // 모든 플레이어를 순회하며 태그 확인
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.scoreboardTags.contains("Escape")) {
                performAction(player)
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
        val blindEffect = PotionEffect(PotionEffectType.BLINDNESS, 2, 1, false, false)
        player.addPotionEffect(invisibilityEffect)
        player.addPotionEffect(blindEffect)
        End.EscapePlayerCount++
        End.EscapePlayers.add(player.name)
        player.addScoreboardTag("EscapeComplete")
        player.removeScoreboardTag("Escape")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${SurvivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        player.sendMessage("${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN} 플라이,무적및 투명화가 활성화 되었습니다!")
        if (End.EscapePlayerCount == End.EscapePlayerMaxCount || SurvivalPlayers() == 0 ) {
            if(!isEnding) {
                End.EndAction()
            }
        }

    }


    fun startPlayerCheckTask(plugin: JavaPlugin) {
        playerCheckTask?.cancel()
        playerCheckTask = object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { player ->
                    val loc = player.location
                    if (Teleport.isInDesignatedArea(loc)) {
                        Bukkit.getScheduler().runTask(Loader.instance, Runnable {
                            Teleport.teleportToRandomLocation(player)
                        })
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 20L은 주기, 1초마다 실행
    }

    fun setGameRulesForAllWorlds() {
        // 서버의 모든 월드에 대해 게임룰을 설정합니다
        for (world in Bukkit.getWorlds()) {
            // 즉시 respawn 설정
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
            // 커맨드 블록 출력 비활성화
            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
            //플레이어 커맨드 로그 출력 비활성화
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
            //낙하 데미지 비활성화
            world.setGameRule(GameRule.FALL_DAMAGE, false)
            //몹 스폰 비활성화
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false)

            console.sendMessage("${world.name} 월드의 게임룰설정이 변경되었습니다.")
        }
    }

    fun SurvivalPlayers(): Int {
        val survivalPlayers = Bukkit.getOnlinePlayers()
            .filter { !it.scoreboardTags.contains("manager") && !it.scoreboardTags.contains("EscapeComplete") && !it.scoreboardTags.contains("death") }
        return survivalPlayers.size  // 필터링된 생존 플레이어의 수 반환
    }
}