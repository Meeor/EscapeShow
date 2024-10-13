package kr.rion.plugin.util

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object Item {

    fun createCustomItem(
        itemName: String, // 필수: 아이템 이름
        itemLore: List<String>, // 필수: 아이템 로어 (리스트 형식)
        itemType: Material, // 필수: 아이템 종류 (Material)
        persistentDataKey: String? = null, // 선택적: PersistentDataContainer 키
        nbtKey: String? = null, // 선택적: NBT 태그 키
        nbtValue: Boolean? = null // 선택적: NBT 태그 값
    ): ItemStack {


        // 기본 아이템 생성, 갯수는 무조건 1개로 설정
        val item = ItemStack(itemType, 1)
        val meta: ItemMeta? = item.itemMeta

        // 아이템 이름 설정
        meta?.setDisplayName(itemName)

        // 아이템 로어 설정
        meta?.lore = itemLore

        // PersistentDataContainer에 태그 추가 (키와 값이 둘 다 있을 때만)
        if (persistentDataKey != null) {
            val namespacedKey = NamespacedKey(Loader.instance, persistentDataKey)
            meta?.persistentDataContainer?.set(namespacedKey, PersistentDataType.BOOLEAN, true)
        }

        // 아이템 메타 설정
        item.itemMeta = meta

        // NBT API를 사용해 NBT 태그를 추가 (키와 값이 둘 다 있을 때만)
        val nbtItem = NBTItem(item)
        if (nbtKey != null && nbtValue != null) {
            nbtItem.setBoolean(nbtKey, nbtValue)
        }

        // NBT 아이템을 최종적으로 ItemStack으로 변환하여 반환
        return nbtItem.item
    }

    //플레어건
    fun flamegun(): ItemStack {
        val flamegunName = "${ChatColor.RED}플레어건"
        val flamegunlore = listOf("", "")
        val item = createCustomItem(flamegunName, flamegunlore, Material.FLINT, nbtKey = "flamegun", nbtValue = true)
        return item
    }

    //붕대
    fun heal(): ItemStack {
        val healName = "${ChatColor.GREEN}붕대"
        val heallore = listOf("", "")
        val item = createCustomItem(healName, heallore, Material.PAPER, nbtKey = "heal", nbtValue = true)
        return item
    }

    //농축된 열매
    fun berries(): ItemStack {
        val berriesName = "${ChatColor.GREEN}농축된 열매"
        val berrieslore = listOf("", "")
        val item =
            createCustomItem(berriesName, berrieslore, Material.GLOW_BERRIES, nbtKey = "berries", nbtValue = true)
        return item
    }

    //계약서
    fun contract(): ItemStack {
        val contractName = "${ChatColor.of("#FF4242")}계약서"
        val contractlore = listOf("", "")
        val item = createCustomItem(
            contractName,
            contractlore,
            Material.SKULL_BANNER_PATTERN,
            nbtKey = "contract",
            nbtValue = true
        )
        return item
    }

    //지도
    fun map(): ItemStack {
        val mapName = "${ChatColor.GRAY}지도"
        val maplore = listOf("", "")
        val item = createCustomItem(mapName, maplore, Material.MOJANG_BANNER_PATTERN, nbtKey = "map", nbtValue = true)
        return item
    }

    fun teleportCompass(): ItemStack {
        val itemName = "${ChatColor.LIGHT_PURPLE}플레이어에게 텔레포트"
        val itemlore = listOf("${ChatColor.AQUA}관전용 플레이어 텔레포트 도구", "", "우클릭으로 사용가능합니다.")
        val item = createCustomItem(
            itemName,
            itemlore,
            Material.RECOVERY_COMPASS,
            persistentDataKey = "teleport"
        )
        return item
    }
    fun mainMenu():ItemStack{
        val itemName = "${ChatColor.AQUA}메뉴창 열기"
        val itemlore = listOf("${ChatColor.GREEN}메뉴창을 열수있는 아이템입니다.","","우클릭으로 사용가능합니다.")
        val item = createCustomItem(itemName,itemlore,Material.CLOCK, persistentDataKey = "mainmenu")
        return item
    }
}