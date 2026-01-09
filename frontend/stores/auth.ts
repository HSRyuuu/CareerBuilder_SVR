/**
 * 인증 Store
 * Access Token 관리 및 인증 상태 관리
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore(
  'auth',
  () => {
    // ===== State =====
    const accessToken = ref<string | null>(null);
    const refreshToken = ref<string | null>(null);
    const userId = ref<string | null>(null);
    const userName = ref<string | null>(null);
    const email = ref<string | null>(null);

    // ===== Getters =====
    const isAuthenticated = computed(() => !!accessToken.value);

    // ===== Actions =====

    /**
     * Access Token 조회
     */
    const getAccessToken = (): string | null => {
      return accessToken.value;
    };

    /**
     * Access Token 설정
     */
    const setAccessToken = (token: string): void => {
      accessToken.value = token;
    };

    /**
     * Refresh Token 조회
     */
    const getRefreshToken = (): string | null => {
      return refreshToken.value;
    };

    /**
     * Refresh Token 설정
     */
    const setRefreshToken = (token: string): void => {
      refreshToken.value = token;
    };

    /**
     * 토큰 설정 (Access + Refresh)
     */
    const setTokens = (access: string, refresh: string): void => {
      accessToken.value = access;
      refreshToken.value = refresh;
    };

    /**
     * 사용자 정보 설정
     */
    const setUserInfo = (info: { userId: string; userName: string; email: string }): void => {
      userId.value = info.userId;
      userName.value = info.userName;
      email.value = info.email;
    };

    /**
     * 인증 정보 초기화 (로그아웃)
     */
    const clearAuth = (): void => {
      accessToken.value = null;
      refreshToken.value = null;
      userId.value = null;
      userName.value = null;
      email.value = null;
    };

    return {
      // State
      accessToken,
      refreshToken,
      userId,
      userName,
      email,
      // Getters
      isAuthenticated,
      // Actions
      getAccessToken,
      setAccessToken,
      getRefreshToken,
      setRefreshToken,
      setTokens,
      setUserInfo,
      clearAuth,
    };
  },
  {
    // pinia-plugin-persistedstate 옵션
    persist: {
      key: 'auth',
      storage: piniaPluginPersistedstate.localStorage(),
      pick: ['accessToken', 'refreshToken', 'userId', 'userName', 'email'],
    },
  }
);
