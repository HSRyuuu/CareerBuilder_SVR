package com.hsryuuu.careerbuilder.domain.ai.service

import com.hsryuuu.careerbuilder.domain.ai.provider.AiProvider
import com.hsryuuu.careerbuilder.domain.ai.provider.AiResponse
import com.hsryuuu.careerbuilder.domain.ai.provider.GeminiProvider
import com.hsryuuu.careerbuilder.domain.ai.provider.OpenAiProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * AI 서비스
 * 
 * 다양한 AI Provider를 통합하여 AI 기능을 제공합니다.
 * 현재 Gemini가 기본 Provider이며, OpenAI는 선택적으로 사용할 수 있습니다.
 */
@Service
class AiService(
    private val geminiProvider: GeminiProvider,
    private val openAiProvider: OpenAiProvider
) {
    private val logger = LoggerFactory.getLogger(AiService::class.java)

    /**
     * 사용 가능한 Provider 조회
     */
    fun getAvailableProviders(): List<String> {
        return listOf("gemini", "openai")
    }

    /**
     * 기본 Provider 조회 (Gemini)
     */
    fun getDefaultProvider(): String = "gemini"

    /**
     * Provider 선택
     */
    private fun getProvider(providerName: String): AiProvider {
        return when (providerName.lowercase()) {
            "gemini" -> geminiProvider
            "openai" -> openAiProvider
            else -> geminiProvider // 기본값
        }
    }

    /**
     * 텍스트 요약 생성
     */
    suspend fun generateSummary(
        content: String,
        maxLength: Int = 200,
        provider: String = "gemini"
    ): AiResponse {
        logger.info("Generating summary with provider: $provider")
        return getProvider(provider).generateSummary(content, maxLength)
    }

    /**
     * 키워드 추출
     */
    suspend fun extractKeywords(
        content: String,
        maxKeywords: Int = 10,
        provider: String = "gemini"
    ): AiResponse {
        logger.info("Extracting keywords with provider: $provider")
        return getProvider(provider).extractKeywords(content, maxKeywords)
    }

    /**
     * 경력 분석
     */
    suspend fun analyzeCareer(
        achievements: List<String>,
        provider: String = "gemini"
    ): AiResponse {
        logger.info("Analyzing career with provider: $provider, achievements count: ${achievements.size}")
        return getProvider(provider).analyzeCareer(achievements)
    }

    /**
     * 일반 텍스트 생성
     */
    suspend fun generateText(
        prompt: String,
        provider: String = "gemini"
    ): AiResponse {
        logger.info("Generating text with provider: $provider")
        return getProvider(provider).generateText(prompt)
    }
}
