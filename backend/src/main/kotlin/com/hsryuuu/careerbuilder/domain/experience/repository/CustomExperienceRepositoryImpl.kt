package com.hsryuuu.careerbuilder.domain.experience.repository

import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceStatsSummary
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.entity.QExperience
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

@Repository
class CustomExperienceRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomExperienceRepository {

    private val experience = QExperience.experience

    override fun searchExperience(
        user: AppUser,
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
            .where(experience.user.eq(user), searchCondition, statusCondition)
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

    /**
     * 검색 조건 생성
     */
    private fun createSearchCondition(searchKeyword: String?): BooleanExpression? {
        if (searchKeyword.isNullOrBlank()) {
            return null
        }

        val keyword = searchKeyword.lowercase()
        return experience.title.lower().contains(keyword)
            .or(experience.background.lower().contains(keyword))
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