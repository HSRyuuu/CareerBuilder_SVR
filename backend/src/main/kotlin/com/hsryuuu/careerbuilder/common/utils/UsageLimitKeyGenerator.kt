package com.hsryuuu.careerbuilder.common.utils

import com.hsryuuu.careerbuilder.common.dto.type.CommonPeriod
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiProcessType
import java.time.LocalDate
import java.util.*

object UsageLimitKeyGenerator {
    private const val PREFIX = "usage:"

    fun generate(userId: UUID, resource: AiProcessType, startDate: LocalDate, period: CommonPeriod): String {
        val resourceKey = "ai_" + resource.name.lowercase()
        val dateKey = startDate.toString()
        val periodKey = period.name.lowercase()
        return "$PREFIX:$resourceKey:$dateKey:$periodKey:$userId"
    }
}