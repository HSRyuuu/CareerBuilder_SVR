package com.hsryuuu.careerbuilder.domain.notification.event

import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType
import java.util.*

data class NotificationEvent(
    val userId: UUID,
    val type: NotificationType,
    val targetId: String? = null // 상세 페이지 이동을 위한 ID
)