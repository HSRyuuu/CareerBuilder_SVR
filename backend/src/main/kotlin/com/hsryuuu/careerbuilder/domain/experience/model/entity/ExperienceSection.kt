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
    NONE("미정"),         // 아직 정하지 않음
    GOAL("목표/문제"),      // 목표/문제 정의
    ACTION("실행 내용"),        // 내가 실제로 한 일
    RESULT("결과 및 성과"),            // 결과, 성과 (숫자)
    CONTEXT("배경/상황"),   // 일을 하게 된 배경 / 상황
    CHALLENGE("어려웠던 점"),   // 어려웠던 점
    LEARNING("배운 점"),    // 배운 점 / 성장
    FEEDBACK("피드백/평가"), // 받은 피드백, 평가
    ARTIFACT("증빙 자료/링크");
}