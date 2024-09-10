package kr.rion.plugin.event

import kr.rion.plugin.Loader
import kr.rion.plugin.item.ItemAction.handleBerries
import kr.rion.plugin.item.ItemAction.handleContract
import kr.rion.plugin.item.ItemAction.handleFlameGun
import kr.rion.plugin.item.ItemAction.handleHeal
import kr.rion.plugin.item.ItemAction.handleMap
import kr.rion.plugin.util.global.prefix
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable


/*

init {
    // 각종 이벤트 리스너 등록
    Bukkit.getPluginManager().registerEvents(PlayerInteractListener(), Bukkit.getPluginManager().plugins[0])
    Bukkit.getPluginManager().registerEvents(MovementListener(), Bukkit.getPluginManager().plugins[0])
    Bukkit.getPluginManager().registerEvents(EntityDamageListener(), Bukkit.getPluginManager().plugins[0])
    // 다른 이벤트 리스너들도 추가 가능
}

*/




/////////////////////파일 분리 예정/////////////////
class EventListener(private val plugin: Loader) : Listener {
    val line = "=".repeat(40)
    val teleport = plugin.getTeleport()


    //사망시 관전모드
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.SPECTATOR
        Bukkit.broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.RESET}님께서 ${ChatColor.RED}사망${ChatColor.RESET}하였습니다.")
    }

    //사망시 리스폰위치변경
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        // 리스폰 위치를 변경하지 않거나 특정 위치로 설정할 수 있습니다.
        // 예를 들어, 원래 리스폰 위치를 설정하지 않도록 하고 싶다면 이 코드를 추가합니다:
        event.respawnLocation = player.location // 플레이어의 사망 위치로 리스폰 설정
    }

    //채팅금지
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!event.player.hasPermission("chat.on")) {
            event.isCancelled = true
            event.player.sendMessage("$prefix §l§c채팅이 금지되어있습니다.")
        }
    }

    //접속 메세지
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onjoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                // 비동기 작업에서 플레이어에게 인사 메시지 전송
                event.player.sendMessage("${ChatColor.GOLD}$line\n\n")
                event.player.sendMessage("")
                event.player.sendMessage(
                    "${ChatColor.YELLOW}왁타버스 마인크래프트 조공 컨텐츠\n" + "${ChatColor.GREEN}Escape Show ${ChatColor.YELLOW}에 오신것을 환영합니다."
                )
                event.player.sendMessage("")
                event.player.sendMessage(
                    "${ChatColor.AQUA}Esc 키 -> 설정 -> 비디오 설정 -> 쉐이더팩 설정에서\n" + "${ChatColor.LIGHT_PURPLE}Bliss_v2.0.4_(Chocapic13_Shaders_edit).zip\n" + "${ChatColor.AQUA}을 적용해주시길 바랍니다."
                )
                event.player.sendMessage("")
                event.player.sendMessage("${ChatColor.YELLOW}※TIP※ : 채팅창이 거슬린다면 \n           ${ChatColor.YELLOW}키보드 F3 키와 D 키를 동시에 눌러주세요")
                event.player.sendMessage("")
                event.player.sendMessage("${ChatColor.GOLD}$line")
            }
        }.runTask(Bukkit.getPluginManager().getPlugin("EscapeShow")!!) // YourPluginName을 실제 플러그인 이름으로 변경

    }

    //탈출한 플레이어가 받는 모든데미지 무시
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true
            }
        }
    }

    //탈출한 플레이어가 받는 플레이어공격및엔티티공격 전부무시
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.ADVENTURE && player.scoreboardTags.contains("EscapeComplete")) {
                event.isCancelled = true // 다른 플레이어의 공격도 무효화
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerUse(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val itemMeta = item.itemMeta ?: return
        val player = event.player
        val flamegun = NamespacedKey("EscapeShow", "flamegun")
        val heal = NamespacedKey("EscapeShow", "heal")
        val berries = NamespacedKey("EscapeShow", "berries")
        val contract = NamespacedKey("EscapeShow", "contract")
        val map = NamespacedKey("EscapeShow", "map")
        if (event.hand == EquipmentSlot.HAND) {
            if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                when (item.type) {
                    Material.FLINT -> {
                        if (itemMeta.displayName == "${ChatColor.RED}플레어건" && itemMeta.persistentDataContainer.has(
                                flamegun,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleFlameGun(player)
                        }
                    }

                    Material.PAPER -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}붕대" && itemMeta.persistentDataContainer.has(
                                heal,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleHeal(player)
                        }
                    }

                    Material.GLOW_BERRIES -> {
                        if (itemMeta.displayName == "${ChatColor.GREEN}농축된 열매" && itemMeta.persistentDataContainer.has(
                                berries,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleBerries(player)
                        }
                    }

                    Material.SKULL_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.of("#FF4242")}계약서" && itemMeta.persistentDataContainer.has(
                                contract,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleContract(player)
                        }
                    }

                    Material.MOJANG_BANNER_PATTERN -> {
                        if (itemMeta.displayName == "${ChatColor.GRAY}지도" && itemMeta.persistentDataContainer.has(
                                map,
                                PersistentDataType.STRING
                            )
                        ) {
                            handleMap(player)
                        }
                    }

                    else -> null
                }
            }
        }
    }

}