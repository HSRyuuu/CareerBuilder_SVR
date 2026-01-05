package com.hsryuuu.careerbuilder.domain.notification.utils

import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType


object NotificationUrlProvider {

    fun createUrl(type: NotificationType, targetId: String?): String? {
        return when (type) {
            NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS -> {
                if (targetId == null) null
                else "/experience/$targetId/ai"
            }

            else -> null
        }
    }
}