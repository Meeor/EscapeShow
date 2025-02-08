package kr.rion.plugin.mission

import kr.rion.plugin.Loader
import kr.rion.plugin.mission.missions.*
import org.bukkit.Material

object MissionList {

    // 모든 미션 등록
    fun registerAll() {
        MissionRegistry.registerMission(1, OpenChestMission(50)) //inventoryOpenEvent
        MissionRegistry.registerMission(2, OpenChestMission(100)) //inventoryOpenEvent
        MissionRegistry.registerMission(3, ItemCollectMission(Material.WOODEN_SWORD, 2)) //inventoryCloseEvent
        MissionRegistry.registerMission(4, ItemCollectMission(Material.IRON_INGOT, 10)) //inventoryCloseEvent
        MissionRegistry.registerMission(5, ItemCollectMission(Material.SWEET_BERRIES, 10)) //inventoryCloseEvent
        MissionRegistry.registerMission(6, WeaponAllCollect()) //inventoryCloseEvent
        MissionRegistry.registerMission(7, BreakPlanksMission(5)) //BlockBreakEvent
        MissionRegistry.registerMission(8, BreakPlanksMission(10)) //BlockBreakEvent
        MissionRegistry.registerMission(9, PlacePlanksMission(10)) //BlockPlaceEvent
        MissionRegistry.registerMission(10, PlayerKillMission(1)) //PlayerDeathEvent
        MissionRegistry.registerMission(11, PlayerKillMission(2)) //PlayerDeathEvent
        MissionRegistry.registerMission(12, PlayerKillMission(3)) //PlayerDeathEvent
        MissionRegistry.registerMission(13, KillWithSpecificItemMission(Material.AIR)) //PlayerDeathEvent
        MissionRegistry.registerMission(14, KillWithSpecificItemMission(Material.COBBLESTONE)) //PlayerDeathEvent
        MissionRegistry.registerMission(15, CumulativeDamageMission(20.0)) //EntityDamageByEntityEvent
        MissionRegistry.registerMission(16, ItemCollectMission(Material.CHAINMAIL_CHESTPLATE, 1)) //inventoryCloseEvent
        MissionRegistry.registerMission(
            17,
            CraftingMission(listOf(Material.IRON_CHESTPLATE to 1))
        ) //inventoryClickEvent
        MissionRegistry.registerMission(
            18,
            CraftingMission(listOf(Material.DIAMOND_CHESTPLATE to 1))
        ) //inventoryClickEvent
        MissionRegistry.registerMission(19, ReviveOnceMission())
        MissionRegistry.registerMission(20, StealItemsMission())
        MissionRegistry.registerMission(21, ItemCollectMission(Material.LEATHER, 30)) //inventoryCloseEvent
        MissionRegistry.registerMission(22, CumulativeHealingMission(10.0, Loader.instance)) //PlayerInteractEvent
        MissionRegistry.registerMission(
            23,
            ItemUsageMission(Material.SKULL_BANNER_PATTERN, "contract", 1, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(
            24,
            ItemUsageMission(Material.MOJANG_BANNER_PATTERN, "map", 3, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(
            25,
            ItemUsageMission(Material.NETHER_BRICK, "gips", 1, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(
            26,
            ItemUsageMission(Material.PAPER, "heal", 10, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(
            27,
            ItemUsageMission(Material.PAPER, "heal", 15, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(
            28,
            ItemUsageMission(Material.PAPER, "heal", 20, Loader.instance)
        ) //PlayerInteractEvent
        MissionRegistry.registerMission(29, CumulativeDamageTakenMission(10.0)) //EntityDamageByEntityEvent
        MissionRegistry.registerMission(30, DamageFreeMission(600, Loader.instance))
        MissionRegistry.registerMission(31, LastSurvivorMission())

    }
}
