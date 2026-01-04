package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.event.ExperienceAnalysisEvent
import com.hsryuuu.careerbuilder.domain.ai.model.dto.AiRequestDto
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.model.type.ReferenceType
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ExperienceAnalysisService(
    private val experienceRepository: ExperienceRepository,
    private val aiRequestRepository: AiRequestRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun requestAnalysis(userId: UUID, experienceId: UUID): AiRequestDto {
        val experience = experienceRepository.findByIdOrNull(experienceId)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        validateAiAnalysisAvailable(experience, userId)

        val aiRequest = AiRequest(
            userId = userId,
            requestType = AiRequestType.EXPERIENCE_ANALYSIS,
            status = AiRequestStatus.PENDING,
            referenceType = ReferenceType.EXPERIENCES,
            referenceId = experienceId
        )

        val savedRequest = aiRequestRepository.save(aiRequest)
        // ExperienceAnalysisEventListener.java
        log.info("AI 경험 분석 이벤트 발행 | aiRequestId: ${savedRequest.id}, experienceId: $experienceId, userId: $userId")
        eventPublisher.publishEvent(
            ExperienceAnalysisEvent(
                aiRequestId = savedRequest.id,
                experienceId = experienceId,
                userId = userId
            )
        )

        return AiRequestDto.from(savedRequest)
    }

    private fun validateAiAnalysisAvailable(
        experience: Experience,
        userId: UUID
    ) {
        if (experience.user.id != userId)
            throw GlobalException(ErrorCode.FORBIDDEN)
        if (experience.status == ExperienceStatus.INCOMPLETE)
            throw GlobalException(ErrorCode.EXPERIENCE_INCOMPLETE)
        if (experience.status == ExperienceStatus.AI_ANALYZED)
            throw GlobalException(ErrorCode.AI_EXPERIENCE_ANALYSIS_ALREADY_EXISTS)
    }

}