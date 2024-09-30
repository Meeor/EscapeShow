package kr.rion.plugin.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min

object Bossbar {
    val bossBars: MutableMap<Player, BossBar> = mutableMapOf()

    // 보스바를 생성하고 플레이어에게 방향을 표시
    fun createDirectionBossBar(player: Player, targetLocation: Location) {
        val bossBar = Bukkit.createBossBar("헬기 방향", BarColor.GREEN, BarStyle.SOLID)
        bossBar.addPlayer(player)
        bossBars[player] = bossBar

        updateDirectionBossBar(player, targetLocation)
    }

    // 보스바의 방향 게이지를 업데이트
    fun updateDirectionBossBar(player: Player, targetLocation: Location) {
        val directionToTarget = targetLocation.toVector().subtract(player.location.toVector()).normalize()
        val playerDirection = player.location.direction.normalize()

        // 두 벡터 간의 각도 계산 (0도에 가까울수록 같은 방향을 봄)
        val dotProduct = playerDirection.dot(directionToTarget)
        val angle = acos(dotProduct) // acos로 각도를 구함 (라디안)

        // 각도를 0.0에서 1.0 사이로 변환 (0도일 때는 게이지가 꽉 차고, 180도일 때는 게이지가 비워짐)
        val progress = 1.0 - (angle / Math.PI)

        // 보스바에 반영 (0.0에서 1.0 사이로 게이지를 설정)
        bossBars[player]?.progress = min(1.0, max(0.0, progress))
    }

    // 특정 플레이어에 대한 보스바 제거
    fun removeDirectionBossBar(player: Player) {
        bossBars[player]?.removeAll()
        bossBars.remove(player)
    }
}
