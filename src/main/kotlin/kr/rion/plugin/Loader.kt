package kr.rion.plugin

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import de.maxhenkel.voicechat.api.BukkitVoicechatService
import kr.rion.plugin.event.EventListener
import kr.rion.plugin.event.VoiceChatEvent
import kr.rion.plugin.manager.CommandManager
import kr.rion.plugin.util.Teleport
import kr.rion.plugin.util.global
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


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

        server.pluginManager.registerEvents(EventListener(), this)
        CommandManager(this).registerCommands()

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
                global.checkPlayersWithTag()
            }
        }.runTaskTimer(this, 0L, 1L) // 1 ticks마다 실행 (0.05초마다)
        val core = server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?
        if (core != null) {
            worldManager = core.mvWorldManager
        } else {
            logger.severe("Multiverse-Core 플러그인이 필요합니다!")
        }

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
                global.setGameRulesForAllWorlds()
                console.sendMessage("")
                Teleport.initializeSafeLocations()
                global.startPlayerCheckTask(this@Loader)
            }
        }.runTaskLater(this, 40L)

    }

    override fun onDisable() {
        global.playerCheckTask?.cancel()
    }
}