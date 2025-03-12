package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class CumulativeHealingMission(
    private val requiredHealing: Double,
    private val plugin: JavaPlugin
) : Mission {
    private val playerHealingMap = mutableMapOf<UUID, Double>() // 누적 회복량
    private val lastHealthMap = mutableMapOf<UUID, Double>() // 이전 체력 상태
    private val activeTasks = mutableMapOf<UUID, BukkitRunnable>() // 플레이어별 활성화된 감지 태스크

    override fun missionStart(player: Player) {
        playerHealingMap[player.uniqueId] = 0.0
        lastHealthMap[player.uniqueId] = player.health
    }

    override fun checkEventSuccess(player: Player, event: Event): Boolean {
        if (event is PlayerInteractEvent) {
            val item = event.item ?: return false
            if (isHealingItem(item)) { // 특정 아이템인지 확인
                activateHealingDetection(player)
                return false
            }
        }
        return false
    }

    private fun isHealingItem(item: ItemStack): Boolean {
        // 커스텀 태그 확인
        if (item.type != org.bukkit.Material.MOJANG_BANNER_PATTERN) return false
        val meta = item.itemMeta ?: return false
        val mapKey = NamespacedKey(plugin, "map") // 커스텀 태그 키
        val data = meta.persistentDataContainer
        return data.has(mapKey, PersistentDataType.BYTE) // 해당 태그가 있으면 true 반환
    }

    private fun activateHealingDetection(player: Player) {
        if (activeTasks.containsKey(player.uniqueId)) return // 이미 활성화된 플레이어는 무시

        lastHealthMap[player.uniqueId] = player.health

        // 1틱 간격으로 3분 동안 감지
        val task = object : BukkitRunnable() {
            private var ticks = 0
            override fun run() {
                if (ticks++ >= 3 * 60 * 20) { // 3분 경과 시 종료
                    activeTasks.remove(player.uniqueId)
                    cancel()
                    return
                }

                val currentHealth = player.health
                val lastHealth = lastHealthMap.getOrDefault(player.uniqueId, currentHealth)
                if (currentHealth > lastHealth) { // 체력 증가만 확인
                    val healingAmount = currentHealth - lastHealth
                    addHealing(player, healingAmount)
                }
                lastHealthMap[player.uniqueId] = currentHealth // 체력 갱신
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)
        activeTasks[player.uniqueId] = task // 플레이어별 태스크 저장
    }

    private fun addHealing(player: Player, amount: Double) {
        val currentHealing = playerHealingMap.getOrDefault(player.uniqueId, 0.0) + amount
        playerHealingMap[player.uniqueId] = currentHealing
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            TextComponent("§b누적 회복량: §e$currentHealing§b / §d$requiredHealing")
        )
        if (currentHealing >= requiredHealing) {
            onSuccess(player)
        }
    }

    override fun onSuccess(player: Player) {
        player.addScoreboardTag("MissionSuccess")
        playerHealingMap.remove(player.uniqueId)
        lastHealthMap.remove(player.uniqueId)
        cancelTask(player)
    }

    override fun reset() {
        playerHealingMap.clear()
        lastHealthMap.clear()
        // 모든 태스크 정리
        activeTasks.values.forEach { it.cancel() }
        activeTasks.clear()
    }

    private fun cancelTask(player: Player) {
        activeTasks[player.uniqueId]?.cancel()
        activeTasks.remove(player.uniqueId)
    }
}
