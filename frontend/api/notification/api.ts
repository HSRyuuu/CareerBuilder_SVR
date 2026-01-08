/**
 * Notification(알림) API 함수
 */
import { useApi, HttpMethod } from '@/composables/useApi';
import type {
  TNotificationListParams,
  TNotificationListResponse,
} from './types';

/**
 * 알림 목록 조회
 */
export const fetchNotifications = (params?: TNotificationListParams) => {
  return useApi<TNotificationListResponse>({
    url: '/api/notifications',
    method: HttpMethod.GET,
    params,
  });
};

/**
 * 알림 단건 읽음 처리
 */
export const markNotificationAsRead = (id: string) => {
  return useApi<null>({
    url: `/api/notifications/${id}/read`,
    method: HttpMethod.PUT,
  });
};

/**
 * 알림 전체 읽음 처리
 */
export const markAllNotificationsAsRead = () => {
  return useApi<null>({
    url: '/api/notifications/read-all',
    method: HttpMethod.PUT,
  });
};
