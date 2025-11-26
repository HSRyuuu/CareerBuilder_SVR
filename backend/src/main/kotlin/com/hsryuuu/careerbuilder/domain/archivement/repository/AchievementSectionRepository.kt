package com.hsryuuu.careerbuilder.domain.archivement.repository

import com.hsryuuu.careerbuilder.domain.archivement.model.entity.AchievementSection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AchievementSectionRepository : JpaRepository<AchievementSection, UUID> {
    fun findByAchievementIdOrderBySortOrderAsc(achievementId: UUID): List<AchievementSection>
    fun deleteByAchievementId(achievementId: UUID)
}
