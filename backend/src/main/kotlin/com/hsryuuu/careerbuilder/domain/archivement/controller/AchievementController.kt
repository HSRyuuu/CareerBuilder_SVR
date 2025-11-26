package com.hsryuuu.careerbuilder.domain.archivement.controller

import com.hsryuuu.careerbuilder.application.security.AuthManager
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.AchievementResponse
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.UpdateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.service.AchievementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "성과 관리 API")
@RequestMapping("/api/achievements")
@RestController
class AchievementController(
    private val achievementService: AchievementService,
    private val authManager: AuthManager
) {

    @Operation(summary = "성과 생성", description = "새로운 성과를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAchievement(
        @Valid @RequestBody request: CreateAchievementRequest
    ): AchievementResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return achievementService.createAchievement(userId, request)
    }

    @Operation(summary = "성과 조회", description = "특정 성과를 조회합니다.")
    @GetMapping("/{id}")
    fun getAchievement(@PathVariable id: UUID): AchievementResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return achievementService.getAchievement(id, userId)
    }

    @Operation(summary = "전체 성과 목록 조회", description = "현재 사용자의 모든 성과를 조회합니다.")
    @GetMapping
    fun getAllAchievements(): List<AchievementResponse> {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return achievementService.getAllAchievements(userId)
    }

    @Operation(summary = "상태별 성과 목록 조회", description = "특정 상태의 성과 목록을 조회합니다.")
    @GetMapping("/status/{status}")
    fun getAchievementsByStatus(
        @PathVariable status: AchievementStatus
    ): List<AchievementResponse> {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return achievementService.getAchievementsByStatus(userId, status)
    }

    @Operation(summary = "성과 수정", description = "기존 성과를 수정합니다.")
    @PutMapping("/{id}")
    fun updateAchievement(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAchievementRequest
    ): AchievementResponse {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        return achievementService.updateAchievement(id, userId, request)
    }

    @Operation(summary = "성과 삭제", description = "성과를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAchievement(@PathVariable id: UUID) {
        val userId = authManager.getCurrentUserIdOrElseThrow()
        achievementService.deleteAchievement(id, userId)
    }
}
