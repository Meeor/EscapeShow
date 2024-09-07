package kr.rion.plugin.command

import kr.rion.plugin.util.global.prefix
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
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
}