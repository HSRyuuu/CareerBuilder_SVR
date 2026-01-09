/**
 * Auth 관련 API 타입 정의
 */

/**
 * 로그인 요청 타입
 */
export type TLoginRequest = {
  username: string;
  password: string;
};

/**
 * 사용자 정보 타입
 */
export type TUserInfo = {
  id: string;
  username: string;
  email: string;
  role: string;
};

/**
 * 로그인 응답 타입
 */
export type TLoginResponse = {
  accessToken: string;
  refreshToken: string;
  userInfo: TUserInfo;
};

/**
 * 회원가입 요청 타입
 */
export type TUserSignUpRequest = {
  username: string;
  email: string;
  password: string;
};

/**
 * 회원가입 응답 타입
 */
export type TSignupResponse = {
  id: string;
  username: string;
  email: string;
};

/**
 * 중복 체크 응답 타입
 */
export type TDuplicateCheckResponse = {
  exists: boolean;
};

/**
 * 로그아웃 응답 타입
 */
export type TLogoutResponse = {
  success: boolean;
};
