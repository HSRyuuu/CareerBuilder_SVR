package com.hsryuuu.careerbuilder.domain.ai.model.entity

import com.hsryuuu.careerbuilder.domain.ai.model.MethodBreakdown
import com.hsryuuu.careerbuilder.domain.ai.model.SectionImprovement
import com.hsryuuu.careerbuilder.domain.experience.model.entity.SectionKind
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
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

    @Column(name = "suggested_kind", length = 30)
    @Enumerated(EnumType.STRING)
    val suggestedKind: SectionKind? = null,

    @Column(length = 20)
    val method: String? = null, // STAR, PAR, SHORT

    @Column(columnDefinition = "TEXT")
    val feedback: String? = null,

    @Column(name = "improved_content", columnDefinition = "TEXT")
    val improvedContent: String? = null,

    @Column(columnDefinition = "TEXT")
    val reasoning: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "method_breakdown", columnDefinition = "jsonb")
    val methodBreakdown: MethodBreakdown? = null,

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
                suggestedKind = improvement.suggestedKind,
                method = improvement.method,
                feedback = improvement.feedback,
                improvedContent = improvement.improvedContent,
                reasoning = improvement.reasoning,
                methodBreakdown = improvement.breakdown
            )
        }
    }
}
