package kr.rion.plugin.command

import kr.rion.plugin.item.ItemAction.handleResetContract
import kr.rion.plugin.util.global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object Playeritem {
    fun handleitem(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /리셋 <게임/로비>")
            return
        }
        if (sender is Player) {
            // CommandSender가 Player일 경우 캐스팅
            val itemplayer = sender

            when (args[0].lowercase(Locale.getDefault())) {
                "플레어건" -> GiveItem.FlameGun(itemplayer)
                "붕대" -> GiveItem.Heal(itemplayer)
                "농축된열매" -> GiveItem.Berries(itemplayer)
                "계약서" -> GiveItem.Contract(itemplayer)
                "지도" -> GiveItem.Map(itemplayer)
                else -> sender.sendMessage("$prefix ${ChatColor.RED} 아이템이름을 정확히 입력해주세요 (자동완성 지원됩니다.)")
            }
        } else {
            sender.sendMessage("$prefix ${ChatColor.RED}플레이어만 받을수 있습니다.")
            return
        }
    }
    fun handleItemReset(sender: CommandSender) {
        if (sender.isOp) {
            handleResetContract()
            sender.sendMessage("전체 플레이어의 계약서 상태를 초기화 시켰습니다.")
        }
    }
}