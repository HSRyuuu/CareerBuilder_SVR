package com.hsryuuu.careerbuilder.domain.analytics.controller

import com.hsryuuu.careerbuilder.domain.analytics.model.AnalyticsEvent
import com.hsryuuu.careerbuilder.domain.analytics.service.AnalyticsService
import com.hsryuuu.careerbuilder.domain.analytics.service.EventStats
import com.hsryuuu.careerbuilder.domain.analytics.service.TrackEventRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Analytics API Controller
 * 
 * 이벤트 추적 및 분석 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "이벤트 추적 및 분석 API")
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {

    /**
     * 단일 이벤트 추적
     */
    @PostMapping("/events")
    @Operation(summary = "이벤트 추적", description = "사용자 행동 이벤트를 기록합니다")
    fun trackEvent(
        @RequestBody request: TrackEventRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<AnalyticsEvent> {
        // IP 주소 및 User-Agent 자동 추가
        val enrichedRequest = request.copy(
            ipAddress = request.ipAddress ?: getClientIp(httpRequest),
            userAgent = request.userAgent ?: httpRequest.getHeader("User-Agent")
        )

        val event = analyticsService.trackEvent(enrichedRequest)
        return ResponseEntity.ok(event)
    }

    /**
     * 여러 이벤트 일괄 추적
     */
    @PostMapping("/events/batch")
    @Operation(summary = "이벤트 일괄 추적", description = "여러 이벤트를 한 번에 기록합니다")
    fun trackEvents(
        @RequestBody requests: List<TrackEventRequest>,
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<AnalyticsEvent>> {
        val enrichedRequests = requests.map { request ->
            request.copy(
                ipAddress = request.ipAddress ?: getClientIp(httpRequest),
                userAgent = request.userAgent ?: httpRequest.getHeader("User-Agent")
            )
        }

        val events = analyticsService.trackEvents(enrichedRequests)
        return ResponseEntity.ok(events)
    }

    /**
     * 이벤트 통계 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "이벤트 통계", description = "특정 기간의 이벤트 통계를 조회합니다")
    fun getEventStats(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): ResponseEntity<EventStats> {
        val start = startDate?.let { Instant.parse(it) } 
            ?: Instant.now().minus(7, ChronoUnit.DAYS)
        val end = endDate?.let { Instant.parse(it) } 
            ?: Instant.now()

        val stats = analyticsService.getEventStats(start, end)
        return ResponseEntity.ok(stats)
    }

    /**
     * 사용자 이벤트 히스토리 조회
     */
    @GetMapping("/users/{userId}/events")
    @Operation(summary = "사용자 이벤트 히스토리", description = "특정 사용자의 이벤트 기록을 조회합니다")
    fun getUserEvents(
        @PathVariable userId: String
    ): ResponseEntity<List<AnalyticsEvent>> {
        val events = analyticsService.getUserEventHistory(userId)
        return ResponseEntity.ok(events)
    }

    /**
     * 세션 여정 조회
     */
    @GetMapping("/sessions/{sessionId}/journey")
    @Operation(summary = "세션 여정 조회", description = "특정 세션의 사용자 여정을 조회합니다")
    fun getSessionJourney(
        @PathVariable sessionId: String
    ): ResponseEntity<List<AnalyticsEvent>> {
        val events = analyticsService.getSessionJourney(sessionId)
        return ResponseEntity.ok(events)
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (!xForwardedFor.isNullOrBlank()) {
            xForwardedFor.split(",").first().trim()
        } else {
            request.remoteAddr
        }
    }
}
