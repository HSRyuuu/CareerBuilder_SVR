package com.hsryuuu.careerbuilder.domain.archivement.model.entity

import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "achievements")
class Achievement(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: AppUser,

    @Column(nullable = false, columnDefinition = "TEXT")
    val title: String,

    @Column(name = "org_name", columnDefinition = "TEXT")
    val orgName: String? = null,

    @Column(name = "duration_start", nullable = false)
    val durationStart: LocalDate,

    @Column(name = "duration_end")
    val durationEnd: LocalDate? = null,

    @Column(name = "impact_summary", columnDefinition = "TEXT")
    val impactSummary: String? = null,

    @Column(name = "goal_summary", columnDefinition = "TEXT")
    val goalSummary: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val status: AchievementStatus = AchievementStatus.DRAFT,

    @Column(name = "role_title")
    val roleTitle: String? = null,

    @Column(name = "work_type")
    @Enumerated(EnumType.STRING)
    val workType: WorkType? = null,

    @Column(name = "contribution_level")
    @Enumerated(EnumType.STRING)
    val contributionLevel: ContributionLevel? = null,

    @Column(columnDefinition = "TEXT")
    val skills: String? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(
        mappedBy = "achievement",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true
    )
    val sections: MutableList<AchievementSection> = mutableListOf()
) {
    fun addSection(section: AchievementSection) {
        sections.add(section)
        section.achievement = this // 양방향 연관관계 세팅
    }
}


enum class AchievementStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}

enum class WorkType {
    PROJECT, // 3개월짜리 프로젝트
    OPERATION, // 상시 업무 개선
    INCIDENT, // 장애 대응
    PERSONAL, // 개인 프로젝트
    LEARNING, // 스터디 교육 등
    OTHER; // 기타
}

enum class ContributionLevel {
    OWNER, // 처음부터 끝까지 책임지고 리드
    LEAD, // 팀 리드, 기술 리드
    MEMBER, // 팀의 한 구성원으로 기여
    SUPPORT; // 서포트 / 보조
}