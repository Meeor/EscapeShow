package kr.rion.plugin.customEvent

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

enum class RevivalEventType {
    SUCCESS, // 부활 성공
    FAILED   // 부활 실패
}

class RevivalEvent(
    val player: Player,                 // 부활 대상자
    val relatedPlayer: Player?,         // 부활시켜준 사람 또는 아이템을 가져간 사람
    val eventType: RevivalEventType     // 이벤트 종류
) : Event() {
    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
    override fun getHandlers(): HandlerList {
        return handlerList // ✅ 올바른 방식
    }
}
