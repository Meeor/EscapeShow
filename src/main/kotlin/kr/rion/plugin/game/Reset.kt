package kr.rion.plugin.game

import kr.rion.plugin.Loader
import kr.rion.plugin.manager.ChunkyManager
import kr.rion.plugin.manager.WorldManager
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Teleport.setInitializedSafeLocations
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Reset {
    private val worldManager = WorldManager(Loader.instance)
    fun handleGameReset(): Boolean {
        var removedCount = 0
        setInitializedSafeLocations(false)
        for (player in Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 300 * 20, 1, false, false))
        }
        Bukkit.broadcastMessage("$prefix 게임월드 리셋을 시작합니다.")
        movePlayersToLobby("game")
        Bukkit.broadcastMessage("${ChatColor.GOLD}** 게임맵을 리셋하는 도중에는 잠시 서버가 렉이 걸릴수있습니다. ** \n** 움직임을 최소화 해주시길 바랍니다. **")
        // 월드 제거
        //Bukkit.broadcastMessage("$prefix 사용한 게임맵을 제거중입니다..")
        if (!worldManager.deleteWorld("game")) {
            return false
        }
        // 월드 복사
        //Bukkit.broadcastMessage("$prefix 백업된 게임맵을 불러오는 중입니다...")
        if (!worldManager.copyWorld("backupgame", "game")) {
            return false
        }


        //모든월드의 엔티티제거(플레이어제외)
        Bukkit.getWorlds().forEach { world ->
            world.entities.forEach { entity ->
                if (entity !is Player) {
                    entity.remove() // 플레이어가 아닌 엔티티를 제거
                    removedCount++
                }
            }
        }


        val console: CommandSender = Bukkit.getConsoleSender()

        val consolemessage = Bukkit.getServer().consoleSender
        consolemessage.sendMessage("$prefix 총 ${ChatColor.YELLOW}$removedCount ${ChatColor.GREEN}개의 엔티티가 제거되었습니다.")

        Bukkit.dispatchCommand(console, "save-all")
        Bukkit.dispatchCommand(console, "mv unload game")
        Bukkit.dispatchCommand(console, "mv load game")
        Bukkit.dispatchCommand(console, "plugman reload Multiverse-Portals")
        ChunkyManager.loadchunky()
        return true
    }

    fun handleLobbyReset(player: Player): Boolean {
        player.sendMessage("$prefix 로비월드 리셋을 시작합니다.")
        movePlayersToLobby("lobby")
        // 월드 제거
        //sender.sendMessage("$prefix 사용한 로비맵을 제거중입니다..")
        if (!worldManager.deleteWorld("lobby")) {
            return false
        }
        // 월드 복사
        //sender.sendMessage("$prefix 백업된 로비맵을 불러오는 중입니다...")
        if (!worldManager.copyWorld("backuplobby", "lobby")) {
            return false
        }

        // 포탈 플러그인 설정 리로드
        //sender.sendMessage("$prefix 포탈 정보를 다시 불러오고 있습니다...")
        val console: CommandSender = Bukkit.getConsoleSender()
        val cmd = "plugman reload Multiverse-Portals" // 실행할 명령어

        Bukkit.dispatchCommand(console, cmd)
        player.sendMessage("$prefix 로비맵 리셋이 완료되었습니다.")
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

    fun resetplayerAttribute() {
        for (player in Bukkit.getOnlinePlayers()) {

            val playerhealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            playerhealth?.baseValue = 20.0 // 최대 체력을 설정합니다.
            player.health = 20.0 // 현재 체력도 최대 체력에 맞춰줍니다.
            val playerdamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            if (playerdamage != null) {
                playerdamage.baseValue = 1.0
            }
        }
    }
}