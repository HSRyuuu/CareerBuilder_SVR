import { ref, computed } from 'vue';
import { fetchMe, fetchMyUsage } from '~/api/user/api';
import type { TUser, TUserUsage } from '~/api/user/types';
import { PlanType } from '~/types/ai-plan-types';

// 공유 상태를 사용하여 여러 컴포넌트에서 동일한 데이터를 참조할 수 있게 함
const user = ref<TUser | null>(null);
const usage = ref<TUserUsage | null>(null);
const isLoading = ref(false);

/**
 * 사용자 정보 및 사용량 관련 Composable
 */
export const useUserInfo = () => {
  
  const planType = computed(() => user.value?.subscription.plan.planType || PlanType.BASIC);
  const planName = computed(() => user.value?.subscription.plan.name || 'BASIC');

  /**
   * 사용자 정보 및 사용량 통합 조회
   */
  const fetchAll = async () => {
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
   * 사용량 정보만 별도 갱신 (그때그때 필요할 때)
   */
  const refreshUsage = async () => {
    const { data } = await fetchMyUsage();
    if (data) {
      usage.value = data;
    }
  };

  /**
   * 정보 초기화 (로그아웃 등)
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
