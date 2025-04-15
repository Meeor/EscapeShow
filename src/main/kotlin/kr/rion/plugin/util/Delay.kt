package kr.rion.plugin.util

import kr.rion.plugin.Loader
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object Delay {
    fun delayRun(ticks: Long, block: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable { block() }, ticks)
    }

    fun delayForEachPlayer(
        players: Collection<Player>,
        tickGap: Long = 2L,
        action: (Player) -> Unit,
        onComplete: (() -> Unit)? = null
    ) {
        players.forEachIndexed { i, player ->
            Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                action(player)
                if (i == players.size - 1 && onComplete != null) {
                    delayRun(10) {
                        onComplete()
                    }
                }
            }, i * tickGap)
        }
    }

}