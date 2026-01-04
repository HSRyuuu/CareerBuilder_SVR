package com.hsryuuu.careerbuilder.domain.plan.repository

import com.hsryuuu.careerbuilder.domain.plan.model.entity.Plan
import com.hsryuuu.careerbuilder.domain.plan.model.type.PlanType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanRepository : JpaRepository<Plan, Int> {
    fun findByPlanType(planType: PlanType): Plan?
}