package com.hsryuuu.careerbuilder.domain.archivement.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.CommonPageResponse
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.AchievementResponse
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.UpdateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementSection
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.model.type.AchievementSortKey
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementRepository
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementSectionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository,
    private val achievementSectionRepository: AchievementSectionRepository,
    private val appUserRepository: AppUserRepository
) {

    @Transactional
    fun createAchievement(userId: UUID, request: CreateAchievementRequest): AchievementResponse {
        val user = appUserRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        if (!request.durationStart.isBefore(request.durationEnd))
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)

        val achievement = CreateAchievementRequest.createEntity(user, request)
        request.sections.forEach { sectionRequest ->
            val section = CreateSectionRequest.createEntity(sectionRequest)
            achievement.addSection(section)
        }

        val savedAchievement = achievementRepository.save(achievement)

        return AchievementResponse.fromEntity(savedAchievement, savedAchievement.sections)
    }

    @Transactional(readOnly = true)
    fun searchAchievement(
        userId: UUID,
        searchKeyword: String?,
        page: Int,
        pageSize: Int,
        sort: AchievementSortKey,
        sortDirection: SortDirection? = SortDirection.DESC
    ): CommonPageResponse<AchievementResponse> {
        val user = appUserRepository.findByIdOrNull(userId)
            ?: throw GlobalException(ErrorCode.MEMBER_NOT_FOUND)

        val pageRequest = PageRequest.of(page, pageSize)

        // QueryDSL 기반 검색 실행
        val achievementPage =
            achievementRepository.searchAchievement(user, searchKeyword, sort, sortDirection, pageRequest)

        // Entity를 Response로 변환
        return CommonPageResponse.from(achievementPage) { achievement ->
            AchievementResponse.fromEntityWithoutSections(achievement)
        }
    }


    @Transactional(readOnly = true)
    fun getAchievement(id: UUID, userId: UUID): AchievementResponse {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.ACHIEVEMENT_NOT_FOUND)

        if (achievement.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        val sections = achievementSectionRepository.findByAchievementIdOrderBySortOrderAsc(id)
        return AchievementResponse.fromEntity(achievement, sections)
    }

    @Transactional(readOnly = true)
    fun getAllAchievements(userId: UUID): List<AchievementResponse> {
        val achievements = achievementRepository.findByUserId(userId)
        return achievements.map { achievement ->
            AchievementResponse.fromEntityWithoutSections(achievement)
        }
    }

    @Transactional(readOnly = true)
    fun getAchievementsByStatus(userId: UUID, status: AchievementStatus): List<AchievementResponse> {
        val achievements = achievementRepository.findByUserIdAndStatus(userId, status)
        return achievements.map { achievement ->
            val sections = achievementSectionRepository.findByAchievementIdOrderBySortOrderAsc(achievement.id!!)
            AchievementResponse.fromEntity(achievement, sections)
        }
    }

    @Transactional
    fun updateAchievement(id: UUID, userId: UUID, request: UpdateAchievementRequest): AchievementResponse {
        // 1. Achievement 조회 및 권한 검증
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.ACHIEVEMENT_NOT_FOUND)

        if (achievement.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        // 2. 비즈니스 규칙 검증
        if (request.durationEnd != null && !request.durationStart.isBefore(request.durationEnd)) {
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        // 3. Achievement 정보 업데이트
        achievement.update(
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
        val existingSections = achievement.sections.associateBy { it.id }

        // 4-1. 요청에 없는 기존 섹션 삭제
        val sectionsToRemove = achievement.sections.filter { section ->
            section.id !in requestSectionIds
        }
        sectionsToRemove.forEach { achievement.sections.remove(it) }

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
                val newSection = AchievementSection(
                    kind = sectionRequest.kind,
                    title = sectionRequest.title,
                    content = sectionRequest.content,
                    sortOrder = sectionRequest.sortOrder
                )
                achievement.addSection(newSection)
            }
        }

        achievementRepository.save(achievement)

        return AchievementResponse.fromEntity(achievement, achievement.sections)
    }

    @Transactional
    fun deleteAchievement(id: UUID, userId: UUID) {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.ACHIEVEMENT_NOT_FOUND)

        if (achievement.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        achievementRepository.delete(achievement)
    }

}
