/**
 * Experience(경험) 테이블 컬럼 정의
 */
import { CATEGORY_INFO } from '@/types/experience-types';
import type { Category } from '@/types/experience-types';
import type { TExperience } from '~/api/experience/types';
import type { TTableColumn } from '@/components/organisms/Table/Table.vue';

/**
 * 날짜 포맷팅
 */
export const formatDate = (date?: string | null) => {
  if (!date) return '진행중';
  // yyyy-MM 형식이면 하이픈을 점으로 치환하여 표시
  if (typeof date === 'string' && date.includes('-')) {
    return date.replace('-', '.');
  }
  return date;
};

/**
 * 날짜 시간 포맷팅
 */
export const formatDateTime = (date?: string | null) => {
  if (!date) return '-';
  const d = new Date(date);
  return `${d.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })} ${d.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  })}`;
};

/**
 * 업무 유형 표시 텍스트 반환
 */
export const getcategoryDisplay = (category?: string | null) => {
  if (!category) return '-';
  return CATEGORY_INFO[category as Category]?.display || category;
};

/**
 * 제목 말줄임 처리
 */
export const truncateTitle = (title: string, maxLength: number = 30) => {
  if (title.length > maxLength) {
    return title.substring(0, maxLength) + '...';
  }
  return title;
};

/**
 * 경험 목록 테이블 컬럼 정의
 */
export const experienceColumns: TTableColumn<TExperience>[] = [
  {
    field: 'periodStart',
    headerName: '시작일',
    flex: 1,
    align: 'center',
    cellClass: 'experience-list-table-date',
    valueFormatter: (value) => formatDate(value),
  },
  {
    field: 'periodEnd',
    headerName: '종료일',
    flex: 1,
    align: 'center',
    cellClass: 'experience-list-table-date',
    valueFormatter: (value) => formatDate(value),
  },
  {
    field: 'title',
    headerName: '제목',
    flex: 3,
    align: 'left',
    cellClass: 'experience-list-table-title',
    valueFormatter: (value) => truncateTitle(value || ''),
  },
  {
    field: 'category',
    headerName: '업무 유형',
    flex: 1,
    align: 'center',
    cellClass: 'experience-list-table-work-type',
    valueFormatter: (value) => getcategoryDisplay(value),
  },
  {
    field: 'status',
    headerName: '상태',
    flex: 1,
    align: 'center',
    // 커스텀 렌더링은 slot으로 처리
  },
  {
    field: 'updatedAt',
    headerName: '최종 수정일시',
    flex: 1.5,
    align: 'center',
    cellClass: 'experience-list-table-date',
    valueFormatter: (value) => formatDateTime(value),
  },
];

/**
 * AI 경험 분석 페이지용 간소화된 컬럼 정의 (제목, 상태만)
 * - 분석요청, 이동 버튼은 ExperienceTable props로 추가
 */
export const aiExperienceColumns: TTableColumn<TExperience>[] = [
  {
    field: 'title',
    headerName: '제목',
    flex: 3,
    align: 'left',
    cellClass: 'experience-list-table-title',
    valueFormatter: (value) => truncateTitle(value || ''),
  },
  {
    field: 'status',
    headerName: '상태',
    flex: 1,
    align: 'center',
  },
];
