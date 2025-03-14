package kr.rion.plugin.mission.missions

import kr.rion.plugin.mission.Mission
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class CumulativeHealingMission(
    private val requiredHealing: Double,
    private val plugin: JavaPlugin
) : Mission {
    private val playerHealingMap = mutableMapOf<UUID, Double>() // 누적 회복량
    private val lastHealthMap = mutableMapOf<UUID, Double>() // 이전 체력 상태
    private val activeTasks = mutableMapOf<UUID, BukkitRunnable>() // 플레이어별 활성화된 감지 태스크

    override fun missionStart(player: Player) {
        val uuid = player.uniqueId
        playerHealingMap[uuid] = 0.0
        lastHealthMap[uuid] = player.health
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
        val meta = item.itemMeta ?: return false
        val data = meta.persistentDataContainer

        // 아이템별 커스텀 태그 키 정의
        val validItems = mapOf(
            Material.NETHER_BRICK to "gips",
            Material.PAPER to "heal",
            Material.GLOW_BERRIES to "berries"
        )

        // 아이템이 지정된 목록에 포함되지 않으면 false 반환
        val tagKey = validItems[item.type] ?: return false

        // 지정된 태그 확인
        val namespacedKey = NamespacedKey(plugin, tagKey)
        return data.has(namespacedKey, PersistentDataType.STRING)
    }

    private fun activateHealingDetection(player: Player) {
        val uuid = player.uniqueId
        if (activeTasks.containsKey(uuid)) return // 이미 활성화된 플레이어는 무시

        lastHealthMap[uuid] = player.health

        // 1틱 간격으로 3분 동안 감지
        val task = object : BukkitRunnable() {
            private var ticks = 0
            override fun run() {
                if (ticks++ >= 3 * 60 * 20) { // 3분 경과 시 종료
                    activeTasks.remove(uuid)
                    cancel()
                    return
                }

                val currentHealth = player.health
                val lastHealth = lastHealthMap.getOrDefault(uuid, currentHealth)
                if (currentHealth > lastHealth) { // 체력 증가만 확인
                    val healingAmount = currentHealth - lastHealth
                    addHealing(player, healingAmount)
                }
                lastHealthMap[uuid] = currentHealth // 체력 갱신
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)
        activeTasks[uuid] = task // 플레이어별 태스크 저장
    }

    private fun addHealing(player: Player, amount: Double) {
        val uuid = player.uniqueId
        val currentHealing = playerHealingMap.getOrDefault(uuid, 0.0) + amount
        playerHealingMap[uuid] = currentHealing
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            TextComponent("§b누적 회복량: §e$currentHealing§b / §d$requiredHealing")
        )
        if (currentHealing >= requiredHealing) {
            onSuccess(player)
        }
    }

    override fun onSuccess(player: Player) {
        val uuid = player.uniqueId
        player.addScoreboardTag("MissionSuccess")
        playerHealingMap.remove(uuid)
        lastHealthMap.remove(uuid)
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
        val uuid = player.uniqueId
        activeTasks[uuid]?.cancel()
        activeTasks.remove(uuid)
    }
}
