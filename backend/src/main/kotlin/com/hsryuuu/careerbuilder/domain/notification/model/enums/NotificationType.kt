package com.hsryuuu.careerbuilder.domain.notification.model.enums

enum class NotificationType(val title: String, val message: String) {
    AI_EXPERIENCE_ANALYSIS_SUCCESS(NotificationTitle.AI_EXPERIENCE_ANALYSIS, "AI 경험분석 성공했습니다."),
    AI_EXPERIENCE_ANALYSIS_FAIL(NotificationTitle.AI_EXPERIENCE_ANALYSIS, "AI 경험분석에 실패했습니다. 이용횟수는 차감되지 않습니다."),
}

object NotificationTitle {
    const val AI_EXPERIENCE_ANALYSIS = "AI경험분석"
}
