import { computed } from 'vue';
import { fetchMe, fetchMyUsage } from '~/api/user/api';
import type { TUser, TUserUsage } from '~/api/user/types';
import { PlanType } from '~/types/ai-plan-types';

/**
 * 사용자 정보 및 사용량 관련 Composable
 * Nuxt의 useState를 사용하여 SSR 환경에서도 안전하게 상태 공유
 */
export const useUserInfo = () => {
  // 공유 상태 정의 (공통 키 'user_info' 사용)
  const user = useState<TUser | null>('user_profile', () => null);
  const usage = useState<TUserUsage | null>('user_usage', () => null);
  const isLoading = useState<boolean>('user_info_loading', () => false);

  // Computed 속성 (함수 내부에서 정의하여 매번 새로운 반응형 객체 반환)
  const planType = computed(() => user.value?.subscription.plan.planType || PlanType.BASIC);
  const planName = computed(() => user.value?.subscription.plan.name || 'BASIC');

  /**
   * 사용자 정보 및 사용량 통합 조회
   */
  const fetchAll = async () => {
    if (isLoading.value) return;
    
    isLoading.value = true;
    try {
      const [userRes, usageRes] = await Promise.all([
        fetchMe(),
        fetchMyUsage(),
      ]);
      if (userRes.data) user.value = userRes.data;
      if (usageRes.data) usage.value = usageRes.data;
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * 사용량 정보만 별도 갱신
   */
  const refreshUsage = async () => {
    const { data } = await fetchMyUsage();
    if (data) {
      usage.value = data;
    }
  };

  /**
   * 정보 초기화
   */
  const clearUserInfo = () => {
    user.value = null;
    usage.value = null;
  };

  return {
    user,
    usage,
    isLoading,
    planType,
    planName,
    fetchAll,
    refreshUsage,
    clearUserInfo,
  };
};
