package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.command.Reset
import kr.rion.plugin.item.FlameGunActions.flaregunstart
import kr.rion.plugin.item.FlameGunActions.playersAtParticle
import kr.rion.plugin.item.FlameGunActions.startEscape
import kr.rion.plugin.item.ItemAction.handleResetContract
import kr.rion.plugin.util.Bossbar.bossBars
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Helicopter.HelicopterisSpawn
import org.bukkit.*
import org.bukkit.command.CommandSender

object End {
    var isEnding: Boolean = false

    var EscapePlayerCount: Int = 0
    var EscapePlayerMaxCount: Int = 6
    var EscapePlayers: MutableList<String> = mutableListOf()
    private val soundName = "custom.bye"
    fun EndAction() {
        val world = Bukkit.getWorld("game")  // Multiverse 대신 Bukkit API로 월드 가져오기
        val worldWait = Bukkit.getWorld("vip") // vip 월드도 동일하게 Bukkit API로 가져옴
        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd = "function server_datapack:reset" // 실행할 명령어

        // 월드가 null인 경우 로그 출력
        if (world == null) {
            Bukkit.getLogger().warning("game 월드를 가져오지 못했습니다. worldManager 또는 해당 월드 확인 필요.")
        }
        if (worldWait == null) {
            Bukkit.getLogger().warning("vip 월드를 가져오지 못했습니다. worldManager 또는 해당 월드 확인 필요.")
        }

        //게임종료후 각종변수및 정보들 리셋작업.
        EscapePlayerCount = 0
        Helicopter.remove()
        HelicopterisSpawn = false
        //플레어건 탈출파티클 끝내기
        startEscape = false
        flaregunstart?.cancel()
        flaregunstart = null
        Bukkit.broadcastMessage("${global.prefix} 게임이 종료되었습니다.")

        //여기까지.종료직후리셋.
        //모든플레이어에게 게임종료사운드보내기.
        for (player in Bukkit.getOnlinePlayers()) {
            player.playSound(player, soundName, SoundCategory.MASTER, 1.0f, 1.0f)
        }

        //사망자,탈출자,운영자를제외한 남은플레이어 죽이기!
        world?.players?.forEach { player ->
            // 플레이어가 서바이벌 모드인 경우
            if (!player.scoreboardTags.contains("death") && !player.scoreboardTags.contains("manager") && !player.scoreboardTags.contains("EscapeComplete")) {
                Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                    player.health = 0.0 // 플레이어의 체력을 0으로 설정하여 죽이기
                }, 20L * 3)
            }
        }

        //모든플레이어들.. 게임모드나 플라이등의 설정변경.
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                player.allowFlight = false
                player.isFlying = false
                removeDirectionBossBar(player)
                player.removeScoreboardTag("death")
                player.removeScoreboardTag("EscapeComplete")
                player.inventory.clear()
                for (effect in player.activePotionEffects) {
                    player.removePotionEffect(effect.type)
                }

                if (worldWait != null) {
                    player.teleport(Location(worldWait, 15.5, 58.5, -44.5))
                } else {
                    player.sendMessage("${global.prefix}${ChatColor.RED}플러그인에 버그가 발생하였습니다. 운영자에게 이동을 요청하시길 바랍니다.")
                }
            }
            val line = "=".repeat(40)
            val gongback = " ".repeat(22)
            val message =
                "$gongback${ChatColor.GREEN}탈출한 플레이어 \n${ChatColor.YELLOW}${EscapePlayers.joinToString(" \n$gongback${ChatColor.YELLOW}")}"

            // 메시지를 모든 플레이어에게 브로드캐스트
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
            Bukkit.broadcastMessage(message)
            Bukkit.broadcastMessage("${ChatColor.GOLD}$line")

            // EscapePlayers 리스트를 초기화 (비우기)
            EscapePlayers.clear()
        }, 20L * 8)
        //게임종료 마지막부분에 리셋되어야할것들
        playersAtParticle.clear()
        Reset.handleGameReset()
        handleResetContract()

        // 게임 종료 처리 완료 후 플래그 해제
        isEnding = false
    }
}