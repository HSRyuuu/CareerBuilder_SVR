package com.hsryuuu.careerbuilder.domain.ai.repository

import com.hsryuuu.careerbuilder.domain.ai.model.entity.AiRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AiRequestRepository : JpaRepository<AiRequest, UUID> {
}
