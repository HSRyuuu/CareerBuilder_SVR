package com.hsryuuu.careerbuilder.common.dto

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.math.ceil

/**
 * 공통 페이징 응답 DTO
 *
 * @param T 리스트 요소 타입
 */
data class CommonPageResponse<T>(
    /** 조회 결과의 총 개수 */
    val total: Long = 0L,

    /** 현재 페이지 (0-based, Spring Data Page 기준) */
    val currentPage: Int = 0,

    /** 페이지 크기 */
    val pageSize: Int = 0,

    /** 전체 페이지 개수 */
    val totalPages: Int = 0,

    /** 조회 결과 리스트 */
    val list: List<T> = emptyList()
) {

    /**
     * Spring Data Page<T> 를 그대로 감싸는 생성자
     */
    constructor(page: Page<T>) : this(
        total = page.totalElements,
        currentPage = page.number,
        pageSize = page.size,
        totalPages = page.totalPages,
        list = page.content
    )

    companion object {

        /**
         * total + pageable + list 를 이용해서 CommonPageResponse 생성
         *
         * - total: 전체 개수
         * - pageable: page, size 정보
         * - list: 현재 페이지에 해당하는 데이터 목록
         */
        fun <T> of(
            total: Long,
            pageable: Pageable,
            list: List<T>
        ): CommonPageResponse<T> {
            // PageImpl 를 한 번 거쳐도 되고, 직접 계산해도 됩니다.
            // 원래 코드 스타일을 살려서 PageImpl 사용 버전으로 구현합니다.
            val page: Page<T> = PageImpl(list, pageable, total)
            return CommonPageResponse(page)
        }

        /**
         * Page<T> 에서 CommonPageResponse<R> 로 매핑
         *
         * - Page<T> 의 메타데이터(total, page, size, totalPages)는 그대로 가져오고
         * - content(T)는 mapper를 통해 R로 변환합니다.
         */
        fun <T, R> from(page: Page<T>, mapper: (T) -> R): CommonPageResponse<R> {
            val mappedList = page.content.map(mapper)

            return CommonPageResponse(
                total = page.totalElements,
                currentPage = page.number,
                pageSize = page.size,
                totalPages = page.totalPages,
                list = mappedList
            )
        }

        /**
         * total, currentPage, pageSize, list 로 직접 생성하는 헬퍼
         * (Java 버전의 두 번째 생성자 대체용)
         */
        fun <T> of(
            total: Long,
            currentPage: Int,
            pageSize: Int,
            list: List<T>
        ): CommonPageResponse<T> {
            val totalPages = if (pageSize > 0) {
                ceil(total.toDouble() / pageSize.toDouble()).toInt()
            } else {
                0
            }

            return CommonPageResponse(
                total = total,
                currentPage = currentPage,
                pageSize = pageSize,
                totalPages = totalPages,
                list = list
            )
        }
    }
}