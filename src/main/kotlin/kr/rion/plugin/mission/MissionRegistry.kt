package kr.rion.plugin.mission

object MissionRegistry {
    private val missions = mutableMapOf<Int, Mission>()

    // 미션 등록
    fun registerMission(id: Int, mission: Mission) {
        if (missions.containsKey(id)) {
            throw IllegalArgumentException("이미 존재하는 미션 ID입니다: $id")
        }
        missions[id] = mission
    }

    // 고유 번호로 미션 조회
    fun getMission(id: Int): Mission? {
        return missions[id]
    }

    // 모든 미션 반환
    fun getAllMissions(): Map<Int, Mission> {
        return missions
    }
}