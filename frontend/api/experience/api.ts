/**
 * Experience(경험) API 함수
 */
import { useApi, HttpMethod } from '@/composables/useApi';
import type {
  TExperience,
  TExperienceCreate,
  TExperienceUpdate,
  TExperienceListParams,
  TPageResponse,
  TExperienceStatsSummary,
  TExperienceAIAnalysisResponse,
} from './types';

/**
 * 경험 목록 조회
 */
export const fetchExperiences = (params?: TExperienceListParams) => {
  return useApi<TPageResponse<TExperience>>({
    url: '/api/experiences',
    method: HttpMethod.GET,
    params,
  });
};

/**
 * 경험 단건 조회
 */
export const fetchExperience = (id: string) => {
  return useApi<TExperience>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.GET,
  });
};

/**
 * 경험 생성
 */
export const createExperience = (body: TExperienceCreate) => {
  return useApi<TExperience>({
    url: '/api/experiences',
    method: HttpMethod.POST,
    body,
  });
};

/**
 * 경험 수정
 */
export const updateExperience = (id: string, body: TExperienceUpdate) => {
  return useApi<TExperience>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.PUT,
    body,
  });
};

/**
 * 경험 삭제
 */
export const deleteExperience = (id: string) => {
  return useApi<null>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.DELETE,
  });
};

/**
 * 경험 통계 요약 조회
 */
export const fetchExperienceStatsSummary = () => {
  return useApi<TExperienceStatsSummary>({
    url: '/api/experiences/stats/summary',
    method: HttpMethod.GET,
  });
};

/**
 * 경험 AI 분석 결과 조회
 */
export const fetchExperienceAIAnalysis = (id: string) => {
  return useApi<TExperienceAIAnalysisResponse>({
    url: `/api/experiences/${id}/ai`,
    method: HttpMethod.GET,
  });
};
