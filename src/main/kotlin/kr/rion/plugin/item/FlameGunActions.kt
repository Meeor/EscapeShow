package kr.rion.plugin.item

import kr.rion.plugin.Loader
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
                            spawnHelicopter(initialLoc.clone().add(0.0,50.0,0.0))
                            removeHelicopter(initialLoc.clone().add(0.0,50.0,0.0))
                        }, 4 * 20L)
                        cancel()


                    }
                }
            }.runTaskTimer(Loader.instance, 0L, 1L)
        }
    }



    private fun spawnHelicopter(getloc: Location) {
        setBlockWithAttributes(setloc(getloc,1.0,0.0,2.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc,0.0,0.0,2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-1.0,0.0,2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-2.0,0.0,2.0), Material.ANDESITE)
        fillBlocks(setloc(getloc,-3.0,0.0,2.0), setloc(getloc,-6.0,0.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-7.0,0.0,2.0),Material.ANDESITE)
        setBlockWithAttributes(setloc(getloc,-8.0,0.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-9.0,0.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-10.0,0.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-11.0,0.0,2.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc,-2.0,1.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-7.0,1.0,2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-2.0,1.0,1.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.SOUTH)
        setBlockWithAttributes(setloc(getloc,-7.0,1.0,1.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.SOUTH)
        //
        setBlockWithAttributes(setloc(getloc,1.0,0.0,-2.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc,0.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-1.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-2.0,0.0,-2.0),Material.ANDESITE)
        fillBlocks(setloc(getloc,-3.0,0.0,-2.0),setloc(getloc,-6.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-7.0,0.0,-2.0),Material.ANDESITE)
        setBlockWithAttributes(setloc(getloc,-8.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-9.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-10.0,0.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-11.0,0.0,-2.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc,-2.0,1.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-7.0,1.0,-2.0),Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc,-2.0,1.0,-1.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.NORTH)
        setBlockWithAttributes(setloc(getloc,-7.0,1.0,-1.0),Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.NORTH)
        //
        fillBlocks(setloc(getloc,1.0,2.0,1.0),setloc(getloc,-10.0,2.0,-1.0),Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc,0.0,2.0,2.0),setloc(getloc,-10.0,2.0,2.0),Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        fillBlocks(setloc(getloc,0.0,2.0,-2.0),setloc(getloc,-10.0,2.0,-2.0),Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        fillBlocks(setloc(getloc,-11.0,2.0,1.0),setloc(getloc,-11.0,2.0,-1.0),Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        setBlockWithAttributes(setloc(getloc,2.0,2.0,0.0),Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc,3.0,2.0,0.0),Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        //
        fillBlocks(setloc(getloc,-11.0,3.0,1.0),setloc(getloc,-11.0,3.0,-1.0),Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc,1.0,3.0,2.0),setloc(getloc,-10.0,3.0,2.0),Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc,1.0,3.0,-2.0),setloc(getloc,-10.0,3.0,-2.0),Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc,2.0,3.0,-1.0),setloc(getloc,3.0,3.0,-1.0),Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc,2.0,3.0,1.0),setloc(getloc,3.0,3.0,1.0),Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc,4.0,3.0,0.0),Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        fillBlocks(setloc(getloc,11.0,3.0,0.0),setloc(getloc,13.0,3.0,0.0),Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        //이 뒤부턴 챗지피티가 코드형식에맟춰 바꿔준부분
        setBlockWithAttributes(setloc(getloc, -11.0, 4.0, 1.0), Material.BLUE_STAINED_GLASS)
        setBlockWithAttributes(setloc(getloc, -10.0, 4.0, 2.0), Material.BLUE_STAINED_GLASS)
        setBlockWithAttributes(setloc(getloc, -10.0, 4.0, -2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -8.0, 4.0, -2.0), setloc(getloc, -6.0, 4.0, -2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -8.0, 4.0, 2.0), setloc(getloc, -6.0, 4.0, 2.0), Material.POLISHED_DEEPSLATE)
        setBlockWithAttributes(setloc(getloc, -5.0, 4.0, 2.0), Material.BLUE_STAINED_GLASS)
        setBlockWithAttributes(setloc(getloc, -5.0, 4.0, -2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -2.0, 4.0, -2.0), setloc(getloc, 0.0, 4.0, -2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -2.0, 4.0, 2.0), setloc(getloc, 0.0, 4.0, 2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, 1.0, 4.0, -2.0), setloc(getloc, 3.0, 4.0, -2.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 1.0, 4.0, 2.0), setloc(getloc, 3.0, 4.0, 2.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 4.0, 4.0, 1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 4.0, 4.0, -1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 4.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, 10.0, 4.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 11.0, 4.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, 12.0, 4.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 13.0, 4.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 14.0, 4.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        //
        fillBlocks(setloc(getloc, -10.0, 5.0, 1.0), setloc(getloc, -10.0, 5.0, -1.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -9.0, 5.0, 2.0), setloc(getloc, -8.0, 5.0, 2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -9.0, 5.0, -2.0), setloc(getloc, -8.0, 5.0, -2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -7.0, 5.0, -2.0), setloc(getloc, -5.0, 5.0, -2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -7.0, 5.0, 2.0), setloc(getloc, -5.0, 5.0, 2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -4.0, 5.0, 2.0), setloc(getloc, -2.0, 5.0, 2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -4.0, 5.0, -2.0), setloc(getloc, -2.0, 5.0, -2.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -1.0, 5.0, -2.0), setloc(getloc, 2.0, 5.0, -2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -1.0, 5.0, 2.0), setloc(getloc, 2.0, 5.0, 2.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, 3.0, 5.0, 2.0), setloc(getloc, 4.0, 5.0, 2.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 3.0, 5.0, -2.0), setloc(getloc, 4.0, 5.0, -2.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 5.0, 1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 5.0, -1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 6.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        fillBlocks(setloc(getloc, 7.0, 5.0, 0.0), setloc(getloc, 8.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP)
        setBlockWithAttributes(setloc(getloc, 9.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 10.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 11.0, 5.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 12.0, 5.0, 0.0), Material.SMOOTH_STONE)
        setBlockWithAttributes(setloc(getloc, 13.0, 5.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, 14.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(setloc(getloc, -9.0, 6.0, 1.0), setloc(getloc, -8.0, 6.0, -1.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -7.0, 6.0, 1.0), setloc(getloc, 4.0, 6.0, -1.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, -7.0, 6.0, -2.0), setloc(getloc, 3.0, 6.0, -2.0), Material.POLISHED_DEEPSLATE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -7.0, 6.0, 2.0), setloc(getloc, 3.0, 6.0, 2.0), Material.POLISHED_DEEPSLATE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, 4.0, 6.0, 2.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, 4.0, 6.0, -2.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, -2.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.SOUTH)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 2.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.NORTH)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, -1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 0.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, 6.0, 6.0, 1.0), setloc(getloc, 6.0, 6.0, -1.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 7.0, 6.0, 0.0), setloc(getloc, 10.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 11.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, 12.0, 6.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, 13.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 14.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(setloc(getloc, -4.0, 7.0, 1.0), setloc(getloc, -2.0, 7.0, -1.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -4.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, -3.0, 7.0, 1.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.NORTH)
        setBlockWithAttributes(setloc(getloc, -3.0, 7.0, -1.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.SOUTH)
        setBlockWithAttributes(setloc(getloc, -2.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.WEST)
        setBlockWithAttributes(setloc(getloc, -3.0, 7.0, 0.0), Material.PISTON, pistonExtended = true, blockFace = BlockFace.UP)
        setBlockWithAttributes(setloc(getloc, -3.0, 8.0, 0.0), Material.PISTON_HEAD, blockFace = BlockFace.UP)
        setBlockWithAttributes(setloc(getloc, -3.0, 6.0, 0.0), Material.REDSTONE_BLOCK)
        setBlockWithAttributes(setloc(getloc, 10.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, 11.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, 12.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_STAIRS, stairsHalf = Half.BOTTOM, blockFace = BlockFace.EAST)
        setBlockWithAttributes(setloc(getloc, 13.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 14.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(setloc(getloc, -8.0, 9.0, 0.0), setloc(getloc, 2.0, 9.0, 0.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -3.0, 9.0, 5.0), setloc(getloc, -3.0, 9.0, -5.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, 1.0, 9.0, 1.0), setloc(getloc, 7.0, 9.0, 1.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, 5.0, 9.0, 2.0), setloc(getloc, 7.0, 9.0, 2.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -7.0, 9.0, -1.0), setloc(getloc, -13.0, 9.0, -1.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -11.0, 9.0, -2.0), setloc(getloc, -13.0, 9.0, -2.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -4.0, 9.0, 4.0), setloc(getloc, -4.0, 9.0, 10.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -5.0, 9.0, 8.0), setloc(getloc, -5.0, 9.0, 10.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -2.0, 9.0, -4.0), setloc(getloc, -2.0, 9.0, -10.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        fillBlocks(setloc(getloc, -1.0, 9.0, -8.0), setloc(getloc, -1.0, 9.0, -10.0), Material.SMOOTH_STONE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -3.0, 9.0, 0.0), Material.POLISHED_BLACKSTONE)
    }
    private fun removeHelicopter(getloc: Location) {
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            // 블럭을 공기로 변경
            setBlockWithAttributes(setloc(getloc, 1.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 0.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -1.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 0.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, -3.0, 0.0, 2.0), setloc(getloc, -6.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -8.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -9.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -10.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -11.0, 0.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 1.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 1.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 1.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 1.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 1.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 0.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -1.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 0.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -3.0, 0.0, -2.0), setloc(getloc, -6.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -8.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -9.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -10.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -11.0, 0.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 1.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 1.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 1.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -7.0, 1.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 2.0, 1.0), setloc(getloc, -10.0, 2.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, 0.0, 2.0, 2.0), setloc(getloc, -10.0, 2.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, 0.0, 2.0, -2.0), setloc(getloc, -10.0, 2.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -11.0, 2.0, 1.0), setloc(getloc, -11.0, 2.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 2.0, 2.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 3.0, 2.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -11.0, 3.0, 1.0), setloc(getloc, -11.0, 3.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 3.0, 2.0), setloc(getloc, -10.0, 3.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 3.0, -2.0), setloc(getloc, -10.0, 3.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, 2.0, 3.0, -1.0), setloc(getloc, 3.0, 3.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, 2.0, 3.0, 1.0), setloc(getloc, 3.0, 3.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 4.0, 3.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, 11.0, 3.0, 0.0), setloc(getloc, 13.0, 3.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -11.0, 4.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -10.0, 4.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -10.0, 4.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -8.0, 4.0, -2.0), setloc(getloc, -6.0, 4.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -8.0, 4.0, 2.0), setloc(getloc, -6.0, 4.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -5.0, 4.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -5.0, 4.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -2.0, 4.0, -2.0), setloc(getloc, 0.0, 4.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -2.0, 4.0, 2.0), setloc(getloc, 0.0, 4.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 4.0, -2.0), setloc(getloc, 3.0, 4.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 4.0, 2.0), setloc(getloc, 3.0, 4.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 4.0, 4.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 4.0, 4.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 4.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 10.0, 4.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 11.0, 4.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 12.0, 4.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 13.0, 4.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 14.0, 4.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -10.0, 5.0, 1.0), setloc(getloc, -10.0, 5.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, -9.0, 5.0, 2.0), setloc(getloc, -8.0, 5.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, -9.0, 5.0, -2.0), setloc(getloc, -8.0, 5.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 5.0, -2.0), setloc(getloc, -5.0, 5.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 5.0, 2.0), setloc(getloc, -5.0, 5.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, -4.0, 5.0, 2.0), setloc(getloc, -2.0, 5.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, -4.0, 5.0, -2.0), setloc(getloc, -2.0, 5.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -1.0, 5.0, -2.0), setloc(getloc, 2.0, 5.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -1.0, 5.0, 2.0), setloc(getloc, 2.0, 5.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, 3.0, 5.0, 2.0), setloc(getloc, 4.0, 5.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, 3.0, 5.0, -2.0), setloc(getloc, 4.0, 5.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 5.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 5.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 6.0, 5.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, 7.0, 5.0, 0.0), setloc(getloc, 8.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 9.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 10.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 11.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 12.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 13.0, 5.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 14.0, 5.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -9.0, 6.0, 1.0), setloc(getloc, -8.0, 6.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 6.0, 1.0), setloc(getloc, 4.0, 6.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 6.0, -2.0), setloc(getloc, 3.0, 6.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 6.0, 2.0), setloc(getloc, 3.0, 6.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 4.0, 6.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 4.0, 6.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 6.0, -2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 2.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 6.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, 6.0, 6.0, 1.0), setloc(getloc, 6.0, 6.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, 7.0, 6.0, 0.0), setloc(getloc, 10.0, 6.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 11.0, 6.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 12.0, 6.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 13.0, 6.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 14.0, 6.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -4.0, 7.0, 1.0), setloc(getloc, -2.0, 7.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -4.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 7.0, 1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 7.0, -1.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -2.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 8.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 6.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 10.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 11.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 12.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 13.0, 7.0, 0.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, 14.0, 7.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -8.0, 9.0, 0.0), setloc(getloc, 2.0, 9.0, 0.0), Material.AIR)
            fillBlocks(setloc(getloc, -3.0, 9.0, 5.0), setloc(getloc, -3.0, 9.0, -5.0), Material.AIR)
            fillBlocks(setloc(getloc, 1.0, 9.0, 1.0), setloc(getloc, 7.0, 9.0, 1.0), Material.AIR)
            fillBlocks(setloc(getloc, 5.0, 9.0, 2.0), setloc(getloc, 7.0, 9.0, 2.0), Material.AIR)
            fillBlocks(setloc(getloc, -7.0, 9.0, -1.0), setloc(getloc, -13.0, 9.0, -1.0), Material.AIR)
            fillBlocks(setloc(getloc, -11.0, 9.0, -2.0), setloc(getloc, -13.0, 9.0, -2.0), Material.AIR)
            fillBlocks(setloc(getloc, -4.0, 9.0, 4.0), setloc(getloc, -4.0, 9.0, 10.0), Material.AIR)
            fillBlocks(setloc(getloc, -5.0, 9.0, 8.0), setloc(getloc, -5.0, 9.0, 10.0), Material.AIR)
            fillBlocks(setloc(getloc, -2.0, 9.0, -4.0), setloc(getloc, -2.0, 9.0, -10.0), Material.AIR)
            fillBlocks(setloc(getloc, -1.0, 9.0, -8.0), setloc(getloc, -1.0, 9.0, -10.0), Material.AIR)
            setBlockWithAttributes(setloc(getloc, -3.0, 9.0, 0.0), Material.AIR)
        }, 20L * 300) // 300초 후 (5분 후 실행)
    }


    fun setloc(base: Location, x: Double, y: Double, z: Double): Location {
        return base.clone().add(x, y, z)
    }

    fun setBlockWithAttributes(
        location: Location,
        material: Material,
        slabType: Slab.Type? = null,
        stairsHalf: Half? = null,
        blockFace: BlockFace? = null,
        pistonExtended: Boolean? = null
    ) {

        val block = location.block
        block.type = material

        val blockData = block.blockData

        when {
            // 슬랩 처리
            blockData is Slab && slabType != null -> {
                blockData.type = slabType
            }
            // 계단 처리
            blockData is Stairs && stairsHalf != null && blockFace != null -> {
                blockData.half = stairsHalf  // 계단의 상단/하단 설정
                blockData.facing = blockFace  // 계단의 방향 설정
            }
            // 피스톤 처리
            blockData is Piston && pistonExtended != null && blockFace != null -> {
                blockData.isExtended = pistonExtended  // 피스톤의 확장 여부 설정
                blockData.facing = blockFace  // 피스톤의 방향 설정
            }
        }

        block.blockData = blockData  // 마지막에 blockData 한 번만 설정
    }

    // fillBlocks 함수를 Location 객체로 좌표를 받도록 수정
    fun fillBlocks(
        startLocation: Location,   // 시작 좌표
        endLocation: Location,     // 끝 좌표
        material: Material,
        slabType: Slab.Type? = null,
        stairsHalf: Half? = null,   // 계단의 상하 위치 설정
        blockFace: BlockFace? = null,        // 계단이나 피스톤의 방향 설정
        pistonExtended: Boolean? = null      // 피스톤의 확장 여부 속성 추가
    ) {
        val world = startLocation.world

        // 좌표 범위를 설정
        val xMin = minOf(startLocation.blockX, endLocation.blockX)
        val xMax = maxOf(startLocation.blockX, endLocation.blockX)
        val yMin = minOf(startLocation.blockY, endLocation.blockY)
        val yMax = maxOf(startLocation.blockY, endLocation.blockY)
        val zMin = minOf(startLocation.blockZ, endLocation.blockZ)
        val zMax = maxOf(startLocation.blockZ, endLocation.blockZ)

        // 블록을 범위 내에 채우기
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                for (z in zMin..zMax) {
                    val blockLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                    setBlockWithAttributes(blockLocation, material, slabType, stairsHalf, blockFace, pistonExtended)
                }
            }
        }
    }
}