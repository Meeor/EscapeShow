package kr.rion.plugin.event

import org.bukkit.Bukkit
import org.bukkit.event.Listener


/////////////////////파일 분리 예정/////////////////
class EventListener : Listener {
    init {
        // 각종 이벤트 리스너 등록
        Bukkit.getPluginManager().registerEvents(ChatEvent(), Bukkit.getPluginManager().plugins[0])
        Bukkit.getPluginManager().registerEvents(DamageEvent(), Bukkit.getPluginManager().plugins[0])
        Bukkit.getPluginManager().registerEvents(DeathEvent(), Bukkit.getPluginManager().plugins[0])
        Bukkit.getPluginManager().registerEvents(ItemUseEvent(), Bukkit.getPluginManager().plugins[0])
        Bukkit.getPluginManager().registerEvents(JoinEvent(), Bukkit.getPluginManager().plugins[0])
        Bukkit.getPluginManager().registerEvents(RespawnEvent(), Bukkit.getPluginManager().plugins[0])
    }
}