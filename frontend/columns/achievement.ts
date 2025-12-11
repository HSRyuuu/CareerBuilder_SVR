/**
 * Achievement(성과) 테이블 컬럼 정의
 */
import { WORK_TYPE_INFO } from '@/types/achievement-types';
import type { WorkType } from '@/types/achievement-types';
import type { TAchievement } from '~/api/achievement/types';
import type { TTableColumn } from '@/components/organisms/Table/Table.vue';

/**
 * 날짜 포맷팅
 */
export const formatDate = (date?: string | null) => {
  if (!date) return '진행중';
  return new Date(date).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
};

/**
 * 업무 유형 표시 텍스트 반환
 */
export const getWorkTypeDisplay = (workType?: string | null) => {
  if (!workType) return '-';
  return WORK_TYPE_INFO[workType as WorkType]?.display || workType;
};

/**
 * 성과 목록 테이블 컬럼 정의
 */
export const achievementColumns: TTableColumn<TAchievement>[] = [
  {
    field: 'durationStart',
    headerName: '시작일',
    flex: 1.5,
    align: 'center',
    cellClass: 'career-list-table-date',
    valueFormatter: (value) => formatDate(value),
  },
  {
    field: 'durationEnd',
    headerName: '종료일',
    flex: 1.5,
    align: 'center',
    cellClass: 'career-list-table-date',
    valueFormatter: (value) => formatDate(value),
  },
  {
    field: 'title',
    headerName: '제목',
    flex: 3,
    align: 'center',
    cellClass: 'career-list-table-title',
  },
  {
    field: 'workType',
    headerName: '업무 유형',
    flex: 1.5,
    align: 'center',
    cellClass: 'career-list-table-work-type',
    valueFormatter: (value) => getWorkTypeDisplay(value),
  },
  {
    field: 'status',
    headerName: '상태',
    flex: 1,
    align: 'center',
    // 커스텀 렌더링은 slot으로 처리
  },
];
