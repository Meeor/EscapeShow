package kr.rion.plugin.event

import de.tr7zw.nbtapi.NBTEntity
import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Global.processedPlayers
import kr.rion.plugin.util.Global.respawnTask
import kr.rion.plugin.util.Global.reviveFlags
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent


class onEntitySpawn: Listener {

    private val sneakingTimers = mutableMapOf<String, Int>() // 웅크리는 시간 추적


    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if(!isStarting) return
        val entity = event.entity
        Bukkit.getLogger().info(entity.toString())

        // MohistModsEntity 감지
        if (entity.toString().contains("CORPSE_CORPSE")) {
            val corpseEntity = entity as com.mohistmc.bukkit.entity.MohistModsEntity
            Bukkit.getLogger().info(corpseEntity.toString())

            //NBTAPI를 사용하여 데이터 가져오기
            val nbtEntity = NBTEntity(corpseEntity)
            val deathData = nbtEntity.getCompound("Death") // Death NBT 태그 접근
            val playerName = deathData?.getString("PlayerName") ?: run {
                Bukkit.getLogger().warning("PlayerName not found in Death NBT data!")
                return
            }
            // 플레이어별 부활 플래그 초기화
            if (!reviveFlags.containsKey(playerName)) {
                reviveFlags[playerName] = true
            }

            // 30초 후 부활 불가능 처리
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                reviveFlags[playerName] = false // 부활 불가능 상태로 변경
            }, 600L) // 30초 = 600 ticks

            val task = Bukkit.getScheduler().runTaskTimer(Loader.instance, Runnable {
                if (processedPlayers.contains(playerName)) {
                    respawnTask.remove(playerName)?.cancel() // 이미 처리된 플레이어라면 타이머 종료
                    return@Runnable
                }
                if (!corpseEntity.isValid || !reviveFlags[playerName]!!) {
                    sneakingTimers.remove(playerName) // 플래그 상태 변경 시 타이머 초기화
                    processedPlayers.add(playerName) // 처리된 플레이어로 추가
                    respawnTask.remove(playerName)?.cancel()
                    // 인벤토리 초기화 및 관전 모드로 변경
                    val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                    player.inventory.clear()
                    player.gameMode = GameMode.SPECTATOR
                    player.sendMessage("§c부활 시간이 초과되었습니다. 관전 모드로 변경됩니다.")
                    player.removeScoreboardTag("DeathAndAlive")
                    player.addScoreboardTag("death")
                    return@Runnable
                }

                // 플레이어가 웅크리고 반경 1칸 이내에 있는지 확인
                val nearbyEntities = corpseEntity.location.world?.getNearbyEntities(corpseEntity.location, 1.0, 1.0, 1.0)
                val nearbyPlayers = nearbyEntities?.filterIsInstance<Player>() ?: emptyList()
                val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                player.sendMessage("${corpseEntity.location}")
                for (nearbyPlayer in nearbyPlayers) {
                    Bukkit.getLogger().info("Player ${nearbyPlayer.name} sneaking: ${nearbyPlayer.isSneaking}  playername : ${playerName}")
                    if (nearbyPlayer.name == playerName && nearbyPlayer.isSneaking) {
                        val currentTime = sneakingTimers.getOrDefault(playerName, 0) + 1
                        sneakingTimers[playerName] = currentTime
                        val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                        player.sendMessage("$currentTime 초지남.")

                        if (currentTime >= 3) { // 3초간 웅크림 확인
                            // 부활 조건 충족
                            reviveFlags[playerName] = false // 부활 불가능 상태로 변경
                            sneakingTimers.remove(playerName)

                            val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                            player.gameMode = GameMode.SURVIVAL
                            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue ?: 20.0
                            player.sendMessage("§a당신은 부활했습니다!")
                            processedPlayers.add(playerName)

                            corpseEntity.remove() // 시체 엔티티 제거
                            break
                        }
                    } else {
                        val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                        player.sendMessage("웅크리기가 풀려 시간이 초기화 되었습니다.")
                        sneakingTimers.remove(playerName) // 웅크리지 않으면 시간 초기화
                    }
                }
            }, 20L, 20L) // 매초마다 체크
            respawnTask[playerName] = task
        }
    }

}
