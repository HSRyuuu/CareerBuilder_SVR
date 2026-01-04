package com.hsryuuu.careerbuilder.domain.plan.model.dto

import com.hsryuuu.careerbuilder.common.dto.type.CommonPeriod
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiProcessType

data class SubscriptionUsageDto(
    val usageSummary: Map<AiProcessType, AiUsageDetail>
)

data class AiUsageDetail(
    val limit: Int,
    val current: Int,
    val period: CommonPeriod
)