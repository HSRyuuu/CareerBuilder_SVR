package com.hsryuuu.careerbuilder.domain.analytics.model

import jakarta.persistence.*
import java.time.Instant

/**
 * 분석 이벤트 엔티티
 * 
 * 프론트엔드에서 전송된 사용자 행동 이벤트를 저장합니다.
 * PostHog/Mixpanel로도 전송되지만, 자체 분석을 위해 DB에도 저장합니다.
 */
@Entity
@Table(
    name = "analytics_events",
    indexes = [
        Index(name = "idx_analytics_event_name", columnList = "eventName"),
        Index(name = "idx_analytics_user_id", columnList = "userId"),
        Index(name = "idx_analytics_timestamp", columnList = "timestamp"),
        Index(name = "idx_analytics_session_id", columnList = "sessionId")
    ]
)
data class AnalyticsEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val eventName: String,

    @Column(nullable = false, length = 50)
    val eventCategory: String,

    @Column(columnDefinition = "TEXT")
    val properties: String? = null, // JSON 형태의 이벤트 속성

    @Column(length = 100)
    val userId: String? = null,

    @Column(length = 100)
    val sessionId: String? = null,

    @Column(length = 500)
    val pageUrl: String? = null,

    @Column(length = 500)
    val referrer: String? = null,

    @Column(length = 500)
    val userAgent: String? = null,

    @Column(length = 50)
    val ipAddress: String? = null,

    @Column(nullable = false)
    val timestamp: Instant,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

/**
 * 이벤트 카테고리 Enum
 */
enum class EventCategory {
    PAGE,
    USER,
    ACHIEVEMENT,
    CAREER,
    AI,
    ONBOARDING
}
