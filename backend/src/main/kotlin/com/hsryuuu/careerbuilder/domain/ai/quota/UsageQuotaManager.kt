package com.hsryuuu.careerbuilder.domain.ai.quota

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.plan.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsageQuotaManager(
    private val subscriptionRepository: SubscriptionRepository,
    private val usageLimitManager: UsageLimitManager,
) {

    /**
     * 사용자 이용량 제한 체크
     */
    fun checkQuota(userId: UUID, type: AiRequestType) {
        val subscription = subscriptionRepository.findByUserId(userId)
            ?: throw GlobalException(ErrorCode.PLAN_NOT_FOUND)

        val usage = usageLimitManager.getUsageCount(userId, type)
        if (usage >= subscription.plan.experienceAnalysisLimitPerDay) {
            throw GlobalException(ErrorCode.EXCEED_LIMIT)
        }
    }
}