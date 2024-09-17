package kr.rion.plugin.command

import kr.rion.plugin.Loader
import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.Teleport
import kr.rion.plugin.util.Teleport.setInitializedSafeLocations
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import java.util.*

object Reset {
    private val worldManager = WorldManager(Loader.instance)
    fun handleResetCommand(sender: CommandSender, args: Array<out String>) {
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

    private fun handleGameReset(sender: CommandSender): Boolean {
        setInitializedSafeLocations(false)
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

        sender.sendMessage("$prefix 랜덤좌표를 선정 중입니다.....")
        Teleport.initializeSafeLocations()

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