import { useApi, HttpMethod } from '~/composables/useApi';
import type { TUser, TUserUsage } from './types';

/**
 * 내 정보 조회
 */
export const fetchMe = async () => {
  return await useApi<TUser>({
    url: '/api/users/me',
    method: HttpMethod.GET,
  });
};

/**
 * 내 사용량 정보 조회
 */
export const fetchMyUsage = async () => {
  return await useApi<TUserUsage>({
    url: '/api/users/me/usage',
    method: HttpMethod.GET,
  });
};
