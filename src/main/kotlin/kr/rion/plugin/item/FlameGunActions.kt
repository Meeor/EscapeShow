package kr.rion.plugin.item

import kr.rion.plugin.Loader
import kr.rion.plugin.util.End
import kr.rion.plugin.util.Helicopter
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.Bisected.Half
import org.bukkit.block.data.type.Piston
import org.bukkit.block.data.type.Stairs
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object FlameGunActions {

    fun launchFlare(player: Player) {
        // 플레이어에게 사운드 재생
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.5f)
            playerall.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f)
        }

        val flamegun = NamespacedKey("EscapeShow", "flamegun")
        // 사용한 아이템의 메타데이터 변경
        val item = player.inventory.itemInMainHand
        val itemMeta = item.itemMeta ?: return
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.ITALIC}${ChatColor.STRIKETHROUGH}플레어건")
        itemMeta.persistentDataContainer.remove(flamegun)
        item.itemMeta = itemMeta


        val initialLoc = player.location.clone().add(0.0, 1.0, 0.0) // 플레이어 머리 위치 (약간 위로) 복제본 생성
        val particleData1 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f)
        val particleData2 = Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f)

        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd1 =
            "summon minecraft:armor_stand ${initialLoc.x} ${initialLoc.y} ${initialLoc.z} {Tags:[ArmorStandTags],Invisible:1b}" // 실행할 명령어
        val cmd2 = "function server_datapack:flare"
        val cmd3 = "function server_datapack:helicopter"
        Bukkit.dispatchCommand(console, cmd1)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            object : BukkitRunnable() {
                private var t = 0.0
                private val loc = initialLoc.clone()

                override fun run() {
                    t += 1
                    loc.add(0.0, 1.0, 0.0) // 매 틱마다 위치를 위로 이동
                    onlinePlayer.world.spawnParticle(
                        Particle.CLOUD,
                        loc,
                        2,
                        0.0,
                        0.6,
                        0.0,
                        0.0
                    )
                    onlinePlayer.world.spawnParticle(
                        Particle.FLAME,
                        loc,
                        2,
                        0.0,
                        0.6,
                        0.0,
                        0.0
                    )
                    // 80틱 작업 끝나면 중지
                    if (t > 120) {
                        val movedLoc = loc.clone().add(0.0, -5.0, 0.0)
                        onlinePlayer.world.spawnParticle(
                            Particle.REDSTONE,
                            movedLoc,
                            50,
                            1.0,
                            1.0,
                            1.0,
                            0.1,
                            particleData1,
                            true
                        )
                        onlinePlayer.world.spawnParticle(
                            Particle.REDSTONE,
                            loc,
                            50,
                            0.0,
                            0.6,
                            0.0,
                            0.1,
                            particleData2,
                            true
                        )
                        Bukkit.dispatchCommand(console, cmd2)
                        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                            if (End.EscapePlayerCount == 0) {
                                Helicopter.spawn(initialLoc.clone().add(0.0, 50.0, 0.0))
                            }
                        }, 4 * 20L)
                        cancel()


                    }
                }
            }.runTaskTimer(Loader.instance, 0L, 1L)
        }
    }

}