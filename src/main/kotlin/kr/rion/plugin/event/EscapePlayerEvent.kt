package kr.rion.plugin.event

import kr.rion.plugin.Loader
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class EscapePlayerEvent : Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager

        // 공격자가 플레이어인지 확인
        if (damager is Player && damager.gameMode == GameMode.ADVENTURE && listOf(
                "EscapeComplete",
                "DeathAndAlive"
            ).any { damager.scoreboardTags.contains(it) }
        ) {
            event.isCancelled = true
        }
    }


    //탈출한 플레이어가 받는 모든데미지 무시
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && listOf(
                    "EscapeComplete",
                    "DeathAndAlive"
                ).any { player.scoreboardTags.contains(it) }
            ) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 아이템 떨구는것을 방지
    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player

        if (player.gameMode == GameMode.ADVENTURE && listOf(
                "EscapeComplete",
                "DeathAndAlive"
            ).any { player.scoreboardTags.contains(it) }
        ) {
            event.isCancelled = true
        }
    }

    //탈출한 플레이어가 떨어진 아이템 먹는것을 방지
    @EventHandler
    fun onPlayerPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return

        if (player.gameMode == GameMode.ADVENTURE && listOf(
                "EscapeComplete",
                "DeathAndAlive"
            ).any { player.scoreboardTags.contains(it) }
        ) {
            event.isCancelled = true
        }
    }

    //탈출 플레이어가 텔레포트 나침반을 제외한 모든상호작용을 못하게 막기
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        // 탈출 플레이어인지 확인
        if (player.gameMode == GameMode.ADVENTURE && listOf(
                "EscapeComplete",
                "DeathAndAlive"
            ).any { player.scoreboardTags.contains(it) }
        ) {
            val item = player.inventory.itemInMainHand

            // 텔레포트 나침반인지 확인
            if (item.type == Material.RECOVERY_COMPASS) {
                val meta = item.itemMeta
                if (meta != null && hasCustomTag(meta, "teleport")) {
                    event.isCancelled = true // 나침반을 들고 있어도 블록 상호작용 차단
                    player.sendMessage("${ChatColor.RED}나침반을 들고 블록을 클릭할 수 없습니다.")
                    return
                }
            }
            // 나침반이 아니면 모든 상호작용 차단
            event.isCancelled = true
        }
    }

    // 태그가 있는지 확인하는 함수
    private fun hasCustomTag(meta: ItemMeta, tag: String): Boolean {
        val key = NamespacedKey(Loader.instance, tag)
        return meta.persistentDataContainer.has(key, PersistentDataType.BOOLEAN)
    }

}