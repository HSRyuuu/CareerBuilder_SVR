package com.hsryuuu.careerbuilder.domain.ai.repository

import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiExperienceAnalysis
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AiExperienceAnalysisRepository : JpaRepository<AiExperienceAnalysis, UUID> {
}
