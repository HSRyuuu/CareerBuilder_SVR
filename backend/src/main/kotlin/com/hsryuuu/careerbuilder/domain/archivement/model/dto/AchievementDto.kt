package com.hsryuuu.careerbuilder.domain.archivement.model.dto

import com.hsryuuu.careerbuilder.domain.archivement.model.entity.*
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

// Request DTOs
data class CreateAchievementRequest(
    val title: String,
    val orgName: String? = null,
    val durationStart: LocalDate,
    val durationEnd: LocalDate? = null,
    val impactSummary: String? = null,
    val goalSummary: String? = null,
    val status: AchievementStatus = AchievementStatus.DRAFT,
    val roleTitle: String? = null,
    val workType: WorkType? = null,
    val contributionLevel: ContributionLevel? = null,
    val skills: String? = null,
    val sections: List<CreateSectionRequest> = emptyList()
) {
    companion object {
        fun createEntity(
            user: AppUser,
            request: CreateAchievementRequest
        ): Achievement = Achievement(
            user = user,
            title = request.title,
            orgName = request.orgName,
            durationStart = request.durationStart,
            durationEnd = request.durationEnd,
            impactSummary = request.impactSummary,
            goalSummary = request.goalSummary,
            status = request.status,
            roleTitle = request.roleTitle,
            workType = request.workType,
            contributionLevel = request.contributionLevel,
            skills = request.skills
        )
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
        ): AchievementSection = AchievementSection(
            achievement = null,
            kind = sectionRequest.kind,
            title = sectionRequest.title,
            content = sectionRequest.content,
            sortOrder = sectionRequest.sortOrder
        )
    }
}

data class UpdateAchievementRequest(
    val title: String,
    val orgName: String? = null,
    val durationStart: LocalDate,
    val durationEnd: LocalDate? = null,
    val impactSummary: String? = null,
    val goalSummary: String? = null,
    val roleTitle: String? = null,
    val workType: WorkType? = null,
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
data class AchievementResponse(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val orgName: String?,
    val durationStart: LocalDate,
    val durationEnd: LocalDate?,
    val impactSummary: String?,
    val goalSummary: String?,
    val status: AchievementStatus,
    val roleTitle: String?,
    val workType: WorkType?,
    val contributionLevel: ContributionLevel?,
    val skills: String?,
    val sections: List<SectionResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {

        fun fromEntityWithoutSections(achievement: Achievement): AchievementResponse {
            return AchievementResponse(
                id = achievement.id!!,
                userId = achievement.user.id!!,
                title = achievement.title,
                orgName = achievement.orgName,
                durationStart = achievement.durationStart,
                durationEnd = achievement.durationEnd,
                impactSummary = achievement.impactSummary,
                goalSummary = achievement.goalSummary,
                status = achievement.status,
                roleTitle = achievement.roleTitle,
                workType = achievement.workType,
                contributionLevel = achievement.contributionLevel,
                skills = achievement.skills,
                sections = emptyList(),
                createdAt = achievement.createdAt,
                updatedAt = achievement.updatedAt
            )
        }

        fun fromEntity(achievement: Achievement, sections: List<AchievementSection>): AchievementResponse {
            return AchievementResponse(
                id = achievement.id!!,
                userId = achievement.user.id!!,
                title = achievement.title,
                orgName = achievement.orgName,
                durationStart = achievement.durationStart,
                durationEnd = achievement.durationEnd,
                impactSummary = achievement.impactSummary,
                goalSummary = achievement.goalSummary,
                status = achievement.status,
                roleTitle = achievement.roleTitle,
                workType = achievement.workType,
                contributionLevel = achievement.contributionLevel,
                skills = achievement.skills,
                sections = sections.map { SectionResponse.from(it) },
                createdAt = achievement.createdAt,
                updatedAt = achievement.updatedAt
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
        fun from(section: AchievementSection): SectionResponse {
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
