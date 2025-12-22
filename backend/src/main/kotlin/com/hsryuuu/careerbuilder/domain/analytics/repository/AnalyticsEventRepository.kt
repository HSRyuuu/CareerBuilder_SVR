package com.hsryuuu.careerbuilder.domain.analytics.repository

import com.hsryuuu.careerbuilder.domain.analytics.model.AnalyticsEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * Analytics 이벤트 Repository
 */
@Repository
interface AnalyticsEventRepository : JpaRepository<AnalyticsEvent, Long> {

    /**
     * 특정 기간 내 이벤트 조회
     */
    fun findByTimestampBetween(start: Instant, end: Instant): List<AnalyticsEvent>

    /**
     * 특정 사용자의 이벤트 조회
     */
    fun findByUserIdOrderByTimestampDesc(userId: String): List<AnalyticsEvent>

    /**
     * 특정 세션의 이벤트 조회
     */
    fun findBySessionIdOrderByTimestampAsc(sessionId: String): List<AnalyticsEvent>

    /**
     * 특정 이벤트명의 이벤트 수 조회
     */
    fun countByEventName(eventName: String): Long

    /**
     * 특정 기간 내 이벤트 수 조회
     */
    @Query("""
        SELECT e.eventName, COUNT(e) 
        FROM AnalyticsEvent e 
        WHERE e.timestamp BETWEEN :start AND :end 
        GROUP BY e.eventName
    """)
    fun countEventsByNameBetween(start: Instant, end: Instant): List<Array<Any>>

    /**
     * 일별 이벤트 수 조회 (간단한 DAU 계산용)
     */
    @Query("""
        SELECT CAST(e.timestamp AS DATE), COUNT(DISTINCT e.userId) 
        FROM AnalyticsEvent e 
        WHERE e.timestamp BETWEEN :start AND :end AND e.userId IS NOT NULL
        GROUP BY CAST(e.timestamp AS DATE)
    """)
    fun countDailyActiveUsers(start: Instant, end: Instant): List<Array<Any>>
}
