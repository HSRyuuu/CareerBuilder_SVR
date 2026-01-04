<template>
  <div class="user-usage-card" :class="{ loading: isLoading }">
    <!-- 멤버십 정보 -->
    <div v-if="user" class="membership-header">
      <div class="membership-badge" :class="planType.toLowerCase()">
        {{ planName }} 멤버십
      </div>
      <div class="membership-label">
        {{ user.subscription.status === 'ACTIVE' ? '이용 중' : '만료됨' }}
      </div>
    </div>

    <!-- 로딩 상태 (Skeleton) -->
    <div v-if="isLoading && !usage" class="usage-loading">
      <v-skeleton-loader type="list-item-two-line, list-item-two-line" />
    </div>

    <!-- 사용량 목록 -->
    <div v-else class="usage-items">
      <!-- 경험 분석 -->
      <div v-if="showExperience" class="usage-item">
        <div class="item-header">
          <div class="item-label">경험 분석</div>
          <div class="usage-count">
            일일 사용량 <span>{{ getCurrent(AiProcessType.EXPERIENCE_ANALYSIS) }}</span> / {{ getLimit(AiProcessType.EXPERIENCE_ANALYSIS) }}회
          </div>
        </div>
        <v-progress-linear
          :model-value="getProgress(AiProcessType.EXPERIENCE_ANALYSIS)"
          color="#8b5cf6"
          height="6"
          rounded
          class="usage-progress"
        />
      </div>

      <!-- 커리어 분석 -->
      <div v-if="showCareer" class="usage-item">
        <div class="item-header">
          <div class="item-label">커리어 분석</div>
          <div class="usage-count">
            월간 사용량 <span>{{ getCurrent(AiProcessType.CAREER) }}</span> / {{ getLimit(AiProcessType.CAREER) }}회
          </div>
        </div>
        <v-progress-linear
          :model-value="getProgress(AiProcessType.CAREER)"
          color="#3b82f6"
          height="6"
          rounded
          class="usage-progress"
        />
      </div>

      <!-- 이력서 작성 -->
      <div v-if="showResume" class="usage-item">
        <div class="item-header">
          <div class="item-label">이력서 작성</div>
          <div class="usage-count">
            월간 사용량 <span>{{ getCurrent(AiProcessType.RESUME) }}</span> / {{ getLimit(AiProcessType.RESUME) }}회
          </div>
        </div>
        <v-progress-linear
          :model-value="getProgress(AiProcessType.RESUME)"
          color="#10b981"
          height="6"
          rounded
          class="usage-progress"
        />
      </div>
    </div>

    <!-- 업그레이드 안내 (Basic 멤버십인 경우) -->
    <div v-if="planType === PlanType.BASIC" class="upgrade-info">
      <v-icon size="small" color="#8b5cf6">mdi-information-outline</v-icon>
      충분한 분석을 위해 <span>PREMIUM</span>으로 업그레이드 해보세요!
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useUserInfo } from '~/composables/useUserInfo';
import { PlanType, AiProcessType, type TAiProcessType } from '~/types/ai-plan-types';

export type TUserUsageCardProps = {
  showExperience?: boolean;
  showCareer?: boolean;
  showResume?: boolean;
};

const {
  showExperience = false,
  showCareer = false,
  showResume = false,
} = defineProps<TUserUsageCardProps>();

const { user, usage, planType, planName, isLoading, refreshUsage } = useUserInfo();

onMounted(async () => {
  // 사용량 정보는 마운트 시점에 다시 조회 (그때그때)
  await refreshUsage();
});

/**
 * 항목별 한도 조회
 */
const getLimit = (type: TAiProcessType) => {
  if (!user.value) return 0;
  const plan = user.value.subscription.plan;
  switch (type) {
    case AiProcessType.EXPERIENCE_ANALYSIS:
      return plan.experienceAnalysisLimitPerDay;
    case AiProcessType.CAREER:
      return plan.careerAnalysisLimitPerMonth;
    case AiProcessType.RESUME:
      return plan.resumeLimitPerMonth;
    default:
      return 0;
  }
};

/**
 * 항목별 현재 사용량 조회
 */
const getCurrent = (type: TAiProcessType) => {
  if (!usage.value || !user.value) return 0;
  
  const summary = usage.value.usageSummary[type];
  if (summary) {
    return summary.current;
  }
  
  return 0;
};

/**
 * 항목별 진행률 계산 (사용량 기준)
 */
const getProgress = (type: TAiProcessType) => {
  const limit = getLimit(type);
  const current = getCurrent(type);
  if (limit === 0) return 0;
  return (current / limit) * 100;
};
</script>

<style lang="scss" scoped>
@use './UserUsageCard.scss';
</style>
