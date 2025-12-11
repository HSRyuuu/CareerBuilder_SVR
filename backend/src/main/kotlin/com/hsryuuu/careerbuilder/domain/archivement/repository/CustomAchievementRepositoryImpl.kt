package com.hsryuuu.careerbuilder.domain.archivement.repository

import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.Achievement
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.QAchievement
import com.hsryuuu.careerbuilder.domain.archivement.model.type.AchievementSortKey
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CustomAchievementRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomAchievementRepository {

    private val achievement = QAchievement.achievement

    override fun searchAchievement(
        searchKeyword: String?, sortKey: AchievementSortKey,
        sortDirection: SortDirection?, pageable: Pageable
    ): Page<Achievement> {
        // 검색 조건 생성
        val searchCondition = createSearchCondition(searchKeyword)

        // 정렬 조건 생성
        val orderSpecifiers = createOrderSpecifiers(sortKey, sortDirection)

        // 데이터 조회
        val results = queryFactory
            .selectFrom(achievement)
            .where(searchCondition)
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 개수 조회
        val total = queryFactory
            .select(achievement.count())
            .from(achievement)
            .where(searchCondition)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    /**
     * 검색 조건 생성
     */
    private fun createSearchCondition(searchKeyword: String?): BooleanExpression? {
        if (searchKeyword.isNullOrBlank()) {
            return null
        }

        val keyword = searchKeyword.lowercase()
        return achievement.title.lower().contains(keyword)
            .or(achievement.orgName.lower().contains(keyword))
    }

    /**
     * 정렬 조건 생성
     */
    private fun createOrderSpecifiers(
        sortKey: AchievementSortKey,
        sortDirection: SortDirection?
    ): List<OrderSpecifier<*>> {

        // 정렬 방향 결정 (기본값: DESC)
        val direction = when (sortDirection) {
            SortDirection.ASC -> Order.ASC
            SortDirection.DESC, null -> Order.DESC
        }

        // 1차 정렬 기준: sortKey 에 따라 createdAt / updatedAt
        val primary = when (sortKey) {
            AchievementSortKey.DURATION_START -> OrderSpecifier(direction, achievement.durationStart)
            AchievementSortKey.UPDATED_AT -> OrderSpecifier(direction, achievement.updatedAt)
        }

        // 2차 정렬 기준: 같은 시간일 때 정렬 안정성을 위해 id 기준 추가 (선택사항)
        val secondary = OrderSpecifier(Order.DESC, achievement.id)

        return listOf(primary, secondary)
    }
}
