package com.hsryuuu.careerbuilder.domain.experience.model.dto

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.ai.model.MethodBreakdown
import com.hsryuuu.careerbuilder.domain.ai.model.ScoreMetrics
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceSectionAnalysis
import com.hsryuuu.careerbuilder.domain.experience.model.entity.*
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import java.time.LocalDateTime
import java.util.*

// Request DTOs
data class CreateExperienceRequest(
    val title: String,
    val background: String? = null,
    val periodStart: String,
    val periodEnd: String? = null,
    val keyAchievements: String? = null,
    val goalSummary: String? = null,
    val status: ExperienceStatus = ExperienceStatus.INCOMPLETE,
    val role: String? = null,
    val category: WorkCategory? = null,
    val contributionLevel: ContributionLevel? = null,
    val skills: String? = null,
    val sections: List<CreateSectionRequest> = emptyList()
) {
    companion object {
        fun createEntity(
            user: AppUser,
            request: CreateExperienceRequest
        ): Experience {
            // periodStart는 periodEnd보다 앞서야 함
            if (request.periodEnd != null && request.periodStart > request.periodEnd) {
                throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
            }

            return Experience(
                user = user,
                title = request.title,
                background = request.background,
                periodStart = request.periodStart,
                periodEnd = request.periodEnd,
                keyAchievements = request.keyAchievements,
                goalSummary = request.goalSummary,
                status = request.status,
                role = request.role,
                category = request.category,
                contributionLevel = request.contributionLevel,
                skills = request.skills
            )
        }
    }
}

data class CreateSectionRequest(
    val kind: SectionKind = SectionKind.NONE,
    val title: String,
    val content: String,
    val sortOrder: Int = 0
) {
    companion object {
        fun createEntity(
            sectionRequest: CreateSectionRequest
        ): ExperienceSection = ExperienceSection(
            experience = null,
            kind = sectionRequest.kind,
            title = sectionRequest.title,
            content = sectionRequest.content,
            sortOrder = sectionRequest.sortOrder
        )
    }
}

data class UpdateExperienceRequest(
    val title: String,
    val background: String? = null,
    val periodStart: String,
    val periodEnd: String? = null,
    val keyAchievements: String? = null,
    val goalSummary: String? = null,
    val role: String? = null,
    val category: WorkCategory? = null,
    val contributionLevel: ContributionLevel? = null,
    val skills: String? = null,
    val sections: List<UpdateSectionRequest> = emptyList()
)

data class UpdateSectionRequest(
    val id: UUID? = null,
    val kind: SectionKind = SectionKind.NONE,
    val title: String,
    val content: String,
    val sortOrder: Int = 0
)

// Response DTOs
data class ExperienceResponse(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val background: String?,
    val periodStart: String,
    val periodEnd: String?,
    val keyAchievements: String?,
    val goalSummary: String?,
    val status: ExperienceStatus,
    val role: String?,
    val category: WorkCategory?,
    val contributionLevel: ContributionLevel?,
    val skills: String?,
    val sections: List<SectionResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {

        fun fromEntityWithoutSections(experience: Experience): ExperienceResponse {
            return ExperienceResponse(
                id = experience.id!!,
                userId = experience.user.id!!,
                title = experience.title,
                background = experience.background,
                periodStart = experience.periodStart,
                periodEnd = experience.periodEnd,
                keyAchievements = experience.keyAchievements,
                goalSummary = experience.goalSummary,
                status = experience.status,
                role = experience.role,
                category = experience.category,
                contributionLevel = experience.contributionLevel,
                skills = experience.skills,
                sections = emptyList(),
                createdAt = experience.createdAt,
                updatedAt = experience.updatedAt
            )
        }

        fun fromEntity(experience: Experience, sections: List<ExperienceSection>): ExperienceResponse {
            return ExperienceResponse(
                id = experience.id!!,
                userId = experience.user.id!!,
                title = experience.title,
                background = experience.background,
                periodStart = experience.periodStart,
                periodEnd = experience.periodEnd,
                keyAchievements = experience.keyAchievements,
                goalSummary = experience.goalSummary,
                status = experience.status,
                role = experience.role,
                category = experience.category,
                contributionLevel = experience.contributionLevel,
                skills = experience.skills,
                sections = sections.map { SectionResponse.from(it) },
                createdAt = experience.createdAt,
                updatedAt = experience.updatedAt
            )
        }
    }
}

data class SectionResponse(
    val id: UUID,
    val kind: SectionKind,
    val title: String,
    val content: String,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(section: ExperienceSection): SectionResponse {
            return SectionResponse(
                id = section.id!!,
                kind = section.kind,
                title = section.title,
                content = section.content,
                sortOrder = section.sortOrder,
                createdAt = section.createdAt,
                updatedAt = section.updatedAt
            )
        }
    }
}

data class ExperienceStatsSummary(
    val total: Long,
    val incomplete: Long,
    val completed: Long,
    val analyzing: Long,
    val analyzed: Long
)

data class ExperienceWithAnalysisResponse(
    val experience: ExperienceResponse,
    val analysis: AiExperienceAnalysisDto?,
    val sections: List<SectionWithAnalysisDto>
)

data class SectionWithAnalysisDto(
    val section: SectionResponse,
    val analysis: AiExperienceSectionAnalysisDto?
)

data class AiExperienceAnalysisDto(
    val id: UUID,
    val totalScore: Int,
    val scoreMetrics: ScoreMetrics?,
    val overallSummary: String?,
    val overallFeedback: String?,
    val goalFeedback: String?,
    val goalImprovedContent: String?,
    val achievementFeedback: String?,
    val achievementImprovedContent: String?,
    val recommendedKeywords: List<String>?,
    val sectionAnalyses: List<AiExperienceSectionAnalysisDto> = emptyList() // Default to empty if not used or explicit empty list passed
) {
    companion object {
        fun from(analysis: AiExperienceAnalysis): AiExperienceAnalysisDto {
            return AiExperienceAnalysisDto(
                id = analysis.id!!,
                totalScore = analysis.totalScore,
                scoreMetrics = analysis.scoreMetrics,
                overallSummary = analysis.overallSummary,
                overallFeedback = analysis.overallFeedback,
                goalFeedback = analysis.goalFeedback,
                goalImprovedContent = analysis.goalImprovedContent,
                achievementFeedback = analysis.achievementFeedback,
                achievementImprovedContent = analysis.achievementImprovedContent,
                recommendedKeywords = analysis.recommendedKeywords?.split(",")?.map { it.trim() }
                    ?.filter { it.isNotEmpty() },
                sectionAnalyses = analysis.sections.map { AiExperienceSectionAnalysisDto.from(it) }
            )
        }
    }
}

data class AiExperienceSectionAnalysisDto(
    val id: UUID,
    val sectionId: UUID,
    val suggestedKind: SectionKind?,
    val method: String?,
    val feedback: String?,
    val improvedContent: String?,
    val reasoning: String?,
    val methodBreakdown: MethodBreakdown?
) {
    companion object {
        fun from(sectionAnalysis: AiExperienceSectionAnalysis): AiExperienceSectionAnalysisDto {
            return AiExperienceSectionAnalysisDto(
                id = sectionAnalysis.id!!,
                sectionId = sectionAnalysis.sectionId,
                suggestedKind = sectionAnalysis.suggestedKind,
                method = sectionAnalysis.method,
                feedback = sectionAnalysis.feedback,
                improvedContent = sectionAnalysis.improvedContent,
                reasoning = sectionAnalysis.reasoning,
                methodBreakdown = sectionAnalysis.methodBreakdown
            )
        }
    }
}
                