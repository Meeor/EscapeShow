package kr.rion.plugin.util

import kr.rion.plugin.game.End
import kr.rion.plugin.game.End.EscapePlayers
import kr.rion.plugin.game.End.MissionSuccessEscapePlayers
import kr.rion.plugin.game.End.isEnding
import kr.rion.plugin.game.Reset.resetplayerAttribute
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestEnable
import kr.rion.plugin.item.FlameGunActions.playersAtParticle
import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.manager.MissionManager.endGame
import kr.rion.plugin.manager.TeamManager
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Helicopter.HelicopterisSpawn
import kr.rion.plugin.util.Item.teleportCompass
import kr.rion.plugin.util.Teleport.console
import kr.rion.plugin.util.Teleport.stopPlayer
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

object Global {
    var prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"
    var EscapePlayerCount: Int = 0
    var MissionSuccessCount: Int = 0
    var EscapePlayerMaxCount: Int = 3
    var MissionEscapeMaxCount: Int = 3
    var helicopterfindattempt: Int = 100
    var teamsMaxPlayers: Int = 3
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
            } else if (survivalPlayers().count <= 5) {
                missionclearAction()
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
        player.addScoreboardTag("EscapeComplete")
        player.removeScoreboardTag("Escape")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다. \n${ChatColor.LIGHT_PURPLE}남은 플레이어 : ${ChatColor.YELLOW}${survivalPlayers().count}${ChatColor.LIGHT_PURPLE}명 ${ChatColor.GREEN}/ ${ChatColor.AQUA}남은 팀 : ${ChatColor.YELLOW}${TeamManager.getSurviverCount()}${ChatColor.AQUA} 팀")
        EscapePlayerCount++
        EscapePlayers.add(player.name)
        val remainingPlayers = EscapePlayerMaxCount - EscapePlayerCount

        if (remainingPlayers > 0) {
            Bukkit.broadcastMessage("${ChatColor.YELLOW}$remainingPlayers${ChatColor.LIGHT_PURPLE} 명이 탈출 가능합니다.")
        } else {
            Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}탈출 허용 인원이 가득 차 헬기가 떠납니다.")
            Helicopter.remove() // 내부적으로 null 체크를 처리함
        }
        player.sendMessage("$prefix 플라이,무적및 투명화가 활성화 되었습니다!")
        removeDirectionBossBar(player)
        endingPlayer()
    }

    fun missionclearAction() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.scoreboardTags.any {
                    it in listOf(
                        "DeathAndAlive",
                        "EscapeComplete",
                        "death",
                        "manager",
                        "MissionSuccessEscape"
                    )
                } && player.scoreboardTags.contains("MissionSuccess") && !MissionSuccessEscapePlayers.contains(player.name)) {
                if (MissionSuccessCount < MissionEscapeMaxCount) {
                    // 게임 모드 변경
                    player.gameMode = GameMode.ADVENTURE
                    // 플라이 허용
                    player.allowFlight = true
                    player.isFlying = true

                    // 투명화 버프 부여 (무한지속시간)
                    val invisibilityEffect =
                        PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false)
                    val blindEffect = PotionEffect(PotionEffectType.BLINDNESS, 2, 1, false, false)
                    val hangerEffect = PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1, false, false)
                    val healthEffect = PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 5, false, false)
                    player.addPotionEffect(invisibilityEffect)
                    player.addPotionEffect(blindEffect)
                    player.addPotionEffect(hangerEffect)
                    player.addPotionEffect(healthEffect)
                    player.inventory.clear()
                    player.inventory.setItem(8, teleportCompass())
                    player.scoreboardTags.clear()
                    MissionSuccessCount++
                    MissionSuccessEscapePlayers.add(player.name)
                    Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이${ChatColor.AQUA}미션 클리어로 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하신것으로 처리되었습니다. ")
                    player.addScoreboardTag("MissionSuccessEscape")
                    removeDirectionBossBar(player)
                    player.sendMessage("$prefix 플라이,무적및 투명화가 활성화 되었습니다!")
                }
            }
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
                    !player.scoreboardTags.contains("DeathAndAlive") &&
                    !player.scoreboardTags.contains("MissionSuccessEscape")
        }
        return SurvivalInfo(survivalPlayers.size, survivalPlayers.map { it.name })
    }


    fun endingPlayer() {
        val remainingTeams = TeamManager.getSurviverCount() // 생존한 팀 수 가져오기
        val remainingPlayers = survivalPlayers().count // 생존한 플레이어 수

        // ✅ 생존한 팀이 1팀만 남거나, 생존자가 1명만 남은 경우 게임 종료
        if (remainingTeams == 1 || remainingPlayers == 1) {
            if (!isEnding) return
            endGame()

            // ✅ 생존자가 1명 남았다면 마지막 생존자로 처리
            if (remainingPlayers == 1) {
                val lastSurvivor = survivalPlayers().names.firstOrNull()
                lastSurvivor?.let {
                    Bukkit.getPlayer(it)?.addScoreboardTag("lastsuriver")
                }
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

    fun GameAllReset() {
        EscapePlayerCount = 0
        chestEnable = false
        bossbarEnable = 0 // 보스바 업데이트 종료
        playerItem.clear() //플레이어 핫바및 갑옷슬룻 저장값 초기화
        MissionManager.resetMissions()
        cancelAllTasks()
        door = true
        HelicopterisSpawn = false
        stopPlayer.clear()
    }

    fun GameAllReset2() {
        resetplayerAttribute()
        playersAtParticle.clear()
        EscapePlayers.clear()
        MissionSuccessEscapePlayers.clear()
        reviveFlags.clear()
    }

    fun PlayerAllReset() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.scoreboardTags.contains("manager")) {
                // manager 태그가 없는 플레이어작업
                removeDirectionBossBar(player)
                player.scoreboardTags.clear()
                player.allowFlight = false
                player.isFlying = false
                player.gameMode = GameMode.ADVENTURE
            }
            player.inventory.clear()
            for (effect in player.activePotionEffects) {
                player.removePotionEffect(effect.type)
            }
            val team = scoreboard?.getEntryTeam(player.name)
            team?.removeEntry(player.name) // ✅ 팀에서 플레이어 제거 (색상 초기화)
            player.customName = null // ✅ 머리 위 닉네임 리셋
            player.setDisplayName(player.name)
            player.setPlayerListName(player.name) // ✅ Tab 목록 닉네임 리셋
        }
    }


}