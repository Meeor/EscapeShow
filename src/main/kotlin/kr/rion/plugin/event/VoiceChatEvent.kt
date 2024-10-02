package kr.rion.plugin.event

import de.maxhenkel.voicechat.api.VoicechatApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import org.bukkit.Bukkit

class VoiceChatEvent : VoicechatPlugin {
    override fun getPluginId(): String {
        return "EscapeShow" // 고유 플러그인 ID를 문자열로 반환
    }

    override fun initialize(api: VoicechatApi) {
        // 여기서 필요한 초기화 작업을 수행할 수 있습니다.
        // 예를 들어 로그를 남기거나, API를 사용한 추가 작업 등을 설정합니다.
        Bukkit.getLogger().info("VoiceChat API가 초기화되었습니다!")
    }

    // 반드시 이벤트 등록!
    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(MicrophonePacketEvent::class.java, this::onMicrophonePacket)
    }

    fun onMicrophonePacket(event: MicrophonePacketEvent) {

        val api = event.voicechat  // VoiceChat API 인스턴스 가져오기
        val senderUUID = event.senderConnection?.player?.uuid // 송신자 UUID 가져오기
        val sender = senderUUID?.let { Bukkit.getPlayer(it) }  // 송신자 Player 객체



        // 송신자 태그 확인
        if (sender != null && (sender.scoreboardTags.contains("EscapeComplete") ||
                    sender.scoreboardTags.contains("death"))
        ) {
            event.cancel()

            // 서버에 있는 모든 플레이어 순회
            for (onlinePlayer in Bukkit.getServer().onlinePlayers) {
                // 송신자 본인에게는 소리 전송하지 않음
                if (onlinePlayer.uniqueId == sender.uniqueId) continue

                // 송신자의 Location에서 Position으로 변환
                val senderLocation = sender.location
                val senderPosition = api.createPosition(senderLocation.x, senderLocation.y, senderLocation.z)

                // 수신자 연결 정보 가져오기
                val connection = api.getConnectionOf(onlinePlayer.uniqueId) ?: continue

                // 수신자도 태그가 있는지 확인
                if (onlinePlayer.scoreboardTags.contains("EscapeComplete") ||
                    onlinePlayer.scoreboardTags.contains("death") ||
                    onlinePlayer.scoreboardTags.contains("manager")
                ) {

                    // 태그가 일치하는 수신자에게 위치 기반 사운드 패킷 전송 (거리 기반 효과 포함)
                    val locationalSoundPacket = event.packet.toLocationalSoundPacket(senderPosition)
                    api.sendLocationalSoundPacketTo(connection, locationalSoundPacket)
                }
                else {
                    // 태그가 없는 플레이어는 소리 전송 안 함
                    continue
                }
            }
        }
    }
}

