package kr.rion.plugin.util

import kr.rion.plugin.manager.WorldManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
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

    var hasInitializedSafeLocations = false

    fun initialize(plugin: JavaPlugin) {
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
            Bukkit.getLogger().warning("월드 '${destinationWorldName}'가 로드되지 않았습니다.")
            return
        }


        val rand = Random()
        val startTime = System.currentTimeMillis()
        Bukkit.getLogger().info("이동될 안전한좌표탐색을 시작합니다.")


        val minX = -372
        val maxX = 718
        val minY = 53
        val maxY = 97
        val minZ = -710
        val maxZ = 45

        val requiredSafeLocations = 100 // 목표: 100개
        val maxAttempts = 50000 // 시도 횟수 제한 (필요에 따라 조정 가능)
        var attempts = 0

        while (safeLocations.size < requiredSafeLocations && attempts < maxAttempts) {
            val x = rand.nextInt(maxX - minX + 1) + minX
            val y = rand.nextInt(maxY - minY + 1) + minY
            val z = rand.nextInt(maxZ - minZ + 1) + minZ

            val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
            if (isLocationSafe(location) && !safeLocations.contains(location)) {
                safeLocations.add(location)
            }
            attempts++
        }

        val endTime = System.currentTimeMillis()
        Bukkit.getLogger().info("안전한 좌표 ${safeLocations.size} 개를 찾았습니다. 걸린시간 : ${endTime - startTime}ms")
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
        destinationWorld = worldManager?.getMultiverseWorld(destinationWorldName) ?: return
        val startTime = System.currentTimeMillis()
        val maxAttempts = 100
        var randomLocation: Location? = null

        for (attempt in 1..maxAttempts) {
            val safeLocation = safeLocations.randomOrNull() ?: continue

            if (isLocationSafe(safeLocation)) {
                randomLocation = safeLocation
                break
            }
        }

        if (randomLocation == null) {
            Bukkit.getLogger().warning(" $maxAttempts 회를 시도했지만 안전위치를 찾을수 없었습니다.")
            return
        }


        player.teleport(randomLocation)
        safeLocations.remove(randomLocation)
        val endTime = System.currentTimeMillis()
        Bukkit.getLogger().info("teleportToRandomLocation 지연시간: ${endTime - startTime}ms")
        setImmune(player, 3000)
    }

    fun isLocationSafe(loc: Location): Boolean {
        val block = loc.block
        val blockAbove = loc.clone().add(0.0, 1.0, 0.0).block
        val blockBelow = loc.clone().subtract(0.0, 1.0, 0.0).block

        // 바닥 블록이 공기가 아니고 안전하지 않은 경우
        return block.type == Material.AIR &&
                blockAbove.type == Material.AIR &&
                blockBelow.type.isSolid &&
                blockBelow.type !in setOf(
            Material.AZALEA_LEAVES, // 진달래잎
            Material.FERN, // 고사리
            Material.LARGE_FERN, // 큰고사리
            Material.GRASS, // 잔디
            Material.CRIMSON_BUTTON    //진홍빛버튼
        )
    }

    private val immunePlayers = mutableMapOf<Player, Long>() // 플레이어와 면역 시간 맵

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val currentTime = System.currentTimeMillis()
            // 플레이어가 면역 상태인지 확인
            if (immunePlayers[player]?.let { it > currentTime } == true) {
                event.isCancelled = true // 면역 상태일 경우 데미지 무시
            }
        }
    }

    fun setImmune(player: Player, duration: Long) {
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