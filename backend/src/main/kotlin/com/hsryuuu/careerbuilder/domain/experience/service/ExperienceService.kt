package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceStatsSummary
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceResponse
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateExperienceRequest
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceSection
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceSectionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ExperienceService(
    private val experienceRepository: ExperienceRepository,
    private val experienceSectionRepository: ExperienceSectionRepository,
    private val appUserRepository: AppUserRepository
) {

    @Transactional
    fun createExperience(userId: UUID, request: CreateExperienceRequest): ExperienceResponse {
        val user = appUserRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        if (request.durationEnd != null && !request.durationStart.isBefore(request.durationEnd))
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)

        val experience = CreateExperienceRequest.createEntity(user, request)
        request.sections.forEach { sectionRequest ->
            val section = CreateSectionRequest.createEntity(sectionRequest)
            experience.addSection(section)
        }

        val savedExperience = experienceRepository.save(experience)

        return ExperienceResponse.fromEntity(savedExperience, savedExperience.sections)
    }

    @Transactional(readOnly = true)
    fun searchExperience(
        userId: UUID,
        searchKeyword: String?,
        status: ExperienceStatus?,
        page: Int,
        pageSize: Int,
        sort: ExperienceSortKey,
        sortDirection: SortDirection? = SortDirection.DESC
    ): CommonPageResponse<ExperienceResponse> {
        val user = appUserRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        val pageRequest = PageRequest.of(page, pageSize)

        // QueryDSL 기반 검색 실행
        val experiencePage =
            experienceRepository.searchExperience(user, searchKeyword, status,sort, sortDirection, pageRequest)

        // Entity를 Response로 변환
        return CommonPageResponse.from(experiencePage) { experience ->
            ExperienceResponse.fromEntityWithoutSections(experience)
        }
    }


    @Transactional(readOnly = true)
    fun getExperience(id: UUID, userId: UUID): ExperienceResponse {
        val experience = experienceRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        if (experience.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        val sections = experienceSectionRepository.findByExperienceIdOrderBySortOrderAsc(id)
        return ExperienceResponse.fromEntity(experience, sections)
    }

    @Transactional
    fun updateExperience(id: UUID, userId: UUID, request: UpdateExperienceRequest): ExperienceResponse {
        // 1. Experience 조회 및 권한 검증
        val experience = experienceRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        if (experience.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        // 2. 비즈니스 규칙 검증
        if (request.durationEnd != null && !request.durationStart.isBefore(request.durationEnd)) {
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        // 3. Experience 정보 업데이트
        experience.update(
            title = request.title,
            orgName = request.orgName,
            durationStart = request.durationStart,
            durationEnd = request.durationEnd,
            impactSummary = request.impactSummary,
            goalSummary = request.goalSummary,
            roleTitle = request.roleTitle,
            workType = request.workType,
            contributionLevel = request.contributionLevel,
            skills = request.skills
        )

        // 4. Sections 업데이트 (ID 기반 Merge 전략)
        val requestSectionIds = request.sections.mapNotNull { it.id }.toSet()
        val existingSections = experience.sections.associateBy { it.id }

        // 4-1. 요청에 없는 기존 섹션 삭제
        val sectionsToRemove = experience.sections.filter { section ->
            section.id !in requestSectionIds
        }
        sectionsToRemove.forEach { experience.sections.remove(it) }

        // 4-2. 섹션 수정 또는 추가
        request.sections.forEach { sectionRequest ->
            if (sectionRequest.id != null && existingSections.containsKey(sectionRequest.id)) {
                // 기존 섹션 업데이트
                existingSections[sectionRequest.id]?.update(
                    kind = sectionRequest.kind,
                    title = sectionRequest.title,
                    content = sectionRequest.content,
                    sortOrder = sectionRequest.sortOrder
                )
            } else {
                // 새로운 섹션 추가
                val newSection = ExperienceSection(
                    kind = sectionRequest.kind,
                    title = sectionRequest.title,
                    content = sectionRequest.content,
                    sortOrder = sectionRequest.sortOrder
                )
                experience.addSection(newSection)
            }
        }

        experienceRepository.save(experience)

        return ExperienceResponse.fromEntity(experience, experience.sections)
    }

    @Transactional
    fun deleteExperience(id: UUID, userId: UUID) {
        val experience = experienceRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        if (experience.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        experienceRepository.delete(experience)
    }

    @Transactional(readOnly = true)
    fun getStatsSummary(userId: UUID): ExperienceStatsSummary {
        val user = appUserRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        return experienceRepository.getStatsSummary(user)
    }

}