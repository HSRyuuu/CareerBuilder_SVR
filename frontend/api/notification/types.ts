/**
 * Notification(알림) 관련 타입 정의
 */

/**
 * 알림 타입
 */
export const NotificationType = {
  AI_EXPERIENCE_ANALYSIS_SUCCESS: 'AI_EXPERIENCE_ANALYSIS_SUCCESS',
  AI_EXPERIENCE_ANALYSIS_FAILED: 'AI_EXPERIENCE_ANALYSIS_FAILED',
  WELCOME: 'WELCOME',
  SUBSCRIPTION_EXPIRING: 'SUBSCRIPTION_EXPIRING',
} as const;

export type TNotificationType = (typeof NotificationType)[keyof typeof NotificationType];

/**
 * 알림 아이템
 */
export type TNotification = {
  id: string;
  type: TNotificationType;
  title: string;
  content: string;
  url: string;
  isRead: boolean;
  readAt: string | null;
  createdAt: string;
};

/**
 * 알림 목록 조회 파라미터
 */
export type TNotificationListParams = {
  isRead?: boolean | null;
  page?: number;
  pageSize?: number;
};

/**
 * 알림 목록 조회 응답
 */
export type TNotificationListResponse = {
  total: number;
  currentPage: number;
  pageSize: number;
  totalPages: number;
  list: TNotification[];
};
