package com.hsryuuu.careerbuilder.domain.experience.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "experience_sections")
class ExperienceSection(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    var experience: Experience? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var kind: SectionKind = SectionKind.NONE,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Section 정보를 업데이트합니다.
     */
    fun update(
        kind: SectionKind,
        title: String,
        content: String,
        sortOrder: Int
    ) {
        this.kind = kind
        this.title = title
        this.content = content
        this.sortOrder = sortOrder
    }
}

enum class SectionKind(
    val display: String
) {
    NONE("미정"),
    SITUATION("배경 및 목표"),       // Context + Goal 통합
    TASK("수행 내용"),              // 단순 실행 내역
    DECISION("의사결정 및 근거"),     // 왜 그렇게 했는지 (전문성)
    TROUBLESHOOTING("문제 해결 과정"), // 위기 극복 (역량)
    ACHIEVEMENT("성과 및 결과"),     // Result -> Achievement
    LEARNING("회고 및 성장"),       // Learning + Feedback 통합
    ARTIFACT("증빙 자료/링크");

    companion object {
        val allNamesAndDisplays: String
            get() = entries.joinToString { "${it.name}(${it.display})" }
    }
}