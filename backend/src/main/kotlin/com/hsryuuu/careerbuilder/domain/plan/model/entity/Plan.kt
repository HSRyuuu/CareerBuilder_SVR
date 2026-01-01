package com.hsryuuu.careerbuilder.domain.plan.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "plans")
@EntityListeners(AuditingEntityListener::class)
class Plan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 50)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 20)
    var planType: PlanType,

    @Column(nullable = false)
    var price: Long = 0,

    @Column(name = "experience_analysis_model", length = 50)
    var experienceAnalysisModel: String, // 경험 분석 모델 명

    @Column(name = "experience_analysis_limit_per_day", nullable = false)
    var experienceAnalysisLimitPerDay: Int = 0, // 경험 분석 제한 / 월

    @Column(name = "career_analysis_model", length = 50)
    var careerAnalysisModel: String, // 커리어 분석 모델 명

    @Column(name = "career_analysis_limit_per_month", nullable = false)
    var careerAnalysisLimitPerMonth: Int = 0, // 커리어 분석 제한 / 월

    @Column(name = "resume_model", length = 50)
    var resumeModel: String, // 이력서 생성 모델 명

    @Column(name = "resume_limit_per_month", nullable = false)
    var resumeLimitPerMonth: Int = 0, // 이력서 생성 제한 /월

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

enum class PlanType { BASIC, LITE, PRO, MAX }