package kr.rion.plugin.util

import kr.rion.plugin.Loader
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object TPSManager {
    private var lastTime = System.currentTimeMillis()
    private var tickCount = 0
    var tps = 20.0 // 기본값 20 TPS

    init {
        object : BukkitRunnable() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val elapsed = (currentTime - lastTime) / 1000.0 // ✅ 경과 시간(초)

                if (elapsed > 0) {
                    tps = tickCount / elapsed // ✅ 초당 틱 수(TPS) 계산
                }

                lastTime = currentTime
                tickCount = 0 // ✅ 1초마다 틱 카운트 초기화
            }
        }.runTaskTimer(Loader.instance, 0L, 20L) // ✅ 1초(20틱)마다 실행

        // ✅ 서버의 틱 이벤트 감지하여 tickCount 증가
        Bukkit.getScheduler().runTaskTimer(Loader.instance, Runnable {
            tickCount++ // ✅ 매 틱마다 증가
        }, 0L, 1L) // ✅ 매 틱(1L)마다 실행
    }
}
