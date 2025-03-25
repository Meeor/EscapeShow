package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End.EndAction
import kr.rion.plugin.game.Reset.handleGameReset
import kr.rion.plugin.game.Reset.handleLobbyReset
import kr.rion.plugin.game.Start.isStart
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.game.Start.startAction
import kr.rion.plugin.gameEvent.FlameGunSpawn.spawnFlareGunChest
import kr.rion.plugin.gameEvent.GameEvent
import kr.rion.plugin.gui.Event.eventGUI
import kr.rion.plugin.gui.Giveitem.ItemGUI
import kr.rion.plugin.gui.Resetgui.ResetGUI
import kr.rion.plugin.gui.SettingMenu.settingGUI
import kr.rion.plugin.manager.TeamManager.teamPvpBoolean
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Global.door
import kr.rion.plugin.util.Global.prefix
import kr.rion.plugin.util.Helicopter.fillBlocks
import kr.rion.plugin.util.Helicopter.setBlockWithAttributes
import kr.rion.plugin.util.Item.berries
import kr.rion.plugin.util.Item.flamegun
import kr.rion.plugin.util.Item.mainMenu
import kr.rion.plugin.util.Item.teleportCompass
import kr.rion.plugin.util.delay
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class InventoryClickListener : Listener {
    private val worldWait = Bukkit.getWorld("vip")

    @EventHandler
    fun escapePlayerTeleport(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        val clickedMeta = clickedItem.itemMeta as? SkullMeta ?: return


        if (event.view.title == "${ChatColor.DARK_GREEN}텔레포트할 플레이어 선택") {
            // 클릭한 아이템이 플레이어 머리인 경우
            if (clickedItem.type == Material.PLAYER_HEAD) {
                val targetPlayer = Bukkit.getPlayer(clickedMeta.owningPlayer?.name ?: return)

                if (targetPlayer != null) {
                    // 텔레포트 처리
                    player.teleport(targetPlayer.location)
                    player.sendMessage("$prefix ${ChatColor.GREEN}${targetPlayer.name}님에게 텔레포트되었습니다.")
                } else {
                    player.sendMessage("$prefix ${ChatColor.RED}해당 플레이어는 서버에 접속한상태가 아닌것같습니다.")
                }

                event.isCancelled = true
                player.closeInventory()
            }
        }
    }

    @EventHandler
    fun mainMenuClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}메뉴") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "game-start") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    if (!isStarting && !isStart) {
                        startAction()
                    } else {
                        player.sendMessage("$prefix 이미 시작중입니다.")
                    }
                }

                hasCustomTag(meta, "game-stop") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    if (isStarting && !isStart) {
                        EndAction()
                    } else {
                        player.sendMessage("$prefix 게임이 시작되지 않았습니다.")
                    }
                }

                hasCustomTag(meta, "game-starting") -> {
                    event.isCancelled = true
                }


                hasCustomTag(meta, "game-flamegun") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    if (isStarting) {
                        spawnFlareGunChest(player, player.location)
                    } else {
                        player.sendMessage("$prefix 게임 진행중이 아닌것같습니다.")
                    }
                }

                hasCustomTag(meta, "game-giveitem") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    ItemGUI(player)
                }

                hasCustomTag(meta, "game-event") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    eventGUI(player)
                }

                hasCustomTag(meta, "game-setting") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    settingGUI(player)
                }

                hasCustomTag(meta, "game-reset") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    ResetGUI(player)
                }

                hasCustomTag(meta, "game-door") -> {
                    event.isCancelled = true
                    if (door) {
                        fillBlocks(
                            Location(worldWait, 23.0, 60.0, -46.0),
                            Location(worldWait, 23.0, 57.0, -44.0),
                            Material.AIR
                        )
                        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.AIR)
                        door = !door
                        player.sendMessage("$prefix ${ChatColor.YELLOW}문을 열었습니다.")
                    } else {
                        fillBlocks(
                            Location(worldWait, 23.0, 60.0, -46.0),
                            Location(worldWait, 23.0, 57.0, -44.0),
                            Material.OAK_FENCE
                        )
                        setBlockWithAttributes(Location(worldWait, 23.0, 61.0, -45.0), Material.OAK_FENCE)
                        door = !door
                        player.sendMessage("$prefix ${ChatColor.YELLOW}문을 닫았습니다.")
                    }
                }

                hasCustomTag(meta, "game-player") -> {
                    event.isCancelled = true
                }


                hasCustomTag(meta, "nothing") -> {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun giveItemClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}아이템지급") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "give-flamegun") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    player.inventory.addItem(flamegun())
                }

                hasCustomTag(meta, "give-berries") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    player.inventory.addItem(berries())
                }

                hasCustomTag(meta, "give-teleportCompass") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    player.inventory.addItem(teleportCompass())
                }

                hasCustomTag(meta, "give-mainMenu") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    player.inventory.addItem(mainMenu())
                }
            }
        }
    }

    @EventHandler
    fun resetClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}리셋") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "reset-game") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    handleGameReset()
                }

                hasCustomTag(meta, "reset-lobby") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    handleLobbyReset(player)
                }
            }
        }
    }

    @EventHandler
    fun settingClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}게임설정") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return
            if (isStarting && isStart) {
                player.sendMessage("$prefix §c게임 진행중에는 변경하실수 없습니다.")
                return
            }

            when {
                hasCustomTag(meta, "team-pvp-noting") -> {
                    event.isCancelled = true
                }

                hasCustomTag(meta, "team-pvp-true") || hasCustomTag(meta, "team-pvp-false") -> {
                    event.isCancelled = true
                    teamPvpBoolean = !teamPvpBoolean // ✅ 상태 반전 (true → false, false → true)

                    delay.delayRun(2) {
                        player.closeInventory()
                        settingGUI(player) // ✅ 변경된 상태 반영하여 GUI 다시 열기
                    }

                    val statusMessage = if (teamPvpBoolean) {
                        "${ChatColor.GREEN}팀 PVP를 활성화 하였습니다."
                    } else {
                        "${ChatColor.RED}팀 PVP를 비활성화 하였습니다."
                    }
                    player.sendMessage("$prefix $statusMessage")
                }

                hasCustomTag(meta, "team-game") || hasCustomTag(meta, "solo-game") -> {
                    event.isCancelled = true
                    TeamGame = !TeamGame // ✅ 상태 반전 (true → false, false → true)
                    delay.delayRun(2) {
                        player.closeInventory()
                        settingGUI(player) // ✅ 변경된 상태 반영하여 GUI 다시 열기
                    }

                    val statusMessage = if (TeamGame) {
                        "${ChatColor.AQUA}게임방식이 ${ChatColor.YELLOW}팀전${ChatColor.AQUA}으로 변경되었습니다."
                    } else {
                        "${ChatColor.AQUA}게임방식이 ${ChatColor.YELLOW}개인전${ChatColor.AQUA}으로 변경되었습니다."
                    }
                    player.sendMessage("$prefix $statusMessage")
                }

            }
        }
    }

    @EventHandler
    fun gameEventClick(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}이벤트") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "event-sun") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    if (isStarting) {
                        GameEvent.weatherClear()
                    } else {
                        player.sendMessage("$prefix 게임 진행중이 아닌것같습니다.")
                    }
                }

                hasCustomTag(meta, "event-rain") -> {
                    event.isCancelled = true
                    player.closeInventory()
                    if (isStarting) {
                        GameEvent.weatherRain()
                    } else {
                        player.sendMessage("$prefix 게임 진행중이 아닌것같습니다.")
                    }
                }

                hasCustomTag(meta, "nothing") -> {
                    event.isCancelled = true
                }
            }
        }
    }

    // 태그가 있는지 확인하는 함수
    private fun hasCustomTag(meta: ItemMeta, tag: String): Boolean {
        val key = NamespacedKey(Loader.instance, tag)
        return meta.persistentDataContainer.has(key, PersistentDataType.BOOLEAN)
    }
}
