package kr.rion.plugin.command

import kr.rion.plugin.Loader.Companion.instance
import kr.rion.plugin.util.Global.damageBuff
import kr.rion.plugin.util.Global.prefix
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.spigotmc.SpigotConfig.config

object BuffDamageSetting {
    fun handleDamageSetting(sender: CommandSender, args: Array<out String>) {
        // 인수가 비었는지 확인
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /버프데미지 <값>")
            return
        }
        // 입력 값이 유효한 double인지 체크
        val damage = try {
            args[0].toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("$prefix ${ChatColor.RED}유효한 숫자 값을 입력하세요.")
            return
        }
        // 공격력 범위 체크 (예시: 최대 100까지 허용)
        if (damage <= 0) {
            sender.sendMessage("$prefix ${ChatColor.RED}1 이상의 값만 입력 가능합니다.")
            return
        }
        damageBuff = damage
        config.set("damagebuff", damage)
        sender.sendMessage("$prefix 미션클리어시 공격력이 ${ChatColor.YELLOW}$damage ${ChatColor.GREEN}만큼 증가합니다.")
        instance.saveConfig()
    }
}