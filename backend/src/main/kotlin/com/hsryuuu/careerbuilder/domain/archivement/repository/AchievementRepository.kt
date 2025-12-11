package com.hsryuuu.careerbuilder.domain.archivement.repository

import com.hsryuuu.careerbuilder.domain.archivement.model.entity.Achievement
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AchievementRepository : JpaRepository<Achievement, UUID>, CustomAchievementRepository {
    fun findByUserId(userId: UUID): List<Achievement>
    fun findByUserIdAndStatus(userId: UUID, status: AchievementStatus): List<Achievement>
    fun existsByIdAndUserId(id: UUID, userId: UUID): Boolean
}
