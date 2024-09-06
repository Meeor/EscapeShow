package kr.rion.plugin.manager

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVWorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class WorldManager(plugin: JavaPlugin) {
    private var worldManager: MVWorldManager? = null
    private val prefix = "${ChatColor.BOLD}${ChatColor.AQUA}[Escape Show]${ChatColor.RESET}${ChatColor.RED}"
    private val console = plugin.server.consoleSender

    init {
        // MultiverseCore 플러그인에서 MVWorldManager 를 초기화합니다.
        val multiverseCore = Bukkit.getPluginManager().getPlugin("Multiverse-Core") as? MultiverseCore
        worldManager = multiverseCore?.mvWorldManager // getMVWorldManager() 메서드를 사용
    }

    fun deleteWorld(worldName: String, sender: CommandSender): Boolean {
        if (worldManager == null) {
            console.sendMessage("$prefix ${ChatColor.YELLOW}MVWorldManager${ChatColor.RED}가 초기화되지 않았습니다.")
            sender.sendMessage("$prefix ${ChatColor.YELLOW}MVWorldManager${ChatColor.RED}가 초기화되지 않았습니다.")
            return false
        }
        val world = worldManager?.getMVWorld(worldName)
        if (world != null) {
            // 월드를 언로드합니다.
            Bukkit.unloadWorld(worldName, false)

            // 월드를 삭제합니다.
            return if (worldManager?.deleteWorld(worldName) == true) {
                console.sendMessage("$prefix ${ChatColor.YELLOW}월드 ${ChatColor.GREEN}[$worldName] ${ChatColor.YELLOW}가 ${ChatColor.RED}삭제${ChatColor.YELLOW}되었습니다.")
                true
            } else {
                console.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$worldName] ${ChatColor.RED}삭제에 실패했습니다.")
                sender.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$worldName] ${ChatColor.RED}삭제에 실패했습니다.")
                false
            }
        } else {
            console.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$worldName] ${ChatColor.RED}를 찾을 수 없습니다.")
            sender.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$worldName] ${ChatColor.RED}를 찾을 수 없습니다.")
            return false
        }
    }

    fun copyWorld(copyWorldName: String, newWorldName: String, sender: CommandSender): Boolean {
        if (worldManager == null) {
            console.sendMessage("$prefix ${ChatColor.YELLOW}MVWorldManager${ChatColor.RED}가 초기화되지 않았습니다.")
            sender.sendMessage("$prefix ${ChatColor.YELLOW}MVWorldManager${ChatColor.RED}가 초기화되지 않았습니다.")
            return false
        }
        val world = worldManager?.getMVWorld(copyWorldName)
        return if (world != null) {
            // 월드를 복사합니다.
            if (worldManager?.cloneWorld(copyWorldName, newWorldName) == true) {
                console.sendMessage("$prefix ${ChatColor.GREEN}[$copyWorldName] ${ChatColor.YELLOW}를 ${ChatColor.GREEN}[$newWorldName]${ChatColor.YELLOW}으로 복사하였습니다.")
                true
            } else {
                console.sendMessage("$prefix ${ChatColor.GREEN}[$copyWorldName] ${ChatColor.RED}복사에 실패했습니다.")
                sender.sendMessage("$prefix ${ChatColor.GREEN}[$copyWorldName] ${ChatColor.RED}복사에 실패했습니다.")
                false
            }
        } else {
            console.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$copyWorldName] ${ChatColor.RED}를 찾을 수 없습니다.")
            sender.sendMessage("$prefix 월드 ${ChatColor.GREEN}[$copyWorldName] ${ChatColor.RED}를 찾을 수 없습니다.")
            false
        }
    }

    fun getMultiverseWorld(worldName: String): World? {
        val mvWorld = worldManager?.getMVWorld(worldName)
        return mvWorld?.cbWorld  // CraftBukkit 월드를 반환
    }
}

