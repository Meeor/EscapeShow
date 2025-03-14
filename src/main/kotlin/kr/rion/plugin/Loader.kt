package kr.rion.plugin

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import de.maxhenkel.voicechat.api.BukkitVoicechatService
import kr.rion.plugin.event.EventListener
import kr.rion.plugin.event.VoiceChatEvent
import kr.rion.plugin.manager.CommandManager
import kr.rion.plugin.manager.MissionManager
import kr.rion.plugin.mission.MissionList
import kr.rion.plugin.util.Global
import kr.rion.plugin.util.Global.EscapePlayerMaxCount
import kr.rion.plugin.util.Global.MissionEscapeMaxCount
import kr.rion.plugin.util.Global.helicopterfindattempt
import kr.rion.plugin.util.Global.teamsMaxPlayers
import kr.rion.plugin.util.Teleport
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File


class Loader : JavaPlugin() {
    private var worldManager: MVWorldManager? = null

    companion object {
        lateinit var instance: Loader
            private set
    }


    private val console = server.consoleSender
    override fun onEnable() {
        instance = this

        Teleport.initialize(this)
        val line = "=".repeat(42)

        server.pluginManager.registerEvents(EventListener(), this) //이벤트 등록
        MissionList.registerAll() //미션 등록
        CommandManager(this).registerCommands() //명령어 등록

        ///voicechat를위한 추가이벤트리스너
        // VoiceChat API 서비스 불러오기
        val service = server.servicesManager.load(BukkitVoicechatService::class.java)
        if (service != null) {
            // VoiceChat 이벤트 리스너 등록
            service.registerPlugin(VoiceChatEvent())
            logger.info("VoiceChat 이벤트 리스너가 등록되었습니다.")
        } else {
            logger.severe("VoiceChat API를 불러올 수 없습니다.")
        }

        object : BukkitRunnable() {
            override fun run() {
                Global.checkPlayersWithTag()
            }
        }.runTaskTimer(this, 0L, 1L) // 1 ticks마다 실행 (0.05초마다)
        val core = server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?
        if (core != null) {
            worldManager = core.mvWorldManager
        } else {
            logger.severe("Multiverse-Core 플러그인이 필요합니다!")
        }

        // config.yml 생성
        saveDefaultConfig()
        //message.txt 생성
        setupMessageFile()


        // 콘피그 값 로드
        EscapePlayerMaxCount = config.getInt("EscapePlayerMaxCount", 3)
        MissionEscapeMaxCount = config.getInt("MissionEscapeMaxCount", 3)
        helicopterfindattempt = config.getInt("helicopterfindattempt", 100)
        teamsMaxPlayers = config.getInt("teamsMaxPlayers", 3)


        object : BukkitRunnable() {
            override fun run() {
                console.sendMessage("${ChatColor.GOLD}$line")
                console.sendMessage("")
                console.sendMessage("    ${ChatColor.GREEN}Escape Show 서버가 시작되었습니다.")
                console.sendMessage("")
                console.sendMessage("        ${ChatColor.AQUA}서버에는 왁타버스 마크조공")
                console.sendMessage("       ${ChatColor.AQUA}Escape Show 를 위한 자체제작")
                console.sendMessage("        ${ChatColor.AQUA}플러그인이 적용되어있습니다.")
                console.sendMessage("")
                console.sendMessage("${ChatColor.GOLD}$line")
                console.sendMessage("")
                Global.setGameRulesForAllWorlds()
                console.sendMessage("")
            }
        }.runTaskLater(this, 40L)

    }

    override fun onDisable() {
        Global.playerCheckTask?.cancel()
        saveConfig()
        MissionManager.resetMissions() //미션 내부정보들 전부 초기화
    }

    // message.txt 파일 생성
    private fun setupMessageFile() {
        val file = File(dataFolder, "message.txt")

        if (!file.exists()) {
            dataFolder.mkdirs()
            file.writeText(
                """
                §e게임의 안내를 시작하겠습니다.\n
                §e게임의 장르는 헝거게임이며, 맵에있는 §a통§e에서 아이템을얻고 조합하여 무기를 만들고.\n§e다른플레이어들과 싸워 탈출하는 게임입니다.\n
                §e게임시작직후 각플레이어에게 미션이 지급됩니다. \n§e미션은 인벤토리를 열어 책과깃펜 아이템을 클릭하면 \n§e받은 미션을 확인하실수 있습니다.\n
                §e게임은 팀게임으로 진행되며 팀은 플러그인에 의해 자동으로 설정됩니다.\n
                §e게임 시작후 §a채팅 메세지§e는 §b팀과의 소통§e으로 사용하실수있으며 §a음성채팅§e은 생존자모두에게 들립니다.\n§e음성채팅은 거리에따라 사운드가 줄어들거나 커지며 플레이어간의 거리가 멀어지면 목소리가 들리지 않습니다.\n
                §e탈출 방법에는 §a미션 클리어§e,§a플레어건 탈출§e,§a최후의 1팀§e이 있습니다.\n
                §e미션 탈출의경우 생존자가 5명 남았을때 처리되며 그때 살아남아있고 미션을 클리어한 사람중 제한인원에 맞게 자동 탈출이됩니다.\n
                §e플레어건 탈출의 경우 운영자가 수동으로 플레어건을 소환해드리기에 불시에 플레어건이 소환됩니다.\n§e플레어건이 소환될경우 상단에 플레어건위치가 본인이 바라보고있는방향에 있는지 표시되며.\n§e소환됬다는 메세지가 화면 중앙에 나타납니다.\n
                §e플레어건을 사용할경우 사용한지점으로부터 반경 30칸 이내 랜덤한위치에 헬기가 소환되며 소환된자리에 나타나는 빛줄기에 §a3§e초간 가까이 붙어있으면 탈출이 됩니다.\n
                §e각 아이템에 관련된 설명은 §a/아이템§e 명령어를 사용하여 확인하시길 바랍니다.\n
                §e조합은 인벤토리를열어 조합아이템을 클릭하면 조합창이열리며 리스트에 있는 아이템에 맞게 넣으실경우 조합을 하실수 있습니다. \n§e조합법을 설명으로 보시려면 §a/조합법 §e명령어를 사용하여 주시기 바랍니다.
                §e해당 게임에는 부활시스템이 존재합니다.\n§e첫 사망시 시체가 남으며 시체위에는 부활이라는 메세지가 떠있게됩니다.\n§e다른사람이 본인의 시체위에서 §a웅크리고§e있을경우 §b부활§e을 시켜줄수 있으며. \n§e시체의 아이템을 §a한개라도§e가져갈경우 시체의주인은 부활이 금지되며 즉시 사망하게됩니다.\n
                §e탈출자들은 9번째칸에 지급되는 나침반을 우클릭하여 다른플레이어에게 텔레포트가 가능하며 자유롭게 관전이 가능해집니다.
                §e또한 탈출자및 사망자는 서로 음성대화가 가능해지며 탈출자와 사망자의 목소리는 생존자에게 들리지 않습니다.
                §e게임이 종료된이후에는 게임맵 복구를위하여 렉이 걸릴수있습니다. 종료시점엔 잠시 마우스와 키보드에서 손을떼고 대기하여 주시길 바랍니다. \n§l§b감사합니다.
            """.trimIndent()
            )
        }
        val items = File(dataFolder, "Items.txt")
        if (!items.exists()) {
            dataFolder.mkdirs()
            items.writeText(
                """
                §e돌맹이 §b-> §a잡템
                §e비늘 §b-> §a재료아이템
                §e칼날 §b-> §a재료아이템
                §e검날 §b-> §a재료아이템
                §e먹물 §b-> §a재료아이템
                §e날 §b-> §a재료아이템
                §e판자 §b-> §a재료아이템
                §e유리조각 §b-> §a재료아이템
                §e가죽 §b-> §a재료아이템
                §e잔해 §b-> §a재료아이템
                §e철덩어리 §b-> §a재료아이템
                §e철 §b-> §a재료아이템
                §e광석 §b-> §a모닥불을 설치하고 모닥불반경 3*3이내에서 광석을 들고 우클릭을 하면 구워집니다.
                §e막대기 §b-> §a재료아이템,§b무기로도 쓸수있다.
                §e판자더미 §b-> §a미션 아이템, 재료아이템
                §e사냥도끼 §b-> §c무기
                §e단검 §b-> §c무기
                §e마체테 §b-> §c무기
                §e돌덩이 §b-> §c무기
                §e대형망치 §b-> §c무기
                §e모닥불 §b-> §a점프하며 설치하면 광석을 구울수있는 모닥불이 설치됩니다.
                §e재킷 §b-> §a체력이 §b5§a칸 추가됩니다.
                §e사슬흉갑 §b-> §a체력이 §b10§a칸 추가됩니다
                §e갑옷 §b-> §a체력이 §b15§a칸 추가됩니다
                §e강철갑옷 §b-> §a체력이 §b20§a칸 추가됩니다
                §e붕대 §b-> §a사용후 §b3초§a뒤 일정량의체력을 회복합니다.
                §e부목 §b-> §a사용시 5초간 느려지지만 붕대보다 더많은 체력을 회복합니다.
                §e농축된 열매 §b-> §a사용시 배고픔이 즉시 채워지며 3초간 재생버프를 받는다.
                §e계약서 §b-> §c최대체력§a이 줄고 §b데미지가 2배§a가 됩니다.
                §e지도 §b-> §a주위 §b50블럭§a 이내의 사람에게 발광효과를 부여합니다.
                §d플레어건 §b-> §6사용시 헬기를 호출하며 최대 3명까지 탈출할수 있다.
            """.trimIndent()
            )
        }
        val crafting = File(dataFolder, "Crafting.txt")
        if (!crafting.exists()) {
            dataFolder.mkdirs()
            crafting.writeText(
                """
                §e잔해 + 잔해 §b-> §a판자
                §e판자 + 판자 §b-> §a판자더미
                §e판자더미 + 붕대 §b-> §a부목
                §e날 + 막대기 §b-> §a사냥도끼
                §e가죽 + 유리조각 §b-> §a지도
                §e가죽 + 먹물 §b-> §a계약서
                §e막대기 + 철4개 §b-> §a대형망치
                §e잔해 + 막대기 §b-> §a모닥불
                §e가죽 + 가죽 §b-> §a재킷
                §e비늘 + 비늘 §b-> §a사슬 흉갑
                §e철4개 + 사슬흉갑 §b-> §a갑옷
                §e철덩어리 + 갑옷 §b-> §a강철갑옷
                §e철2개 §b-> §a날
                §e칼날2개 §b-> §a검날
                §e철1개 §b-> §a칼날
                §e칼날1개 + 막대기 §b-> §a단검
                §e검날 + 철2개 §b-> §a마체테
                §e철 10개 §b-> §a철덩어리
                §e광석 §b-> 모닥불 -> §a철
                §e막대기 §b-> 모닥불 -> §a철덩어리
            """.trimIndent()
            )
        }
    }
}