package kr.rion.plugin

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import kr.rion.plugin.command.CommandHandler
import kr.rion.plugin.event.EventListener
import kr.rion.plugin.util.TabComplete
import kr.rion.plugin.util.Teleport
import kr.rion.plugin.util.global
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


class Loader : JavaPlugin() {
    private var worldManager: MVWorldManager? = null

    fun getTeleport(): Teleport {
        return Teleport
    }

    companion object {
        lateinit var instance: Loader
            private set
    }

    private val console = server.consoleSender
    override fun onEnable() {
        instance = this


        /*
        CommandHandler(this).registerCommands()
        server.pluginManager.registerEvents(EventListener(), this)
        */
        val commandHandler = CommandHandler(this, Teleport)
        Teleport.initialize(this)
        val line = "=".repeat(50)

        server.pluginManager.registerEvents(EventListener(this), this)

        getCommand("리셋")?.setExecutor(commandHandler)
        getCommand("좌표공개")?.setExecutor(commandHandler)
        getCommand("랜덤티피")?.setExecutor(commandHandler)
        getCommand("아이템지급")?.setExecutor(commandHandler)
        object : BukkitRunnable() {
            override fun run() {
                global.checkPlayersWithTag("Escape")
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
                console.sendMessage("        ${ChatColor.AQUA}Escape Show 를 위한 자체제작")
                console.sendMessage("        ${ChatColor.AQUA}플러그인이 적용되어있습니다.")
                console.sendMessage("")
                console.sendMessage("${ChatColor.GOLD}$line")
                Teleport.initializeSafeLocations()
                global.startPlayerCheckTask(this@Loader)
            }
        }.runTaskLater(this, 40L)

        //자동완성 등록
        getCommand("리셋")?.tabCompleter = TabComplete()
        getCommand("랜덤티피")?.tabCompleter = TabComplete()
        getCommand("아이템지급")?.tabCompleter = TabComplete()
    }

    override fun onDisable() {
        global.playerCheckTask?.cancel()
    }


}