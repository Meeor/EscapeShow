package kr.rion.plugin.command

import kr.rion.plugin.util.global.prefix
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.awt.Component

private val TagType = org.bukkit.persistence.PersistentDataType.STRING
private val TagValue = true.toString()

object GiveItem {

    /////플레어건
    fun FlameGun(player: Player) {
        val itemName = "${ChatColor.RED}${ChatColor.ITALIC}플레어건"
        val quantity = 1
        val flamegun = NamespacedKey("EscapeShow", "flamegun")

        val item = ItemStack(Material.FLINT, quantity)
        val meta = item.itemMeta
        meta.setDisplayName(itemName)
        meta.persistentDataContainer.set(flamegun, TagType, TagValue)
        item.itemMeta = meta

        player.inventory.addItem(item)
        player.sendMessage("$prefix $itemName${ChatColor.GREEN}을(를) 지급하였습니다.")
    }
    ///붕대
    fun Heal(player: Player){
        val itemName = "${ChatColor.GREEN}${ChatColor.ITALIC}붕대"
        val quantity = 1
        val heal = NamespacedKey("EscapeShow", "heal")
        val item = ItemStack(Material.PAPER, quantity)
        val lore = listOf("하트 2.5칸을 회복하는 아이템이다.")
        val meta = item.itemMeta
        // List<String>을 List<Component>로 변환합니다.
        val loreComponents: List<TextComponent> = lore.map { line ->
            LegacyComponentSerializer.legacySection().deserialize(line) // ChatColor 및 기타 마크업을 처리합니다.
        }
        meta.setDisplayName(itemName)
        meta.lore(loreComponents)
        meta.persistentDataContainer.set(heal, TagType, TagValue)
        item.itemMeta = meta

        player.inventory.addItem(item)
        player.sendMessage("$prefix $itemName${ChatColor.GREEN}을(를) 지급하였습니다.")
    }
    /////농축된 열매
    fun Berries(player: Player){
        val itemName = "${ChatColor.GREEN}${ChatColor.ITALIC}농축된 열매"
        val quantity = 1
        val berries = NamespacedKey("EscapeShow", "berries")
        val item = ItemStack(Material.GLOW_BERRIES, quantity)
        val lore = listOf("${ChatColor.AQUA}${ChatColor.BOLD}훨씬 달고 묘하게 힘이 쏫아나는 맛")
        val meta = item.itemMeta
        // List<String>을 List<Component>로 변환합니다.
        val loreComponents: List<TextComponent> = lore.map { line ->
            LegacyComponentSerializer.legacySection().deserialize(line) // ChatColor 및 기타 마크업을 처리합니다.
        }
        meta.setDisplayName(itemName)
        meta.lore(loreComponents)
        meta.persistentDataContainer.set(berries, TagType, TagValue)
        item.itemMeta = meta

        player.inventory.addItem(item)
        player.sendMessage("$prefix $itemName${ChatColor.GREEN}을(를) 지급하였습니다.")
    }
    fun Contract(player: Player){
        val itemName = "${ChatColor.GREEN}${ChatColor.ITALIC}농축된 열매"
        val quantity = 1
        val contract = NamespacedKey("EscapeShow", "contract")
        val item = ItemStack(Material.SKULL_BANNER_PATTERN, quantity)
        val meta = item.itemMeta
        meta.setDisplayName(itemName)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS)
        meta.persistentDataContainer.set(contract, TagType, TagValue)
        item.itemMeta = meta

        player.inventory.addItem(item)
        player.sendMessage("$prefix $itemName${ChatColor.GREEN}을(를) 지급하였습니다.")
    }
}