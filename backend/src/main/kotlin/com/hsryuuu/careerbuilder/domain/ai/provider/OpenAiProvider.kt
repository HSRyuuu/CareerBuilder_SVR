package com.hsryuuu.careerbuilder.domain.ai.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * OpenAI Provider (확장용)
 * 
 * OpenAI API가 필요할 때 사용할 수 있도록 준비된 Provider입니다.
 * 현재는 API 키가 없으면 비활성화됩니다.
 */
@Component
class OpenAiProvider(
    @Value("\${ai.openai.api-key:}") private val apiKey: String,
    @Value("\${ai.openai.model:gpt-4o-mini}") private val model: String
) : AiProvider {

    private val logger = LoggerFactory.getLogger(OpenAiProvider::class.java)

    override val providerName: String = "openai"

    private val webClient = WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
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
                error = "OpenAI API key is not configured"
            )
        }

        val startTime = System.currentTimeMillis()

        return try {
            withContext(Dispatchers.IO) {
                val requestBody = OpenAiRequest(
                    model = model,
                    messages = listOf(
                        OpenAiMessage(role = "user", content = prompt)
                    ),
                    temperature = 0.7,
                    maxTokens = 1024
                )

                val response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .awaitBody<OpenAiResponse>()

                val latencyMs = System.currentTimeMillis() - startTime
                val text = response.choices?.firstOrNull()?.message?.content ?: ""
                val tokenCount = response.usage?.totalTokens

                logger.debug("OpenAI response received in ${latencyMs}ms, tokens: $tokenCount")

                AiResponse(
                    success = true,
                    content = text,
                    tokenCount = tokenCount,
                    latencyMs = latencyMs,
                    provider = providerName
                )
            }
        } catch (e: Exception) {
            logger.error("OpenAI API error: ${e.message}", e)
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

// OpenAI API Request/Response DTOs
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val temperature: Double = 0.7,
    val maxTokens: Int = 1024
)

data class OpenAiMessage(
    val role: String,
    val content: String
)

data class OpenAiResponse(
    val id: String?,
    val choices: List<OpenAiChoice>?,
    val usage: OpenAiUsage?
)

data class OpenAiChoice(
    val message: OpenAiMessage?,
    val finishReason: String?
)

data class OpenAiUsage(
    val promptTokens: Int?,
    val completionTokens: Int?,
    val totalTokens: Int?
)
