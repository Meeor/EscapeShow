package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.Global.prefix
import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


object Teleport {


    private var worldManager: WorldManager? = null

    private lateinit var plugin: JavaPlugin
    lateinit var safeLocations: MutableList<Location>
    private val designatedWorldName = "lobby"
    private val destinationWorldName = "game"
    private var designatedWorld: World? = null
    private var destinationWorld: World? = null
    val console = Bukkit.getServer().consoleSender
    val immunePlayers = mutableMapOf<Player, Long>() // 플레이어와 면역 시간 맵

    private var hasInitializedSafeLocations = false

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
        Bukkit.getScheduler().runTaskLater(plugin, object : Runnable {
            override fun run() {
                worldManager = WorldManager(plugin)
                safeLocations = mutableListOf()
            }
        }, 20L) // 20틱 (1초) 지연
    }

    fun initializeSafeLocations() {

        destinationWorld = worldManager?.getMultiverseWorld(destinationWorldName)

        val destinationWorld: World? = worldManager?.getMultiverseWorld(destinationWorldName)
        if (hasInitializedSafeLocations) return

        val world = destinationWorld
        if (world == null) {
            console.sendMessage("$prefix 월드 ${ChatColor.YELLOW}'${destinationWorldName}'${ChatColor.GREEN}가 로드되지 않았습니다.")
            return
        }

        val rand = Random()
        val startTime = System.currentTimeMillis()
        console.sendMessage("$prefix 이동될 안전한 좌표 탐색을 시작합니다.")
        safeLocations.clear()

        // 중심 좌표 설정
        val centerX = 80
        val centerZ = -377

        // 반경 및 Y 좌표 범위 설정
        val radius = 326 // 반경 326 블록
        val minY = 53  // 최소 Y 좌표
        val maxY = 97  // 최대 Y 좌표

        val requiredSafeLocations = 60 // 목표: 60개
        val maxAttempts = 20000 // 시도 횟수 제한 (필요에 따라 조정 가능)
        var attempts = 0

        while (safeLocations.size < requiredSafeLocations && attempts < maxAttempts) {
            // 원형 범위 안에서 무작위 좌표 생성
            val angle = rand.nextDouble() * 2 * Math.PI  // 0에서 2π 사이의 랜덤 각도
            val distance = rand.nextDouble() * radius   // 반경 내의 랜덤 거리
            val x = centerX + distance * Math.cos(angle)  // 중심점에서 X 좌표
            val z = centerZ + distance * Math.sin(angle)  // 중심점에서 Z 좌표
            val y = rand.nextInt(maxY - minY + 1) + minY  // Y 좌표는 범위 내에서 랜덤 선택

            val location = Location(world, x, y.toDouble(), z)
            if (isLocationSafe(location) && !safeLocations.contains(location)) {
                safeLocations.add(location)
            }
            attempts++
        }

        val endTime = System.currentTimeMillis()
        console.sendMessage("$prefix 안전한 좌표 ${ChatColor.YELLOW}${safeLocations.size} ${ChatColor.GREEN}개를 찾았습니다. 걸린시간 : ${ChatColor.LIGHT_PURPLE}${endTime - startTime}ms")
        hasInitializedSafeLocations = true
    }


    fun isInDesignatedArea(loc: Location): Boolean {
        designatedWorld = worldManager?.getMultiverseWorld(designatedWorldName)
        val world = loc.world ?: return false
        if (world != designatedWorld) {
            return false
        }
        return loc.x in -69.0..11.0 &&
                loc.y in 124.0..124.0 &&
                loc.z in -40.0..40.0
    }

    fun teleportToRandomLocation(player: Player) {
        if (!hasInitializedSafeLocations) {
            player.sendMessage("$prefix 안전한 좌표가 초기화되지 않았습니다. 나중에 다시 시도해주세요.")
            return
        }

        val startTime = System.currentTimeMillis()

        val safeLocation = safeLocations.randomOrNull()
        val targetChunk = safeLocation?.chunk
        if (targetChunk != null) {
            if (!targetChunk.isLoaded) {
                targetChunk.load()  // 청크 강제 로드
            }
        }
        if (safeLocation == null) {
            console.sendMessage("$prefix 좌표를 찾을 수 없습니다. 재설정을 해주세요.")
            player.sendMessage("$prefix 저장된 좌표값이 없어 이동에 실패하였습니다.\n$prefix 운영자에게 좌표설정을 부탁하시길 바랍니다.")
            return
        }

        // 메인 스레드에서 텔레포트 작업 수행
        Bukkit.getScheduler().runTask(Loader.instance, Runnable {
            try {
                setImmune(player, 5000) // 텔레포트 후 면역 처리
                player.teleport(safeLocation)
                // 텔레포트 완료 후 작업 처리
                safeLocations.remove(safeLocation) // 성공 시 좌표 제거

                val endTime = System.currentTimeMillis()
                console.sendMessage("$prefix 플레이어 텔레포트 지연시간: ${endTime - startTime}ms")
                Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                    player.playSound(player, "custom.start", SoundCategory.MASTER, 1.0f, 1.0f)
                    player.sendTitle("${ChatColor.GREEN}게임을 시작합니다.", "${ChatColor.YELLOW}상대를 죽이고 탈출수단을 이용해서 이곳을 탈출하세요.")
                }, 30)
            } catch (e: Exception) {
                console.sendMessage("$prefix 텔레포트 중 오류가 발생했습니다: ${e.message}")
            }
        })
    }


    private fun isLocationSafe(loc: Location): Boolean {
        val block = loc.block
        val blockAbove = loc.clone().add(0.0, 1.0, 0.0).block
        val blockAboveTwo = loc.clone().add(0.0, 2.0, 0.0).block
        val blockBelow = loc.clone().subtract(0.0, 1.0, 0.0).block

        // 바닥 블록이 공기가 아니고 안전하지 않은 경우
        return block.type == Material.AIR &&
                blockAbove.type == Material.AIR &&
                blockAboveTwo.type == Material.AIR &&
                blockBelow.type.isSolid &&
                !setOf(
                    Material.AZALEA_LEAVES, // 진달래잎
                    Material.FERN, // 고사리
                    Material.LARGE_FERN, // 큰고사리
                    Material.GRASS, // 잔디
                    Material.CRIMSON_BUTTON, //진홍빛버튼
                    Material.WATER, //물
                    Material.LAVA //용암
                ).contains(blockBelow.type)
    }

    private fun setImmune(player: Player, duration: Long) {
        val currentTime = System.currentTimeMillis()
        immunePlayers[player] = currentTime + duration
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            immunePlayers.remove(player)
        }, (duration / 50))
    }

    fun setInitializedSafeLocations(status: Boolean) {
        hasInitializedSafeLocations = status
    }


}