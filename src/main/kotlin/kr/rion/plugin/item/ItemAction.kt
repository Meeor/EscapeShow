package kr.rion.plugin.item

import org.bukkit.entity.Player

object ItemAction {
    fun handleFlameGun(player: Player) {
        FlameGunActions.launchFlare(player)
    }

    fun handleHeal(player: Player) {
        HealAction.lunchHeal(player)
    }

    fun handleBerries(player: Player) {
        BerriesAction.lunchBerries(player)
    }

    fun handleContract(player: Player) {
        ContractAction.lunchContract(player)
    }

    fun handleMap(player: Player) {
        MapAction.lunchMap(player)
    }
}