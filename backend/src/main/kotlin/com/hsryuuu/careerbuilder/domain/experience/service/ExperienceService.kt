package com.hsryuuu.careerbuilder.domain.experience.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.*
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.experience.repository.ExperienceRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ExperienceService(
    private val experienceRepository: ExperienceRepository,
    private val userRepository: AppUserRepository
) {

    @Transactional
    fun createExperience(userId: UUID, request: CreateExperienceRequest): ExperienceResponse {
        val user = getReferenceUser(userId)

        val experience = CreateExperienceRequest.createEntity(user, request)
        request.sections.forEach { sectionRequest ->
            val section = CreateSectionRequest.createEntity(sectionRequest)
            experience.addSection(section)
        }

        experience.validateRequiredValue() // 필수값 검증
        experience.calculateProgressScore() // 점수 계산
        experience.setStatusByProgressScore(isEdit = false) // 상태 세팅


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

        val pageRequest = PageRequest.of(page, pageSize)

        // QueryDSL 기반 검색 실행
        val experiencePage =
            experienceRepository.searchExperience(userId, searchKeyword, status, sort, sortDirection, pageRequest)

        // Entity를 Response로 변환
        return CommonPageResponse.from(experiencePage) { experience ->
            ExperienceResponse.fromEntityWithoutSections(experience)
        }
    }


    @Transactional(readOnly = true)
    fun getExperience(experienceId: UUID, userId: UUID): ExperienceResponse {
        val experience = experienceRepository.findByIdOrNull(experienceId)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        // 권한 체크
        if (experience.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        return ExperienceResponse.fromEntity(experience, experience.sections)
    }

    @Transactional(readOnly = true)
    fun getExperienceWithAIAnalysisResult(experienceId: UUID, userId: UUID): ExperienceWithAnalysisResponse {

        val result = experienceRepository.getExperienceWithAnalysis(experienceId, userId)
        if (result != null) {
            return result
        }

        throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun aiAnalysisResultExists(id: UUID, userId: UUID): Boolean {
        return experienceRepository.existsAiAnalysisResultById(id, userId)
    }


    @Transactional
    fun updateExperience(id: UUID, userId: UUID, request: UpdateExperienceRequest): ExperienceResponse {
        // 1. Experience 조회 및 권한 검증
        val experience = experienceRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.EXPERIENCE_NOT_FOUND)

        // 권한 체크
        if (experience.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        // 2. Experience 정보 업데이트
        experience.update(
            title = request.title,
            background = request.background,
            periodStart = request.periodStart,
            periodEnd = request.periodEnd,
            keyAchievements = request.keyAchievements,
            goalSummary = request.goalSummary,
            role = request.role,
            category = request.category,
            contributionLevel = request.contributionLevel,
            skills = request.skills
        )

        // 3. Sections 업데이트
        experience.updateSections(request.sections)

        experience.validateRequiredValue() // 필수값 검증
        experience.calculateProgressScore() // 점수 계산
        experience.setStatusByProgressScore(isEdit = true) // 상태 세팅

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
        val user = userRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        return experienceRepository.getStatsSummary(user)
    }

    fun getReferenceUser(userId: UUID): AppUser = userRepository.getReferenceById(userId)


}