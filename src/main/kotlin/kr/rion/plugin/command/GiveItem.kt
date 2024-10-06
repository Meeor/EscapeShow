import de.tr7zw.nbtapi.NBTItem
import kr.rion.plugin.Loader
import kr.rion.plugin.util.global.prefix
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
private val TagType = org.bukkit.persistence.PersistentDataType.STRING
private val TagValue = true.toString()

object GiveItem {

    fun Compass(player: Player){
        val itemName = "${ChatColor.AQUA}플레이어에게 텔레포트"
        val quantity = 1
        val compass = NamespacedKey(Loader.instance, "teleport")

        val item = ItemStack(Material.COMPASS, quantity) // Fire Charge를 예시로 사용
        val meta = item.itemMeta
        meta.setDisplayName(itemName)
        meta.persistentDataContainer.set(compass, TagType, TagValue)
        item.itemMeta = meta

        player.inventory.setItem(8,item)
    }

    private fun giveCustomItem(
        player: Player,
        material: Material,
        name: String,
        loreLines: List<String>? = null,
        nbtKey: String
    ) {
        val item = ItemStack(material, 1)
        val meta = item.itemMeta!!
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
        loreLines?.let {
            meta.lore(it.map { line ->
                LegacyComponentSerializer.legacySection().deserialize(line)
            })
        }
        item.itemMeta = meta
        val nbtItem = NBTItem(item)
        nbtItem.setBoolean(nbtKey, true)
        item.itemMeta = nbtItem.item.itemMeta
        player.inventory.addItem(item)
        player.sendMessage("$prefix ${meta.displayName}${ChatColor.GREEN}을(를) 지급하였습니다.")
    }

    fun FlameGun(player: Player) = giveCustomItem(player, Material.FLINT, "${ChatColor.RED}플레어건", null, "flamegun")

    fun Heal(player: Player) = giveCustomItem(
        player,
        Material.PAPER,
        "${ChatColor.GREEN}붕대",
        listOf("${ChatColor.AQUA}${ChatColor.BOLD}하트 2.5칸을 회복하는 아이템이다."),
        "heal"
    )

    fun Berries(player: Player) = giveCustomItem(
        player,
        Material.GLOW_BERRIES,
        "${ChatColor.GREEN}농축된 열매",
        listOf("${ChatColor.AQUA}${ChatColor.BOLD}훨씬 달고 묘하게 힘이 쏫아나는 맛"),
        "berries"
    )

    fun Contract(player: Player) =
        giveCustomItem(player, Material.SKULL_BANNER_PATTERN, "${ChatColor.of("#FF4242")}계약서", null, "contract")

    fun Map(player: Player) = giveCustomItem(player, Material.MOJANG_BANNER_PATTERN, "${ChatColor.GRAY}지도", null, "map")
}
