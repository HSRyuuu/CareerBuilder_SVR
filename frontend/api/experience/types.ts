/**
 * Experience(경험) API 타입 정의
 */
import type {
  ExperienceSectionKind,
  ExperienceStatus,
  Category,
  ContributionLevel,
  TExperienceStatsSummary,
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
  background?: string;
  role?: string;
  periodStart: string;
  periodEnd?: string;
  category?: Category | string;
  contributionLevel?: ContributionLevel | string;
  goalSummary?: string;
  keyAchievements?: string;
  skills?: string;
  status?: ExperienceStatus | string;
  sections: TExperienceSectionCreate[];
};

/**
 * 경험 수정 요청 타입
 */
export type TExperienceUpdate = {
  title: string;
  background?: string;
  role?: string;
  periodStart: string;
  periodEnd?: string;
  category?: Category | string;
  contributionLevel?: ContributionLevel | string;
  goalSummary?: string;
  keyAchievements?: string;
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
  background?: string;
  role?: string;
  periodStart: string;
  periodEnd?: string;
  category?: Category | string;
  contributionLevel?: ContributionLevel | string;
  goalSummary?: string;
  keyAchievements?: string;
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
  status?: ExperienceStatus; // 상태 필터
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
 * 경험 통계 응답 타입 (Alias for TExperienceStatsSummary)
 */
export type { TExperienceStatsSummary };
export type TExperienceStats = TExperienceStatsSummary;

/**
 * AI 분석 점수 메트릭
 */
export type TScoreMetrics = {
  specificity: number;
  resultOriented: number;
  logicalFlow: number;
  jobRelevance: number;
};

/**
 * AI 전체 분석 결과
 */
export type TExperienceOverallAnalysis = {
  id: string;
  totalScore: number;
  scoreMetrics: TScoreMetrics;
  overallSummary: string;
  overallFeedback: string;
  goalFeedback: string;
  goalImprovedContent: string;
  achievementFeedback: string;
  achievementImprovedContent: string;
  recommendedKeywords: string[];
};

/**
 * STAR/PAR 분석 내역
 */
export type TMethodBreakdown = {
  situation?: string | null;
  task?: string | null;
  action?: string | null;
  result?: string | null;
  decision?: string | null;
  troubleshooting?: string | null;
  learning?: string | null;
};

/**
 * 경험 블록별 AI 분석 결과
 */
export type TExperienceSectionAnalysis = {
  id: string;
  sectionId: string;
  suggestedKind: ExperienceSectionKind | string;
  method: 'STAR' | 'PAR' | string;
  feedback: string;
  improvedContent: string;
  reasoning: string;
  methodBreakdown: TMethodBreakdown;
};

/**
 * 경험 블록과 해당 분석 결과 쌍
 */
export type TExperienceSectionWithAnalysis = {
  section: TExperienceSection;
  analysis: TExperienceSectionAnalysis;
};

/**
 * AI 분석 단건 조회 응답 타입
 */
export type TExperienceAIAnalysisResponse = {
  experience: TExperience;
  analysis: TExperienceOverallAnalysis;
  sections: TExperienceSectionWithAnalysis[];
};
