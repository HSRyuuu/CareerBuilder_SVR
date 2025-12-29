package com.hsryuuu.careerbuilder.domain.ai.handler

import com.hsryuuu.careerbuilder.domain.ai.event.ExperienceAnalysisEvent
import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.repository.AiExperienceAnalysisRepository
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.ai.service.AiGenerationService
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.converter.BeanOutputConverter
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
    private val aiGenerationService: AiGenerationService,
) {


    private val log = LoggerFactory.getLogger(javaClass)


    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleExperienceAnalysis(event: ExperienceAnalysisEvent) {
        val aiRequest = aiRequestRepository.findById(event.aiRequestId).orElse(null)
        if (aiRequest == null) {
            log.error("AiRequest not found id: {}", event.aiRequestId)
            return
        }
        try {
            // 1. 상태 변경: PROCESSING
            aiRequest.status = AiRequestStatus.PROCESSING
            aiRequestRepository.saveAndFlush(aiRequest)

            // 2. Experience 데이터 조회
            val experience = experienceRepository.findById(event.experienceId).orElseThrow {
                IllegalArgumentException("Experience not found id: ${event.experienceId}")
            }
            // 3. AI 호출 (Service 위임 - Exception 처리됨)
            val chatResponse = aiGenerationService.analyzeExperience(experience)
            // 4. 결과 저장 (Entity 생성 및 저장)
            val aiAnalysis = saveAiAnalysis(
                aiRequest,
                experience!!,
                BeanOutputConverter(ExperienceAnalysisResponse::class.java)
                    .convert(chatResponse.result.output.content)
            )
            // 5. Ai Request 설공 처리
            completeAiRequest(chatResponse, aiRequest, aiAnalysis.id!!)

        } catch (e: Exception) {
            log.error("AI Analysis Failed for request: ${aiRequest.id}", e)
            aiRequest.fail(e.message ?: "Unknown error")
        } finally {
            aiRequestRepository.save(aiRequest)
        }
    }

    private fun completeAiRequest(
        chatResponse: ChatResponse,
        aiRequest: AiRequest,
        aiAnalysisId: UUID
    ) {
        val usage = chatResponse.metadata.usage
        val providerId = chatResponse.metadata.id ?: "unknown"
        val model = chatResponse.metadata.model ?: "unknown"

        //  AI Request 성공 처리
        aiRequest.complete(
            aiProviderId = providerId,
            modelName = model,
            promptTokens = usage.promptTokens.toInt(),
            completionTokens = usage.generationTokens.toInt(),
            totalTokens = usage.totalTokens.toInt(),
            rawResponse = "Saved to DB (ID: ${aiAnalysisId})"
        )

        log.info("[END] AI Analysis Completed. RequestID: {}", aiRequest.id)
        log.info(
            "Usage - Prompt: {}, Completion: {}, Total: {}",
            usage.promptTokens,
            usage.generationTokens,
            usage.totalTokens
        )
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
