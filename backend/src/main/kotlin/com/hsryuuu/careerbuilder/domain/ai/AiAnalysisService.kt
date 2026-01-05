package com.hsryuuu.careerbuilder.domain.ai

import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.repository.AiExperienceAnalysisRepository
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AiAnalysisService(
    private val aiRequestRepository: AiRequestRepository,
    private val experienceRepository: ExperienceRepository,
    private val aiExperienceAnalysisRepository: AiExperienceAnalysisRepository,

    ) {

    @Transactional
    fun saveAiRequest(aiRequest: AiRequest) {
        aiRequestRepository.save(aiRequest)
    }

    @Transactional
    fun applyAiRequestSuccess(
        aiRequest: AiRequest,
        experience: Experience,
        chatResponse: ChatResponse,
        converter: BeanOutputConverter<ExperienceAnalysisResponse>,
    ) {
        // ai 분석 결과 저장
        val analysisEntity = AiExperienceAnalysis.create(
            requestId = aiRequest.id,
            experienceId = experience.id!!,
            response = converter.convert(chatResponse.result.output.content)
        )
        val aiAnalysis = aiExperienceAnalysisRepository.save(analysisEntity)
        // ai 요청 정보 업데이트
        aiRequest.updateByAiChatResponse(aiAnalysis.id!!, chatResponse)
        aiRequestRepository.save(aiRequest.apply { status = AiRequestStatus.SUCCESS })

        // 경험 상태 업데이트
        experienceRepository.save(experience.apply { status = ExperienceStatus.AI_ANALYZED })
    }

}