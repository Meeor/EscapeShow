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
        val flamegunlore = listOf("")
        val item = createCustomItem(flamegunName, flamegunlore, Material.FLINT, nbtKey = "flamegun", nbtValue = true)
        return item
    }

    //농축된 열매
    fun berries(): ItemStack {
        val berriesName = "${ChatColor.GREEN}농축된 열매"
        val berrieslore = listOf("")
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

    fun speedItem(): ItemStack {
        val itemName = "${ChatColor.GREEN}복숭아"
        val itemlore = listOf("")
        val item = createCustomItem(itemName, itemlore, Material.BEETROOT, nbtKey = "speed", nbtValue = true)
        return item
    }

    fun upgradeItem(): ItemStack {
        val itemName = "${ChatColor.GREEN}강화제"
        val itemlore = listOf("")
        val item = createCustomItem(itemName, itemlore, Material.PUFFERFISH, nbtKey = "upgrade", nbtValue = true)
        return item
    }

    // 미션 탈출 종이 생성 함수
    fun createMissionEscapePaper(): ItemStack {
        return createCustomItem(
            itemName = "§f미션 클리어 인증서",
            itemLore = listOf("§b 어딘가에 이종이를 넣으면 탈출 할 수 있다."),
            itemType = Material.PIGLIN_BANNER_PATTERN,
            persistentDataKey = "mission_escape_paper"
        )
    }

    fun ItemGuideBook(): ItemStack {
        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.title = "아이템 룰북"
        meta.author = "주최자측"

        val pages = listOf(
            centerBookTextVertical(" §f☜ §0재킷 §0가죽 + 가죽으로 만들 수 있다 입으면 추가체력 5가 추가된다"),
            centerBookTextVertical(" §f☞ §0사슬갑옷 §0비늘 + 비늘로 만들 수 있다 입으면 추가체력 10이 추가된다"),
            centerBookTextVertical(" §f↔ §0갑옷 §0사슬갑옷 + 철 4개로 만들 수 있다 입으면 추가체력 15가 추가된다"),
            centerBookTextVertical(" §f← §0강철갑옷 §0철덩어리 + 갑옷 으로 만들 수 있다 입으면 추가체력 20이 추가된다"),
            centerBookTextVertical(" §f↑ §0계약서 §0가죽 + 먹물로 만들 수 있다 사용시 10초동안 최대체력이 반 줄고 데미지가 2배 강해진다"),
            centerBookTextVertical(" §f↓ §0지도 §0가죽 + 유리조각으로 만들 수 있다 사용시 주변 50m 내에 모든 플레이어가 빛난다"),
            centerBookTextVertical(" §f③ §0나무막대기 §0재료로 자주 쓰이는 아이템이다. 약하지만 무기로도 쓸 수 있다"),
            centerBookTextVertical(" §f→ §0돌덩이 §0데미지가 4정도 되는 준수한 무기다"),
            centerBookTextVertical(" §f⚅ §0단검 §0데미지가 4지만 공격속도가 빠르다"),
            centerBookTextVertical(" §f⚄ §0사냥도끼 §04의 데미지와 보통의 공격속도를 가진 무기다"),
            centerBookTextVertical(" §f⚃ §0마체테 §05의 데미지와 평범한 공속을 가진 무기다"),
            centerBookTextVertical(" §f⚂ §0대형망치 §0공속이 느리지만 7의 데미지를 줄 수 있다"),
            centerBookTextVertical(" §f⚁ §0모닥불 §0바닥에 설치할 수 있다 모닥불에서 우클릭으로 광석을 구울 수 있다"),
            centerBookTextVertical(" §f⚀ §0붕대 §0사용시 3초후 피가 5가찬다 추가체력은 채워지지않는다"),
            centerBookTextVertical(" §f♦ §0부목 §05초동안 느려지지만 피 5를 회복한다 추가체력도 회복할 수 있다"),
            centerBookTextVertical(" §f♥ §0판자더미 §0판자 + 판자로 만들 수 있다 설치 할 수 있으며 도끼와 망치로 부술 수 있다"),
            centerBookTextVertical(" §f♣ §0비늘 §0물가 근처만 나온다 재료로 쓸 수 있다"),
            centerBookTextVertical(" §f♠ §0유리조각 §0재료아이템으로 쓸 수 있다"),
            centerBookTextVertical(" §f♬ §0잔해 §0부서진 판자이다"),
            centerBookTextVertical(" §f♪ §0판자 §0잔해 + 잔해로 만들 수 있다"),
            centerBookTextVertical(" §f♩ §0철 §0광석으로 구워 만들 수 있다"),
            centerBookTextVertical(" §f♫ §0가죽 §0재료아이템이다"),
            centerBookTextVertical(" §f⓪ §0농축된 열매 §0사용시 배고픔이 채워지며 10초동안 조금씩 회복한다"),
            centerBookTextVertical(" §f① §0광석 §0모닥불로 구워 철을 얻을 수 있다"),
            centerBookTextVertical(" §f② §0플레어건 §0사용시 하늘에 발사하며, 30초 후 사용한 곳에서 50블럭 내에 랜덤하게 헬기가 도착한다")

        )
        meta.pages = pages
        book.itemMeta = meta
        return book
    }
}