package kr.rion.plugin.gui

import kr.rion.plugin.game.End.ifEnding
import kr.rion.plugin.game.Start.isStart
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Global.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.endingPlayerMaxCount
import kr.rion.plugin.util.Item.createCustomItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object MainMenu {
    fun openMainGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.DARK_BLUE}메뉴")

        val gameStatus = when {
            !isStarting -> gameStatus.START
            !isStart -> gameStatus.STOP
            else -> gameStatus.WORKING
        }

        val game = createGameItem(gameStatus)
        //게임설정메뉴
        val flamegunName = "${ChatColor.RED}플레어건 소환"
        val flamegunLore = listOf(
            "플레어건이 들어있는 상자를 소환하고",
            "상자위치에 파티클을 지속적으로 소환합니다.",
            "",
            "게임진행 도중에만 사용가능합니다."
        )
        val flamegun = createCustomItem(flamegunName, flamegunLore, Material.FLINT, persistentDataKey = "game-flamegun")

        //아이템지급창
        val giveitemName = "${ChatColor.GREEN}아이템 지급"
        val giveitemLore = listOf(
            "아이템 지급메뉴로 이동합니다.",
            "",
            "플러그인에의해 기능이생긴아이템들을 지급받을수있습니다."
        )
        val giveitem =
            createCustomItem(giveitemName, giveitemLore, Material.DIAMOND_PICKAXE, persistentDataKey = "game-giveitem")

        //이벤트창
        val eventguiName = "${ChatColor.RED}이벤트"
        val eventguiLore = listOf(
            "이벤트 메뉴로 이동합니다.",
            "",
            "게임 이벤트를 실행하는 메뉴입니다."
        )
        val eventGUI =
            createCustomItem(eventguiName, eventguiLore, Material.MAGMA_BLOCK, persistentDataKey = "game-event")

        //랜덤티피창
        val randomtpName = "${ChatColor.GREEN}랜덤티피"
        val randomtpLore = listOf(
            "랜덤티피 메뉴로 이동합니다.",
            "",
            "설정되어있는 랜덤티피에 관련된 메뉴입니다."
        )
        val randomtp =
            createCustomItem(randomtpName, randomtpLore, Material.BEACON, persistentDataKey = "game-randomtp")

        //리셋창
        val resetGUIName = "${ChatColor.RED}리셋"
        val resetGUILore = listOf(
            "리셋메뉴로 이동합니다.",
            "",
            "맵을 리셋시킵니다.",
            "해당메뉴에서 게임맵과 로비맵을 선택할수있습니다"
        )
        val resetGUI =
            createCustomItem(resetGUIName, resetGUILore, Material.CRYING_OBSIDIAN, persistentDataKey = "game-reset")


        val doorName = "${ChatColor.GREEN}대기실 문"
        val doorLore = listOf("대기실 문을 열거나 닫습니다.")
        val dooritem = createCustomItem(doorName, doorLore, Material.OAK_FENCE, persistentDataKey = "game-door")

        val MaxplayerName = "${ChatColor.AQUA}설정된 인원수"
        val MaxplayerLore = listOf(
            "",
            "${ChatColor.GREEN}설정된 탈출 가능 인원수 : ${ChatColor.YELLOW}$EscapePlayerMaxCount${ChatColor.GREEN}명",
            "${ChatColor.GREEN}게임종료에 필요한 인원수 : ${ChatColor.YELLOW}$endingPlayerMaxCount${ChatColor.GREEN}명"
        )
        val Maxplayer =
            createCustomItem(MaxplayerName, MaxplayerLore, Material.NETHER_STAR, persistentDataKey = "game-player")
        val Ending: ItemStack

        if (ifEnding) {
            val EndingName = "${ChatColor.GREEN}게임 자동 종료 상태"
            val EndingLore = listOf("${ChatColor.BOLD}${ChatColor.GREEN} Enable(활성화)", "", "클릭시 수동으로 변경가능합니다.")
            Ending = createCustomItem(EndingName, EndingLore, Material.LIME_WOOL, persistentDataKey = "game-ending")
        } else {
            val EndingName = "${ChatColor.GREEN}게임 자동 종료 상태"
            val EndingLore = listOf("${ChatColor.BOLD}${ChatColor.GREEN} Disable(비활성화)", "", "클릭시 수동으로 변경가능합니다.")

            Ending = createCustomItem(EndingName, EndingLore, Material.RED_WOOL, persistentDataKey = "game-ending")
        }


        val notitem = createCustomItem("", listOf(""), Material.BARRIER, persistentDataKey = "nothing")


        gui.setItem(1, game)
        gui.setItem(3, flamegun)
        gui.setItem(5, notitem)
        gui.setItem(7, giveitem)
        gui.setItem(11, eventGUI)
        gui.setItem(13, randomtp)
        gui.setItem(15, resetGUI)
        gui.setItem(18, Ending)
        gui.setItem(22, dooritem)
        gui.setItem(26, Maxplayer)
        player.openInventory(gui) // 인벤토리열기
    }

    // 상태별 아이템 생성 함수
    private fun createGameItem(status: gameStatus): ItemStack {
        return when (status) {
            gameStatus.START -> createCustomItem(
                "${ChatColor.GREEN}게임 시작",
                listOf("게임을 시작시킵니다."),
                Material.SHROOMLIGHT,
                persistentDataKey = "game-start"
            )

            gameStatus.STOP -> createCustomItem(
                "${ChatColor.RED}게임 종료",
                listOf(
                    "게임을 강제종료합니다.",
                    "",
                    "확인기능없이 클릭시 바로 종료되니 조심히 사용해주시길 바랍니다."
                ),
                Material.REDSTONE_LAMP,
                persistentDataKey = "game-stop"
            )

            gameStatus.WORKING -> createCustomItem(
                "${ChatColor.YELLOW}게임 작업 중입니다.",
                listOf(
                    "",
                    "게임 시작에 필요한 작업 중에 있습니다.",
                    ""
                ),
                Material.OBSIDIAN,
                persistentDataKey = "game-starting"
            )
        }
    }
}


