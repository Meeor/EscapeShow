package kr.rion.plugin.command

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object FlameGunSpawn {
    private var chestLocation: Location? = null // 플레어건 상자의 위치 저장
    private var particleTask: BukkitTask? = null // 파티클 반복 작업 저장

    // 플레어건 상자 생성 함수
    fun spawnFlareGunChest(location: Location) {
        chestLocation = location // 상자 위치 저장
        location.block.type = Material.CHEST
        val chest = location.block.state as Chest

        // 플레어건을 상자의 14번 칸(정중앙)에 배치
        chest.inventory.setItem(13, createFlareGunItem()) // 인덱스는 0부터 시작하므로 13번이 14번째 칸

        chest.update()

        // 파티클을 상자 위에 반복적으로 소환
        particleTask = object : BukkitRunnable() {
            override fun run() {
                if (location.block.type == Material.CHEST) {
                    location.world.spawnParticle(Particle.END_ROD, location.add(0.5, 1.0, 0.5), 5)
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(Loader.instance, 0, 20) // 1초마다 반복
        Bukkit.broadcastMessage("플레어건이 나타났습니다 (${location.blockX}, ${location.blockY}, ${location.blockZ})")
    }

    // 플레어건 아이템 생성 함수
    fun createFlareGunItem(): ItemStack {
        val item = ItemStack(Material.FLINT, 1)
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.RED}플레어건")
        item.itemMeta = meta
        // NBT 태그 추가
        val nbtItem = NBTItem(item)
        nbtItem.setBoolean("flamegun", true)
        item.itemMeta = nbtItem.item.itemMeta
        return item
    }
}