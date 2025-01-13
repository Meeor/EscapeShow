package kr.rion.plugin.event

import kr.rion.plugin.manager.MissionManager.activeMissions
import kr.rion.plugin.util.Global.centerText
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class PlayerSlotLimiter: Listener {
    private val missionDetails = mapOf(
        1 to
                "§9맵에 설치된 §5통을 §650§9번 열으세요!\n" +
                "\n" +
                "§d같은 통을 열어도 인정됩니다!",
        2 to
                "§9맵에 설치된 §5통을 §6100§9번 열으세요!\n" +
                "\n" +
                "§d같은 통을 열어도 인정됩니다!",
        3 to
                "단검을 2개 얻으세요!\n",
        4 to
                "철을 30개 구우세요!\n" +
                "\n" +
                "",
        5 to
                "달콤한 열매를 30개 얻으세요!\n",
        6 to
                "모든 무기종류를 얻으세요!",
        7 to
                "나무판자를 5개 캐세요!",
        8 to
                "나무판자를 10개 캐세요!",
        9 to
                "나무판자 10개를 설치하세요!",
        10 to
                "플레이어 3명을 죽이세요!",
        11 to
                "플레이어 5명을 죽이세요!",
        12 to
                "플레이어 10명을 죽이세요!",
        13 to
                "맨손으로 플레이어 1명을 죽이세요!",
        14 to
                "조약돌로 플레이어 1명을 죽이세요!",
        15 to
                "누적 20데미지를 넣으세요!"
        // 다른 미션 정보 추가
    )


    @EventHandler(ignoreCancelled = false)
    fun onInventoryClick(event: InventoryClickEvent) {
        val slot = event.slot // 클릭된 슬롯 번호
        val player = event.whoClicked as? Player ?: return

        // 플레이어 인벤토리에서만 처리
        if (event.clickedInventory == player.inventory) {
            if (slot == 20) { // 20번 슬롯에 대해서만 처리
                event.isCancelled = true // 기본 동작 취소

                // 미션 ID 가져오기
                val missionId = activeMissions[player] ?: run {
                    player.sendMessage("현재 활성화된 미션이 없습니다.")
                    return
                }

                // 책과 깃펜 아이템 생성
                val book = ItemStack(Material.WRITTEN_BOOK)
                val meta = book.itemMeta as BookMeta

                // 책 내용 설정 (미션 ID에 따라 다르게 설정)
                meta.addPage(
                    "${centerText("§l§1Mission")}\n\n" +
                            centerText(missionDetails[missionId] ?: "§l§c미션 정보 없음")
                )

                // 메타데이터를 아이템에 적용
                book.itemMeta = meta

                // 플레이어에게 책을 보여줌
                player.openBook(book)
            } else if (slot in 9..35) { // 다른 슬롯은 클릭 비활성화
                event.isCancelled = true
            }
        }
    }
}