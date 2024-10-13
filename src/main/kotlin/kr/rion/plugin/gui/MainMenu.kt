package kr.rion.plugin.gui

import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.util.Item
import kr.rion.plugin.util.Item.createCustomItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object MainMenu {
    fun openMainGUI(player: Player){
        val gui = Bukkit.createInventory(null,18,"${ChatColor.DARK_BLUE}메뉴")

        val game: ItemStack
        //게임시작및 종료
        if(!isStarting){
            val gameitemName = "${ChatColor.GREEN}게임 시작"
            val gameitemLore = listOf(
                "게임을 시작시킵니다."
            )
            game = createCustomItem(gameitemName,gameitemLore, Material.SHROOMLIGHT, persistentDataKey = "game-start")
        }else{
            val gameitemName = "${ChatColor.GREEN}게임 종료"
            val gameitemLore = listOf(
                "게임을 강제종료합니다.",
                "",
                "확인기능없이 클릭시 바로 종료되니 조심히 사용해주시길 바랍니다."
            )
            game = createCustomItem(gameitemName,gameitemLore, Material.REDSTONE_LAMP, persistentDataKey = "game-stop")
        }
        //게임설정메뉴
        val settingName = "${ChatColor.LIGHT_PURPLE}게임설정"
        val settingLore = listOf(
            "게임설정 메뉴로 이동합니다.",
            "",
            "각종 게임과 관련된 설정을 바꾸실수있습니다.",
            "(해당설정은 config.yml에 저장됩니다.)"
        )
        val setting = createCustomItem(settingName,settingLore,Material.PAPER, persistentDataKey = "game-setting")

        //수동조작메뉴
        val controlName = "${ChatColor.YELLOW}게임수동조작"
        val controlLore = listOf(
            "게임수동조작 메뉴로 이동합니다.",
            "",
            "게임에 있는 효과들을 실시간으로 활성화,비활성화 합니다.",
            "(해당효과들은 일시적이며 서버가재부팅되거나",
            "게임이 시작및종료 될경우 원래상태로 돌아갈수가있습니다.)"
        )
        val control = createCustomItem(controlName,controlLore,Material.GOLDEN_PICKAXE, persistentDataKey = "game-contorl")

        //아이템지급창
        val giveitemName = "${ChatColor.GREEN}아이템 지급"
        val giveitemLore = listOf(
            "아이템 지급메뉴로 이동합니다.",
            "",
            "플러그인에의해 기능이생긴아이템들을 지급받을수있습니다."
        )
        val giveitem = createCustomItem(giveitemName,giveitemLore,Material.DIAMOND_PICKAXE, persistentDataKey = "game-giveitem")

        //이벤트창
        val eventguiName = "${ChatColor.RED}이벤트"
        val eventguiLore = listOf(
            "이벤트 메뉴로 이동합니다.",
            "",
            "게임 이벤트를 실행하는 메뉴입니다."
        )
        val eventGUI = createCustomItem(eventguiName,eventguiLore,Material.MAGMA_BLOCK, persistentDataKey = "game-event")

        //랜덤티피창
        val randomtpName = "${ChatColor.GREEN}랜덤티피"
        val randomtpLore = listOf(
            "랜덤티피 메뉴로 이동합니다.",
            "",
            "설정되어있는 랜덤티피에 관련된 메뉴입니다."
        )
        val randomtp = createCustomItem(randomtpName,randomtpLore,Material.BEACON, persistentDataKey = "game-randomtp")

        //리셋창
        val resetGUIName = "${ChatColor.RED}리셋"
        val resetGUILore = listOf(
            "리셋메뉴로 이동합니다.",
            "",
            "맵을 리셋시킵니다.",
            "해당메뉴에서 게임맵과 로비맵을 선택할수있습니다"
        )
        val resetGUI = createCustomItem(resetGUIName,resetGUILore,Material.CRYING_OBSIDIAN, persistentDataKey = "game-reset")


        gui.setItem(1,game)
        gui.setItem(3,setting)
        gui.setItem(5,control)
        gui.setItem(7,giveitem)
        gui.setItem(11,eventGUI)
        gui.setItem(13,randomtp)
        gui.setItem(15,resetGUI)
        player.openInventory(gui) // 인벤토리열기
    }
}