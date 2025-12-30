package com.hsryuuu.careerbuilder.domain.ai.service

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
class AiGenerationService(
    private val chatClient: ChatClient
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun analyzeExperience(experience: Experience): ChatResponse {
        log.info("[START] AI 경험분석 요청 | experienceId: ${experience.id}, title: ${experience.title}")

        // AI 응답 타입 정의
        val converter = BeanOutputConverter(ExperienceAnalysisResponse::class.java)

        // 프롬프트
        val variables: Map<String, Any> = mapOf(
            "title" to experience.title,
            "background" to (experience.background ?: "미입력"),
            "role" to (experience.role ?: "미입력"),
            "category" to (experience.category?.name ?: "미지정"),
            "keyAchievements" to (experience.keyAchievements ?: ""),
            "goalSummary" to (experience.goalSummary ?: ""),
            "additionalSections" to experience.sections.joinToString("\n") { "- [${it.kind} / id:(${it.id})] ${it.content}" },
            "format" to converter.format
        )

        val template = PromptTemplate(ExperiencePrompts.EXPERIENCE_ANALYSIS_PROMPT)
        val prompt = template.render(variables)

        // AI 분석 요청
        val chatResponse = chatClient.prompt()
            .user { userSpec -> userSpec.text(prompt) }
            .call()
            .chatResponse()


        if (chatResponse == null) {
            throw RuntimeException("AI Response is null")
        }
        log.info("AI 경험분석 응답 완료")

        return chatResponse
    }
}
