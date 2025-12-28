package com.hsryuuu.careerbuilder.domain.experience.model.dto

import com.hsryuuu.careerbuilder.domain.experience.model.entity.*
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

// Request DTOs
data class CreateExperienceRequest(
    val title: String,
    val orgName: String? = null,
    val durationStart: LocalDate,
    val durationEnd: LocalDate? = null,
    val impactSummary: String? = null,
    val goalSummary: String? = null,
    val status: ExperienceStatus = ExperienceStatus.INCOMPLETE,
    val roleTitle: String? = null,
    val workType: WorkType? = null,
    val contributionLevel: ContributionLevel? = null,
    val skills: String? = null,
    val sections: List<CreateSectionRequest> = emptyList()
) {
    companion object {
        fun createEntity(
            user: AppUser,
            request: CreateExperienceRequest
        ): Experience = Experience(
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
data class ExperienceResponse(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val orgName: String?,
    val durationStart: LocalDate,
    val durationEnd: LocalDate?,
    val impactSummary: String?,
    val goalSummary: String?,
    val status: ExperienceStatus,
    val roleTitle: String?,
    val workType: WorkType?,
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
                orgName = experience.orgName,
                durationStart = experience.durationStart,
                durationEnd = experience.durationEnd,
                impactSummary = experience.impactSummary,
                goalSummary = experience.goalSummary,
                status = experience.status,
                roleTitle = experience.roleTitle,
                workType = experience.workType,
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
                orgName = experience.orgName,
                durationStart = experience.durationStart,
                durationEnd = experience.durationEnd,
                impactSummary = experience.impactSummary,
                goalSummary = experience.goalSummary,
                status = experience.status,
                roleTitle = experience.roleTitle,
                workType = experience.workType,
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
                