package com.hsryuuu.careerbuilder.domain.experience.controller

import com.hsryuuu.careerbuilder.application.security.AuthManager
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceResponse
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceStatsSummary
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateExperienceRequest
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
    private val experienceService: ExperienceService,
    private val authManager: AuthManager
) {

    @Operation(summary = "경험 생성", description = "새로운 경험을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createExperience(
        @Valid @RequestBody request: CreateExperienceRequest
    ): ExperienceResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
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
        @RequestParam(required = false, name = "sortDir", defaultValue = "DESC") sortDirection: SortDirection?
    ): CommonPageResponse<ExperienceResponse> {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return experienceService.searchExperience(userId, searchKeyword, status,page - 1, pageSize, sort, sortDirection);
    }

    @Operation(summary = "경험 조회", description = "특정 경험을 조회합니다.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getExperience(@PathVariable id: UUID): ExperienceResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return experienceService.getExperience(id, userId)
    }

    @Operation(summary = "경험 수정", description = "기존 경험을 수정합니다.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateExperience(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateExperienceRequest
    ): ExperienceResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return experienceService.updateExperience(id, userId, request)
    }

    @Operation(summary = "경험 삭제", description = "경험을 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteExperience(@PathVariable id: UUID) {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        experienceService.deleteExperience(id, userId)
    }

    @Operation(summary = "상태별 경험 통계", description = "상태별 경험 통계를 조회합니다.")
    @GetMapping("/stats/summary")
    @ResponseStatus(HttpStatus.OK)
    fun getExperienceStatsSummary(): ExperienceStatsSummary {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return experienceService.getStatsSummary(userId)
    }

}