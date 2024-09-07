package kr.rion.plugin.item

import org.bukkit.entity.Player

object ItemAction {
    fun handleFlameGun(player: Player) {
        // 플레어건 행동을 호출
        FlameGunActions.launchFlare(player)
    }
    fun handleHeal(player: Player){
        HealAction.lunchHeal(player)
    }
}