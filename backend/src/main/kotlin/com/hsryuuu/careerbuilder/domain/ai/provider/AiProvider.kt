package com.hsryuuu.careerbuilder.domain.ai.provider

/**
 * AI Provider 인터페이스
 * 
 * 다양한 AI 서비스(OpenAI, Gemini 등)를 추상화합니다.
 */
interface AiProvider {
    val providerName: String

    /**
     * 텍스트 요약 생성
     */
    suspend fun generateSummary(content: String, maxLength: Int = 200): AiResponse

    /**
     * 키워드 추출
     */
    suspend fun extractKeywords(content: String, maxKeywords: Int = 10): AiResponse

    /**
     * 일반 텍스트 생성
     */
    suspend fun generateText(prompt: String): AiResponse

    /**
     * 경력 분석 및 추천
     */
    suspend fun analyzeCareer(achievements: List<String>): AiResponse
}

/**
 * AI 응답 DTO
 */
data class AiResponse(
    val success: Boolean,
    val content: String,
    val tokenCount: Int? = null,
    val latencyMs: Long? = null,
    val provider: String,
    val error: String? = null
)
