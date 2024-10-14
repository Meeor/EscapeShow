package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.game.End.EndAction
import kr.rion.plugin.game.RandomTp.handleRandomListClear
import kr.rion.plugin.game.RandomTp.handleRandomReset
import kr.rion.plugin.game.RandomTp.handleRandomTPList
import kr.rion.plugin.game.Reset.handleGameReset
import kr.rion.plugin.game.Reset.handleLobbyReset
import kr.rion.plugin.game.Start.isStart
import kr.rion.plugin.game.Start.isStarting
import kr.rion.plugin.game.Start.startAction
import kr.rion.plugin.gameEvent.GameEvent
import kr.rion.plugin.gui.Event.eventGUI
import kr.rion.plugin.gui.Giveitem.ItemGUI
import kr.rion.plugin.gui.Resetgui.ResetGUI
import kr.rion.plugin.gui.randomTP.RandomTpGUI
import kr.rion.plugin.util.Item.berries
import kr.rion.plugin.util.Item.contract
import kr.rion.plugin.util.Item.flamegun
import kr.rion.plugin.util.Item.heal
import kr.rion.plugin.util.Item.mainMenu
import kr.rion.plugin.util.Item.map
import kr.rion.plugin.util.Item.teleportCompass
import kr.rion.plugin.util.global.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class InventoryClickListener : Listener {
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
                    if (!isStarting && !isStart) {
                        startAction()
                        event.isCancelled = true
                        player.closeInventory()
                    }
                }

                hasCustomTag(meta, "game-stop") -> {
                    if (isStarting) {
                        EndAction()
                        event.isCancelled = true
                        player.closeInventory()
                    }
                }

                hasCustomTag(meta, "game-setting") -> {
                    player.sendMessage("기능 제작중입니다")
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "game-contorl") -> {
                    player.sendMessage("기능 제작중입니다")
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "game-giveitem") -> {
                    player.closeInventory()
                    ItemGUI(player)
                    event.isCancelled = true
                }

                hasCustomTag(meta, "game-event") -> {
                    player.closeInventory()
                    eventGUI(player)
                    event.isCancelled = true

                }

                hasCustomTag(meta, "game-randomtp") -> {
                    player.closeInventory()
                    RandomTpGUI(player)
                    event.isCancelled = true
                }

                hasCustomTag(meta, "game-reset") -> {
                    player.closeInventory()
                    ResetGUI(player)
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun GiveItemClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}아이템지급") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "give-flamegun") -> {
                    player.inventory.addItem(flamegun())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-heal") -> {
                    player.inventory.addItem(heal())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-berries") -> {
                    player.inventory.addItem(berries())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-contract") -> {
                    player.inventory.addItem(contract())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-map") -> {
                    player.inventory.addItem(map())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-teleportCompass") -> {
                    player.inventory.addItem(teleportCompass())
                    event.isCancelled = true
                    player.closeInventory()
                }

                hasCustomTag(meta, "give-mainMenu") -> {
                    player.inventory.addItem(mainMenu())
                    event.isCancelled = true
                    player.closeInventory()
                }
            }
        }
    }

    @EventHandler
    fun ResetClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}리셋") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "reset-game") -> {
                    event.isCancelled = true
                    handleGameReset()
                    player.closeInventory()
                }

                hasCustomTag(meta, "reset-lobby") -> {
                    event.isCancelled = true
                    handleLobbyReset(player)
                    player.closeInventory()
                }
            }
        }
    }

    @EventHandler
    fun RandomTpClickEvent(event: InventoryClickEvent) {
        if (event.view.title != "${ChatColor.DARK_BLUE}랜덤티피") return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem
        if (item != null && item.hasItemMeta()) {
            val meta = item.itemMeta ?: return

            when {
                hasCustomTag(meta, "randomtp-list") -> {
                    handleRandomTPList(player)
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "randomtp-reset") -> {
                    event.isCancelled = true
                    handleRandomReset(player)
                    player.closeInventory()
                }
                hasCustomTag(meta, "randomtp-delete") -> {
                    handleRandomListClear(player)
                    event.isCancelled = true
                    player.closeInventory()
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
                    GameEvent.weatherClear()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-rain") -> {
                    GameEvent.weatherRain()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-gravity") -> {
                    GameEvent.gravity()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-earthQuake") -> {
                    GameEvent.earthQuake(player)
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-donation") -> {
                    GameEvent.donation()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-deathCoin") -> {
                    GameEvent.deathCoin()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-betting") -> {
                    GameEvent.betting()
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta, "event-random") -> {
                    GameEvent.randomEvent(player)
                    event.isCancelled = true
                    player.closeInventory()
                }
                hasCustomTag(meta,"nothing") -> {
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
