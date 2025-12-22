package com.hsryuuu.careerbuilder.domain.analytics.service

import com.hsryuuu.careerbuilder.domain.analytics.model.AnalyticsEvent
import com.hsryuuu.careerbuilder.domain.analytics.repository.AnalyticsEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Analytics 서비스
 * 
 * 이벤트 추적 및 분석 기능을 제공합니다.
 */
@Service
class AnalyticsService(
    private val analyticsEventRepository: AnalyticsEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(AnalyticsService::class.java)

    /**
     * 이벤트 추적
     */
    @Transactional
    fun trackEvent(request: TrackEventRequest): AnalyticsEvent {
        val event = AnalyticsEvent(
            eventName = request.eventName,
            eventCategory = request.eventCategory ?: categorizeEvent(request.eventName),
            properties = request.properties?.let { objectMapper.writeValueAsString(it) },
            userId = request.userId,
            sessionId = request.sessionId,
            pageUrl = request.pageUrl,
            referrer = request.referrer,
            userAgent = request.userAgent,
            ipAddress = request.ipAddress,
            timestamp = request.timestamp?.let { Instant.parse(it) } ?: Instant.now()
        )

        val savedEvent = analyticsEventRepository.save(event)
        logger.debug("Tracked event: ${savedEvent.eventName} for user: ${savedEvent.userId}")
        return savedEvent
    }

    /**
     * 여러 이벤트 일괄 추적
     */
    @Transactional
    fun trackEvents(requests: List<TrackEventRequest>): List<AnalyticsEvent> {
        return requests.map { trackEvent(it) }
    }

    /**
     * 이벤트 이름에서 카테고리 자동 추출
     */
    private fun categorizeEvent(eventName: String): String {
        return when {
            eventName.startsWith("page_") -> "PAGE"
            eventName.startsWith("user_") -> "USER"
            eventName.startsWith("achievement_") -> "ACHIEVEMENT"
            eventName.startsWith("career_") -> "CAREER"
            eventName.startsWith("ai_") -> "AI"
            eventName.startsWith("onboarding_") -> "ONBOARDING"
            else -> "OTHER"
        }
    }

    /**
     * 특정 기간의 이벤트 통계 조회
     */
    fun getEventStats(startDate: Instant, endDate: Instant): EventStats {
        val eventCounts = analyticsEventRepository.countEventsByNameBetween(startDate, endDate)
        val dauCounts = analyticsEventRepository.countDailyActiveUsers(startDate, endDate)

        return EventStats(
            totalEvents = eventCounts.sumOf { (it[1] as Long) },
            eventBreakdown = eventCounts.associate { it[0] as String to it[1] as Long },
            dailyActiveUsers = dauCounts.associate { it[0].toString() to it[1] as Long }
        )
    }

    /**
     * 특정 사용자의 이벤트 히스토리 조회
     */
    fun getUserEventHistory(userId: String): List<AnalyticsEvent> {
        return analyticsEventRepository.findByUserIdOrderByTimestampDesc(userId)
    }

    /**
     * 특정 세션의 이벤트 시퀀스 조회 (사용자 여정 분석)
     */
    fun getSessionJourney(sessionId: String): List<AnalyticsEvent> {
        return analyticsEventRepository.findBySessionIdOrderByTimestampAsc(sessionId)
    }
}

/**
 * 이벤트 추적 요청 DTO
 */
data class TrackEventRequest(
    val eventName: String,
    val eventCategory: String? = null,
    val properties: Map<String, Any>? = null,
    val userId: String? = null,
    val sessionId: String? = null,
    val pageUrl: String? = null,
    val referrer: String? = null,
    val userAgent: String? = null,
    val ipAddress: String? = null,
    val timestamp: String? = null
)

/**
 * 이벤트 통계 DTO
 */
data class EventStats(
    val totalEvents: Long,
    val eventBreakdown: Map<String, Long>,
    val dailyActiveUsers: Map<String, Long>
)
