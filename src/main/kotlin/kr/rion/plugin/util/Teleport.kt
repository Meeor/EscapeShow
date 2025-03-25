package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.delay.delayForEachPlayer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


object Teleport {


    private var worldManager: WorldManager? = null

    private lateinit var plugin: JavaPlugin
    lateinit var safeLocations: MutableList<Location>
    private val destinationWorldName = "game"
    private var destinationWorld: World? = null
    val console = Bukkit.getServer().consoleSender
    val immunePlayers = mutableSetOf<Player>() // 플레이어와 면역 시간 맵
    val stopPlayer: MutableMap<Player, Boolean> = mutableMapOf()

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

    fun initializeSafeLocations(teamcount: Int) {

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

        val requiredSafeLocations = 5 + teamcount // 목표: 60개
        val maxAttempts = 1000000 // 시도 횟수 제한 (필요에 따라 조정 가능)
        var attempts = 0

        val minDistance = radius * 1.5 / teamcount // 최소 거리 설정 (30 블록)

        while (safeLocations.size < requiredSafeLocations && attempts < maxAttempts) {
            // 원형 범위 안에서 무작위 좌표 생성
            val angle = rand.nextDouble() * 2 * Math.PI  // 0에서 2π 사이의 랜덤 각도
            val distance = rand.nextDouble() * radius   // 반경 내의 랜덤 거리
            val x = centerX + distance * Math.cos(angle)  // 중심점에서 X 좌표
            val z = centerZ + distance * Math.sin(angle)  // 중심점에서 Z 좌표
            val y = rand.nextInt(maxY - minY + 1) + minY  // Y 좌표는 범위 내에서 랜덤 선택

            val location = Location(world, x, y.toDouble(), z)

            // 최소 거리 조건 확인
            if (isLocationCaveAir(location) && safeLocations.none { it.distance(location) < minDistance }) {
                safeLocations.add(location)
            }
            attempts++
        }

        val endTime = System.currentTimeMillis()
        Bukkit.broadcastMessage("$prefix 플레이어들이 이동될 좌표를 찾았습니다. 걸린시간 : ${ChatColor.LIGHT_PURPLE}${endTime - startTime}ms")
        hasInitializedSafeLocations = true
    }

    fun teleportTeamToRandomLocation(players: List<Player>) {
        Bukkit.getLogger().warning("[DEBUG] 함수 호출됨.")
        if (!hasInitializedSafeLocations) {
            players.forEach { it.sendMessage("$prefix 안전한 좌표가 초기화되지 않았습니다. 나중에 다시 시도해주세요.") }

            Bukkit.getLogger().warning("[DEBUG] 안전한 좌표가 초기화되지 않았습니다. 나중에 다시 시도해주세요.")
            return
        }

        val startTime = System.currentTimeMillis()
        val safeLocation = safeLocations.randomOrNull()

        if (safeLocation == null) {
            console.sendMessage("$prefix 좌표를 찾을 수 없습니다. 재설정을 해주세요.")
            players.forEach { it.sendMessage("$prefix 저장된 좌표값이 없어 이동에 실패하였습니다.\n$prefix 운영자에게 좌표설정을 부탁하시길 바랍니다.") }
            return
        }

        val targetChunk = safeLocation.chunk
        if (!targetChunk.isLoaded) {
            targetChunk.load()  // 청크 강제 로드
        }

        delayForEachPlayer(
            players,
            action = { player ->
                player.teleport(safeLocation)
            },
            onComplete = {
                // 텔레포트 완료 후 중앙 위치 제거
                safeLocations.remove(safeLocation)

                val endTime = System.currentTimeMillis()
                console.sendMessage("$prefix 텔레포트 지연시간: ${endTime - startTime}ms")
            }
        )
    }

    fun teleportSoleToRandomLocation(player: Player) {
        Bukkit.getLogger().warning("[DEBUG] 함수 호출됨.")
        if (!hasInitializedSafeLocations) {
            player.sendMessage("$prefix 안전한 좌표가 초기화되지 않았습니다. 나중에 다시 시도해주세요.")

            Bukkit.getLogger().warning("[DEBUG] 안전한 좌표가 초기화되지 않았습니다. 나중에 다시 시도해주세요.")
            return
        }

        val startTime = System.currentTimeMillis()
        val safeLocation = safeLocations.randomOrNull()

        if (safeLocation == null) {
            console.sendMessage("$prefix 좌표를 찾을 수 없습니다. 재설정을 해주세요.")
            player.sendMessage("$prefix 저장된 좌표값이 없어 이동에 실패하였습니다.\n$prefix 운영자에게 좌표설정을 부탁하시길 바랍니다.")
            return
        }

        val targetChunk = safeLocation.chunk
        if (!targetChunk.isLoaded) {
            targetChunk.load()  // 청크 강제 로드
        }

        Bukkit.getScheduler().runTask(Loader.instance, Runnable {
            player.teleport(safeLocation)
            // 텔레포트 완료 후 중앙 위치 제거
            safeLocations.remove(safeLocation)

            val endTime = System.currentTimeMillis()
            console.sendMessage("$prefix 팀 텔레포트 지연시간: ${endTime - startTime}ms")
        })
    }


    private fun isLocationCaveAir(loc: Location): Boolean {
        val block = loc.block
        // 해당 위치의 블록이 CAVE_AIR인지 확인
        return block.type == Material.CAVE_AIR
    }


    fun setInitializedSafeLocations(status: Boolean) {
        hasInitializedSafeLocations = status
    }


}