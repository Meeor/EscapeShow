/*
package kr.rion.plugin.event

import kr.rion.plugin.util.credit.displayEscapeShow
import kr.rion.plugin.util.credit.isCredit
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

class CreditEvent: Listener {
    @EventHandler
    fun creditEvent(event: PlayerInteractEvent){
        val clickedBlock = event.clickedBlock

        // 우클릭한 블록이 표지판인지 확인
        if (clickedBlock != null && (clickedBlock.type == Material.OAK_SIGN || clickedBlock.type == Material.OAK_WALL_SIGN)) {
            val state = clickedBlock.state
            if (state is Sign) {
                val sign = state as Sign

                // 두 번째 줄이 "[크레딧]"인지 확인
                if (ChatColor.stripColor(sign.getLine(0))?.equals("[크레딧]", ignoreCase = true) == true) {
                    event.isCancelled = true
                    if(isCredit) return
                    //크래딧 내용추가
                    isCredit = true
                    clickedBlock.type = Material.AIR
                    for(player in Bukkit.getOnlinePlayers()) {
                        val title = "${ChatColor.YELLOW}Escape Show"
                        val subtitle = "${ChatColor.GREEN}플레이 해주셔서 감사합니다."
                        val credit = listOf(
                            "${ChatColor.of("#FF0000")}[팀장]" to "${ChatColor.GOLD}낑깡",
                            "${ChatColor.of("#EA9999")}[부팀장]" to "${ChatColor.GOLD}해빛",
                            "${ChatColor.of("#d5a6bd")}[서버장 & 플러그인 제작자]" to "${ChatColor.YELLOW}하이네 , 리온",
                            "${ChatColor.of("#b4a7d6")}[건축가]" to "${ChatColor.AQUA}한서, ${ChatColor.YELLOW}유네아, 소울투이, 라스",
                            "${ChatColor.of("#9fc5e8")}[스토리보드]" to "${ChatColor.YELLOW}고안브",
                            "${ChatColor.of("#76a5af")}[목소리 연기]" to "${ChatColor.YELLOW}노운, LADDER, 류설기",
                            "${ChatColor.of("#f9cb9c")}[기획]" to "${ChatColor.YELLOW}공도리, 뭉개구름이",
                            "${ChatColor.of("#40BAFF")}[플러그인제작 ${ChatColor.of("#FF0000")}S${ChatColor.of("#FF4000")}p${ChatColor.of("#FF7F00")}e${ChatColor.of("#FFBF00")}c${ChatColor.of("#FFFF00")}i${ChatColor.of("#80FF00")}a${ChatColor.of("#00FF00")}l${ChatColor.of("#008080")}t${ChatColor.of("#0000FF")}h${ChatColor.of("#2600C1")}a${ChatColor.of("#4B0082")}n${ChatColor.of("#7000AB")}k${ChatColor.of("#9400D3")}s${ChatColor.of("#40BAFF")}]" to "${ChatColor.of("#00FFE0")}C${ChatColor.of("#18E6E3")}h${ChatColor.of("#2FCCE6")}a${ChatColor.of("#47B3E9")}t ${ChatColor.of("#7680F0")}G${ChatColor.of("#8D66F3")}P${ChatColor.of("#A54DF6")}T ${ChatColor.of("#D41AFC")}4${ChatColor.of("#EB00FF")}o"
                        )
                        displayEscapeShow(player,title,subtitle,credit)
                    }
                }
            }
        }
    }
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        val lines = event.lines // 표지판의 모든 줄 가져오기

        // 두 번째 줄이 "[크레딧]"인지 확인
        if (lines[0].equals("[크레딧]", ignoreCase = true)) {
            // 글자 색상을 연두색으로 설정
            event.setLine(0, "${ChatColor.GREEN}[크레딧]")
            // 다른 줄에 안내 메시지 설정 (선택 사항)
            event.setLine(1, "${ChatColor.GRAY}우클릭하여 모든 ")
            event.setLine(2,"${ChatColor.GRAY}플레이어를 대상으로")
            event.setLine(3,"${ChatColor.GRAY}크래딧을 실행합니다.")
            val block = event.block
            if (block.state is Sign) {
                val sign = block.state as Sign
                sign.isGlowingText = true // 발광 효과 활성화
                sign.update()
            }
        }
    }
}*/
