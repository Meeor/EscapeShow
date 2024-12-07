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
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class EscapePlayerEvent : Listener {
    //탈출한 플레이어가 생존자 때리는것을 방지
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 받는 모든데미지 무시
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 상자아이템 훔치는것을 방지.
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        // 나침반 GUI와는 상관없도록 예외 처리
        if (event.view.title == "${ChatColor.DARK_GREEN}텔레포트할 플레이어 선택") {
            return // 텔레포트 GUI는 그대로 작동
        }
        // 조건 확인: 어드벤처 모드이고 EscapeComplete 태그가 있는 경우
        if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
            // 상자 아이템 가져가기를 방지
            if (event.clickedInventory?.type in listOf(
                    InventoryType.CHEST,
                    InventoryType.BARREL,
                    InventoryType.SHULKER_BOX
                )
            ) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 아이템 떨구는것을 방지
    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player

        if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
            event.isCancelled = true
        }
    }

    //탈출한 플레이어가 떨어진 아이템 먹는것을 방지
    @EventHandler
    fun onPlayerPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return

        if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
            event.isCancelled = true
        }
    }

    //탈출 플레이어가 텔레포트 나침반을 제외한 모든상호작용을 못하게 막기
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        // 탈출 플레이어인지 확인
        if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
            val item = player.inventory.itemInMainHand

            // 텔레포트 나침반인지 확인
            if (item.type == Material.RECOVERY_COMPASS) {
                val meta = item.itemMeta
                if (meta != null && hasCustomTag(meta, "teleport")) {
                    return // 텔레포트 나침반은 상호작용 허용
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