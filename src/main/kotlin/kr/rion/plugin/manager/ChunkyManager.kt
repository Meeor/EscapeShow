package kr.rion.plugin.manager

import kr.rion.plugin.Loader
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Teleport
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.potion.PotionEffectType
import org.popcraft.chunky.api.ChunkyAPI
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent


object ChunkyManager {
    private var chunky: ChunkyAPI? = Bukkit.getServer().servicesManager.load(ChunkyAPI::class.java)

    fun loadChunkyForWorld(worldName: String) {
        // 청키 API가 로드되지 않았을 때 예외 처리
        chunky?.let { api ->
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                Bukkit.broadcastMessage("$prefix ${ChatColor.RED}월드 '$worldName'가 존재하지 않습니다.")
                return
            }

            if (api.version() == 0) {
                if (worldName == "game") {
                    // game 월드: 기존 코드 유지
                    api.startTask(
                        "game", "square", 0.0, 0.0, 2000.0, 2000.0, "concentric"
                    )
                    api.onGenerationComplete { event: GenerationCompleteEvent ->
                        val eventWorld = Bukkit.getWorld(event.world) // 이벤트의 월드를 Bukkit에서 가져옴
                        if (eventWorld != null && eventWorld == world) {
                            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                                Teleport.initializeSafeLocations()
                                for (player in eventWorld.players) {
                                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                                }
                                Bukkit.broadcastMessage("$prefix 게임맵 리셋이 완료되었습니다.")
                            }, 100L)
                        }
                    }
                } else if (worldName == "vip") {
                    // vip 월드: 작업 반경 10 청크
                    api.startTask(
                        "vip", "square", 0.0, 0.0, 50.0, 50.0, "concentric" // 10 청크 ≈ 160 블록 반경
                    )
                    api.onGenerationComplete { event: GenerationCompleteEvent ->
                        val eventWorld = Bukkit.getWorld(event.world) // 이벤트의 월드를 Bukkit에서 가져옴
                        if (eventWorld != null && eventWorld == world) {
                        }
                    }
                } else {
                    Bukkit.broadcastMessage("$prefix ${ChatColor.YELLOW}월드 '$worldName'에 대한 로딩 작업이 정의되지 않았습니다.")
                }
            }
        } ?: run {
            Bukkit.broadcastMessage("$prefix ${ChatColor.RED}청크로딩 플러그인이 정상 작동하지 않습니다.")
        }
    }


}