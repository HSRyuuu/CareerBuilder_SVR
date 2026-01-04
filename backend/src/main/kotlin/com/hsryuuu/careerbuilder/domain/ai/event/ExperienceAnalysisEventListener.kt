package com.hsryuuu.careerbuilder.domain.ai.event

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.external.LLMRequestExecutor
import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.quota.UsageLimitManager
import com.hsryuuu.careerbuilder.domain.ai.repository.AiExperienceAnalysisRepository
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*

@Component
class ExperienceAnalysisEventListener(
    private val aiRequestRepository: AiRequestRepository,
    private val experienceRepository: ExperienceRepository,
    private val aiExperienceAnalysisRepository: AiExperienceAnalysisRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val llmRequestExecutor: LLMRequestExecutor,
    private val usageLimitManager: UsageLimitManager
) {


    private val log = LoggerFactory.getLogger(javaClass)


    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleExperienceAnalysis(event: ExperienceAnalysisEvent) {
        val aiRequest = aiRequestRepository.findByIdOrNull(event.aiRequestId)
            ?: return Unit.also { log.error("AiRequest not found id: {}", event.aiRequestId) }

        val experience = experienceRepository.findByIdOrNull(event.experienceId)
            ?: return Unit.also {
                log.error("Experience not found id: ${event.experienceId}")
                updateAiRequest(aiRequest, AiRequestStatus.FAILURE)
            }

        // AI 요청 시작
        updateAiRequest(aiRequest, AiRequestStatus.PROCESSING)
        try {
            // 1. 응답 타입 지정
            val converter = BeanOutputConverter(ExperienceAnalysisResponse::class.java)
            // 2. AI 호출
            val chatResponse =
                llmRequestExecutor.analyzeExperience(experience, getAiModelName(event.userId), converter)
            // 3. 결과 저장 (Entity 생성 및 저장)
            val aiAnalysis = saveAiAnalysis(
                aiRequest,
                experience,
                converter.convert(chatResponse.result.output.content)
            )
            // 4. Ai Request 성공 처리
            aiRequest.updateByAiChatResponse(aiAnalysis.id!!, chatResponse)
            log.info("[END] AI Analysis Completed. RequestID: {}", aiRequest.id)

            // 5. Experience - AI 분석 상태 변경
            experience.status = ExperienceStatus.AI_ANALYZED
            // 6. 사용 횟수 증가 (Redis)
            usageLimitManager.incrementUsage(event.userId, AiRequestType.EXPERIENCE_ANALYSIS)

        } catch (e: Exception) {
            log.error("AI Analysis Failed for request: ${aiRequest.id}", e)
            aiRequest.fail(e.message ?: "Unknown error")
        } finally {
            experienceRepository.save(experience)
            aiRequestRepository.save(aiRequest)
        }
    }

    private fun updateAiRequest(aiRequest: AiRequest, status: AiRequestStatus) {
        aiRequest.status = status
        aiRequestRepository.saveAndFlush(aiRequest)
    }

    private fun getAiModelName(userId: UUID): String {
        val subscription =
            subscriptionRepository.findByUserId(userId) ?: throw GlobalException(ErrorCode.PLAN_NOT_FOUND)
        return subscription.plan.experienceAnalysisModel
    }

    private fun saveAiAnalysis(
        aiRequest: AiRequest,
        experience: Experience,
        response: ExperienceAnalysisResponse?
    ): AiExperienceAnalysis {
        val analysisEntity = AiExperienceAnalysis.create(
            requestId = aiRequest.id,
            experienceId = experience.id!!,
            response = response
        )
        aiExperienceAnalysisRepository.save(analysisEntity)
        return analysisEntity
    }

}