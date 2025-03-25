package kr.rion.plugin.mission

import kr.rion.plugin.Loader
import kr.rion.plugin.mission.missions.*
import org.bukkit.Material

object MissionList {

    // 모든 미션 등록
    fun registerAll() {
        MissionRegistry.registerMission(1, OpenChestMission(100)) //inventoryOpenEvent
        MissionRegistry.registerMission(2, OpenChestMission(200)) //inventoryOpenEvent
        MissionRegistry.registerMission(3, ItemCollectMission(Material.WOODEN_SWORD, 5)) //inventoryCloseEvent
        MissionRegistry.registerMission(4, IronMeltMission(15)) //IronMeltEvent(CustomEvent)
        MissionRegistry.registerMission(5, ItemCollectMission(Material.SWEET_BERRIES, 20)) //inventoryCloseEvent
        MissionRegistry.registerMission(6, WeaponAllCollect()) //inventoryCloseEvent
        MissionRegistry.registerMission(7, PlacePlanksMission(20)) //BlockPlaceEvent
        MissionRegistry.registerMission(8, PlayerKillMission(1)) //PlayerDeathEvent
        MissionRegistry.registerMission(9, PlayerKillMission(2)) //PlayerDeathEvent
        MissionRegistry.registerMission(10, PlayerKillMission(3)) //PlayerDeathEvent
        MissionRegistry.registerMission(11, KillWithSpecificItemMission(Material.AIR)) //PlayerDeathEvent
        MissionRegistry.registerMission(12, KillWithSpecificItemMission(Material.FIREWORK_STAR)) //PlayerDeathEvent
        MissionRegistry.registerMission(13, CumulativeDamageMission(40.0)) //EntityDamageByEntityEvent
        MissionRegistry.registerMission(
            14,
            CraftingMission(listOf(Material.IRON_CHESTPLATE to 1))
        ) //inventoryClickEvent
        MissionRegistry.registerMission(
            15,
            CraftingMission(listOf(Material.DIAMOND_CHESTPLATE to 1))
        ) //inventoryClickEvent
        MissionRegistry.registerMission(16, ReviveOnceMission())//RevivalEvent(CustomEvent)
        MissionRegistry.registerMission(17, StealItemsMission())//RevivalEvent(CustomEvent)
        MissionRegistry.registerMission(18, ItemCollectMission(Material.LEATHER, 30)) //inventoryCloseEvent
        MissionRegistry.registerMission(19, CumulativeHealingMission(20.0, Loader.instance)) //PlayerInteractEvent
        MissionRegistry.registerMission(20, ItemUsageMission(Material.SKULL_BANNER_PATTERN, 3)) //PlayerInteractEvent
        MissionRegistry.registerMission(21, ItemUsageMission(Material.MOJANG_BANNER_PATTERN, 3)) //PlayerInteractEvent
        MissionRegistry.registerMission(22, ItemUsageMission(Material.NETHER_BRICK, 5)) //PlayerInteractEvent
        MissionRegistry.registerMission(23, ItemUsageMission(Material.PAPER, 10)) //PlayerInteractEvent
        MissionRegistry.registerMission(24, ItemUsageMission(Material.PAPER, 15)) //PlayerInteractEvent
        MissionRegistry.registerMission(25, ItemUsageMission(Material.PAPER, 20)) //PlayerInteractEvent
        MissionRegistry.registerMission(26, CumulativeDamageTakenMission(20.0)) //EntityDamageByEntityEvent
        MissionRegistry.registerMission(27, DamageFreeMission(600, Loader.instance))
        MissionRegistry.registerMission(28, LastSurvivorMission())

    }
}
