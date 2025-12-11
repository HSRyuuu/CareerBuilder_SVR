/**
 * Achievement(성과) API 함수
 */
import { useApi, HttpMethod } from '@/composables/useApi';
import type {
  TAchievement,
  TAchievementCreate,
  TAchievementUpdate,
  TAchievementListParams,
  TPageResponse,
  TAchievementStats,
} from './types';

/**
 * 성과 목록 조회
 */
export const fetchAchievements = (params?: TAchievementListParams) => {
  return useApi<TPageResponse<TAchievement>>({
    url: '/api/achievements',
    method: HttpMethod.GET,
    params,
  });
};

/**
 * 성과 단건 조회
 */
export const fetchAchievement = (id: string) => {
  return useApi<TAchievement>({
    url: `/api/achievements/${id}`,
    method: HttpMethod.GET,
  });
};

/**
 * 성과 생성
 */
export const createAchievement = (body: TAchievementCreate) => {
  return useApi<TAchievement>({
    url: '/api/achievements',
    method: HttpMethod.POST,
    body,
  });
};

/**
 * 성과 수정
 */
export const updateAchievement = (id: string, body: TAchievementUpdate) => {
  return useApi<TAchievement>({
    url: `/api/achievements/${id}`,
    method: HttpMethod.PUT,
    body,
  });
};

/**
 * 성과 삭제
 */
export const deleteAchievement = (id: string) => {
  return useApi<null>({
    url: `/api/achievements/${id}`,
    method: HttpMethod.DELETE,
  });
};

/**
 * 성과 상태 변경 (발행/보관)
 */
export const updateAchievementStatus = (id: string, status: string) => {
  return useApi<TAchievement>({
    url: `/api/achievements/${id}/status`,
    method: HttpMethod.PATCH,
    body: { status },
  });
};

/**
 * 성과 통계 조회
 */
export const fetchAchievementStats = () => {
  return useApi<TAchievementStats>({
    url: '/api/achievements/stats',
    method: HttpMethod.GET,
  });
};
