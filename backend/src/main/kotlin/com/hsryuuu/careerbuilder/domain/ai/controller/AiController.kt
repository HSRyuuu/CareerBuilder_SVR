package com.hsryuuu.careerbuilder.domain.ai.controller

import com.hsryuuu.careerbuilder.domain.ai.provider.AiResponse
import com.hsryuuu.careerbuilder.domain.ai.service.AiService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * AI API Controller
 * 
 * AI 요약, 키워드 추출, 경력 분석 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI 기반 텍스트 분석 API")
class AiController(
    private val aiService: AiService
) {

    /**
     * 사용 가능한 AI Provider 목록 조회
     */
    @GetMapping("/providers")
    @Operation(summary = "AI Provider 목록", description = "사용 가능한 AI Provider 목록을 조회합니다")
    fun getProviders(): ResponseEntity<ProvidersResponse> {
        return ResponseEntity.ok(
            ProvidersResponse(
                providers = aiService.getAvailableProviders(),
                defaultProvider = aiService.getDefaultProvider()
            )
        )
    }

    /**
     * 텍스트 요약 생성
     */
    @PostMapping("/summary")
    @Operation(summary = "텍스트 요약", description = "입력된 텍스트를 AI로 요약합니다")
    fun generateSummary(
        @RequestBody request: SummaryRequest
    ): ResponseEntity<AiResponse> = runBlocking {
        val response = aiService.generateSummary(
            content = request.content,
            maxLength = request.maxLength ?: 200,
            provider = request.provider ?: "gemini"
        )
        ResponseEntity.ok(response)
    }

    /**
     * 키워드 추출
     */
    @PostMapping("/keywords")
    @Operation(summary = "키워드 추출", description = "입력된 텍스트에서 핵심 키워드를 추출합니다")
    fun extractKeywords(
        @RequestBody request: KeywordsRequest
    ): ResponseEntity<AiResponse> = runBlocking {
        val response = aiService.extractKeywords(
            content = request.content,
            maxKeywords = request.maxKeywords ?: 10,
            provider = request.provider ?: "gemini"
        )
        ResponseEntity.ok(response)
    }

    /**
     * 경력 분석
     */
    @PostMapping("/career/analyze")
    @Operation(summary = "경력 분석", description = "성과 목록을 분석하여 핵심 역량, 추천 직무 등을 제안합니다")
    fun analyzeCareer(
        @RequestBody request: CareerAnalysisRequest
    ): ResponseEntity<AiResponse> = runBlocking {
        val response = aiService.analyzeCareer(
            achievements = request.achievements,
            provider = request.provider ?: "gemini"
        )
        ResponseEntity.ok(response)
    }

    /**
     * 일반 텍스트 생성 (자유 프롬프트)
     */
    @PostMapping("/generate")
    @Operation(summary = "텍스트 생성", description = "자유로운 프롬프트로 AI 텍스트를 생성합니다")
    fun generateText(
        @RequestBody request: GenerateRequest
    ): ResponseEntity<AiResponse> = runBlocking {
        val response = aiService.generateText(
            prompt = request.prompt,
            provider = request.provider ?: "gemini"
        )
        ResponseEntity.ok(response)
    }
}

// Request/Response DTOs
data class ProvidersResponse(
    val providers: List<String>,
    val defaultProvider: String
)

data class SummaryRequest(
    val content: String,
    val maxLength: Int? = 200,
    val provider: String? = "gemini"
)

data class KeywordsRequest(
    val content: String,
    val maxKeywords: Int? = 10,
    val provider: String? = "gemini"
)

data class CareerAnalysisRequest(
    val achievements: List<String>,
    val provider: String? = "gemini"
)

data class GenerateRequest(
    val prompt: String,
    val provider: String? = "gemini"
)
