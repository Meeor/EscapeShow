package kr.rion.plugin.event

import de.maxhenkel.voicechat.api.VoicechatApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import kr.rion.plugin.command.Broadcast.isPlayerInBroadcast
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
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
        registration.registerEvent(MicrophonePacketEvent::class.java, this::broadcastPacket)
    }

    fun onMicrophonePacket(event: MicrophonePacketEvent) {
        val api = event.voicechat  // VoiceChat API 인스턴스 가져오기
        val senderUUID = event.senderConnection?.player?.uuid  // 송신자 UUID 가져오기
        val sender = senderUUID?.let { Bukkit.getPlayer(it) }  // 송신자 Player 객체 가져오기

        // 송신자 null 여부 및 태그 확인
        if (sender != null && sender.scoreboardTags.any { it in listOf("EscapeComplete", "death") }) {
            event.cancel()  // 이벤트 취소

            // 서버의 모든 플레이어를 순회하며 태그 조건 확인 후 소리 전송
            Bukkit.getServer().onlinePlayers.filter { onlinePlayer ->
                onlinePlayer.uniqueId != sender.uniqueId && onlinePlayer.scoreboardTags.any {
                    it in listOf("EscapeComplete", "death", "manager","MissionSuccessEscape")
                }
            }.forEach { onlinePlayer ->
                val senderLocation = sender.location
                val senderPosition = api.createPosition(senderLocation.x, senderLocation.y, senderLocation.z)

                val connection = api.getConnectionOf(onlinePlayer.uniqueId) ?: return@forEach
                val locationalSoundPacket = event.packet.toLocationalSoundPacket(senderPosition)

                api.sendLocationalSoundPacketTo(connection, locationalSoundPacket)
            }
        }
    }


    fun broadcastPacket(event: MicrophonePacketEvent) {
        val api = event.voicechat  // VoiceChat API 인스턴스 가져오기
        val senderUUID = event.senderConnection?.player?.uuid  // 송신자 UUID 가져오기
        val sender = senderUUID?.let { Bukkit.getPlayer(it) }  // 송신자 Player 객체 가져오기

        // 송신자가 null이 아니고 방송 모드에 있는 경우
        if (sender != null && isPlayerInBroadcast(sender.name)) {
            sender.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent("§a방송모드")
            )
            // 서버에 있는 모든 플레이어에게 패킷 전송
            Bukkit.getServer().onlinePlayers.forEach { onlinePlayer ->
                if (onlinePlayer.uniqueId == sender.uniqueId) return@forEach // 송신자 본인은 제외
                val connection = api.getConnectionOf(onlinePlayer.uniqueId) ?: return@forEach
                val staticPacket = event.packet.toStaticSoundPacket()
                api.sendStaticSoundPacketTo(connection, staticPacket)
            }
        }
    }

}

