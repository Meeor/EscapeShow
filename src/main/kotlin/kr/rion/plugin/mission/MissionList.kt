package kr.rion.plugin.mission

import kr.rion.plugin.mission.missions.*
import org.bukkit.Material

object MissionList {

    // 모든 미션 등록
    fun registerAll() {
        MissionRegistry.registerMission(1, OpenChestMission(50)) //inventoryOpenEvent
        MissionRegistry.registerMission(2, OpenChestMission(100)) //inventoryOpenEvent
        MissionRegistry.registerMission(3, ItemCollectMission(Material.STONE_HOE, 2)) //inventoryCloseEvent
        MissionRegistry.registerMission(4, ItemCollectMission(Material.IRON_INGOT, 30)) //inventoryCloseEvent
        MissionRegistry.registerMission(5, ItemCollectMission(Material.SWEET_BERRIES, 30)) //inventoryCloseEvent
        MissionRegistry.registerMission(6, WeaponAllCollect()) //inventoryCloseEvent
        MissionRegistry.registerMission(7, BreakPlanksMission(5)) //BlockBreakEvent
        MissionRegistry.registerMission(8, BreakPlanksMission(10)) //BlockBreakEvent
        MissionRegistry.registerMission(9, PlacePlanksMission(10)) //BlockPlaceEvent
        MissionRegistry.registerMission(10, PlayerKillMission(3)) //PlayerDeathEvent
        MissionRegistry.registerMission(11, PlayerKillMission(5)) //PlayerDeathEvent
        MissionRegistry.registerMission(12, PlayerKillMission(10)) //PlayerDeathEvent
        MissionRegistry.registerMission(13, KillWithSpecificItemMission(Material.AIR)) //PlayerDeathEvent
        MissionRegistry.registerMission(14, KillWithSpecificItemMission(Material.COBBLESTONE)) //PlayerDeathEvent
        MissionRegistry.registerMission(15, CumulativeDamageMission(20.0)) //EntityDamageByEntityEvent
        /*
        val test = CraftingMission(listOf(Material.NETHERITE_SCRAP to 5, Material.LEATHER_CHESTPLATE to 1))
        MissionRegistry.registerMission(16, test) //inventoryClickEvent
        */
    }
}
