package com.hsryuuu.careerbuilder.domain.user.appuser.controller

import com.hsryuuu.careerbuilder.application.annotation.CurrentUserId
import com.hsryuuu.careerbuilder.domain.plan.model.dto.SubscriptionUsageDto
import com.hsryuuu.careerbuilder.domain.plan.service.UsageService
import com.hsryuuu.careerbuilder.domain.user.appuser.model.dto.UserDetailResponse
import com.hsryuuu.careerbuilder.domain.user.appuser.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Tag(name = "Experience AI", description = "경험 AI 분석 API")
@RestController
@RequestMapping("/api/users/me")
class UserController(
    private val usageService: UsageService,
    private val userService: UserService
) {

    @Operation(summary = "유저 정보 조회")
    @GetMapping("")
    fun getUserDetail(@CurrentUserId userId: UUID): UserDetailResponse {
        return userService.getUserDetail(userId)
    }

    @Operation(summary = "유저 AI 사용량 조회")
    @GetMapping("/usage")
    fun getAiUsage(@CurrentUserId userId: UUID): SubscriptionUsageDto {
        return usageService.getUserSubscriptionUsage(userId)
    }

}