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
import kr.rion.plugin.util.Global.damageBuff
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
        // 디스크에서 읽어옴
        reloadConfig()


        // 콘피그 값 로드
        EscapePlayerMaxCount = config.getInt("EscapePlayerMaxCount", 3)
        MissionEscapeMaxCount = config.getInt("MissionEscapeMaxCount", 3)
        helicopterfindattempt = config.getInt("helicopterfindattempt", 100)
        teamsMaxPlayers = config.getInt("teamsMaxPlayers", 3)
        damageBuff = config.getDouble("damagebuff", 3.0)


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

}