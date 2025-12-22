package com.hsryuuu.careerbuilder.domain.log.model.dto

import com.hsryuuu.careerbuilder.domain.log.UserActionLog
import java.time.LocalDateTime

// Request DTO
data class CreateUserActionLogRequest(
    val eventName: String,
    val eventCategory: String,
    val properties: String? = null,
    val userId: String? = null,
    val sessionId: String? = null,
    val pageUrl: String? = null,
    val referrer: String? = null
) {
    fun toEntity(
        userAgent: String?,
        ipAddress: String?
    ): UserActionLog = UserActionLog(
        eventName = eventName,
        eventCategory = eventCategory,
        properties = properties,
        userId = userId,
        sessionId = sessionId,
        pageUrl = pageUrl,
        referrer = referrer,
        userAgent = userAgent,
        ipAddress = ipAddress
    )
}

// Response DTO
data class UserActionLogResponse(
    val id: Long,
    val eventName: String,
    val eventCategory: String,
    val properties: String?,
    val userId: String?,
    val sessionId: String?,
    val pageUrl: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: UserActionLog): UserActionLogResponse = UserActionLogResponse(
            id = entity.id,
            eventName = entity.eventName,
            eventCategory = entity.eventCategory,
            properties = entity.properties,
            userId = entity.userId,
            sessionId = entity.sessionId,
            pageUrl = entity.pageUrl,
            createdAt = entity.createdAt
        )
    }
}
