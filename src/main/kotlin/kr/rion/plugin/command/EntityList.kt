package kr.rion.plugin.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType

object EntityList {
    fun sendEntityList(sender: CommandSender) {
        val entityCountMap = mutableMapOf<EntityType, Int>()

        // 모든 월드의 엔티티를 확인
        for (world in Bukkit.getWorlds()) {
            for (entity in world.entities) {
                val entityType = entity.type
                entityCountMap[entityType] = entityCountMap.getOrDefault(entityType, 0) + 1
            }
        }

        // 결과 메세지 생성 및 플레이어에게 전달
        if (entityCountMap.isEmpty()) {
            sender.sendMessage("현재 서버에 소환된 엔티티가 없습니다.")
        } else {
            sender.sendMessage("서버에 소환된 엔티티 목록:")
            for ((entityType, count) in entityCountMap) {
                sender.sendMessage("${entityType.name}: $count")
            }
        }
    }
}