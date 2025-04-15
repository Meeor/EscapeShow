package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End.MissionSuccessPlayers
import kr.rion.plugin.gameEvent.FlameGunSpawn.chestLocation
import kr.rion.plugin.gameEvent.FlameGunSpawn.particleTask
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import kr.rion.plugin.util.Global.missionclearSoleAction
import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockState
import org.bukkit.block.Container
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class InventoryCloseEvent : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory
        val holder = inventory.holder

        // 닫힌 상자가 플레어건 상자와 일치하지 않으면 리턴

        if (chestLocation == null || holder !is BlockState || holder.block.location != chestLocation) {
            return
        }

        // 인벤토리가 비었는지 확인
        if (inventory.isEmpty) {
            chestLocation!!.block.type = Material.AIR // 상자 제거
            particleTask?.cancel() // 파티클 반복 종료
            chestLocation = null // 위치 초기화
            Bukkit.broadcastMessage("$prefix 누군가가 플레어건을 획득했습니다!")
            bossbarEnable = 0
            for (player in Bukkit.getOnlinePlayers()) {
                removeDirectionBossBar(player)
            }
        }
    }

    @EventHandler
    fun missionClearCheck(event: InventoryCloseEvent) {
        val holder = event.inventory.holder as? Container ?: return
        val loc = holder.location
        val player = event.player as Player

        // 이미 성공한 플레이어면 리턴
        if (MissionSuccessPlayers.contains(player.name)) return

        val targetLocations = listOf(
            Location(Bukkit.getWorld("game"), 227.0, 51.0, -669.0),
            Location(Bukkit.getWorld("game"), -19.0, 61.0, -460.0),
            Location(Bukkit.getWorld("game"), -38.0, 73.0, -13.0),
            Location(Bukkit.getWorld("game"), 317.0, 57.0, -213.0)
        )

        // 좌표 일치하는지 체크
        if (targetLocations.none { it == loc }) return

        if (!isMissionEscapePaperExists(holder.inventory)) return

        missionclearSoleAction(player)
    }



    fun isMissionEscapePaperExists(inventory: Inventory): Boolean {
        return inventory.contents.any { isMissionEscapePaper(it) }
    }

    // 미션 탈출 종이인지 체크 (PDC만)
    fun isMissionEscapePaper(item: ItemStack?): Boolean {
        if (item == null || item.type != Material.PAPER) return false

        val meta = item.itemMeta ?: return false
        val key = NamespacedKey(Loader.instance, "mission_escape_paper")

        return meta.persistentDataContainer.has(key, PersistentDataType.BOOLEAN)
    }


}