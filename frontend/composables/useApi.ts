/**
 * Spring Backend API 호출을 위한 공통 Composable
 *
 * 주요 기능:
 * - Authorization 헤더 자동 추가
 * - 401 TOKEN_EXPIRED 에러 시 토큰 갱신 후 재시도
 * - ProblemDetail 에러 파싱
 * - Toast 알림 (선택적)
 */
import { useAuthStore } from '@/stores/auth';
import { useUserInfo } from '@/composables/useUserInfo';
import type {
  TApiResponse,
  TApiRequestOptions,
  TProblemDetail,
  TRefreshTokenResponse,
} from '@/types/api-types';
import { HttpMethod } from '@/types/api-types';
import { MENU_URLS } from '~/constants/menus';

// 토큰 갱신 중 여부 (중복 갱신 방지)
let isRefreshing = false;
// 토큰 갱신 대기 중인 요청들
let refreshSubscribers: ((token: string) => void)[] = [];
// 토큰 갱신 실패 시 대기 요청들 reject
let refreshRejectSubscribers: (() => void)[] = [];

/**
 * 토큰 갱신 완료 후 대기 중인 요청들 실행
 */
const onRefreshed = (token: string): void => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
  refreshRejectSubscribers = [];
};

/**
 * 토큰 갱신 실패 시 대기 중인 요청들 reject
 */
const onRefreshFailed = (): void => {
  refreshRejectSubscribers.forEach((callback) => callback());
  refreshSubscribers = [];
  refreshRejectSubscribers = [];
};

/**
 * 토큰 갱신 대기열에 추가
 */
const addRefreshSubscriber = (
  onSuccess: (token: string) => void,
  onFail: () => void
): void => {
  refreshSubscribers.push(onSuccess);
  refreshRejectSubscribers.push(onFail);
};

/**
 * API Base URL 조회
 */
const getBaseUrl = (): string => {
  const config = useRuntimeConfig();
  return config.public.apiBaseUrl as string;
};

/**
 * 쿼리 파라미터를 URL에 추가
 */
const buildUrlWithParams = (
  url: string,
  params?: Record<string, string | number | boolean | null | undefined>
): string => {
  if (!params) return url;

  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      searchParams.append(key, String(value));
    }
  });

  const queryString = searchParams.toString();
  return queryString ? `${url}?${queryString}` : url;
};

/**
 * 토큰 만료 에러인지 확인 (title이 TOKEN_EXPIRED인 경우)
 */
const isTokenExpiredError = (error: TProblemDetail): boolean => {
  return error.status === 401 && error.title === 'TOKEN_EXPIRED';
};

/**
 * 토큰 갱신 요청
 * @returns 새로운 토큰 쌍 또는 null (실패 시)
 */
const refreshTokens = async (
  refreshToken: string
): Promise<TRefreshTokenResponse | null> => {
  try {
    const baseUrl = getBaseUrl();
    const response = await fetch(`${baseUrl}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      return null;
    }

    const data = (await response.json()) as TRefreshTokenResponse;
    return data;
  } catch {
    return null;
  }
};

/**
 * 공통 API 호출 함수
 *
 * @template T - 응답 데이터 타입
 * @template TBody - 요청 바디 타입
 * @param options - API 요청 옵션
 * @returns Promise<TApiResponse<T>> - API 응답
 *
 * @example
 * ```typescript
 * // GET 요청
 * const { data, error } = await useApi<TUser[]>({
 *   url: '/api/users',
 *   method: HttpMethod.GET,
 * });
 *
 * // POST 요청
 * const { data, error } = await useApi<TUser>({
 *   url: '/api/users',
 *   method: HttpMethod.POST,
 *   body: { name: 'John' },
 * });
 * ```
 */
export const useApi = async <T, TBody = unknown>(
  options: TApiRequestOptions<TBody>
): Promise<TApiResponse<T>> => {
  const { url, method, body, params, headers = {}, showErrorToast = true } = options;

  const authStore = useAuthStore();
  const baseUrl = getBaseUrl();
  const fullUrl = buildUrlWithParams(`${baseUrl}${url}`, params);

  // 요청 헤더 구성
  const requestHeaders: Record<string, string> = {
    'Content-Type': 'application/json',
    ...headers,
  };

  // Authorization 헤더 추가
  const accessToken = authStore.getAccessToken();
  if (accessToken) {
    requestHeaders['Authorization'] = `Bearer ${accessToken}`;
  }

  // 요청 옵션 구성
  const fetchOptions: RequestInit = {
    method,
    headers: requestHeaders,
    credentials: 'include',
  };

  // Body 추가 (GET, DELETE가 아닌 경우)
  if (body && method !== HttpMethod.GET && method !== HttpMethod.DELETE) {
    fetchOptions.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(fullUrl, fetchOptions);

    // 성공 응답
    if (response.ok) {
      // 204 No Content 처리
      if (response.status === 204) {
        return { data: null as T, error: null };
      }

      const data = (await response.json()) as T;
      return { data, error: null };
    }

    // 에러 응답 파싱 (백엔드는 항상 ProblemDetail 형식으로 반환)
    const error = (await response.json()) as TProblemDetail;

    // 401 TOKEN_EXPIRED - 토큰 갱신 시도
    if (isTokenExpiredError(error)) {
      const currentRefreshToken = authStore.getRefreshToken();

      // RefreshToken이 없으면 로그아웃
      if (!currentRefreshToken) {
        handleLogout(authStore);
        return {
          data: null,
          error: createProblemDetail(401, '인증이 만료되었습니다. 다시 로그인해주세요.'),
        };
      }

      // 이미 토큰 갱신 중이면 대기
      if (isRefreshing) {
        return new Promise((resolve) => {
          addRefreshSubscriber(
            // 성공 시
            async (newToken: string) => {
              requestHeaders['Authorization'] = `Bearer ${newToken}`;
              fetchOptions.headers = requestHeaders;

              try {
                const retryResponse = await fetch(fullUrl, fetchOptions);
                if (retryResponse.ok) {
                  if (retryResponse.status === 204) {
                    resolve({ data: null as T, error: null });
                    return;
                  }
                  const data = (await retryResponse.json()) as T;
                  resolve({ data, error: null });
                } else {
                  const retryError = (await retryResponse.json()) as TProblemDetail;
                  if (showErrorToast) {
                    showError(retryError.detail);
                  }
                  resolve({ data: null, error: retryError });
                }
              } catch {
                const networkError = createProblemDetail(
                  0,
                  '서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.'
                );
                if (showErrorToast) {
                  showError(networkError.detail);
                }
                resolve({ data: null, error: networkError });
              }
            },
            // 실패 시
            () => {
              resolve({
                data: null,
                error: createProblemDetail(401, '인증이 만료되었습니다. 다시 로그인해주세요.'),
              });
            }
          );
        });
      }

      // 토큰 갱신 시작
      isRefreshing = true;

      const newTokens = await refreshTokens(currentRefreshToken);

      if (newTokens) {
        // 새 토큰 저장
        authStore.setTokens(newTokens.accessToken, newTokens.refreshToken);
        isRefreshing = false;

        // 대기 중인 요청들 실행
        onRefreshed(newTokens.accessToken);

        // 원래 요청 재시도
        requestHeaders['Authorization'] = `Bearer ${newTokens.accessToken}`;
        fetchOptions.headers = requestHeaders;

        const retryResponse = await fetch(fullUrl, fetchOptions);
        if (retryResponse.ok) {
          if (retryResponse.status === 204) {
            return { data: null as T, error: null };
          }
          const data = (await retryResponse.json()) as T;
          return { data, error: null };
        }

        // 재시도도 실패
        const retryError = (await retryResponse.json()) as TProblemDetail;
        if (showErrorToast) {
          showError(retryError.detail);
        }

        return { data: null, error: retryError };
      }

      // 토큰 갱신 실패 - 로그아웃 처리
      isRefreshing = false;
      onRefreshFailed();
      handleLogout(authStore);
      
      return {
        data: null,
        error: createProblemDetail(401, '인증이 만료되었습니다. 다시 로그인해주세요.'),
      };
    }

    // 401인데 TOKEN_EXPIRED가 아닌 경우 (UNAUTHORIZED 등) - 로그인 페이지로 이동
    if (response.status === 401) {
      handleLogout(authStore);
      return {
        data: null,
        error: createProblemDetail(401, '인증이 만료되었습니다. 다시 로그인해주세요.'),
      };
    }

    // 기타 에러 응답
    if (showErrorToast) {
      showError(error.detail);
    }

    return { data: null, error };
  } catch {
    // 네트워크 에러 등
    const error = createProblemDetail(
      0,
      '서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.'
    );

    if (showErrorToast) {
      showError(error.detail);
    }

    return { data: null, error };
  }
};

/**
 * 로그아웃 처리
 */
const handleLogout = (authStore: ReturnType<typeof useAuthStore>): void => {
  authStore.clearAuth();
  useUserInfo().clearUserInfo();
  navigateTo(MENU_URLS.LOGIN);
};

/**
 * ProblemDetail 객체 생성 헬퍼 (네트워크 에러 등 클라이언트 사이드 에러용)
 */
const createProblemDetail = (status: number, detail: string): TProblemDetail => {
  return {
    type: 'about:blank',
    title: getStatusTitle(status),
    status,
    detail,
    instance: '',
  };
};

/**
 * HTTP 상태 코드에 따른 제목 반환
 */
const getStatusTitle = (status: number): string => {
  const titles: Record<number, string> = {
    0: 'Network Error',
    400: 'Bad Request',
    401: 'Unauthorized',
    403: 'Forbidden',
    404: 'Not Found',
    500: 'Internal Server Error',
    502: 'Bad Gateway',
    503: 'Service Unavailable',
  };
  return titles[status] || 'Error';
};

/**
 * 에러 Toast 표시
 */
const showError = (message: string): void => {
  const toast = useToast();
  toast.error(message);
};

// HttpMethod export
export { HttpMethod };
