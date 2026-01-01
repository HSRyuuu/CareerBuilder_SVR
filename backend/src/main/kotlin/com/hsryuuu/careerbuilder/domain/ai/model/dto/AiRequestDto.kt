package com.hsryuuu.careerbuilder.domain.ai.model.dto

import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.model.type.ReferenceType
import java.time.LocalDateTime
import java.util.UUID

data class AiRequestDto(
    val id: UUID,
    val userId: UUID,
    val requestType: AiRequestType,
    val status: AiRequestStatus,
    val referenceId: UUID?,
    val referenceType: ReferenceType?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(entity: AiRequest): AiRequestDto {
            return AiRequestDto(
                id = entity.id,
                userId = entity.userId,
                requestType = entity.requestType,
                status = entity.status,
                referenceId = entity.referenceId,
                referenceType = entity.referenceType,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
