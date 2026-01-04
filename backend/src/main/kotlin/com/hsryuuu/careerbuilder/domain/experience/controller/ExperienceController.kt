package com.hsryuuu.careerbuilder.domain.experience.controller

import com.hsryuuu.careerbuilder.application.annotation.CurrentUserId
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.*
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.experience.service.ExperienceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "경험 관리 API")
@RequestMapping("/api/experiences")
@RestController
class ExperienceController(
    private val experienceService: ExperienceService
) {

    @Operation(summary = "경험 생성", description = "새로운 경험을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createExperience(
        @Valid @RequestBody request: CreateExperienceRequest,
        @CurrentUserId userId: UUID
    ): ExperienceResponse {
        return experienceService.createExperience(userId, request)
    }

    @Operation(summary = "경험 검색", description = "경험을 검색합니다.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun searchExperience(
        @RequestParam(required = false, name = "q") searchKeyword: String?,
        @RequestParam(required = false, name = "status") status: ExperienceStatus?,
        @RequestParam(required = false, name = "p", defaultValue = "1") page: Int,
        @RequestParam(required = false, name = "size", defaultValue = "10") pageSize: Int,
        @RequestParam(required = false, name = "sortKey", defaultValue = "UPDATED_AT") sort: ExperienceSortKey,
        @RequestParam(required = false, name = "sortDir", defaultValue = "DESC") sortDirection: SortDirection?,
        @CurrentUserId userId: UUID
    ): CommonPageResponse<ExperienceResponse> {
        return experienceService.searchExperience(
            userId,
            searchKeyword,
            status,
            page - 1,
            pageSize,
            sort,
            sortDirection
        );
    }

    @Operation(summary = "경험 조회", description = "특정 경험을 조회합니다.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getExperience(@PathVariable id: UUID, @CurrentUserId userId: UUID): ExperienceResponse {
        return experienceService.getExperience(id, userId)
    }

    @Operation(summary = "경험 및 AI 분석 결과 조회", description = "특정 경험과 AI 분석 결과를 조회합니다.")
    @GetMapping("/{id}/ai")
    @ResponseStatus(HttpStatus.OK)
    fun getExperienceWithAIAnalysisResult(
        @PathVariable id: UUID,
        @CurrentUserId userId: UUID
    ): ExperienceWithAnalysisResponse {
        return experienceService.getExperienceWithAIAnalysisResult(id, userId)
    }

    @Operation(summary = "경험 및 AI 분석 결과 존재 여부", description = "AI 분석 결과가 존재하는지 확인합니다.")
    @GetMapping("/{id}/ai/exists")
    @ResponseStatus(HttpStatus.OK)
    fun getAIAnalysisResultExists(@PathVariable id: UUID, @CurrentUserId userId: UUID): Boolean {
        return experienceService.aiAnalysisResultExists(id, userId)
    }

    @Operation(summary = "경험 수정", description = "기존 경험을 수정합니다.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateExperience(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateExperienceRequest,
        @CurrentUserId userId: UUID
    ): ExperienceResponse {
        return experienceService.updateExperience(id, userId, request)
    }

    @Operation(summary = "경험 삭제", description = "경험을 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteExperience(@PathVariable id: UUID, @CurrentUserId userId: UUID) {
        experienceService.deleteExperience(id, userId)
    }

    @Operation(summary = "상태별 경험 통계", description = "상태별 경험 통계를 조회합니다.")
    @GetMapping("/stats/summary")
    @ResponseStatus(HttpStatus.OK)
    fun getExperienceStatsSummary(@CurrentUserId userId: UUID): ExperienceStatsSummary {
        return experienceService.getStatsSummary(userId)
    }

}