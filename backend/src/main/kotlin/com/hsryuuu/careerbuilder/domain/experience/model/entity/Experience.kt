package com.hsryuuu.careerbuilder.domain.experience.model.entity

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.domain.experience.model.dto.UpdateSectionRequest
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "experiences")
class Experience(
    @Id
    @UuidGenerator
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: AppUser,

    @Column(nullable = false, columnDefinition = "TEXT")
    var title: String,

    @Column(name = "background", columnDefinition = "TEXT")
    var background: String? = null, // 배경, 환경

    @Column(name = "period_start", nullable = false)
    var periodStart: String,

    @Column(name = "period_end")
    var periodEnd: String? = null,

    @Column(name = "key_achievements", columnDefinition = "TEXT")
    var keyAchievements: String? = null, // 핵심 성과

    @Column(name = "goal_summary", columnDefinition = "TEXT")
    var goalSummary: String? = null, // 목표

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: ExperienceStatus = ExperienceStatus.INCOMPLETE,

    @Column(name = "progress_score")
    var progressScore: Int = 0,

    @Column(name = "role")
    var role: String? = null,

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    var category: WorkCategory? = null,

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
        mappedBy = "experience",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true
    )
    val sections: MutableList<ExperienceSection> = mutableListOf()
) {
    /**
     * Section을 추가하고 양방향 연관관계를 설정합니다.
     */
    fun addSection(section: ExperienceSection) {
        sections.add(section)
        section.experience = this
    }

    /**
     * Experience의 기본 정보를 업데이트합니다.
     * DDD 패턴: 도메인 로직을 엔티티 내부에 캡슐화
     */
    fun update(
        title: String,
        background: String?,
        periodStart: String,
        periodEnd: String?,
        keyAchievements: String?,
        goalSummary: String?,
        role: String?,
        category: WorkCategory?,
        contributionLevel: ContributionLevel?,
        skills: String?
    ) {
        // periodStart는 periodEnd보다 앞서야 함
        if (periodEnd != null && periodStart > periodEnd) {
            throw GlobalException(ErrorCode.VALIDATION_ERROR_DURATION_SEQUENCE)
        }

        this.title = title
        this.background = background
        this.periodStart = periodStart
        this.periodEnd = periodEnd
        this.keyAchievements = keyAchievements
        this.goalSummary = goalSummary
        this.role = role
        this.category = category
        this.contributionLevel = contributionLevel
        this.skills = skills
    }

    /**
     * Section 컬렉션을 업데이트합니다.
     * 전략: Clear & AddAll 방식 (orphanRemoval=true로 삭제 자동 처리)
     */
    fun updateSections(reqSections: List<UpdateSectionRequest>) {
        val requestSectionIds = reqSections.mapNotNull { it.id }.toSet()
        sections.removeIf { it.id !in requestSectionIds }
        reqSections.forEach { section ->
            val existingSection = this.sections.find { it.id == section.id }
            // 새로 추가된 섹션
            if (existingSection == null) {
                addSection(
                    ExperienceSection(
                        kind = section.kind,
                        title = section.title,
                        content = section.content,
                        sortOrder = section.sortOrder
                    )
                )
            } else {
                existingSection.update(
                    kind = section.kind,
                    title = section.title,
                    content = section.content,
                    sortOrder = section.sortOrder
                )
            }
        }
    }

    fun validateRequiredValue() {
        if (this.title.isBlank() || this.title.length < 5) {
            throw GlobalException(ErrorCode.EXPERIENCE_TITLE_REQUIRED)
        }
        if (this.background.isNullOrBlank()) {
            throw GlobalException(ErrorCode.EXPERIENCE_BACKGROUND_REQUIRED)
        }
        if (this.role.isNullOrBlank()) {
            throw GlobalException(ErrorCode.EXPERIENCE_ROLE_REQUIRED)
        }
    }

    fun calculateProgressScore() {
        var score = 0;
        if (this.title.length > 5) score += 10
        if (!this.background.isNullOrBlank()) score += 10
        if (!this.role.isNullOrBlank()) score += 10
        if (!this.skills.isNullOrBlank()) score += 10

        if (!this.goalSummary.isNullOrBlank() && this.goalSummary!!.length > 30) {
            score += 10
        }
        if (!this.keyAchievements.isNullOrBlank() && this.keyAchievements!!.length > 30) {
            score += 10
        }

        if (this.sections.isNotEmpty()) {
            score += 10;
        }
        // 여기까지 70점 아래는 유연하게 조정

        val remainScore = 100 - score; // 30점
        var sectionScore = 0;
        this.sections.forEach {
            if (it.content.length > 20) {
                sectionScore += 10;
            }
        }
        if (sectionScore > remainScore) {
            score = 100;
        } else {
            score += sectionScore;
        }

        this.progressScore = score;
    }

    fun setStatusByProgressScore(isEdit: Boolean) {
        if (this.progressScore < 70) {
            this.status = ExperienceStatus.INCOMPLETE
        } else {
            this.status = if (isEdit) {
                ExperienceStatus.MODIFIED
            } else {
                ExperienceStatus.COMPLETED
            }
        }
    }


}


enum class ExperienceStatus(val description: String) {
    // 1. 작성 중 (필수 정보가 누락되었거나 사용자가 임시 저장한 상태)
    INCOMPLETE("보완 필요"),

    // 2. 작성 완료 (필수 정보가 모두 입력된 상태)
    COMPLETED("작성 완료"),

    // 3. 수정 완료 (수정 완료된 상태)
    MODIFIED("수정 완료"),

    // 4. AI 분석 요청 상태
    AI_REQUEST("AI 분석 요청"),

    // 5. AI 분석 중
    ANALYZING("AI 분석 중"),

    // 6. AI 분석 완료
    ANALYZED("AI 분석 완료")
}

enum class WorkCategory(val description: String) {
    PROJECT("목표 달성을 위해 초기 기획부터 실행까지 참여한 주요 과업"),
    MAINTENANCE("지속적인 업무 운영과 프로세스 개선 및 품질 향상 활동"),
    TROUBLESHOOTING("예기치 못한 문제 상황 대응 및 병목 구간 해결 경험"),
    R_AND_D("신규 도입을 위한 조사, 타당성 검토 및 모델링/프로토타이핑"),
    LEARNING("전문성 강화를 위한 새로운 지식 습득 및 교육 참여"),
    OTHER("기타 활동");
}

enum class ContributionLevel {
    OWNER, // 처음부터 끝까지 책임지고 리드
    LEAD, // 팀 리드, 기술 리드
    MEMBER, // 팀의 한 구성원으로 기여
    SUPPORT; // 서포트 / 보조
}
