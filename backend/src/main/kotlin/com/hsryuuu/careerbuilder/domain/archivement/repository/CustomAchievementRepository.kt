package com.hsryuuu.careerbuilder.domain.archivement.repository

import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.archivement.model.entity.Achievement
import com.hsryuuu.careerbuilder.domain.archivement.model.type.AchievementSortKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomAchievementRepository {
    /**
     * 성과 검색 (QueryDSL 기반)
     *
     * @param searchKeyword 검색어 (title 또는 orgName)
     * @param pageable 페이징 및 정렬 정보
     * @return Page<Achievement>
     */
    fun searchAchievement(
        searchKeyword: String?, sortKey: AchievementSortKey,
        sortDirection: SortDirection?, pageable: Pageable
    ): Page<Achievement>
}
