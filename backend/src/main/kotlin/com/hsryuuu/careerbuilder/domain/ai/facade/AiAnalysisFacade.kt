package com.hsryuuu.careerbuilder.domain.ai.facade

import com.hsryuuu.careerbuilder.domain.ai.model.dto.AiRequestDto
import com.hsryuuu.careerbuilder.domain.ai.model.type.AiRequestType
import com.hsryuuu.careerbuilder.domain.ai.quota.UsageQuotaManager
import com.hsryuuu.careerbuilder.domain.experience.service.ExperienceAnalysisService
import org.springframework.stereotype.Component
import java.util.*

@Component
class AiAnalysisFacade(
    private val usageQuotaManager: UsageQuotaManager,
    private val experienceAnalysisService: ExperienceAnalysisService
) {
    /**
     * AI 경험 분석 요청 전체 프로세스 조율
     */
    fun requestExperienceAnalysis(userId: UUID, experienceId: UUID): AiRequestDto {
        // 1. 사용 횟수 제한 체크 (Redis 및 DB Fallback 조회)
        usageQuotaManager.checkQuota(userId, AiRequestType.EXPERIENCE_ANALYSIS)
        // 2.실제 분석 요청 저장 및 이벤트 발행 로직 실행
        return experienceAnalysisService.requestAnalysis(userId, experienceId)
    }
}