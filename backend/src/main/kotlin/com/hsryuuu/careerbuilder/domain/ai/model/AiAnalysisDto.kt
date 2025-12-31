package com.hsryuuu.careerbuilder.domain.ai.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.hsryuuu.careerbuilder.domain.experience.model.entity.SectionKind
import java.util.*

data class ExperienceAnalysisResponse(

    // 1. 경험 전체 요약 및 피드백
    val overallSummary: String?,
    val overallFeedback: String?,

    // 2. 핵심 요약(목표) 피드백 및 수정
    val goalImprovement: ImprovementDetail,

    // 3. 핵심 성과 피드백 및 수정
    val achievementImprovement: ImprovementDetail,

    // 4. 각 섹션별 피드백 및 수정 (STAR/PAR 등 적용)
    val sectionImprovements: List<SectionImprovement>?,

    // 5. 메타데이터 추천
    val recommendedKeywords: List<String>?,

    // 6. 총점
    val totalScore: Int,
    val scoreMetrics: ScoreMetrics,
)


data class ScoreMetrics(
    val specificity: Int, // 구체성
    val resultOriented: Int, // 성과 중심
    val logicalFlow: Int, // 논리적 연결
    val jobRelevance: Int,
)

data class ImprovementDetail(
    val feedback: String,
    val improvedContent: String
)

data class SectionImprovement(
    val sectionId: UUID,
    val sectionTitle: String,
    val feedback: String,
    val improvedContent: String,
    val reasoning: String,
    val method: String, // "STAR", "PAR", "SHORT"
    val suggestedKind: SectionKind? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val breakdown: MethodBreakdown?
)

data class MethodBreakdown(
    val situation: String? = null,
    val task: String? = null,
    val action: String? = null,
    val result: String? = null,
    val decision: String? = null,
    val troubleshooting: String? = null,
    val learning: String? = null
)