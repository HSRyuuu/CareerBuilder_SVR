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
    const userId = ref<string | null>(null);
    const userName = ref<string | null>(null);

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
     * 사용자 정보 설정
     */
    const setUserInfo = (info: { userId: string; userName: string }): void => {
      userId.value = info.userId;
      userName.value = info.userName;
    };

    /**
     * 인증 정보 초기화 (로그아웃)
     */
    const clearAuth = (): void => {
      accessToken.value = null;
      userId.value = null;
      userName.value = null;
    };

    return {
      // State
      accessToken,
      userId,
      userName,
      // Getters
      isAuthenticated,
      // Actions
      getAccessToken,
      setAccessToken,
      setUserInfo,
      clearAuth,
    };
  },
  {
    // pinia-plugin-persistedstate 옵션
    persist: {
      key: 'auth',
      storage: piniaPluginPersistedstate.localStorage(),
      pick: ['accessToken', 'userId', 'userName'],
    },
  }
);
