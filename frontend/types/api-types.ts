/**
 * API 공통 타입 정의
 */

/**
 * Spring RFC 7807 ProblemDetail 타입
 * 서버에서 에러 발생 시 반환되는 표준 에러 응답 형식
 */
export type TProblemDetail = {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  properties?: Record<string, unknown>;
};

/**
 * API 응답 래퍼 타입
 * 모든 API 호출의 결과는 이 타입으로 반환됨
 */
export type TApiResponse<T> = {
  data: T | null;
  error: TProblemDetail | null;
};

/**
 * HTTP Method 상수
 */
export const HttpMethod = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH',
} as const;

export type THttpMethod = (typeof HttpMethod)[keyof typeof HttpMethod];

/**
 * API 요청 옵션 타입
 */
export type TApiRequestOptions<TBody = unknown> = {
  url: string;
  method: THttpMethod;
  body?: TBody;
  params?: Record<string, string | number | boolean | null | undefined>;
  headers?: Record<string, string>;
  showErrorToast?: boolean; // 에러 발생 시 Toast 표시 여부 (기본: true)
};

/**
 * 토큰 갱신 응답 타입
 */
export type TRefreshTokenResponse = {
  accessToken: string;
};
