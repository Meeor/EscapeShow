package kr.rion.plugin.item

import org.bukkit.entity.Player

object ItemAction {
    fun handleFlameGun(player: Player) {
        FlameGunActions.launchFlare(player)
    }

    fun handleBerries(player: Player) {
        BerriesAction.lunchBerries(player)
    }
}