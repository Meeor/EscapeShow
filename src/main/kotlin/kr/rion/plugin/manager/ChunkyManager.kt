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

    fun loadchunky() {
        // ChunkyAPI가 로드되지 않았을 때 예외 처리
        chunky?.let {
            if (it.version() == 0) {
                it.startTask("game", "square", 0.0, 0.0, 500.0, 500.0, "concentric")
                it.onGenerationComplete { _: GenerationCompleteEvent ->
                    Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                        Teleport.initializeSafeLocations()
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.removePotionEffect(PotionEffectType.BLINDNESS)
                        }
                        Bukkit.broadcastMessage("$prefix 게임맵 리셋이 완료되었습니다.")
                    }, 100L)
                }
            }
            return
        }
        Bukkit.broadcastMessage("$prefix ${ChatColor.RED}청크로딩플러그인이 정상작동을 하지않습니다.")
        return
    }
}