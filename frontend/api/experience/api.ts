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
  TExperienceStats,
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
 * 경험 통계 조회
 * TODO: 백엔드 API 구현 필요 (현재 Mock 데이터 반환)
 */
export const fetchExperienceStats = () => {
  // return useApi<TExperienceStats>({
  //   url: '/api/experiences/stats',
  //   method: HttpMethod.GET,
  // });
  return Promise.resolve({
    data: {
      total: 0,
      completed: 0,
      needsImprovement: 0,
      aiAnalyzed: 0,
    },
    error: null,
  });
};
