package kr.rion.plugin.util

import kr.rion.plugin.Loader
import kr.rion.plugin.item.FlameGunActions.flaregunstart
import kr.rion.plugin.item.FlameGunActions.startEscape
import kr.rion.plugin.util.Bossbar.bossbarEnable
import kr.rion.plugin.util.Bossbar.createDirectionBossBarForAll
import kr.rion.plugin.util.Bossbar.removeDirectionBossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected.Half
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Piston
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs

object Helicopter {
    var HelicopterLoc: Location? = null
    var HelicopterisSpawn = false
    private val soundName = "custom.hellicop"
    fun spawn(getloc: Location) {
        if (HelicopterisSpawn) return
        HelicopterLoc = getloc
        setBlockWithAttributes(
            setloc(getloc, 1.0, 0.0, 2.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(setloc(getloc, 0.0, 0.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -1.0, 0.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -2.0, 0.0, 2.0), Material.ANDESITE)
        fillBlocks(
            setloc(getloc, -3.0, 0.0, 2.0),
            setloc(getloc, -6.0, 0.0, 2.0),
            Material.ANDESITE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(setloc(getloc, -7.0, 0.0, 2.0), Material.ANDESITE)
        setBlockWithAttributes(setloc(getloc, -8.0, 0.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -9.0, 0.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -10.0, 0.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(
            setloc(getloc, -11.0, 0.0, 2.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, -2.0, 1.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -7.0, 1.0, 2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(
            setloc(getloc, -2.0, 1.0, 1.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.SOUTH
        )
        setBlockWithAttributes(
            setloc(getloc, -7.0, 1.0, 1.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.SOUTH
        )
        //
        setBlockWithAttributes(
            setloc(getloc, 1.0, 0.0, -2.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(setloc(getloc, 0.0, 0.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -1.0, 0.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -2.0, 0.0, -2.0), Material.ANDESITE)
        fillBlocks(
            setloc(getloc, -3.0, 0.0, -2.0),
            setloc(getloc, -6.0, 0.0, -2.0),
            Material.ANDESITE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(setloc(getloc, -7.0, 0.0, -2.0), Material.ANDESITE)
        setBlockWithAttributes(setloc(getloc, -8.0, 0.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -9.0, 0.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -10.0, 0.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(
            setloc(getloc, -11.0, 0.0, -2.0),
            Material.ANDESITE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, -2.0, 1.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(setloc(getloc, -7.0, 1.0, -2.0), Material.ANDESITE_SLAB, slabType = Slab.Type.BOTTOM)
        setBlockWithAttributes(
            setloc(getloc, -2.0, 1.0, -1.0),
            Material.ANDESITE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.NORTH
        )
        setBlockWithAttributes(
            setloc(getloc, -7.0, 1.0, -1.0),
            Material.ANDESITE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.NORTH
        )
        //
        fillBlocks(setloc(getloc, 1.0, 2.0, 1.0), setloc(getloc, -10.0, 2.0, -1.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(
            setloc(getloc, 0.0, 2.0, 2.0),
            setloc(getloc, -10.0, 2.0, 2.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.TOP
        )
        fillBlocks(
            setloc(getloc, 0.0, 2.0, -2.0),
            setloc(getloc, -10.0, 2.0, -2.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.TOP
        )
        fillBlocks(
            setloc(getloc, -11.0, 2.0, 1.0),
            setloc(getloc, -11.0, 2.0, -1.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.TOP
        )
        setBlockWithAttributes(
            setloc(getloc, 2.0, 2.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, 3.0, 2.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.TOP
        )
        //
        fillBlocks(setloc(getloc, -11.0, 3.0, 1.0), setloc(getloc, -11.0, 3.0, -1.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 1.0, 3.0, 2.0), setloc(getloc, -10.0, 3.0, 2.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 1.0, 3.0, -2.0), setloc(getloc, -10.0, 3.0, -2.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 2.0, 3.0, -1.0), setloc(getloc, 3.0, 3.0, -1.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 2.0, 3.0, 1.0), setloc(getloc, 3.0, 3.0, 1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(
            setloc(getloc, 4.0, 3.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
        fillBlocks(
            setloc(getloc, 11.0, 3.0, 0.0),
            setloc(getloc, 13.0, 3.0, 0.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.TOP
        )
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
        setBlockWithAttributes(
            setloc(getloc, 5.0, 4.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, 10.0, 4.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(
            setloc(getloc, 11.0, 4.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, 12.0, 4.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(
            setloc(getloc, 13.0, 4.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(
            setloc(getloc, 14.0, 4.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
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
        setBlockWithAttributes(
            setloc(getloc, 6.0, 5.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
        fillBlocks(
            setloc(getloc, 7.0, 5.0, 0.0),
            setloc(getloc, 8.0, 5.0, 0.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.TOP
        )
        setBlockWithAttributes(
            setloc(getloc, 9.0, 5.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, 10.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(
            setloc(getloc, 11.0, 5.0, 0.0),
            Material.ANDESITE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, 12.0, 5.0, 0.0), Material.SMOOTH_STONE)
        setBlockWithAttributes(
            setloc(getloc, 13.0, 5.0, 0.0), Material.ANDESITE_STAIRS, stairsHalf = Half.TOP, blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(setloc(getloc, 14.0, 5.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(setloc(getloc, -9.0, 6.0, 1.0), setloc(getloc, -8.0, 6.0, -1.0), Material.BLUE_STAINED_GLASS)
        fillBlocks(setloc(getloc, -7.0, 6.0, 1.0), setloc(getloc, 4.0, 6.0, -1.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(
            setloc(getloc, -7.0, 6.0, -2.0),
            setloc(getloc, 3.0, 6.0, -2.0),
            Material.POLISHED_DEEPSLATE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -7.0, 6.0, 2.0),
            setloc(getloc, 3.0, 6.0, 2.0),
            Material.POLISHED_DEEPSLATE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, 4.0, 6.0, 2.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, 4.0, 6.0, -2.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, 5.0, 6.0, -2.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.SOUTH
        )
        setBlockWithAttributes(
            setloc(getloc, 5.0, 6.0, 2.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.NORTH
        )
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, -1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 1.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 5.0, 6.0, 0.0), Material.POLISHED_DEEPSLATE)
        fillBlocks(setloc(getloc, 6.0, 6.0, 1.0), setloc(getloc, 6.0, 6.0, -1.0), Material.POLISHED_BLACKSTONE)
        fillBlocks(setloc(getloc, 7.0, 6.0, 0.0), setloc(getloc, 10.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(
            setloc(getloc, 11.0, 6.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, 12.0, 6.0, 0.0),
            Material.ANDESITE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, 13.0, 6.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.TOP,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, 14.0, 6.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(
            setloc(getloc, -4.0, 7.0, 1.0),
            setloc(getloc, -2.0, 7.0, -1.0),
            Material.POLISHED_BLACKSTONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, -4.0, 7.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(
            setloc(getloc, -3.0, 7.0, 1.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.NORTH
        )
        setBlockWithAttributes(
            setloc(getloc, -3.0, 7.0, -1.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.SOUTH
        )
        setBlockWithAttributes(
            setloc(getloc, -2.0, 7.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.WEST
        )
        setBlockWithAttributes(
            setloc(getloc, -3.0, 7.0, 0.0), Material.PISTON, pistonExtended = true, blockFace = BlockFace.UP
        )
        setBlockWithAttributes(setloc(getloc, -3.0, 8.0, 0.0), Material.PISTON_HEAD, blockFace = BlockFace.UP)
        setBlockWithAttributes(setloc(getloc, -3.0, 6.0, 0.0), Material.REDSTONE_BLOCK)
        setBlockWithAttributes(
            setloc(getloc, 10.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, 11.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE_SLAB, slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(
            setloc(getloc, 12.0, 7.0, 0.0),
            Material.POLISHED_BLACKSTONE_STAIRS,
            stairsHalf = Half.BOTTOM,
            blockFace = BlockFace.EAST
        )
        setBlockWithAttributes(setloc(getloc, 13.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE)
        setBlockWithAttributes(setloc(getloc, 14.0, 7.0, 0.0), Material.POLISHED_BLACKSTONE)
        //
        fillBlocks(
            setloc(getloc, -8.0, 9.0, 0.0),
            setloc(getloc, 2.0, 9.0, 0.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -3.0, 9.0, 5.0),
            setloc(getloc, -3.0, 9.0, -5.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, 1.0, 9.0, 1.0),
            setloc(getloc, 7.0, 9.0, 1.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, 5.0, 9.0, 2.0),
            setloc(getloc, 7.0, 9.0, 2.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -7.0, 9.0, -1.0),
            setloc(getloc, -13.0, 9.0, -1.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -11.0, 9.0, -2.0),
            setloc(getloc, -13.0, 9.0, -2.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -4.0, 9.0, 4.0),
            setloc(getloc, -4.0, 9.0, 10.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -5.0, 9.0, 8.0),
            setloc(getloc, -5.0, 9.0, 10.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -2.0, 9.0, -4.0),
            setloc(getloc, -2.0, 9.0, -10.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        fillBlocks(
            setloc(getloc, -1.0, 9.0, -8.0),
            setloc(getloc, -1.0, 9.0, -10.0),
            Material.SMOOTH_STONE_SLAB,
            slabType = Slab.Type.BOTTOM
        )
        setBlockWithAttributes(setloc(getloc, -3.0, 9.0, 0.0), Material.POLISHED_BLACKSTONE)
        for (allplayer in Bukkit.getOnlinePlayers()) {
            allplayer.playSound(allplayer, soundName, SoundCategory.MASTER, 1.0f, 1.0f)
        }
        createDirectionBossBarForAll(HelicopterLoc!!.clone().add(0.0, -50.0, 0.0), "헬기 방향")
        bossbarEnable = 2 //헬기위치로 변경
        HelicopterisSpawn = true
    }

    fun remove() {
        if (HelicopterLoc == null) return
        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
            // 블럭을 공기로 변경
            HelicopterLoc?.let { getloc ->
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
            }
        }, 20L * 30) // 30초 후
        startEscape = false
        flaregunstart?.cancel()
        flaregunstart = null
        bossbarEnable = 0 //보스바 업데이트 종료
        for (player in Bukkit.getOnlinePlayers()) {
            removeDirectionBossBar(player)
        }
    }

    private fun setloc(base: Location, x: Double, y: Double, z: Double): Location {
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
            // 피스톤 머리 처리
            blockData is Directional && material == Material.PISTON_HEAD && blockFace != null -> {
                blockData.facing = blockFace  // 피스톤 머리의 방향 설정
            }
        }

        block.blockData = blockData
    }

    // fillBlocks 함수를 Location 객체로 좌표를 받도록 수정
    fun fillBlocks(
        startLocation: Location,   // 시작 좌표
        endLocation: Location,     // 끝 좌표
        material: Material, slabType: Slab.Type? = null, stairsHalf: Half? = null,   // 계단의 상하 위치 설정
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


