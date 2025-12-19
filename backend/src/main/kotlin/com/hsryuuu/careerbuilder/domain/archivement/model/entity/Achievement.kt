package com.hsryuuu.careerbuilder.domain.archivement.model.entity

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
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
    var title: String,

    @Column(name = "org_name", columnDefinition = "TEXT")
    var orgName: String? = null,

    @Column(name = "duration_start", nullable = false)
    var durationStart: LocalDate,

    @Column(name = "duration_end")
    var durationEnd: LocalDate? = null,

    @Column(name = "impact_summary", columnDefinition = "TEXT")
    var impactSummary: String? = null,

    @Column(name = "goal_summary", columnDefinition = "TEXT")
    var goalSummary: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: AchievementStatus = AchievementStatus.DRAFT,

    @Column(name = "role_title")
    var roleTitle: String? = null,

    @Column(name = "work_type")
    @Enumerated(EnumType.STRING)
    var workType: WorkType? = null,

    @Column(name = "contribution_level")
    @Enumerated(EnumType.STRING)
    var contributionLevel: ContributionLevel? = null,

    @Column(columnDefinition = "TEXT")
    var skills: String? = null,

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
    /**
     * Section을 추가하고 양방향 연관관계를 설정합니다.
     */
    fun addSection(section: AchievementSection) {
        sections.add(section)
        section.achievement = this
    }

    /**
     * Achievement의 기본 정보를 업데이트합니다.
     * DDD 패턴: 도메인 로직을 엔티티 내부에 캡슐화
     */
    fun update(
        title: String,
        orgName: String?,
        durationStart: LocalDate,
        durationEnd: LocalDate?,
        impactSummary: String?,
        goalSummary: String?,
        roleTitle: String?,
        workType: WorkType?,
        contributionLevel: ContributionLevel?,
        skills: String?
    ) {
        // durationStart는 durationEnd보다 앞서야 함
        if (durationEnd != null && !durationStart.isBefore(durationEnd)) {
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        this.title = title
        this.orgName = orgName
        this.durationStart = durationStart
        this.durationEnd = durationEnd
        this.impactSummary = impactSummary
        this.goalSummary = goalSummary
        this.roleTitle = roleTitle
        this.workType = workType
        this.contributionLevel = contributionLevel
        this.skills = skills
    }

    /**
     * Section 컬렉션을 업데이트합니다.
     * 전략: Clear & AddAll 방식 (orphanRemoval=true로 삭제 자동 처리)
     */
    fun updateSections(newSections: List<AchievementSection>) {
        sections.clear()
        newSections.forEach { addSection(it) }
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