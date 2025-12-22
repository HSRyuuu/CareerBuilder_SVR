package com.hsryuuu.careerbuilder.domain.log.controller

import com.hsryuuu.careerbuilder.domain.log.model.dto.CreateUserActionLogRequest
import com.hsryuuu.careerbuilder.domain.log.model.dto.UserActionLogResponse
import com.hsryuuu.careerbuilder.domain.log.service.UserActionLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "사용자 행동 로그 API")
@RequestMapping("/api/logs")
@RestController
class LogController(
    private val userActionLogService: UserActionLogService
) {

    @Operation(summary = "사용자 행동 로그 생성", description = "사용자의 행동 로그를 기록합니다.")
    @PostMapping("/actions")
    @ResponseStatus(HttpStatus.CREATED)
    fun createActionLog(
        @Valid @RequestBody request: CreateUserActionLogRequest,
        httpRequest: HttpServletRequest
    ): UserActionLogResponse {
        val userAgent = httpRequest.getHeader("User-Agent")
        val ipAddress = getClientIpAddress(httpRequest)

        return userActionLogService.createLog(request, userAgent, ipAddress)
    }

    private fun getClientIpAddress(request: HttpServletRequest): String? {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (!xForwardedFor.isNullOrBlank()) {
            xForwardedFor.split(",").firstOrNull()?.trim()
        } else {
            request.remoteAddr
        }
    }
}
