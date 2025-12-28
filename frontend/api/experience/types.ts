/**
 * Experience(경험) API 타입 정의
 */
import type {
  ExperienceSectionKind,
  ExperienceStatus,
  WorkType,
  ContributionLevel,
} from '@/types/experience-types';

/**
 * 경험 블록 생성 요청 타입
 */
export type TExperienceSectionCreate = {
  kind: ExperienceSectionKind | string;
  title: string;
  content: string;
  sortOrder: number;
};

/**
 * 경험 블록 수정 요청 타입 (id 포함)
 */
export type TExperienceSectionUpdate = {
  id?: string; // 기존 블록: 서버 id, 새 블록: new_section_* 또는 undefined
  kind: ExperienceSectionKind | string;
  title: string;
  content: string;
  sortOrder: number;
};

/**
 * 경험 블록 응답 타입
 */
export type TExperienceSection = {
  id: string;
  kind: ExperienceSectionKind | string;
  title: string;
  content: string;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
};

/**
 * 경험 생성 요청 타입
 */
export type TExperienceCreate = {
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
  status?: ExperienceStatus | string;
  sections: TExperienceSectionCreate[];
};

/**
 * 경험 수정 요청 타입
 */
export type TExperienceUpdate = {
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
  status?: ExperienceStatus | string;
  sections: TExperienceSectionUpdate[];
};

/**
 * 경험 응답 타입
 */
export type TExperience = {
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
  status: ExperienceStatus | string;
  createdAt: string;
  updatedAt: string;
  sections: TExperienceSection[];
};

/**
 * 경험 정렬 키
 */
export type ExperienceSortKey = 'UPDATED_AT' | 'DURATION_START';

/**
 * 정렬 방향
 */
export type SortDirection = 'ASC' | 'DESC';

/**
 * 경험 목록 조회 파라미터 타입
 */
export type TExperienceListParams = {
  q?: string; // 검색 키워드
  p?: number; // 페이지 번호 (1부터 시작)
  size?: number; // 페이지 크기
  sortKey?: ExperienceSortKey; // 정렬 키
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
 * 경험 통계 응답 타입
 */
export type TExperienceStats = {
  total: number;
  completed: number;
  needsImprovement: number;
  aiAnalyzed: number;
};
