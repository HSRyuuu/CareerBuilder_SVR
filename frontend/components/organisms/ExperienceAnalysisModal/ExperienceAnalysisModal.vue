<template>
  <v-dialog
    v-model="isVisible"
    max-width="500px"
    class="experience-analysis-modal"
    @click:outside="handleClose"
  >
    <v-card class="modal-card">
      <div class="modal-header">
        <div class="header-icon">
          <v-icon color="#8b5cf6" size="28">mdi-auto-fix</v-icon>
        </div>
        <div class="header-text">
          <h3 class="modal-title">AI 분석 요청</h3>
          <p class="modal-subtitle">AI가 당신의 경험을 심층 분석합니다.</p>
        </div>
        <v-btn icon="mdi-close" variant="text" size="small" class="close-btn" @click="handleClose" />
      </div>

      <v-card-text class="modal-body">
      <div v-if="experience" class="experience-info">
        <div class="info-label">대상 경험</div>
        <div class="info-value">{{ experience.title }}</div>
      </div>

      <!-- 멤버십/사용량 정보 -->
      <UserUsageCard show-experience />

      <div class="analysis-options">
        <div class="option-item">
          <div class="checkbox-area">
            <v-checkbox-btn v-model="options.deepAnalysis" color="#8b5cf6" density="compact" hide-details />
          </div>
          <div class="option-text">
            <div class="option-title">심층 역량 키워드 추출</div>
            <div class="option-desc">경험 속 숨겨진 역량을 정밀하게 분석합니다.</div>
          </div>
        </div>
        <div class="option-item" :class="{ 'disabled': membershipGrade === PlanType.BASIC }">
          <div class="checkbox-area">
            <v-checkbox-btn v-model="options.suggestImprovedContent" color="#8b5cf6" :disabled="membershipGrade === PlanType.BASIC" density="compact" hide-details />
          </div>
          <div class="option-text">
            <div class="option-title">AI 문장 자동 교정 <span class="premium-tag" v-if="membershipGrade === PlanType.BASIC">PREMIUM</span></div>
            <div class="option-desc">더 전문적인 표현으로 문장을 자동 재구성합니다.</div>
          </div>
        </div>
      </div>
    </v-card-text>

      <v-card-actions class="modal-actions">
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          block
          class="cancel-btn"
          @click="handleClose"
        >
          취소
        </Button>
        <Button
          :variant="ButtonVariant.Primary"
          :size="CommonSize.Medium"
          :round="true"
          block
          :disabled="isLimitExceeded"
          class="confirm-btn"
          @click="handleRequest"
        >
          분석 시작하기
        </Button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import type { TExperience } from '~/api/experience/types';
import { useUserInfo } from '~/composables/useUserInfo';
import UserUsageCard from '@/components/organisms/UserUsageCard/UserUsageCard.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

const isVisible = defineModel<boolean>({ default: false });

interface Props {
  experience: TExperience | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'request': [experience: TExperience, options: any];
}>();

const { usage, planType, refreshUsage } = useUserInfo();

import { PlanType, AiProcessType } from '~/types/ai-plan-types';

const membershipGrade = computed(() => planType.value);

const isLimitExceeded = computed(() => {
  const expUsage = usage.value?.usageSummary[AiProcessType.EXPERIENCE_ANALYSIS];
  if (!expUsage) return false;
  return expUsage.current >= expUsage.limit;
});

const options = reactive({
  deepAnalysis: true,
  suggestImprovedContent: false,
});

const handleClose = () => {
  isVisible.value = false;
};

const handleRequest = () => {
  if (props.experience) {
    emit('request', props.experience, { ...options });
    handleClose();
  }
};
</script>

<style lang="scss" scoped>
@use './ExperienceAnalysisModal.scss';
</style>
