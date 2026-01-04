<template>
  <div class="manage-plan-page">
    <PageHeader 
      title="구독 플랜 관리" 
      subtitle="회원님의 커리어 성장에 가장 적합한 플랜을 선택해보세요."
    />

    <div class="plan-container">
      <!-- <div class="plan-toggle">
        <div class="toggle-btn active">월간 결제</div>
        <div class="toggle-btn">연간 결제 <span class="discount-tag">-17%</span></div>
      </div> -->

      <div class="plan-grid">
        <div 
          v-for="plan in plans" 
          :key="plan.key" 
          class="plan-card"
          :class="{ featured: plan.isFeatured }"
        >
          <div v-if="plan.isFeatured" class="badge">MOST POPOULAR</div>
          
          <div class="plan-header">
            <h3 class="plan-name">{{ plan.name }}</h3>
            <p class="plan-subtitle">{{ plan.subtitle }}</p>
            <div class="plan-price">
              <span class="currency">₩</span>
              <span class="amount">{{ plan.price.toLocaleString() }}</span>
              <span class="period">/월</span>
            </div>
          </div>

          <div class="plan-action">
            <Button
              :variant="getButtonVariant(plan)"
              :size="CommonSize.Large"
              :block="true"
              :disabled="isCurrentPlan(plan.key)"
              @click="handleUpgrade(plan)"
            >
              {{ getButtonText(plan.key) }}
            </Button>
          </div>

          <div class="plan-features">
            <div class="feature-group">
              <div class="group-title">
                <v-icon>mdi-database-outline</v-icon>
                생성 한도
              </div>
              <div class="feature-list">
                <div v-for="feature in plan.limitFeatures" :key="feature" class="feature-item">
                  <v-icon size="16">mdi-check</v-icon>
                  {{ feature }}
                </div>
              </div>
            </div>

            <div v-if="plan.plusFeatures.length > 0" class="feature-group">
              <div class="feature-list">
                <div class="feature-item plus-info">
                  {{ plan.plusTitle }}
                </div>
                <div v-for="feature in plan.plusFeatures" :key="feature" class="feature-item">
                  <v-icon size="16">mdi-check</v-icon>
                  {{ feature }}
                </div>
              </div>
            </div>
          </div>

          <div class="plan-model-info">
            <div 
              v-for="highlight in plan.aiHighlights" 
              :key="highlight"
              class="model-item"
            >
              <v-icon v-if="highlight !== ''" size="12" color="var(--primary-color)">mdi-alert-circle-outline</v-icon>
              <span>{{ highlight }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useUserInfo } from '~/composables/useUserInfo';
import { PlanType, PlanRank, type TPlanType } from '~/types/ai-plan-types';

definePageMeta({
  layout: 'default',
});

const toast = useToast();
const { planType: currentPlanType } = useUserInfo();

const isCurrentPlan = (planKey: string) => {
  return currentPlanType.value === planKey;
};

const getButtonVariant = (plan: any) => {
  if (isCurrentPlan(plan.key)) return ButtonVariant.Secondary;
  return plan.isFeatured ? ButtonVariant.Primary : ButtonVariant.Outlined;
};

const getButtonText = (planKey: string) => {
  if (isCurrentPlan(planKey)) return '현재 이용 중';
  
  if (planKey === PlanType.BASIC) return '무료로 이용하기';
  
  const currentRank = PlanRank[currentPlanType.value as TPlanType] ?? 0;
  const targetRank = PlanRank[planKey as TPlanType] ?? 0;
  
  if (targetRank < currentRank) return '구독 변경';
  return '업그레이드';
};

const plans = [
  {
    key: PlanType.BASIC,
    name: 'Basic',
    subtitle: '시작하는 분들을 위한 기본 플랜',
    price: 0,
    isFeatured: false,
    limitFeatures: [
      '하루 3회 AI 경험 분석',
      'AI 커리어 분석 체험',
      'AI 이력서 생성 체험',
    ],
    plusTitle: '',
    plusFeatures: [],
    aiHighlights: ['기본 AI 엔진 탑재', '표준 분석 모델 제공', ''],
  },
  {
    key: PlanType.LITE,
    name: 'Lite',
    subtitle: '모든 기능을 체험해보세요',
    price: 1900,
    isFeatured: false,
    limitFeatures: [
      '하루 10회 AI 경험 분석',
      '월 5회 AI 커리어 분석',
      '월 3회 AI 이력서 생성',
    ],
    plusTitle: 'Basic의 모든 기능 포함 +',
    plusFeatures: ['AI 이력서 생성 기능 open'],
    aiHighlights: ['분석 한도 대폭 완화', '더 강력한 AI 모델 탑재', ''],
    
  },
  {
    key: PlanType.PRO,
    name: 'Pro',
    subtitle: '강력한 AI 코칭을 경험해보세요',
    price: 5900,
    isFeatured: true,
    limitFeatures: [
      '하루 50회 AI 경험 분석',
      '월 10회 AI 커리어 분석',
      '월 5회 AI 이력서 생성 + 첨삭',
    ],
    plusTitle: 'Lite의 모든 기능 포함 +',
    plusFeatures: ['AI 모델 업그레이드', '직무별 고급 피드백' ],
    aiHighlights: ['복잡한 추론 작업에 탁월한 모델 탑재', '정교한 문장 교정 엔진', '커리어, 이력서 심층 분석'],
  },
  {
    key: PlanType.MAX,
    name: 'Max',
    subtitle: '커리어 성장의 모든 성과를 기록하세요',
    price: 9900,
    isFeatured: false,
    limitFeatures: [
      '무제한 AI 경험 분석',
      '무제한 AI 커리어 분석',
      '월 30회 AI 이력서 코칭',
    ],
    plusTitle: 'Pro의 모든 기능 포함 +',
    plusFeatures: ['무제한 경험 등록', '프리미엄 AI 모델 우선권'],
    aiHighlights: ['최상위 플래그십 AI 모델', '차세대 코칭 시뮬레이터', ''],
  },
];

const handleUpgrade = (plan: any) => {
  toast.info(`플랜 결제 기능은 준비 중입니다.`);
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/manage-plan.scss';
</style>
