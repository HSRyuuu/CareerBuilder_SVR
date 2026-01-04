package com.hsryuuu.careerbuilder.domain.plan.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.type.CommonPeriod
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.quota.UsageLimitManager
import com.hsryuuu.careerbuilder.domain.plan.model.dto.AiUsageDetail
import com.hsryuuu.careerbuilder.domain.plan.model.dto.SubscriptionUsageDto
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsageService(
    private val subscriptionRepository: SubscriptionRepository,
    private val usageLimitManager: UsageLimitManager
) {

    fun getUserSubscriptionUsage(userId: UUID): SubscriptionUsageDto {
        val subscription =
            subscriptionRepository.findByUserId(userId) ?: throw GlobalException(ErrorCode.SUBSCRIPTION_NOT_FOUND)

        val plan = subscription.plan

        val summary = mapOf(
            AiRequestType.EXPERIENCE_ANALYSIS to AiUsageDetail(
                limit = plan.experienceAnalysisLimitPerDay,
                current = usageLimitManager.getUsageCount(userId, AiRequestType.EXPERIENCE_ANALYSIS),
                period = CommonPeriod.DAY
            ),
            AiRequestType.CAREER to AiUsageDetail(
                limit = plan.careerAnalysisLimitPerMonth,
                current = 0,
                period = CommonPeriod.MONTH
            ),
            AiRequestType.RESUME to AiUsageDetail(
                limit = plan.resumeLimitPerMonth,
                current = 0,
                period = CommonPeriod.MONTH
            )
        )

        return SubscriptionUsageDto(usageSummary = summary)
    }
}