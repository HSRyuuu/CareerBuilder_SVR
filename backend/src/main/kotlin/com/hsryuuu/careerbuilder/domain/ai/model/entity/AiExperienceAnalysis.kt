package com.hsryuuu.careerbuilder.domain.ai.model.entity

import com.hsryuuu.careerbuilder.domain.ai.model.ExperienceAnalysisResponse
import com.hsryuuu.careerbuilder.domain.experience.model.entity.WorkCategory
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "ai_experience_analysis")
class AiExperienceAnalysis(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @Column(name = "request_id", nullable = false)
    val requestId: UUID,

    @Column(name = "experience_id", nullable = false)
    val experienceId: UUID,

    @Column(name = "overall_summary", columnDefinition = "TEXT")
    var overallSummary: String? = null,

    @Column(name = "overall_feedback", columnDefinition = "TEXT")
    var overallFeedback: String? = null,

    @Column(name = "goal_feedback", columnDefinition = "TEXT")
    var goalFeedback: String? = null,

    @Column(name = "goal_improved_content", columnDefinition = "TEXT")
    var goalImprovedContent: String? = null,

    @Column(name = "impact_feedback", columnDefinition = "TEXT")
    var impactFeedback: String? = null,

    @Column(name = "impact_improved_content", columnDefinition = "TEXT")
    var impactImprovedContent: String? = null,

    @Column(name = "recommended_category", length = 30)
    @Enumerated(EnumType.STRING)
    var recommendedCategory: WorkCategory? = null,

    @Column(name = "recommended_keywords", columnDefinition = "TEXT")
    var recommendedKeywords: String? = null, // Comma separated

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "analysis", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sections: MutableList<AiExperienceSectionAnalysis> = mutableListOf()
) {
    // DDD: 생성자 대신 정적 팩토리 메서드나 보조 생성자 활용
    companion object {
        fun create(
            requestId: UUID,
            experienceId: UUID,
            response: ExperienceAnalysisResponse?
        ): AiExperienceAnalysis {
            if (response == null) {
                throw IllegalArgumentException("Response cannot be null")
            }

            val analysis = AiExperienceAnalysis(
                requestId = requestId,
                experienceId = experienceId,
                overallSummary = response.overallSummary,
                overallFeedback = response.overallFeedback,
                goalFeedback = response.goalImprovement.feedback,
                goalImprovedContent = response.goalImprovement.improvedContent,
                impactFeedback = response.impactImprovement.feedback,
                impactImprovedContent = response.impactImprovement.improvedContent,
                recommendedCategory = response.recommendedCategory,
                recommendedKeywords = response.recommendedKeywords.joinToString(",")
            )

            response.sectionImprovements.forEach { improvement ->
                val sectionAnalysis = AiExperienceSectionAnalysis.create(analysis, improvement)
                analysis.addSection(sectionAnalysis)
            }

            return analysis
        }
    }

    // 연관관계 편의 메서드
    fun addSection(section: AiExperienceSectionAnalysis) {
        this.sections.add(section)
    }
}
