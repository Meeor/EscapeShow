package kr.rion.plugin.util

import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import kr.rion.plugin.util.ChatUtil.centerBookTextVertical
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
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

    //농축된 열매
    fun berries(): ItemStack {
        val berriesName = "${ChatColor.GREEN}농축된 열매"
        val berrieslore = listOf("", "")
        val item =
            createCustomItem(berriesName, berrieslore, Material.GLOW_BERRIES, nbtKey = "berries", nbtValue = true)
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

    fun mainMenu(): ItemStack {
        val itemName = "${ChatColor.AQUA}메뉴창 열기"
        val itemlore = listOf("${ChatColor.GREEN}메뉴창을 열수있는 아이템입니다.", "", "우클릭으로 사용가능합니다.")
        val item = createCustomItem(itemName, itemlore, Material.CLOCK, persistentDataKey = "mainmenu")
        return item
    }
    fun ItemGuideBook(): ItemStack {
        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.title = "조합법"
        meta.author = "주최자측"

        val pages = listOf(
            centerBookTextVertical(" §f☜ \n§0재킷 \n§0체력이 5칸 추가된다"),
            centerBookTextVertical(" §f☞ \n§0사슬갑옷 \n§0체력이 10칸 추가된다"),
            centerBookTextVertical(" §f↔ \n§0갑옷 \n§0체력이 15칸 추가된다"),
            centerBookTextVertical(" §f← \n§0강철갑옷 \n§0체력이 20칸 추가된다"),
            centerBookTextVertical(" §f↑ \n§0계약서 \n§0최대 체력이 줄고 데미지가 2배가 된다"),
            centerBookTextVertical(" §f↓ \n§0지도 \n§0주위 50블럭 이내의 사람이 모두 보인다"),
            centerBookTextVertical(" §f③ \n§0나무막대기 \n§0재료 아이템. 무기로도 쓸 수 있다"),
            centerBookTextVertical(" §f→ \n§0돌덩이 \n§0무기"),
            centerBookTextVertical(" §f⚅ \n§0단검 \n§0무기"),
            centerBookTextVertical(" §f⚄ \n§0사냥도끼 \n§0무기"),
            centerBookTextVertical(" §f⚃ \n§0마체테 \n§0무기"),
            centerBookTextVertical(" §f⚂ \n§0대형망치 \n§0무기"),
            centerBookTextVertical(" §f⚁ \n§0모닥불 \n§0설치할 수 있다"),
            centerBookTextVertical(" §f⚀ \n§0붕대 \n§0사용 후 3초 뒤 회복한다"),
            centerBookTextVertical(" §f♦ \n§0부목 \n§05초간 느려지지만 크게 회복한다"),
            centerBookTextVertical(" §f♥ \n§0판자더미 \n§0설치할 수 있다"),
            centerBookTextVertical(" §f♣ \n§0사슬 \n§0재료 아이템"),
            centerBookTextVertical(" §f♠ \n§0유리조각 \n§0재료 아이템"),
            centerBookTextVertical(" §f♬ \n§0잔해 \n§0재료 아이템"),
            centerBookTextVertical(" §f♪ \n§0판자 \n§0재료 아이템"),
            centerBookTextVertical(" §f♩ \n§0철 \n§0재료 아이템"),
            centerBookTextVertical(" §f♫ \n§0가죽 \n§0재료 아이템"),
            centerBookTextVertical(" §f⓪ \n§0농축된 열매 \n§0배고픔이 채워지며 조금씩 회복한다"),
            centerBookTextVertical(" §f① \n§0광석 \n§0모닥불로 구워 철을 얻을 수 있다"),
            centerBookTextVertical(" §f② \n§0플레어건 \n§03명까지 탈출할 수 있다")
        )
        meta.pages = pages
        book.itemMeta = meta
        return book
    }
}