package com.hsryuuu.careerbuilder.domain.archivement.service

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.AchievementResponse
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateSectionRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.UpdateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementRepository
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementSectionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private const val string = "Hello"

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

        return AchievementResponse.from(savedAchievement, savedAchievement.sections)
    }


    @Transactional(readOnly = true)
    fun getAchievement(id: UUID, userId: UUID): AchievementResponse {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.ACHIEVEMENT_NOT_FOUND)

        if (achievement.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        val sections = achievementSectionRepository.findByAchievementIdOrderBySortOrderAsc(id)
        return AchievementResponse.from(achievement, sections)
    }

    @Transactional(readOnly = true)
    fun getAllAchievements(userId: UUID): List<AchievementResponse> {
        val achievements = achievementRepository.findByUserId(userId)
        return achievements.map { achievement ->
            val sections = achievementSectionRepository.findByAchievementIdOrderBySortOrderAsc(achievement.id!!)
            AchievementResponse.from(achievement, sections)
        }
    }

    @Transactional(readOnly = true)
    fun getAchievementsByStatus(userId: UUID, status: AchievementStatus): List<AchievementResponse> {
        val achievements = achievementRepository.findByUserIdAndStatus(userId, status)
        return achievements.map { achievement ->
            val sections = achievementSectionRepository.findByAchievementIdOrderBySortOrderAsc(achievement.id!!)
            AchievementResponse.from(achievement, sections)
        }
    }

    @Transactional
    fun updateAchievement(id: UUID, userId: UUID, request: UpdateAchievementRequest): AchievementResponse {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(ErrorCode.ACHIEVEMENT_NOT_FOUND)

        if (achievement.user.id != userId) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

//        val updatedAchievement = achievement.copy(
//            title = request.title,
//            orgName = request.orgName,
//            durationStart = request.durationStart,
//            durationEnd = request.durationEnd,
//            impactSummary = request.impactSummary,
//            goalSummary = request.goalSummary,
//            status = request.status,
//            roleTitle = request.roleTitle,
//            workType = request.workType,
//            contributionLevel = request.contributionLevel,
//            skills = request.skills
//        )
//
//        val saved = achievementRepository.save(updatedAchievement)

//        // 기존 섹션 삭제
//        achievementSectionRepository.deleteByAchievementId(id)
//
//        // 새 섹션 추가
//        val savedSections = request.sections.map { sectionRequest ->
//            val section = AchievementSection(
//                achievement = saved,
//                kind = sectionRequest.kind,
//                title = sectionRequest.title,
//                content = sectionRequest.content,
//                sortOrder = sectionRequest.sortOrder
//            )
//            achievementSectionRepository.save(section)
//        }

        return AchievementResponse.from(
            achievement,
            achievement.sections
        );//AchievementResponse.from(saved, savedSections)
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
