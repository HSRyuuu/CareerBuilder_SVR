package com.hsryuuu.careerbuilder.domain.archivement.service

import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.AchievementResponse
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.CreateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.dto.UpdateAchievementRequest
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.Achievement
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementSection
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementRepository
import com.hsryuuu.careerbuilder.domain.archivement.repository.AchievementSectionRepository
import com.hsryuuu.careerbuilder.domain.user.appuser.repository.AppUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
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
            ?: throw GlobalException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val achievement = Achievement(
            user = user,
            title = request.title,
            orgName = request.orgName,
            durationStart = request.durationStart,
            durationEnd = request.durationEnd,
            impactSummary = request.impactSummary,
            status = request.status,
            skills = request.skills
        )

        val savedAchievement = achievementRepository.save(achievement)

        // 섹션 추가
        val savedSections = request.sections.map { sectionRequest ->
            val section = AchievementSection(
                achievement = savedAchievement,
                kind = sectionRequest.kind,
                title = sectionRequest.title,
                content = sectionRequest.content,
                sortOrder = sectionRequest.sortOrder
            )
            achievementSectionRepository.save(section)
        }

        return AchievementResponse.from(savedAchievement, savedSections)
    }

    @Transactional(readOnly = true)
    fun getAchievement(id: UUID, userId: UUID): AchievementResponse {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(HttpStatus.NOT_FOUND, "성과를 찾을 수 없습니다.")

        if (achievement.user.id != userId) {
            throw GlobalException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.")
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
            ?: throw GlobalException(HttpStatus.NOT_FOUND, "성과를 찾을 수 없습니다.")

        if (achievement.user.id != userId) {
            throw GlobalException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.")
        }

        val updatedAchievement = achievement.copy(
            title = request.title,
            orgName = request.orgName,
            durationStart = request.durationStart,
            durationEnd = request.durationEnd,
            impactSummary = request.impactSummary,
            status = request.status,
            skills = request.skills
        )

        val saved = achievementRepository.save(updatedAchievement)

        // 기존 섹션 삭제
        achievementSectionRepository.deleteByAchievementId(id)

        // 새 섹션 추가
        val savedSections = request.sections.map { sectionRequest ->
            val section = AchievementSection(
                achievement = saved,
                kind = sectionRequest.kind,
                title = sectionRequest.title,
                content = sectionRequest.content,
                sortOrder = sectionRequest.sortOrder
            )
            achievementSectionRepository.save(section)
        }

        return AchievementResponse.from(saved, savedSections)
    }

    @Transactional
    fun deleteAchievement(id: UUID, userId: UUID) {
        val achievement = achievementRepository.findByIdOrNull(id)
            ?: throw GlobalException(HttpStatus.NOT_FOUND, "성과를 찾을 수 없습니다.")

        if (achievement.user.id != userId) {
            throw GlobalException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.")
        }

        achievementRepository.delete(achievement)
    }
}
