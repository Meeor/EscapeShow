package kr.rion.plugin.command


import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.teleport
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class CommandHandler(plugin: JavaPlugin, private val teleport: teleport) : CommandExecutor {

    private val worldManager = WorldManager(plugin)
    private var UserCount: Int = 0
    private val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.GREEN}"


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label.equals("리셋", ignoreCase = true) -> handleResetCommand(sender, args)
            label.equals("좌표공개", ignoreCase = true) -> handleCoordinatesCommand(sender)
            label.equals("랜덤티피", ignoreCase = true) -> handleRandomTP(sender, args)
            label.equals("아이템지급", ignoreCase = true) -> handleitem(sender, args)
            else -> return false
        }
        return true
    }

    private fun handleCoordinatesCommand(sender: CommandSender) {
        if (sender is Player) {
            if (sender.isOp) {
                val players = Bukkit.getOnlinePlayers().filter {
                    it.gameMode == GameMode.SURVIVAL
                }
                if (players.isEmpty()) {
                    sender.sendMessage("$prefix ${ChatColor.RED}공개할 플레이어가 없습니다.")
                } else {
                    val line = "=".repeat(30)
                    UserCount = 0
                    Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("   $prefix 플레이어 좌표공개")
                    Bukkit.broadcastMessage(" ")
                    for (player in players) {
                        val location = player.location
                        val message =
                            "${ChatColor.GREEN}${player.name}: ${ChatColor.YELLOW}X: ${location.blockX}, Y: ${location.blockY}, Z: ${location.blockZ}"
                        Bukkit.broadcastMessage(message)
                        UserCount++
                    }
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("${ChatColor.GREEN}생존자 수 : ${ChatColor.GOLD}$UserCount")
                    Bukkit.broadcastMessage(" ")
                    Bukkit.broadcastMessage("${ChatColor.GOLD}$line")
                }
                return // 명령어 처리 완료
            } else {
                sender.sendMessage("$prefix ${ChatColor.RED}이 명령어는 운영자만 사용가능합니다.")
                return // 명령어 처리 완료
            }
        } else {
            sender.sendMessage("$prefix ${ChatColor.RED}이 명령어는 플레이어만 사용할 수 있습니다.")
            return // 명령어 처리 완료
        }
    }

    private fun handleitem(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /리셋 <게임/로비>")
            return
        }
        if (sender is Player) {
            // CommandSender가 Player일 경우 캐스팅
            val itemplayer = sender

            when (args[0].lowercase(Locale.getDefault())) {
                "플레어건" -> GiveItem.FlameGun(itemplayer)
                else -> sender.sendMessage("$prefix ${ChatColor.RED} 아이템이름을 정확히 입력해주세요 (자동완성 지원됩니다.)")
            }
        } else {
            sender.sendMessage("$prefix ${ChatColor.RED}플레이어만 받을수 있습니다.")
            return
        }
    }

    private fun handleResetCommand(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /리셋 <게임/로비>")
            return
        }
        //권한 체크
        if (!sender.hasPermission("world.reset")) {
            sender.sendMessage("$prefix ${ChatColor.RED}이 명령어를 사용할 권한이 없습니다.")
            return  // 명령어 처리 완료
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "게임" -> handleGameReset(sender)
            "로비" -> handleLobbyReset(sender)
            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. <게임/로비>를 입력하세요.")
        }
    }

    private fun handleRandomTP(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("$prefix 사용법: /랜덤티피 <목록/재설정>")
            return
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "목록" -> handleRandomTPList(sender)
            "재설정" -> {
                teleport.setInitializedSafeLocations(false)
                Bukkit.broadcastMessage("$prefix 랜덤이동좌표를 새롭게 정하고있습니다.\n$prefix${ChatColor.GOLD} 잠시 렉이 걸릴수 있습니다.")
                teleport.initializeSafeLocations()
                val coordinates = teleport.safeLocations
                sender.sendMessage("$prefix 랜덤이동좌표를 새롭게 설정하였습니다. ${ChatColor.YELLOW}총설정된 갯수 : ${coordinates.size}")

            }

            else -> sender.sendMessage("$prefix ${ChatColor.RED}알 수 없는 인수입니다. <게임/로비>를 입력하세요.")
        }
    }

    private fun handleRandomTPList(sender: CommandSender): Boolean {
        // 좌표 리스트를 문자열로 변환하여 출력
        val coordinates = teleport.safeLocations
        if (coordinates.isNotEmpty()) {
            sender.sendMessage("$prefix 현재 저장된 좌표 목록${ChatColor.GOLD}(${coordinates.size}개)${ChatColor.GREEN}:")
            coordinates.forEachIndexed { index, location ->
                sender.sendMessage("§e${index + 1}: ${location.x}, ${location.y}, ${location.z}")
            }
        } else {
            sender.sendMessage("$prefix 저장된 좌표가 없습니다.")
        }
        return true
    }

    private fun handleGameReset(sender: CommandSender): Boolean {
        teleport.setInitializedSafeLocations(false)
        Bukkit.broadcastMessage("$prefix 게임월드 리셋을 시작합니다.")
        movePlayersToLobby("game")
        Bukkit.broadcastMessage("${ChatColor.GOLD}** 게임맵을 리셋하는 도중에는 잠시 서버가 렉이 걸릴수있습니다. ** \n** 움직임을 최소화 해주시길 바랍니다. **")
        // 월드 제거
        Bukkit.broadcastMessage("$prefix 사용한 게임맵을 제거중입니다..")
        if (!worldManager.deleteWorld("game", sender)) {
            return false
        }
        // 월드 복사
        Bukkit.broadcastMessage("$prefix 백업된 게임맵을 불러오는 중입니다...")
        if (!worldManager.copyWorld("backupgame", "game", sender)) {
            return false
        }

        // 포탈 플러그인 설정 리로드
        Bukkit.broadcastMessage("$prefix 포탈 정보를 다시 불러오고 있습니다...")
        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd = "plugman reload Multiverse-Portals" // 실행할 명령어


        Bukkit.dispatchCommand(console, cmd)
        Bukkit.broadcastMessage("$prefix 게임맵 리셋이 완료되었습니다.")
        return true
    }

    private fun handleLobbyReset(sender: CommandSender): Boolean {
        sender.sendMessage("$prefix 로비월드 리셋을 시작합니다.")
        movePlayersToLobby("lobby")
        // 월드 제거
        sender.sendMessage("$prefix 사용한 로비맵을 제거중입니다..")
        if (!worldManager.deleteWorld("lobby", sender)) {
            return false
        }
        // 월드 복사
        sender.sendMessage("$prefix 백업된 로비맵을 불러오는 중입니다...")
        if (!worldManager.copyWorld("backuplobby", "lobby", sender)) {
            return false
        }

        // 포탈 플러그인 설정 리로드
        sender.sendMessage("$prefix 포탈 정보를 다시 불러오고 있습니다...")
        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd = "plugman reload Multiverse-Portals" // 실행할 명령어

        Bukkit.dispatchCommand(console, cmd)
        sender.sendMessage("$prefix 로비맵 리셋이 완료되었습니다.")
        return true
    }


    private fun movePlayersToLobby(world: String) {
        val worldName = Bukkit.getWorld(world) ?: return
        val lobbyLocation = Location(Bukkit.getWorld("vip"), 15.5, 58.5, -44.5)
        for (player in worldName.players) {
            player.teleport(lobbyLocation)
            player.sendMessage("$prefix ${ChatColor.YELLOW}맵 리셋작업이 시작되어 대기실로 이동되었습니다.")
        }
    }
}