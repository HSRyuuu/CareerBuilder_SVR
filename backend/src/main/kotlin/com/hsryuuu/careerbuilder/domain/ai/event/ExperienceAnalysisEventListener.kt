package com.hsryuuu.careerbuilder.domain.ai.event

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.AiAnalysisService
import com.hsryuuu.careerbuilder.domain.ai.external.LLMRequestExecutor
import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiProcessType
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.quota.UsageLimitManager
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.notification.event.NotificationEvent
import com.hsryuuu.careerbuilder.domain.notification.model.enums.NotificationType
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.context.ApplicationEventPublisher
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
    private val subscriptionRepository: SubscriptionRepository,
    private val llmRequestExecutor: LLMRequestExecutor,
    private val usageLimitManager: UsageLimitManager,
    private val aiAnalysisService: AiAnalysisService,
    private val eventPublisher: ApplicationEventPublisher,
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
                aiRequest.fail("Experience not found id: ${event.experienceId}")
                aiAnalysisService.saveAiRequest(aiRequest)
            }

        // AI 분석요청 작업시작
        aiAnalysisService.saveAiRequest(aiRequest.apply { status = AiRequestStatus.PROCESSING })
        try {
            // 1. 응답 타입 지정
            val converter = BeanOutputConverter(ExperienceAnalysisResponse::class.java)
            // 2. AI 호출
            val chatResponse =
                llmRequestExecutor.analyzeExperience(experience, getAiModelName(event.userId), converter)
            // 3. 결과 저장 (Entity 생성 및 저장)
            aiAnalysisService.applyAiRequestSuccess(
                aiRequest,
                experience,
                chatResponse,
                converter
            )

            log.info("[END] AI 응답 결과에 따른 엔티티 저장 완료. RequestID: {}", aiRequest.id)
            // 4. 사용 횟수 증가 (Redis)
            usageLimitManager.incrementUsage(event.userId, AiProcessType.EXPERIENCE_ANALYSIS)
            // 5. 성공 알림
            publishNotificationEvent(event.userId, experience.id!!, NotificationType.AI_EXPERIENCE_ANALYSIS_SUCCESS)

        } catch (e: Exception) {
            log.error("AI Analysis Failed for request: ${aiRequest.id}", e)
            // AI Request 실패 처리
            aiRequest.fail(e.message ?: "Unknown error")
            aiAnalysisService.saveAiRequest(aiRequest)
            // 실패 알림
            publishNotificationEvent(event.userId, experience.id!!, NotificationType.AI_EXPERIENCE_ANALYSIS_FAIL)
        }
    }

    private fun getAiModelName(userId: UUID): String {
        val subscription =
            subscriptionRepository.findByUserId(userId) ?: throw GlobalException(ErrorCode.PLAN_NOT_FOUND)
        return subscription.plan.experienceAnalysisModel
    }

    private fun publishNotificationEvent(userId: UUID, experienceId: UUID, type: NotificationType) {
        eventPublisher.publishEvent(
            NotificationEvent(
                userId = userId,
                type = type,
                targetId = experienceId.toString()
            )
        )
    }

}