package kr.rion.plugin.manager

import kr.rion.plugin.Loader
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.popcraft.chunky.api.ChunkyAPI
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent


object ChunkyManager {
    var chunky: ChunkyAPI? = Bukkit.getServer().servicesManager.load(ChunkyAPI::class.java)

    private val console = Loader.instance.server.consoleSender

    fun loadchunky(world : String): Boolean {
        // ChunkyAPI가 로드되지 않았을 때 예외 처리
        chunky?.let {
            if (it.version() == 0) {
                it.startTask(world, "square", 0.0, 0.0, 500.0, 500.0, "concentric")
                it.onGenerationComplete { event: GenerationCompleteEvent -> console.sendMessage(prefix + event.world() + "월드가 정상로딩 되었습니다.") }
                return true
            }
        }
        Bukkit.broadcastMessage("$prefix ${ChatColor.RED}청크로딩플러그인이 정상작동을 하지않습니다.")
        return false
    }
}