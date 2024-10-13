package kr.rion.plugin.game

import kr.rion.plugin.Loader
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object Start {
    var isStart = false //시작작업상태 플래그
    var isStarting = false //게임 상태 플래그

    fun startAction(){
        isStart = true
        executeBlockFillingAndEffect()
    }


    private fun executeBlockFillingAndEffect() {
        object : BukkitRunnable() {
            var step = 20

            override fun run() {
                if (step > 155) {
                    // 작업 완료 시 추가 작업 실행
                    isStart = false
                    isStarting = true
                    for (player: Player in Bukkit.getOnlinePlayers()) {
                        if (player.scoreboardTags.contains("manager")) {
                            // 콘솔 명령어 실행하여 해당 플레이어를 game 월드로 이동
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${player.name} game")
                        }
                    }
                    cancel() // 반복 종료
                    return
                }

                fillBlocksWithDestroyEffect(step)
                step += 5 // 5단계씩 증가
            }
        }.runTaskTimer(Loader.instance, 0L, 1L) // 0틱 이후 실행, 1틱 간격으로 실행
    }
    // 특정 단계에서 블록을 공기로 설정하고 파괴 효과 적용
    private fun fillBlocksWithDestroyEffect(step: Int) {
        // 월드를 '로비'로 고정
        val lobbyWorld: World = Bukkit.getWorld("lobby") ?: run {
            Bukkit.getLogger().severe("로비 월드를 찾을 수 없습니다!")
            return
        }

        // 각 단계별 블록 좌표 처리
        when (step) {
            20 -> fillBlocksInRange(lobbyWorld, -23, 129, 0, -44, 134, 0)
            25 -> fillBlocksInRange(lobbyWorld, -44, 134, 1, -23, 129, -1)
            30 -> fillBlocksInRange(lobbyWorld, -23, 129, -2, -44, 134, 2)
            35 -> fillBlocksInRange(lobbyWorld, -44, 134, 3, -23, 129, -3)
            40 -> fillBlocksInRange(lobbyWorld, -44, 134, 4, -23, 129, -4)
            45 -> fillBlocksInRange(lobbyWorld, -23, 129, -5, -44, 134, 5)
            50 -> fillBlocksInRange(lobbyWorld, -44, 134, 6, -23, 129, -6)
            55 -> fillBlocksInRange(lobbyWorld, -22, 129, -7, -44, 134, 7)
            60 -> fillBlocksInRange(lobbyWorld, -22, 129, -8, -44, 134, 8)
            65 -> fillBlocksInRange(lobbyWorld, -44, 134, 9, -22, 129, -9)
            70 -> fillBlocksInRange(lobbyWorld, -22, 129, -10, -44, 134, 10)
            75 -> fillBlocksInRange(lobbyWorld, -43, 134, 11, -21, 129, -11)
            80 -> fillBlocksInRange(lobbyWorld, -21, 129, -12, -43, 134, 12)
            85 -> fillBlocksInRange(lobbyWorld, -43, 134, 13, -21, 129, -13)
            90 -> fillBlocksInRange(lobbyWorld, -43, 134, 14, -20, 129, -14)
            95 -> fillBlocksInRange(lobbyWorld, -20, 129, -15, -42, 134, 15)
            100 -> fillBlocksInRange(lobbyWorld, -42, 134, 16, -19, 129, -16)
            105 -> fillBlocksInRange(lobbyWorld, -20, 129, -17, -42, 134, 17)
            110 -> fillBlocksInRange(lobbyWorld, -41, 134, 18, -22, 129, -18)
            115 -> fillBlocksInRange(lobbyWorld, -24, 129, -19, -41, 134, 19)
            120 -> fillBlocksInRange(lobbyWorld, -41, 134, 20, -26, 129, -20)
            125 -> fillBlocksInRange(lobbyWorld, -28, 129, -21, -40, 134, 21)
            130 -> fillBlocksInRange(lobbyWorld, -39, 134, 22, -30, 129, -22)
            135 -> fillBlocksInRange(lobbyWorld, -32, 129, -23, -37, 134, 23)
            140 -> fillBlocksInRange(lobbyWorld, -41, 133, 18, -22, 129, -18)
            145 -> fillBlocksInRange(lobbyWorld, -41, 133, 19, -24, 129, -19)
            150 -> fillBlocksInRange(lobbyWorld, -26, 129, -20, -41, 133, 20)
            155 -> fillBlocksInRange(lobbyWorld, -40, 133, 21, -28, 129, -21)
        }
    }

    // 좌표 범위 내의 모든 블록을 공기로 변경하고 파괴 효과를 적용하는 함수
    private fun fillBlocksInRange(world: World, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        // 범위 내의 모든 블록을 공기로 변경
        for (x in x1.coerceAtMost(x2)..x1.coerceAtLeast(x2)) {
            for (y in y1.coerceAtMost(y2)..y1.coerceAtLeast(y2)) {
                for (z in z1.coerceAtMost(z2)..z1.coerceAtLeast(z2)) {
                    val block = world.getBlockAt(x, y, z)
                    block.type = Material.AIR // 블록을 공기로 변경

                    // 소리와 파티클 효과 추가
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.playSound(block.location, Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f)
                        player.world.spawnParticle(Particle.BLOCK_CRACK, block.location.add(0.5, 0.5, 0.5), 30, block.blockData)
                    }
                }
            }
        }
    }
}