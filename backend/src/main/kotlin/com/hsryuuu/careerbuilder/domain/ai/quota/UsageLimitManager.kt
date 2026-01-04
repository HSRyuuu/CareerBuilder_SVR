package com.hsryuuu.careerbuilder.domain.ai.quota

import com.hsryuuu.careerbuilder.common.dto.type.CommonPeriod
import com.hsryuuu.careerbuilder.common.utils.UsageLimitKeyGenerator
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestStatus
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.repository.AiRequestRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class UsageLimitManager(
    private val redisTemplate: StringRedisTemplate,
    private val aiRequestRepository: AiRequestRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val typeConfigMap = mapOf(
        AiRequestType.EXPERIENCE_ANALYSIS to CommonPeriod.DAY,
        AiRequestType.CAREER to CommonPeriod.MONTH,
        AiRequestType.RESUME to CommonPeriod.MONTH,
        AiRequestType.INTERVIEW_GEN to CommonPeriod.MONTH,
    )

    /**
     * 유저 사용 횟수 조회 (redis or DB 조회)
     */
    fun getUsageCount(userId: UUID, type: AiRequestType, dateStart: LocalDate = LocalDate.now()): Int {
        val period = typeConfigMap[type]
        val key = UsageLimitKeyGenerator.generate(userId, type, dateStart, period!!)

        var count: Int? = null

        try {
            val countStr = redisTemplate.opsForValue().get(key)
            if (countStr != null) {
                count = countStr.toInt()
            }
        } catch (e: Exception) {
            log.warn("Redis get failed during validation: ${e.message}. Proceeding to DB check.")
        }

        if (count == null) {
            val (start, end) = getTimeRange(dateStart, period)

            // DB Fallback
            count = aiRequestRepository.countByUserIdAndRequestTypeAndStatusAndCreatedAtBetween(
                userId,
                type,
                AiRequestStatus.SUCCESS,
                start,
                end
            )

            // Sync Redis (Populate Cache)
            try {
                redisTemplate.opsForValue().set(key, count.toString(), 24, TimeUnit.HOURS)
            } catch (e: Exception) {
                log.warn("Redis set failed during sync: ${e.message}")
            }
        }

        return count
    }

    /**
     * 유저 사용 횟수 증가 (redis)
     */
    fun incrementUsage(userId: UUID, type: AiRequestType, dateStart: LocalDate = LocalDate.now()) {
        val period = typeConfigMap[type]
        val key = UsageLimitKeyGenerator.generate(userId, type, dateStart, period!!)

        try {
            val newCount = redisTemplate.opsForValue().increment(key) ?: 1L

            if (newCount == 1L) {
                redisTemplate.expire(key, 24, TimeUnit.HOURS)
            }
        } catch (e: Exception) {
            log.error("Redis increment failed: ${e.message}. DB is source of truth, so this is non-blocking.", e)
        }
    }

    private fun getTimeRange(dateStart: LocalDate, period: CommonPeriod): Pair<LocalDateTime, LocalDateTime> {
        val startDateTime = LocalDateTime.of(dateStart, LocalTime.MIN)
        return when (period) {
            CommonPeriod.DAY -> startDateTime to LocalDateTime.of(dateStart, LocalTime.MAX)
            CommonPeriod.MONTH -> startDateTime to startDateTime.plusMonths(1)
            else -> throw IllegalArgumentException("Unsupported period: $period")
        }
    }
}