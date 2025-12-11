/**
 * Achievement(성과) API 쿼리 키 상수
 */

// 정적 키 (단순 문자열)
export const ACHIEVEMENT_LIST_KEY = 'achievement-list';
export const ACHIEVEMENT_STATS_KEY = 'achievement-stats';

// 동적 키 (함수형 - ID 포함)
export const ACHIEVEMENT_DETAIL_KEY = (id: string) => `achievement-detail-${id}`;
