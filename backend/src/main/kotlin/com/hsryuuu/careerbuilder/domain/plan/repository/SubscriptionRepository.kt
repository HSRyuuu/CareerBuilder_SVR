package com.hsryuuu.careerbuilder.domain.plan.repository

import com.hsryuuu.careerbuilder.domain.plan.model.entity.Subscription
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, UUID> {

    @EntityGraph(attributePaths = ["plan"])
    fun findByUserId(userId: UUID): Subscription?

}