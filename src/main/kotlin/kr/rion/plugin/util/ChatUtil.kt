package kr.rion.plugin.util

import net.md_5.bungee.api.ChatColor

object ChatUtil {
    private const val CHAT_WIDTH = 320 // Minecraft 기본 채팅창 너비 (픽셀)
    private const val BOOK_WIDTH = 114 // 마인크래프트 책 기준 너비
    private const val BOOK_MAX_LINES = 14 // 한 페이지 최대 줄 수 (14줄)


    /**
     * 글자를 Minecraft 채팅창 기준으로 중앙 정렬합니다.
     * 색 코드를 제거한 후 중앙 정렬하며, 줄바꿈("\n")도 인식합니다.
     */
    fun centerText(text: String): String {
        val lines = text.split("\n") // 줄바꿈 인식
        return lines.joinToString("\n") { centerLine(it, CHAT_WIDTH) }
    }

    /**
     * 마인크래프트 책 기준으로 중앙 정렬된 텍스트 반환 (좌우 정렬)
     */
    fun centerBookText(text: String): String {
        val lines = text.split("\n")
        return lines.joinToString("\n") { centerLine(it, BOOK_WIDTH) }
    }

    /**
     * 마인크래프트 책 기준으로 **위아래 포함 중앙 정렬된 텍스트** 반환 (좌우 + 상하 정렬)
     */
    fun centerBookTextVertical(text: String): String {
        val lines = text.split("\n").map { centerBookText(it) } // 좌우 정렬 먼저 수행
        val totalLines = lines.size

        // 위쪽 공백 추가 (최대한 중앙에 오도록)
        val paddingLines = (BOOK_MAX_LINES - totalLines) / 2
        val blankSpace = "\n".repeat(paddingLines.coerceAtLeast(0))

        return blankSpace + lines.joinToString("\n")
    }


    /**
     * 단일 줄을 채팅창 기준으로 중앙 정렬
     */
    private fun centerLine(text: String, maxWidth: Int): String {
        val strippedText = stripColorCodes(text) // 색 코드 제거
        val textWidth = getTextWidth(strippedText) // 텍스트 픽셀 너비 계산
        val totalPadding = (maxWidth - textWidth) / 2 / SPACE_WIDTH // 여백을 스페이스 개수로 변환

        return " ".repeat(totalPadding.coerceAtLeast(0)) + text
    }

    /**
     * 텍스트에서 색 코드 제거
     */
    private fun stripColorCodes(text: String): String {
        return ChatColor.stripColor(text) ?: text
    }

    /**
     * 텍스트의 실제 너비를 계산 (픽셀 단위)
     */
    private fun getTextWidth(text: String): Int {
        return text.sumOf { getCharWidth(it) }
    }

    /**
     * 마인크래프트 폰트 기준으로 각 문자별 픽셀 너비 반환
     */
    private fun getCharWidth(char: Char): Int {
        return when (char) {
            'i', 'l', '!', '.', ',' -> 2
            'I', 't', '[', ']' -> 4
            'f', 'r', 'k', '(', ')' -> 5
            's', 'x', 'z', 'y' -> 6
            in 'a'..'z', in 'A'..'Z', in '0'..'9', ' ' -> 6 // 일반 문자
            'm', 'w', '@', 'W', 'M' -> 8 // 넓은 글자
            in '\uAC00'..'\uD7A3' -> 12 // ✅ 한글 (가~힣 모든 문자) 추가
            else -> 6 // 기본 너비
        }
    }


    private const val SPACE_WIDTH = 4 // 스페이스 문자 너비 (픽셀 기준)
}
