package com.hsryuuu.careerbuilder.domain.experience.repository

import com.hsryuuu.careerbuilder.application.exception.ErrorCode
import com.hsryuuu.careerbuilder.application.exception.GlobalException
import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.ai.model.entity.*
import com.hsryuuu.careerbuilder.domain.experience.model.dto.*
import com.hsryuuu.careerbuilder.domain.experience.model.entity.*
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import com.querydsl.core.Tuple
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CustomExperienceRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomExperienceRepository {

    private val experience = QExperience.experience
    private val aiAnalysis = QAiExperienceAnalysis.aiExperienceAnalysis
    private val aiRequest = QAiRequest.aiRequest

    override fun searchExperience(
        userId: UUID,
        searchKeyword: String?,
        status: ExperienceStatus?,
        sortKey: ExperienceSortKey,
        sortDirection: SortDirection?, pageable: Pageable
    ): Page<Experience> {
        // 검색 조건 생성
        val searchCondition = createSearchCondition(searchKeyword)
        val statusCondition = status?.let { experience.status.eq(status) }
        // 정렬 조건 생성
        val orderSpecifiers = createOrderSpecifiers(sortKey, sortDirection)

        // 데이터 조회
        val results = queryFactory
            .selectFrom(experience)
            .where(experience.user.id.eq(userId), searchCondition, statusCondition)
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 개수 조회
        val total = queryFactory
            .select(experience.count())
            .from(experience)
            .where(searchCondition, statusCondition)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    override fun getStatsSummary(user: AppUser): ExperienceStatsSummary {
        val results: List<Tuple> = queryFactory
            .select(experience.status, experience.count())
            .from(experience)
            .where(experience.user.eq(user))
            .groupBy(experience.status)
            .fetch()

        var total: Long = 0
        var incomplete: Long = 0
        var completed: Long = 0
        var analyzing: Long = 0
        var analyzed: Long = 0

        results.forEach { tuple ->
            val status = tuple.get(experience.status)
            val count = tuple.get(experience.count()) ?: 0L

            total += count
            when (status) {
                ExperienceStatus.INCOMPLETE -> incomplete = count
                ExperienceStatus.COMPLETED -> completed = count
                ExperienceStatus.ANALYZING -> analyzing = count
                ExperienceStatus.ANALYZED -> analyzed = count
                else -> {}
            }
        }

        return ExperienceStatsSummary(
            total = total,
            incomplete = incomplete,
            completed = completed,
            analyzing = analyzing,
            analyzed = analyzed
        )
    }

    override fun getExperienceWithAnalysis(experienceId: UUID, userId: UUID): ExperienceWithAnalysisResponse? {
        val section = QExperienceSection.experienceSection
        val analysis = QAiExperienceAnalysis.aiExperienceAnalysis
        val sectionAnalysis = QAiExperienceSectionAnalysis.aiExperienceSectionAnalysis

        // 1. 가장 최신의 AI 분석 ID 조회
        val latestAnalysisId = queryFactory
            .select(analysis.id)
            .from(analysis)
            .where(analysis.experienceId.eq(experienceId))
            .orderBy(analysis.createdAt.desc())
            .limit(1)
            .fetchOne()

        if (latestAnalysisId == null) {
            throw GlobalException(ErrorCode.AI_EXPERIENCE_ANALYSIS_NOT_FOUND)
        }

        // 2. Tuple로 필요한 엔티티들을 모두 조회
        // Experience -(OneToMany)-> Section
        // Experience -(Logical OneToOne)-> Analysis (최신건만)
        // Section -(Logical OneToOne)-> SectionAnalysis
        val tuples = queryFactory
            .select(experience, section, analysis, sectionAnalysis)
            .from(experience)
            .leftJoin(experience.sections, section)
            .leftJoin(analysis).on(analysis.id.eq(latestAnalysisId))
            .leftJoin(sectionAnalysis)
            .on(sectionAnalysis.sectionId.eq(section.id).and(sectionAnalysis.analysis.id.eq(latestAnalysisId)))
            .where(experience.id.eq(experienceId), experience.user.id.eq(userId))
            .fetch()

        if (tuples.isEmpty()) {
            return null
        }

        // 데이터 조합
        val expEntity = tuples.first().get(experience)!!
        val sectionsMap = mutableMapOf<UUID, ExperienceSection>()
        var analysisEntity: AiExperienceAnalysis? = null
        val sectionAnalysisMap = mutableMapOf<UUID, AiExperienceSectionAnalysis>()

        tuples.forEach { tuple ->
            tuple.get(section)?.let { sectionsMap[it.id!!] = it }
            if (analysisEntity == null) {
                tuple.get(analysis)?.let { analysisEntity = it }
            }
            tuple.get(sectionAnalysis)?.let { sectionAnalysisMap[it.sectionId] = it }
        }

        // 1. ExperienceResponse (sections 제외)
        val experienceResponse = ExperienceResponse.fromEntityWithoutSections(expEntity)

        // 2. SectionsWithAnalysisDto 리스트 생성
        val sortedSections = sectionsMap.values.sortedBy { it.sortOrder }
        val sectionWithAnalysisList = sortedSections.map { section ->
            val analysisDto = sectionAnalysisMap[section.id]?.let { AiExperienceSectionAnalysisDto.from(it) }
            SectionWithAnalysisDto(
                section = SectionResponse.from(section),
                analysis = analysisDto
            )
        }

        // 3. AiExperienceAnalysisDto 생성 (sectionAnalyses 제외)
        val aiAnalysisDto = analysisEntity?.let { entity ->
            AiExperienceAnalysisDto(
                id = entity.id!!,
                totalScore = entity.totalScore,
                scoreMetrics = entity.scoreMetrics,
                overallSummary = entity.overallSummary,
                overallFeedback = entity.overallFeedback,
                goalFeedback = entity.goalFeedback,
                goalImprovedContent = entity.goalImprovedContent,
                achievementFeedback = entity.achievementFeedback,
                achievementImprovedContent = entity.achievementImprovedContent,
                recommendedKeywords = entity.recommendedKeywords?.split(",")?.map { it.trim() }
                    ?.filter { it.isNotEmpty() },
                sectionAnalyses = emptyList() // 상세 분석은 별도 필드로 전달
            )
        }

        return ExperienceWithAnalysisResponse(
            experience = experienceResponse,
            analysis = aiAnalysisDto,
            sections = sectionWithAnalysisList
        )
    }

    override fun existsAiAnalysisResultById(
        experienceId: UUID,
        userId: UUID
    ): Boolean {

        val fetchOne = queryFactory
            .selectOne()
            .from(aiAnalysis)
            .join(aiRequest).on(aiAnalysis.requestId.eq(aiRequest.id))
            .where(
                aiAnalysis.experienceId.eq(experienceId),
                aiRequest.userId.eq(userId) // 보안 및 데이터 무결성을 위해 userId 조건 추가 권장
            )
            .fetchFirst() // limit(1).fetchOne()과 동일

        return fetchOne != null
    }

    /**
     * 검색 조건 생성
     */
    private fun createSearchCondition(searchKeyword: String?): BooleanExpression? {
        if (searchKeyword.isNullOrBlank()) {
            return null
        }

        val keyword = searchKeyword.lowercase()
        return experience.title.lower().contains(keyword)
            .or(experience.role.upper().contains(keyword))
    }

    /**
     * 정렬 조건 생성
     */
    private fun createOrderSpecifiers(
        sortKey: ExperienceSortKey,
        sortDirection: SortDirection?
    ): List<OrderSpecifier<*>> {

        // 정렬 방향 결정 (기본값: DESC)
        val direction = when (sortDirection) {
            SortDirection.ASC -> Order.ASC
            SortDirection.DESC, null -> Order.DESC
        }

        // 1차 정렬 기준: sortKey 에 따라 createdAt / updatedAt
        val primary = when (sortKey) {
            ExperienceSortKey.DURATION_START -> OrderSpecifier(direction, experience.periodStart)
            ExperienceSortKey.UPDATED_AT -> OrderSpecifier(direction, experience.updatedAt)
        }

        // 2차 정렬 기준: 같은 시간일 때 정렬 안정성을 위해 id 기준 추가 (선택사항)
        val secondary = OrderSpecifier(Order.DESC, experience.id)

        return listOf(primary, secondary)
    }
}