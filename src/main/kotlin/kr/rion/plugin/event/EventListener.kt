package kr.rion.plugin.event

import org.bukkit.Bukkit
import org.bukkit.event.Listener


/////////////////////파일 분리 예정/////////////////
class EventListener : Listener {

    init {
        val pluginManager = Bukkit.getPluginManager()
        val plugin = pluginManager.plugins[0]

        // 이벤트 리스너들을 리스트로 관리
        val events = listOf(
            ChatEvent(),
            DamageEvent(),
            DeathEvent(),
            ItemUseEvent(),
            JoinEvent(),
            RespawnEvent(),
            InventoryClickListener(),
            InventoryCloseEvent(),
            PlayerMoveListener(),
            EscapePlayerEvent(),
            CreditEvent()
        )

        // 한 번에 모든 이벤트 등록
        events.forEach { event ->
            pluginManager.registerEvents(event, plugin)
        }
    }
}