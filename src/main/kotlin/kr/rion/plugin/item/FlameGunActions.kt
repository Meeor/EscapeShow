package kr.rion.plugin.item

import kr.rion.plugin.Loader
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object FlameGunActions {

    fun launchFlare(player: Player) {
        // 플레이어에게 사운드 재생
        for (playerall in Bukkit.getOnlinePlayers()) {
            playerall.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.5f)
            playerall.playSound(player.location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f)
        }
        val flamegun = NamespacedKey("EscapeShow", "flamegun")
        // 사용한 아이템의 메타데이터 변경
        val item = player.inventory.itemInMainHand
        val itemMeta = item.itemMeta ?: return
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.ITALIC}${ChatColor.STRIKETHROUGH}플레어건")
        itemMeta.persistentDataContainer.remove(flamegun)
        item.itemMeta = itemMeta


        val loc = player.location.clone().add(0.0, 1.0, 0.0) // 플레이어 머리 위치 (약간 위로) 복제본 생성
        val particleData1 = Particle.DustOptions(Color.fromRGB(1, 0, 0), 1.5f)
        val particleData2 = Particle.DustOptions(Color.fromRGB(1, 0, 0), 1.0f)
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            object : BukkitRunnable() {
                private var t = 0.0

                override fun run() {
                    t += 1
                    loc.add(0.0, 1.0, 0.0) // 매 틱마다 위치를 위로 이동
                    onlinePlayer.world.spawnParticle(
                        Particle.CLOUD,
                        loc,
                        2,
                        0.0,
                        0.6,
                        0.0,
                        0.0
                    )
                    onlinePlayer.world.spawnParticle(
                        Particle.FLAME,
                        loc,
                        2,
                        0.0,
                        0.6,
                        0.0,
                        0.0
                    )
                    // 80틱 작업 끝나면 중지
                    if (t > 80) {
                        cancel()
                        val movedLoc = loc.clone().add(0.0, -5.0, 0.0)
                        onlinePlayer.world.spawnParticle(
                            Particle.REDSTONE,
                            movedLoc,
                            50,
                            1.0,
                            1.0,
                            1.0,
                            0.1,
                            particleData1,
                            true
                        )
                        onlinePlayer.world.spawnParticle(
                            Particle.REDSTONE,
                            loc,
                            50,
                            0.0,
                            0.6,
                            0.0,
                            0.1,
                            particleData2,
                            true
                        )

                    }
                }
            }.runTaskTimer(Loader.instance ?: throw IllegalStateException("플러그인이 제대로 동작하지 않는것 같습니다."), 0L, 1L)
        }
    }
}