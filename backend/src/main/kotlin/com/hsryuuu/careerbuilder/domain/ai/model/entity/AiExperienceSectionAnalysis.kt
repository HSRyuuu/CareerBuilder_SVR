package com.hsryuuu.careerbuilder.domain.ai.model.entity

import com.hsryuuu.careerbuilder.domain.ai.model.SectionImprovement
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "ai_experience_section_analysis")
class AiExperienceSectionAnalysis(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    val analysis: AiExperienceAnalysis,

    @Column(name = "section_id", nullable = false)
    val sectionId: UUID,

    @Column(length = 10)
    val method: String? = null, // STAR, PAR, SHORT

    @Column(columnDefinition = "TEXT")
    val feedback: String? = null,

    @Column(name = "improved_content", columnDefinition = "TEXT")
    val improvedContent: String? = null,

    @Column(columnDefinition = "TEXT")
    val reasoning: String? = null,

    // STAR/PAR Breakdown
    @Column(name = "breakdown_situation", columnDefinition = "TEXT")
    val breakdownSituation: String? = null,

    @Column(name = "breakdown_task", columnDefinition = "TEXT")
    val breakdownTask: String? = null,

    @Column(name = "breakdown_problem", columnDefinition = "TEXT")
    val breakdownProblem: String? = null,

    @Column(name = "breakdown_action", columnDefinition = "TEXT")
    val breakdownAction: String? = null,

    @Column(name = "breakdown_result", columnDefinition = "TEXT")
    val breakdownResult: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            analysis: AiExperienceAnalysis,
            improvement: SectionImprovement
        ): AiExperienceSectionAnalysis {
            return AiExperienceSectionAnalysis(
                analysis = analysis,
                sectionId = improvement.sectionId,
                method = improvement.method,
                feedback = improvement.feedback,
                improvedContent = improvement.improvedContent,
                reasoning = improvement.reasoning,
                breakdownSituation = improvement.breakdown?.situation,
                breakdownTask = improvement.breakdown?.task,
                breakdownProblem = improvement.breakdown?.problem,
                breakdownAction = improvement.breakdown?.action,
                breakdownResult = improvement.breakdown?.result
            )
        }
    }
}
