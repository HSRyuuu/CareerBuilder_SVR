/**
 * Achievement(성과) API 타입 정의
 */
import type {
  AchievementSectionKind,
  AchievementStatus,
  WorkType,
  ContributionLevel,
} from '@/types/achievement-types';

/**
 * 성과 블록 생성 요청 타입
 */
export type TAchievementSectionCreate = {
  kind: AchievementSectionKind | string;
  title: string;
  content: string;
  sortOrder: number;
};

/**
 * 성과 블록 응답 타입
 */
export type TAchievementSection = {
  id: string;
  achievementId: string;
  kind: AchievementSectionKind | string;
  title: string;
  content: string;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
};

/**
 * 성과 생성 요청 타입
 */
export type TAchievementCreate = {
  title: string;
  orgName?: string;
  roleTitle?: string;
  durationStart: string;
  durationEnd?: string;
  workType?: WorkType | string;
  contributionLevel?: ContributionLevel | string;
  goalSummary?: string;
  impactSummary?: string;
  skills?: string;
  status?: AchievementStatus | string;
  sections: TAchievementSectionCreate[];
};

/**
 * 성과 수정 요청 타입
 */
export type TAchievementUpdate = TAchievementCreate;

/**
 * 성과 응답 타입
 */
export type TAchievement = {
  id: string;
  userId: string;
  title: string;
  orgName?: string;
  roleTitle?: string;
  durationStart: string;
  durationEnd?: string;
  workType?: WorkType | string;
  contributionLevel?: ContributionLevel | string;
  goalSummary?: string;
  impactSummary?: string;
  skills?: string;
  status: AchievementStatus | string;
  createdAt: string;
  updatedAt: string;
  sections: TAchievementSection[];
};

/**
 * 성과 정렬 키
 */
export type AchievementSortKey = 'UPDATED_AT' | 'DURATION_START';

/**
 * 정렬 방향
 */
export type SortDirection = 'ASC' | 'DESC';

/**
 * 성과 목록 조회 파라미터 타입
 */
export type TAchievementListParams = {
  q?: string; // 검색 키워드
  p?: number; // 페이지 번호 (1부터 시작)
  size?: number; // 페이지 크기
  sortKey?: AchievementSortKey; // 정렬 키
  sortDir?: SortDirection; // 정렬 방향
};

/**
 * 페이지네이션 응답 타입 (CommonPageResponse)
 */
export type TPageResponse<T> = {
  total: number; // 전체 항목 수
  currentPage: number; // 현재 페이지 (0부터 시작)
  pageSize: number; // 페이지 크기
  totalPages: number; // 전체 페이지 수
  list: T[]; // 데이터 목록
};

/**
 * 성과 통계 응답 타입
 */
export type TAchievementStats = {
  total: number;
  completed: number;
  needsImprovement: number;
  aiAnalyzed: number;
};
