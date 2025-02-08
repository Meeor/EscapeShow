package kr.rion.plugin.command

import kr.rion.plugin.util.Global.prefix
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object Damage {
    fun handleDamage(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /데미지 <데미지량>")
            return
        }
        // ✅ 입력된 데미지 값을 Int로 변환
        val damageAmount: Double = try {
            args[0].toDouble() // Int보다 Double이 유연함 (소수점 허용 가능)
        } catch (e: NumberFormatException) {
            sender.sendMessage("§c올바른 숫자를 입력하세요!(소수점 가능)")
            return
        }

        // ✅ 모든 온라인 플레이어에게 데미지 적용
        Bukkit.getOnlinePlayers().forEach { player ->
            player.damage(damageAmount) // 플레이어에게 데미지 적용
        }

        // ✅ 명령어 실행 결과 메시지 출력
        sender.sendMessage("§a모든 플레이어에게 §c${damageAmount} §a만큼의 피해를 입혔습니다!")
        return
    }
}