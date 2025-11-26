package com.hsryuuu.careerbuilder.domain.archivement.model.dto

import com.hsryuuu.careerbuilder.domain.archivement.model.entity.Achievement
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementSection
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.SectionKind
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
    val status: AchievementStatus = AchievementStatus.DRAFT,
    val skills: String? = null,
    val sections: List<CreateSectionRequest> = emptyList()
)

data class CreateSectionRequest(
    val kind: SectionKind = SectionKind.NONE,
    val title: String,
    val content: String,
    val sortOrder: Int = 0
)

data class UpdateAchievementRequest(
    val title: String,
    val orgName: String? = null,
    val durationStart: LocalDate,
    val durationEnd: LocalDate? = null,
    val impactSummary: String? = null,
    val status: AchievementStatus,
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
    val status: AchievementStatus,
    val skills: String?,
    val sections: List<SectionResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(achievement: Achievement, sections: List<AchievementSection>): AchievementResponse {
            return AchievementResponse(
                id = achievement.id!!,
                userId = achievement.user.id!!,
                title = achievement.title,
                orgName = achievement.orgName,
                durationStart = achievement.durationStart,
                durationEnd = achievement.durationEnd,
                impactSummary = achievement.impactSummary,
                status = achievement.status,
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
