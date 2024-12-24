package kr.rion.plugin.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.WrappedChatComponent
import org.bukkit.entity.Player

object actionbar {
    fun sendActionBar(player: Player, message: String) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT)
        packet.chatComponents.write(0, WrappedChatComponent.fromText(message))
        packet.bytes.write(0, 2.toByte()) // 2: ActionBar
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

}