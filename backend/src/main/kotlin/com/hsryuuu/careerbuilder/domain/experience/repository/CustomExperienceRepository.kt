package com.hsryuuu.careerbuilder.domain.experience.repository

import com.hsryuuu.careerbuilder.common.dto.type.SortDirection
import com.hsryuuu.careerbuilder.domain.experience.model.dto.ExperienceStatsSummary
import com.hsryuuu.careerbuilder.domain.experience.model.entity.Experience
import com.hsryuuu.careerbuilder.domain.experience.model.entity.ExperienceStatus
import com.hsryuuu.careerbuilder.domain.experience.model.type.ExperienceSortKey
import com.hsryuuu.careerbuilder.domain.user.appuser.model.entity.AppUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomExperienceRepository {
    /**
     * 경험 검색 (QueryDSL 기반)
     *
     * @param searchKeyword 검색어 (title 또는 orgName)
     * @param pageable 페이징 및 정렬 정보
     * @return Page<Experience>
     */
    fun searchExperience(
        user: AppUser,
        searchKeyword: String?,
        status: ExperienceStatus?,
        sortKey: ExperienceSortKey,
        sortDirection: SortDirection?,
        pageable: Pageable
    ): Page<Experience>

    /**
     * 경험 통계 요약 조회 (QueryDSL 기반)
     */
    fun getStatsSummary(user: AppUser): ExperienceStatsSummary
}