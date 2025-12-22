package com.hsryuuu.careerbuilder.domain.ai.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Gemini AI Provider
 * 
 * Google Gemini API를 사용하여 AI 기능을 제공합니다.
 */
@Component
class GeminiProvider(
    @Value("\${ai.gemini.api-key:}") private val apiKey: String,
    @Value("\${ai.gemini.model:gemini-2.0-flash}") private val model: String
) : AiProvider {

    private val logger = LoggerFactory.getLogger(GeminiProvider::class.java)

    override val providerName: String = "gemini"

    private val webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta")
        .build()

    override suspend fun generateSummary(content: String, maxLength: Int): AiResponse {
        val prompt = """
            다음 내용을 ${maxLength}자 이내로 간결하게 요약해주세요.
            핵심 성과와 임팩트를 중심으로 요약하세요.
            
            내용:
            $content
        """.trimIndent()

        return generateText(prompt)
    }

    override suspend fun extractKeywords(content: String, maxKeywords: Int): AiResponse {
        val prompt = """
            다음 내용에서 핵심 키워드를 최대 ${maxKeywords}개 추출해주세요.
            쉼표로 구분하여 나열해주세요.
            직무 역량, 기술 스택, 성과 관련 키워드를 우선적으로 추출하세요.
            
            내용:
            $content
        """.trimIndent()

        return generateText(prompt)
    }

    override suspend fun generateText(prompt: String): AiResponse {
        if (apiKey.isBlank()) {
            return AiResponse(
                success = false,
                content = "",
                provider = providerName,
                error = "Gemini API key is not configured"
            )
        }

        val startTime = System.currentTimeMillis()

        return try {
            withContext(Dispatchers.IO) {
                val requestBody = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.7,
                        maxOutputTokens = 1024
                    )
                )

                val response = webClient.post()
                    .uri("/models/$model:generateContent?key=$apiKey")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .awaitBody<GeminiResponse>()

                val latencyMs = System.currentTimeMillis() - startTime
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val tokenCount = response.usageMetadata?.totalTokenCount

                logger.debug("Gemini response received in ${latencyMs}ms, tokens: $tokenCount")

                AiResponse(
                    success = true,
                    content = text,
                    tokenCount = tokenCount,
                    latencyMs = latencyMs,
                    provider = providerName
                )
            }
        } catch (e: Exception) {
            logger.error("Gemini API error: ${e.message}", e)
            AiResponse(
                success = false,
                content = "",
                latencyMs = System.currentTimeMillis() - startTime,
                provider = providerName,
                error = e.message
            )
        }
    }

    override suspend fun analyzeCareer(achievements: List<String>): AiResponse {
        val achievementText = achievements.mapIndexed { index, text -> 
            "${index + 1}. $text" 
        }.joinToString("\n")

        val prompt = """
            다음 성과/경력 내용을 분석하여 아래 형식으로 정리해주세요:
            
            ## 핵심 역량
            - (3-5개의 핵심 역량)
            
            ## 주요 성과 요약
            - (가장 임팩트 있는 성과 3개)
            
            ## 추천 직무
            - (적합한 직무 2-3개)
            
            ## 개선 제안
            - (성과 기술 개선을 위한 제안)
            
            성과 내용:
            $achievementText
        """.trimIndent()

        return generateText(prompt)
    }
}

// Gemini API Request/Response DTOs
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?,
    val usageMetadata: GeminiUsageMetadata?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

data class GeminiUsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?
)
