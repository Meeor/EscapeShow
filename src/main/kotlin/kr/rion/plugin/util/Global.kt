package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End
import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.util.Item.teleportCompass
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

object Global {
    val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"
    var EscapePlayerCount: Int = 0
    var EscapePlayerMaxCount: Int = 3
    var endingPlayerMaxCount: Int = 6
    var door = true

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
    private fun performAction(player: Player) {
        // 게임 모드 변경
        player.gameMode = GameMode.ADVENTURE

        // 플라이 허용
        player.allowFlight = true
        player.isFlying = true

        // 투명화 버프 부여 (무한지속시간)
        val invisibilityEffect = PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false)
        val blindEffect = PotionEffect(PotionEffectType.BLINDNESS, 2, 1, false, false)
        val hangerEffect = PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1, false, false)
        val healthEffect = PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 5, false, false)
        player.addPotionEffect(invisibilityEffect)
        player.addPotionEffect(blindEffect)
        player.addPotionEffect(hangerEffect)
        player.addPotionEffect(healthEffect)
        player.inventory.clear()
        player.inventory.setItem(8, teleportCompass())
        EscapePlayerCount++
        End.EscapePlayers.add(player.name)
        player.addScoreboardTag("EscapeComplete")
        player.removeScoreboardTag("Escape")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${SurvivalPlayers()}${ChatColor.LIGHT_PURPLE}명)")
        val remainingPlayers = EscapePlayerMaxCount - EscapePlayerCount

        if (remainingPlayers > 0) {
            Bukkit.broadcastMessage("${ChatColor.YELLOW}$remainingPlayers${ChatColor.LIGHT_PURPLE} 명이 탈출 가능합니다.")
        } else {
            Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}탈출 허용 인원이 가득 차 헬기가 떠납니다.")
            Helicopter.remove() // 내부적으로 null 체크를 처리함
        }


        player.sendMessage("${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN} 플라이,무적및 투명화가 활성화 되었습니다!")
        Bossbar.removeDirectionBossBar(player)
        if (ifEnding) {
            endingPlayer()
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
            //사망메세지 제거
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
            //도전과제 알림 비활성화
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)

            console.sendMessage("${world.name} 월드의 게임룰설정이 변경되었습니다.")
        }
    }

    fun SurvivalPlayers(): Int {
        val survivalPlayers = Bukkit.getOnlinePlayers()
            .filter {
                !it.scoreboardTags.contains("manager")
                        && !it.scoreboardTags.contains("EscapeComplete")
                        && !it.scoreboardTags.contains("death")
            }
        return survivalPlayers.size  // 필터링된 생존 플레이어의 수 반환
    }

    fun endingPlayer() {
        val endingPlayerCount: Int = SurvivalPlayers() + EscapePlayerCount

        if (endingPlayerCount == endingPlayerMaxCount) {
            if (!isEnding) return
            End.EndAction()
        }

    }

}