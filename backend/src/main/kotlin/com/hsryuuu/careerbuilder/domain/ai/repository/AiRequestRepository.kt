package com.hsryuuu.careerbuilder.domain.ai.repository

import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiProcessType
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface AiRequestRepository : JpaRepository<AiRequest, UUID> {
    fun countByUserIdAndRequestTypeAndStatusAndCreatedAtBetween(
        userId: UUID,
        requestType: AiProcessType,
        status: AiRequestStatus,
        start: LocalDateTime,
        end: LocalDateTime
    ): Int
}
