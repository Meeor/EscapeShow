package kr.rion.plugin.manager

import kr.rion.plugin.Loader
import kr.rion.plugin.mission.Mission.Companion.MISSIONPREFIX
import kr.rion.plugin.mission.MissionRegistry
import kr.rion.plugin.util.Global.TeamGame
import kr.rion.plugin.util.Item.createMissionEscapePaper
import kr.rion.plugin.util.Delay
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.persistence.PersistentDataType
import java.util.*

object MissionManager {
    val activeMissions = mutableMapOf<UUID, Int>()
    private val missionName = mapOf(
        1 to "통 100번열기",
        2 to "통 200번 열기",
        3 to "단검 5개 얻기",
        4 to "철 15개 굽기",
        5 to "달콤한 열매 20개 얻기",
        6 to "무기로 분류된 아이템 3개 얻기(중복 가능)",
        7 to "판자더미 20개 설치하기",
        8 to "플레이어 1명 처치하기",
        9 to "플레이어 2명 처치하기",
        10 to "플레이어 3명 처치하기",
        11 to "맨손으로 플레이어 처치하기",
        12 to "돌멩이을 손에 들고 플레이어 처치하기",
        13 to "누적 40데미지 넣기",
        14 to "갑옷 조합 하기",
        15 to "강철 갑옷 조합 하기",
        16 to "부활하기",
        17 to "시체 털기",
        18 to "가죽 30개 얻기",
        19 to "누적회복 20 받기",
        20 to "계약서 3번 사용하기",
        21 to "지도 3번 사용하기",
        22 to "부목 3번 사용하기",
        23 to "붕대 10개 사용하기",
        24 to "붕대 15개 사용하기",
        25 to "붕대 20개 사용하기",
        26 to "누적 데미지 20 받기",
        27 to "10분간 데미지 안받기",
        28 to "마지막까지 살아남기"
    )

    fun assignMission(player: Player) {
        // 1~50 사이의 랜덤 번호 생성
        val excludedMissions = if (!TeamGame) listOf(16, 17) else emptyList()
        val availableMissions = (1..28).filterNot { it in excludedMissions }
        val randomMissionId = availableMissions.random()


        val mission = MissionRegistry.getMission(randomMissionId)
        if (mission != null) {
            // 태그 추가
            player.addScoreboardTag("MissionNo$randomMissionId")
            activeMissions[player.uniqueId] = randomMissionId
            mission.missionStart(player)
            player.sendMessage("${MISSIONPREFIX}미션이 지급되었습니다.\n§b당신이 받은 미션은 : §e${missionName[randomMissionId]} 입니다.")
        } else {
            player.sendMessage("${MISSIONPREFIX}등록되지 않은 미션이 확인되어 미션 지급이 취소되었습니다.")
        }
    }

    //테스트용 미션 수동부여 함수
    fun assignSpecificMission(sender: CommandSender, player: Player, missionId: Int) {
        // 유효한 미션 ID 범위 확인
        if (missionId !in 1..28) {
            sender.sendMessage("${MISSIONPREFIX}잘못된 미션 ID입니다. 유효한 범위는 1~28입니다.")
            return
        }
        val mission = MissionRegistry.getMission(missionId)
        if (mission != null) {
            // 태그 추가
            player.addScoreboardTag("MissionNo$missionId")
            activeMissions[player.uniqueId] = missionId
            mission.missionStart(player)
            player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 부여되었습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}미션이 성공적으로 부여되었습니다: ID $missionId")
            player.sendMessage("${MISSIONPREFIX}미션이 지급되었습니다.\n§b당신이 받은 미션은 : §e${missionName[missionId]} 입니다.")
        } else {
            player.sendMessage("${MISSIONPREFIX}해당 미션을 찾을 수 없습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}해당 미션을 찾을 수 없습니다: ID $missionId")
        }
    }

    //테스트용 미션 수동 제거
    fun removeMission(sender: CommandSender, player: Player) {
        // 플레이어에게 활성화된 미션이 있는지 확인
        val missionId = activeMissions[player.uniqueId]
        player.scoreboardTags.clear()
        if (missionId != null) {
            activeMissions.remove(player.uniqueId)
            player.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
            sender.sendMessage("${MISSIONPREFIX}미션이 성공적으로 제거되었습니다: ID $missionId")
        } else {
            player.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
            sender.sendMessage("${MISSIONPREFIX}현재 할당된 미션이 없습니다.")
        }
    }

    // ID와 메시지를 매핑
    private val missionMessages = mapOf(
        1 to "축하합니다! §d통 §e100§b번 열기 미션을 완료하셨습니다!",
        2 to "축하합니다! §d통 §e200§b번 열기 미션을 완료하셨습니다!",
        3 to "축하합니다! §d단검 §e5§b개 얻기 미션을 완료하셨습니다!",
        4 to "축하합니다! §d철 §e15§b개 굽기 미션을 완료하셨습니다!",
        5 to "축하합니다! §d달콤한 열매 §e20§b개 얻기 미션을 완료하셨습니다!",
        6 to "축하합니다! §d무기종류 §e3§b개 얻기 미션을 완료하셨습니다!",
        7 to "축하합니다! §d판자더미 §e10§b개 설치하기 미션을 완료하셨습니다!",
        8 to "축하합니다! §d플레이어 §e1§b명 처치하기 미션을 완료하셨습니다!",
        9 to "축하합니다! §d플레이어 §e2§b명 처치하기 미션을 완료하셨습니다!",
        10 to "축하합니다! §d플레이어 §e3§b명 처치하기 미션을 완료하셨습니다!",
        11 to "축하합니다! §d맨손으로 플레이어 §e1§b명 처치하기 미션을 완료하셨습니다!",
        12 to "축하합니다! §d돌멩이로 플레이어 §e1§b명 처치하기 미션을 완료하셨습니다!",
        13 to "축하합니다! §b누적 §e40§d데미지§b 넣기 미션을 완료하셨습니다!",
        14 to "축하합니다! 갑옷§b 얻기 미션을 완료하셨습니다!",
        15 to "축하합니다! 강철갑옷 §b얻기 미션을 완료하셨습니다!",
        16 to "축하합니다! §b부활 하기 미션을 완료하셨습니다!",
        17 to "축하합니다! §b시체 털기 미션을 완료하셨습니다!",
        18 to "축하합니다! §d가죽 §e30§b개 얻기 미션을 완료하셨습니다!",
        19 to "축하합니다! §b누적 §d회복§e20§b 받기 미션을 완료하셨습니다!",
        20 to "축하합니다! §d계약서 §e3§b번 사용하기 미션을 완료하셨습니다!",
        21 to "축하합니다! §d지도 §e3§b번 사용하기 미션을 완료하셨습니다!",
        22 to "축하합니다! §d부목 §e5§b번 사용하기 미션을 완료하셨습니다!",
        23 to "축하합니다! §d붕대 §e10§b개 사용하기 미션을 완료하셨습니다!",
        24 to "축하합니다! §d붕대 §e15§b개 사용하기 미션을 완료하셨습니다!",
        25 to "축하합니다! §d붕대 §e20§b개 사용하기 미션을 완료하셨습니다!",
        26 to "축하합니다! §b누적 §e20§d데미지 §b받기 미션을 완료하셨습니다!",
        27 to "축하합니다! §e10분§b동안 데미지 안받기 미션을 완료 하셨습니다!",
        28 to "축하합니다! §b마지막까지 살아남으셔서 미션에 성공하셨습니다!"
    )

    fun handleEvent(player: Player, event: Event) {
        // 플레이어의 활성화된 미션 ID 확인
        val missionId = activeMissions[player.uniqueId] ?: return
        val mission = MissionRegistry.getMission(missionId) ?: return
        if (missionId == 28) return

        // 미션 성공 여부 확인
        if (mission.checkEventSuccess(player, event)) {
            mission.onSuccess(player)

            // ID에 따른 메시지 출력
            val successMessage = missionMessages[missionId]
                ?: "축하합니다! 미션을 완료했습니다!" // 기본 메시지
            player.sendMessage("$MISSIONPREFIX$successMessage")
            player.playSound(
                player.location, // 플레이어 위치
                Sound.UI_TOAST_CHALLENGE_COMPLETE, // 사운드 이름
                1.0f, // 볼륨
                1.0f // 피치
            )

            activeMissions.remove(player.uniqueId) // 미션 완료 후 제거

            if (!TeamGame) {
                val item = createMissionEscapePaper()
                val meta = item.itemMeta!!
                val keyOwner = NamespacedKey(Loader.instance, "owner")
                meta.persistentDataContainer.set(keyOwner, PersistentDataType.STRING, player.uniqueId.toString())
                item.itemMeta = meta
                val leftover = player.inventory.addItem(item)

                // 인벤토리에 안 들어간 아이템이 있다면 바닥에 드롭
                if (leftover.isNotEmpty()) {
                    leftover.values.forEach { remainingItem ->
                        val dropped = player.world.dropItemNaturally(player.location, remainingItem)
                        dropped.customName = "§e${player.name}§a의 미션 클리어 인증서"
                        dropped.isCustomNameVisible = true
                        player.sendMessage("§l§c인벤토리에 공간이 부족하여 아이템을 떨궜습니다.")
                    }
                }

                Bukkit.getLogger().info("${player.name} 님께서 미션에 성공하여 종이를 지급했습니다.\n\n아이템 정보 : \n$item\n\n")
            }

        }
    }

    fun endGame() {
        Delay.delayForEachPlayer(
            Bukkit.getOnlinePlayers(),
            action = { player ->
                val missionId = activeMissions[player.uniqueId] ?: return@delayForEachPlayer
                val mission = MissionRegistry.getMission(missionId) ?: return@delayForEachPlayer
                if (missionId != 28) return@delayForEachPlayer
                mission.onSuccess(player)

                // ID에 따른 메시지 출력
                val successMessage = missionMessages[missionId]
                    ?: "축하합니다! 미션을 완료했습니다!" // 기본 메시지
                player.sendMessage("$MISSIONPREFIX$successMessage")
                player.playSound(
                    player.location, // 플레이어 위치
                    Sound.UI_TOAST_CHALLENGE_COMPLETE, // 사운드 이름
                    0.2f, // 볼륨
                    1.0f // 피치
                )

                activeMissions.remove(player.uniqueId) // 미션 완료 후 제거
            }
        )
    }

    fun resetMissions() {
        activeMissions.clear()
        // 모든 등록된 미션의 데이터를 초기화
        MissionRegistry.getAllMissions().values.forEach { mission ->
            mission.reset()
        }
    }

}