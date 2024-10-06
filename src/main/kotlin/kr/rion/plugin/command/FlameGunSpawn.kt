package kr.rion.plugin.command

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import kr.rion.plugin.util.global.prefix
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object FlameGunSpawn {
    var chestLocation: Location? = null // 플레어건 상자의 위치 저장
    var particleTask: BukkitTask? = null // 파티클 반복 작업 저장

    // 플레어건 상자 생성 함수
    fun spawnFlareGunChest(sender: CommandSender, location: Location) {
        if(chestLocation !== null){
            sender.sendMessage("$prefix 이미 플레어건이 소환되어있는것 같습니다.")
            return
        }
        chestLocation = location // 상자 위치 저장
        location.block.type = Material.CHEST
        val chest = location.block.state as Chest

        // 플레어건을 상자의 14번 칸(정중앙)에 배치
        chest.inventory.setItem(13, createFlareGunItem()) // 인덱스는 0부터 시작하므로 13번이 14번째 칸

        chest.update()

        // 파티클을 상자 위아래로 y 좌표 50칸씩 늘려서 반복적으로 소환
        particleTask = object : BukkitRunnable() {
            override fun run() {
                if (location.block.type == Material.CHEST) {
                    val world = location.world
                    // 상자 기준으로 y 좌표 -50칸에서 +50칸까지 파티클 소환
                    for (yOffset in -50..50) {
                        val particleLocation = location.clone().add(0.5, yOffset.toDouble(), 0.5)
                        world.spawnParticle(Particle.END_ROD, particleLocation, 5)
                    }
                } else {
                    cancel() // 상자가 아니면 반복 종료
                }
            }
        }.runTaskTimer(Loader.instance, 0, 20) // 1초마다 반복
        Bukkit.broadcastMessage("$prefix 플레어건이 나타났습니다 (${location.blockX}, ${location.blockY}, ${location.blockZ})")
        Bukkit.getOnlinePlayers().forEach { player ->
        player.sendTitle("${ChatColor.RED}플레어건${ChatColor.YELLOW}이 등장하였습니다!","${ChatColor.GOLD}좌표 : ${ChatColor.AQUA}X :${location.blockX}, Y : ${location.blockY}, Z : ${location.blockZ}",10,20*60,10)
        }

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