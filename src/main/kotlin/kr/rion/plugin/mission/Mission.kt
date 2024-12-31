package kr.rion.plugin.mission

import org.bukkit.entity.Player
import org.bukkit.event.Event


interface Mission {
    fun missionStart(player: Player) // 미션 시작
    fun checkEventSuccess(player: Player, event: Event): Boolean // 성공 조건 확인
    fun onSuccess(player: Player) // 성공 처리
    fun reset() // 데이터 초기화
    companion object {
        const val MISSIONPREFIX = "§l§o§6[Mission] §a"
    }
}
