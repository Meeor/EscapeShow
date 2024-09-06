package kr.rion.plugin.command

import kr.rion.plugin.util.global.prefix
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private val TagType = org.bukkit.persistence.PersistentDataType.STRING
private val TagValue = true.toString()

object GiveItem {

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
}