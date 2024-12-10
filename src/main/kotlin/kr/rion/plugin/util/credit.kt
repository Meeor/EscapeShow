package kr.rion.plugin.util

import kr.rion.plugin.Loader
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object credit {
    var isCredit: Boolean = false
    fun displayEscapeShow(player: Player, title: String, subtitle: String, credits: List<Pair<String, String>>) {
        val maxLen = maxOf(title.length, subtitle.length)
        var index = 0
        var reverse = false

        object : BukkitRunnable() {
            override fun run() {
                if (!reverse && index >= maxLen) {
                    // 대기 후 글자 지우기로 전환
                    Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable { reverse = true }, 14)
                    return
                }

                if (reverse && index <= 0) {
                    this.cancel()
                    displayCredits(player, credits)
                    return
                }

                // 색상 코드를 유지한 상태로 글자 출력
                val displayedTitle = parseColoredText(title, index)
                val displayedSubtitle = parseColoredText(subtitle, index)

                player.sendTitle(displayedTitle, displayedSubtitle, 0, 20, 0)
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)

                // 순서 제어
                if (reverse) index-- else index++
            }
        }.runTaskTimer(Loader.instance, 0, 2) // 2틱 간격 실행
    }

    private fun displayCredits(player: Player, credits: List<Pair<String, String>>) {
        var creditIndex = 0
        var roleIndex = 0
        var nameIndex = 0
        var reverse = false
        var soundPlayed = 0  // 사운드 재생 횟수 추적

        object : BukkitRunnable() {
            override fun run() {
                // 현재 크레딧 역할 및 이름 설정
                val (role, name) = credits[creditIndex]
                val roleToDisplay = parseColoredText(role, roleIndex)
                val nameToDisplay = parseColoredText(name, nameIndex)

                // 색상 코드를 제거한 상태에서 글자가 출력되는지 확인
                val rawRole = role.replace(Regex("§[0-9a-fk-orx]"), "")
                val rawName = name.replace(Regex("§[0-9a-fk-orx]"), "")

                if (roleIndex - 2 < rawRole.length || nameIndex - 2 < rawName.length) {
                    player.sendTitle(roleToDisplay, nameToDisplay, 0, 100,10)
                }
                // 사운드 재생
                if (!reverse && (roleIndex < rawRole.length || nameIndex < rawName.length)) {
                    if (soundPlayed < 10) {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        soundPlayed++
                    }
                }

                if (creditIndex == credits.size -1) {
                    if (roleIndex == rawRole.length && nameIndex == rawName.length) {
                        // 크레딧 요약 메시지 출력
                        val creditSummary = """
${ChatColor.of("#FF0000")}=============== 팀장 =================
                    낑깡
${ChatColor.of("#EA9999")}============== 부팀장 ================
                    해빛
${ChatColor.of("#d5a6bd")}========= 서버장 & 플러그인 제작 =========
                하이네 , 리온
${ChatColor.of("#b4a7d6")}============== 건축가 ================
                   한서
           유네아 , 소울투이 , 라스
${ChatColor.of("#6aa84f")}=============== 스킨 =================
                    워커
${ChatColor.of("#d9d9d9")}============= 커맨드 =================
                    은성
${ChatColor.of("#9fc5e8")}=========== 스토리 보드 ==============
                    고안브
${ChatColor.of("#6d9eeb")}=========== 영상 편집자 ==============
                    김행주
${ChatColor.of("#76a5af")}=========== 목소리 연기 ==============
            노운 , LADDER , 류설기
${ChatColor.of("#00ff00")}============ 로고 & 룰북 =============
                   요1비
${ChatColor.of("#f9cb9c")}============== 기획 =================
             공도리 , 뭉개구름이
${ChatColor.of("#c5d7e8")}============= 테스터 ================
고라니단, 펭귄마크, 정붕이, 이현균, 캐니지,
하루원, 엘미드, 만렙나무, 오지리, 이네모,
프겜, 가가시시, 스마일, 모퉁이, 슈왈츠티거,
빗고리, 오즈, 오개, 미밐, 반요화,
솔롱고스, 이류, 호라용이, 울먹임, !!,
갱후니
${ChatColor.of("#cc99ff")}========== ${ChatColor.of("#FF0000")}S${ChatColor.of("#FF4000")}p${ChatColor.of("#FF7F00")}e${
                            ChatColor.of(
                                "#FFBF00"
                            )
                        }c${ChatColor.of("#FFFF00")}i${ChatColor.of("#80FF00")}a${ChatColor.of("#00FF00")}l${
                            ChatColor.of(
                                "#008080"
                            )
                        }t${ChatColor.of("#0000FF")}h${ChatColor.of("#2600C1")}a${ChatColor.of("#4B0082")}n${
                            ChatColor.of(
                                "#7000AB"
                            )
                        }k${ChatColor.of("#9400D3")}s${ChatColor.of("#cc99ff")} ==========
                ${ChatColor.of("#00FFE0")}C${ChatColor.of("#18E6E3")}h${ChatColor.of("#2FCCE6")}a${ChatColor.of("#47B3E9")}t ${
                            ChatColor.of(
                                "#7680F0"
                            )
                        }G${ChatColor.of("#8D66F3")}P${ChatColor.of("#A54DF6")}T ${ChatColor.of("#D41AFC")}4${
                            ChatColor.of(
                                "#EB00FF"
                            )
                        }o
                    """.trimIndent()
                        Bukkit.broadcastMessage(creditSummary)
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
                        }
                        isCredit = false
                        this.cancel()//종료
                        return
                    }
                }

                // 순서 제어
                if (!reverse) {
                    if (roleIndex < role.length) roleIndex++
                    if (nameIndex < name.length) nameIndex++

                    // 글자 모두 출력된 경우
                    if (roleIndex == role.length && nameIndex == name.length) {
                        // 모두 출력되면 14틱 대기 후 지우기 시작
                        Bukkit.getScheduler().runTaskLater(Loader.instance, Runnable {
                            reverse = true
                        }, 14)
                    }
                } else {
                    if (roleIndex > 0) roleIndex--
                    if (nameIndex > 0) nameIndex--

                    // 글자 모두 지워진 경우
                    if (roleIndex == 0 && nameIndex == 0) {
                        reverse = false
                        soundPlayed = 0
                        creditIndex++
                    }
                }
            }
        }.runTaskTimer(Loader.instance, 0, 2) // 2틱 간격 실행
    }


    /**
     * 색상 코드와 함께 글자를 한 글자씩 출력하도록 처리하는 함수
     */
    fun parseColoredText(text: String, visibleLength: Int): String {
        val builder = StringBuilder()
        var currentColor = ""
        var visibleCount = 0

        var i = 0
        while (i < text.length) {
            val char = text[i]

            // 색상 코드인지 확인
            if (char == '§') {
                if (i + 1 < text.length && text[i + 1] == 'x' && i + 13 <= text.length) {
                    // RGB 색상 코드 (§x§R§R§G§G§B§B)
                    currentColor = text.substring(i, i + 14)
                    i += 14
                } else if (i + 1 < text.length && text[i + 1].isLetterOrDigit()) {
                    // 일반 색상 코드 (§a, §b 등)
                    currentColor = text.substring(i, i + 2)
                    i += 2
                }
                continue
            }

            // 출력할 길이에 도달하면 중단
            if (visibleCount >= visibleLength) break

            // 현재 글자에 색상 코드 추가
            builder.append(currentColor).append(char)
            visibleCount++
            i++
        }

        return builder.toString()
    }

}