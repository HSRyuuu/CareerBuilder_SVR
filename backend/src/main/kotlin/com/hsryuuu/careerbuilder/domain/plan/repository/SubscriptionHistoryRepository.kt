package com.hsryuuu.careerbuilder.domain.plan.repository

import com.hsryuuu.careerbuilder.domain.plan.model.entity.SubscriptionHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionHistoryRepository : JpaRepository<SubscriptionHistory, Long> {

    fun findBySubscriptionId(subscriptionId: UUID): List<SubscriptionHistory>
}