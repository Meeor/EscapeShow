package kr.rion.plugin

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import kr.rion.plugin.command.CommandHandler
import kr.rion.plugin.event.EventListener
import kr.rion.plugin.util.TabComplete
import kr.rion.plugin.util.Teleport
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask


class Loader : JavaPlugin() {
    private var worldManager: MVWorldManager? = null
    private var playerCheckTask: BukkitTask? = null

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
        console.sendMessage("${ChatColor.GOLD}$line")
        console.sendMessage("")
        console.sendMessage("    ${ChatColor.GREEN}Escape Show 플러그인이 활성화 되었습니다.")
        console.sendMessage("")
        console.sendMessage("        ${ChatColor.AQUA}해당 플러그인은 왁타버스 마크조공")
        console.sendMessage("        ${ChatColor.AQUA}Escape Show 를 위해 제작되었습니다.")
        console.sendMessage("")
        console.sendMessage("${ChatColor.GOLD}$line")

        server.pluginManager.registerEvents(EventListener(this), this)

        getCommand("리셋")?.setExecutor(commandHandler)
        getCommand("좌표공개")?.setExecutor(commandHandler)
        getCommand("랜덤티피")?.setExecutor(commandHandler)
        getCommand("아이템지급")?.setExecutor(commandHandler)
        object : BukkitRunnable() {
            override fun run() {
                checkPlayersWithTag("Escape")
            }
        }.runTaskTimer(this, 0L, 1L) // 1 ticks마다 실행 (0.05초마다)
        val core = server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?
        if (core != null) {
            worldManager = core.mvWorldManager
        } else {
            logger.severe("Multiverse-Core 플러그인이 필요합니다!")
        }

        //자동완성 등록
        getCommand("리셋")?.tabCompleter = TabComplete()
        getCommand("랜덤티피")?.tabCompleter = TabComplete()
        getCommand("아이템지급")?.tabCompleter = TabComplete()
    }

    override fun onDisable() {
        playerCheckTask?.cancel()
    }

    private fun checkPlayersWithTag(tag: String) {
        // 모든 플레이어를 순회하며 태그 확인
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.scoreboardTags.contains(tag)) { // 태그가 권한으로 설정되어 있다고 가정
                performAction(player)
            }
            if (!player.scoreboardTags.contains("EscapeComplete")) {
                if (player.gameMode == GameMode.ADVENTURE || player.gameMode == GameMode.SURVIVAL) {
                    noFly(player)
                }
            }
        }
    }

    //탈출시 작업
    private fun performAction(player: Player) {
        // 게임 모드 변경
        player.gameMode = GameMode.ADVENTURE

        // 플라이 허용
        player.allowFlight = true
        player.isFlying = true

        // 투명화 버프 부여 (무한지속시간)
        val invisibilityEffect = PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false)
        player.addPotionEffect(invisibilityEffect)

        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님이 ${ChatColor.GREEN}탈출 ${ChatColor.RESET}하셨습니다.")
        player.addScoreboardTag("EscapeComplete")
        player.removeScoreboardTag("Escape")
        player.sendMessage("${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN} 플라이,무적및 투명화가 활성화 되었습니다!")
    }

    //탈출완료태그없을때 작업
    private fun noFly(player: Player) {
        player.allowFlight = false
        player.isFlying = false
    }

    fun startPlayerCheckTask() {
        playerCheckTask?.cancel()
        playerCheckTask = object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { player ->
                    val loc = player.location
                    if (Teleport.isInDesignatedArea(loc)) {
                        Bukkit.getLogger().warning("지금 텔레포트합니다.")
                        Teleport.teleportToRandomLocation(player)
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L) // 20L은 주기, 1초마다 실행
    }
}