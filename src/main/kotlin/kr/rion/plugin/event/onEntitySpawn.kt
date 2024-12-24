package kr.rion.plugin.event

import de.tr7zw.nbtapi.NBTEntity
import kr.rion.plugin.Loader
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Global.processedPlayers
import kr.rion.plugin.util.Global.respawnTask
import kr.rion.plugin.util.Global.reviveFlags
import kr.rion.plugin.util.Item.createCustomItem
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.scheduler.BukkitRunnable


class onEntitySpawn: Listener {

    private val sneakingTimers = mutableMapOf<String, Int>() // 웅크리는 시간 추적


    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if(!isStarting) return
        val entity = event.entity

        // MohistModsEntity 감지
        if (entity.toString().contains("CORPSE_CORPSE")) {
            val corpseEntity = entity as com.mohistmc.bukkit.entity.MohistModsEntity




            //NBTAPI를 사용하여 데이터 가져오기
            val nbtEntity = NBTEntity(corpseEntity)
            val deathData = nbtEntity.getCompound("Death") // Death NBT 태그 접근
            val playerName = deathData?.getString("PlayerName") ?: run {
                Bukkit.getLogger().warning("PlayerName not found in Death NBT data!")
                return
            }
            if(!processedPlayers.contains(playerName)) createTextDisplay(corpseEntity,"§a부활")
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

                // MainInventory 데이터 확인 및 시체 엔티티 제거 조건
                val mainInventory = deathData?.getCompoundList("MainInventory")
                if(mainInventory == null || mainInventory.isEmpty()){
                    sneakingTimers.remove(playerName) // 플래그 상태 변경 시 타이머 초기화
                    processedPlayers.add(playerName) // 처리된 플레이어로 추가
                    respawnTask.remove(playerName)?.cancel()
                    // 인벤토리 초기화 및 관전 모드로 변경
                    val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                    player.inventory.clear()
                    player.gameMode = GameMode.SPECTATOR
                    player.sendMessage("§c누군가가 당신의 아이템을 가져갔습니다.\n§c부활이 금지되며 관전 모드로 변경됩니다.")
                    player.removeScoreboardTag("DeathAndAlive")
                    player.addScoreboardTag("death")
                    return@Runnable
                }
                if (!corpseEntity.isValid || !reviveFlags[playerName]!!) {
                    sneakingTimers.remove(playerName) // 플래그 상태 변경 시 타이머 초기화
                    processedPlayers.add(playerName) // 처리된 플레이어로 추가
                    respawnTask.remove(playerName)?.cancel()
                    removeTextDisplay(corpseEntity)
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
                for (nearbyPlayer in nearbyPlayers) {
                    if (nearbyPlayer.name == playerName && nearbyPlayer.isSneaking) {
                        val currentTime = sneakingTimers.getOrDefault(playerName, 0) + 1
                        sneakingTimers[playerName] = currentTime

                        if (currentTime >= 3) { // 3초간 웅크림 확인
                            // 부활 조건 충족
                            reviveFlags[playerName] = false // 부활 불가능 상태로 변경
                            sneakingTimers.remove(playerName)

                            val player = Bukkit.getPlayer(playerName) ?: return@Runnable
                            player.gameMode = GameMode.SURVIVAL
                            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue ?: 20.0
                            player.sendMessage("§a당신은 부활했습니다!")
                            removeTextDisplay(corpseEntity)
                            processedPlayers.add(playerName)
                            for (slot in 9..35) {
                                val item = player.inventory.getItem(slot)
                                if (item != null && item.type != Material.AIR) {
                                    // 아이템을 플레이어 위치에 드롭
                                    player.world.dropItemNaturally(player.location, item)
                                    player.inventory.setItem(slot, null) // 슬롯 비우기
                                }
                            }
                            val craftingItem = createCustomItem(
                                "${ChatColor.GREEN}조합아이템",
                                listOf("${ChatColor.YELLOW}손에들고 우클릭시 조합창을 오픈합니다."),
                                Material.SLIME_BALL
                            )
                            val bookAndQuill = createCustomItem(
                                "${ChatColor.GREEN}미션",
                                listOf("${ChatColor.YELLOW}현재 본인이 받은 미션을 확인합니다."),
                                Material.WRITABLE_BOOK
                            )
                            val map = createCustomItem(
                                "${ChatColor.GREEN}지도",
                                listOf("${ChatColor.YELLOW}클릭시 맵 전체 지도를 확인할수있습니다."),
                                Material.MOJANG_BANNER_PATTERN
                            )
                            val barrier = createCustomItem("${ChatColor.RED}사용할수 없는칸", emptyList(), Material.BARRIER)
                            for (i in 8..35) {
                                when (i) {
                                    20 -> player.inventory.setItem(i, bookAndQuill) // 20번 슬롯에 책과 깃펜
                                    24 -> player.inventory.setItem(i, map) // 24번 슬롯에 지도
                                    8 -> player.inventory.setItem(i, craftingItem) // 8번 슬롯에 제작 아이템
                                    else -> player.inventory.setItem(i, barrier) // 나머지 슬롯에 방벽
                                }
                            }

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
    private val corpseTextDisplays: MutableMap<Entity, TextDisplay> = mutableMapOf()

    // 텍스트 디스플레이 생성
    fun createTextDisplay(corpseEntity: Entity, text: String?) {
        // 기존 텍스트 디스플레이 제거 (중복 방지)
        removeTextDisplay(corpseEntity)

        // 텍스트 디스플레이 생성
        val location: Location = corpseEntity.location.add(0.0, 1.5, 0.0) // corpseEntity 위에 표시
        val textDisplay: TextDisplay = corpseEntity.world.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay

        // 텍스트 설정
        textDisplay.customName = text // 텍스트 내용
        textDisplay.isCustomNameVisible = true // 항상 표시
        textDisplay.billboard = org.bukkit.entity.Display.Billboard.CENTER // 카메라 중심
        textDisplay.isShadowed = true // 그림자 효과

        // corpseEntity와 텍스트 디스플레이 매핑 저장
        corpseTextDisplays[corpseEntity] = textDisplay
    }

    // 텍스트 디스플레이 제거
    fun removeTextDisplay(corpseEntity: Entity) {
        // corpseEntity에 해당하는 텍스트 디스플레이 제거
        val textDisplay = corpseTextDisplays[corpseEntity]
        if (textDisplay != null && !textDisplay.isDead) {
            textDisplay.remove()
        }
        corpseTextDisplays.remove(corpseEntity)
    }
}
