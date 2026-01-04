package com.hsryuuu.careerbuilder.domain.ai.controller

import com.hsryuuu.careerbuilder.application.annotation.CurrentUserId
import com.hsryuuu.careerbuilder.domain.ai.facade.AiAnalysisFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Experience AI", description = "경험 AI 분석 API")
@RestController
@RequestMapping("/api/v1/ai/experiences")
class ExperienceAnalysisController(
    private val aiAnalysisFacade: AiAnalysisFacade,
) {
    @Operation(summary = "경험 AI 분석 요청", description = "AI 경험 분석을 요청합니다.")
    @PostMapping("/{experienceId}/analyze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun requestAnalyzeExperience(@PathVariable experienceId: UUID, @CurrentUserId userId: UUID) {
        aiAnalysisFacade.requestExperienceAnalysis(userId, experienceId)
    }
}