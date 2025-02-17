package kr.rion.plugin.util

import kr.rion.plugin.game.End
import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.manager.MissionManager.endGame
import kr.rion.plugin.util.Item.teleportCompass
import kr.rion.plugin.util.Teleport.console
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

object Global {
    val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"
    var EscapePlayerCount: Int = 0
    var EscapePlayerMaxCount: Int = 3
    var endingPlayerMaxCount: Int = 6
    var helicopterfindattempt: Int = 100
    var door = true

    //부활 관련 변수
    val reviveFlags = mutableMapOf<String, Boolean>() // 플레이어별 부활 상태 저장
    var respawnTask = mutableMapOf<String, BukkitTask>()
    val processedPlayers = mutableSetOf<String>() // 이미 처리된 플레이어 저장
    val sneakingTimers = mutableMapOf<String, Int>() // 웅크리는 시간 추적


    //부활시 아이템 복구를 위한 변수
    val playerItem = mutableMapOf<String, MutableList<ItemStack?>>()

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

        val debuger = Bukkit.getPlayer("Meor_")
        debuger?.sendMessage("§l§e${player.name}§c게임모드 변경 확인. §b(Global.kt : 41)")

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
        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다. ${ChatColor.LIGHT_PURPLE}(남은 플레이어 : ${ChatColor.YELLOW}${survivalPlayers().count}${ChatColor.LIGHT_PURPLE}명)")
        val remainingPlayers = EscapePlayerMaxCount - EscapePlayerCount

        if (remainingPlayers > 0) {
            Bukkit.broadcastMessage("${ChatColor.YELLOW}$remainingPlayers${ChatColor.LIGHT_PURPLE} 명이 탈출 가능합니다.")
        } else {
            Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}탈출 허용 인원이 가득 차 헬기가 떠납니다.")
            Helicopter.remove() // 내부적으로 null 체크를 처리함
            ifEnding = true
        }
        player.sendMessage("${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN} 플라이,무적및 투명화가 활성화 되었습니다!")
        Bossbar.removeDirectionBossBar(player)
        if (ifEnding) {
            endingPlayer()
        }
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
            //인벤세이브 강제 비활성화
            world.setGameRule(GameRule.KEEP_INVENTORY, false)
            //자연회복 비활성화
            world.setGameRule(GameRule.NATURAL_REGENERATION, false)

            console.sendMessage("${world.name} 월드의 게임룰설정이 변경되었습니다.")
        }
        Bukkit.clearRecipes()
    }

    data class SurvivalInfo(val count: Int, val names: List<String>)

    fun survivalPlayers(): SurvivalInfo {
        val survivalPlayers = Bukkit.getOnlinePlayers().filter { player ->
            !player.scoreboardTags.contains("manager") &&
                    !player.scoreboardTags.contains("EscapeComplete") &&
                    !player.scoreboardTags.contains("death") &&
                    !player.scoreboardTags.contains("DeathAndAlive")
        }
        return SurvivalInfo(survivalPlayers.size, survivalPlayers.map { it.name })
    }


    fun endingPlayer() {
        val endingPlayerCount: Int = survivalPlayers().count + EscapePlayerCount

        if (endingPlayerCount <= endingPlayerMaxCount || survivalPlayers().count == 1) {
            if (!isEnding) return
            endGame()
            if (survivalPlayers().count == 1) {
                val lastsuriver = survivalPlayers().names.firstOrNull() ?: "없음"
                Bukkit.getPlayer(lastsuriver)?.addScoreboardTag("lastsuriver")
            }
            End.EndAction()
        }

    }


    fun timerReset(playerName: String) {
        sneakingTimers.remove(playerName)
    }

    ///부활체크작업 종료
    fun cancelAllTasks() {
        for ((_, task) in respawnTask) {
            task.cancel() // 모든 작업 종료
        }
        sneakingTimers.clear()
        respawnTask.clear() // 맵 초기화
        processedPlayers.clear()
    }

    fun adjustToAboveSpecificBlock(
        world: World,
        baseLocation: Location,
        targetBlock: Material // 특정 블록 하나
    ): Location? {
        val worldBorder = world.worldBorder // 월드보더 가져오기
        val borderCenter = worldBorder.center // 월드보더 중심
        val borderRadius = worldBorder.size / 2 // 월드보더 반경 (size는 직경이므로 2로 나눔)
        val randomRange = -30..30 // X, Z 좌표 랜덤 범위

        for (attempt in 1..helicopterfindattempt) {
            var randomX: Double
            var randomZ: Double

            do {
                randomX = baseLocation.x + randomRange.random()
                randomZ = baseLocation.z + randomRange.random()
            } while (
                randomX !in (borderCenter.x - borderRadius)..(borderCenter.x + borderRadius) ||
                randomZ !in (borderCenter.z - borderRadius)..(borderCenter.z + borderRadius)
            )

            val randomLocation = Location(world, randomX, baseLocation.y, randomZ)


            val randomXInt = randomLocation.blockX
            val randomZInt = randomLocation.blockZ
            val yStart = randomLocation.blockY
            val minY = world.minHeight // 월드 최소 높이

            // Y 좌표 아래로 탐색
            for (y in yStart downTo minY) {
                val block = world.getBlockAt(randomXInt, y, randomZInt)
                if (block.type == targetBlock) {
                    // 특정 블록을 찾았을 때 해당 블록 위 좌표 반환
                    Bukkit.getLogger().info("헬기위치 결정 완료!  시도횟수 : $attempt")
                    return Location(world, randomXInt.toDouble(), y + 1.0, randomZInt.toDouble())
                }
            }
        }

        // 실패 시 null 반환
        Bukkit.getLogger().warning("탐색 최대횟수 $helicopterfindattempt 에 도달하였지만. 월드보더 내의 좌표를잡지 못하였습니다.")
        return null
    }


    //글자 중앙정렬 함수(색코드 제거하여 중앙정렬)(줄바꿈도 인정됨)
    fun centerText(text: String, lineWidth: Int = 22): String {
        val strippedText = stripColorCodes(text) // 색 코드 제거
        val totalPadding = lineWidth - strippedText.length
        val leftPadding = totalPadding / 2
        val rightPadding = totalPadding - leftPadding // 나머지를 오른쪽에 추가
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding)
    }


    private fun stripColorCodes(text: String): String {
        return text.replace(Regex("§[0-9a-fk-or]"), "")
    }


}