package com.hsryuuu.careerbuilder.domain.ai.external

import com.hsryuuu.careerbuilder.application.factory.AiModelFactory
import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.ai.prompt.ExperiencePrompts
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.stereotype.Service

@Service
class LLMRequestExecutor(
    private val chatClient: ChatClient,
    private val aiModelFactory: AiModelFactory
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * LLM 경험 분석 요청
     * @param experience 분석 대상 경험
     * @param modelName AI Model code (name)
     * @param outputConverter ai 요청에 대한 요구 응답 값
     */
    fun analyzeExperience(
        experience: Experience,
        modelName: String,
        outputConverter: BeanOutputConverter<ExperienceAnalysisResponse>
    ): ChatResponse {
        log.info("[START] AI 경험분석 요청 | experienceId: ${experience.id}, title: ${experience.title}")
        // 프롬프트 생성
        val prompt = getExperienceAnalysisPrompt(experience, outputConverter)
        // 모델 옵션 선택
        val modelOption = aiModelFactory.getModelOption(modelName)
        // AI 분석 요청
        val chatResponse = chatClient.prompt()
            .user { userSpec -> userSpec.text(prompt) }
            .options(modelOption)
            .call()
            .chatResponse()

        if (chatResponse == null) {
            throw RuntimeException("AI Response is null")
        }
        log.info("AI 경험분석 응답 완료")
        return chatResponse
    }

    private fun getExperienceAnalysisPrompt(
        experience: Experience,
        converter: BeanOutputConverter<ExperienceAnalysisResponse>
    ): String {
        var additionalSectionInfo = "추가 섹션 없음"
        if (!experience.sections.isEmpty()) {
            additionalSectionInfo =
                experience.sections.joinToString("\n") { "- [${it.kind} / id:(${it.id})] ${it.content}" }
        }

        // 프롬프트
        val variables: Map<String, Any> = mapOf(
            "title" to experience.title,
            "background" to (experience.background ?: "미입력"),
            "role" to (experience.role ?: "미입력"),
            "category" to (experience.category?.name ?: "미지정"),
            "keyAchievements" to (experience.keyAchievements ?: ""),
            "goalSummary" to (experience.goalSummary ?: ""),
            "additionalSections" to additionalSectionInfo,
            "format" to converter.format
        )

        val template = PromptTemplate(ExperiencePrompts.EXPERIENCE_ANALYSIS_PROMPT)
        return template.render(variables)
    }
}