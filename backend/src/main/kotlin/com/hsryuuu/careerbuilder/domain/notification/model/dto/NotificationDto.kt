package com.hsryuuu.careerbuilder.domain.notification.model.dto

import com.hsryuuu.careerbuilder.domain.notification.model.entity.Notification
import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType
import java.time.LocalDateTime
import java.util.*

data class CreateNotificationRequest(
    val userId: UUID,
    val type: NotificationType,
    val title: String,
    val content: String,
    val url: String? = null
) {
    fun toEntity(): Notification {
        return Notification(
            userId = userId,
            type = type,
            title = title,
            content = content,
            url = url
        )
    }
}

data class NotificationResponse(
    val id: UUID,
    val type: NotificationType,
    val title: String,
    val content: String,
    val url: String?,
    val isRead: Boolean,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id!!,
                type = notification.type,
                title = notification.title,
                content = notification.content,
                url = notification.url,
                isRead = notification.isRead,
                readAt = notification.readAt,
                createdAt = notification.createdAt
            )
        }
    }
}
