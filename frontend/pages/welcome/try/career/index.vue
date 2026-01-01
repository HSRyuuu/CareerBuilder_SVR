<template>
  <div class="try-career-page">
    <!-- Top Navigation / Steps -->
    <div class="steps-container">
      <div
        class="step-item"
        :class="{ active: currentStep === 1, completed: currentStep > 1 }"
      >
        <div class="step-circle">1</div>
        <span class="step-label">경험 등록</span>
      </div>
      <div
        class="step-line"
        :class="{ active: currentStep > 1 }"
      />
      <div
        class="step-item"
        :class="{ active: currentStep === 2, completed: currentStep > 2 }"
      >
        <div class="step-circle">2</div>
        <span class="step-label">AI 분석</span>
      </div>
      <div
        class="step-line"
        :class="{ active: currentStep > 2 }"
      />
      <div
        class="step-item"
        :class="{ active: currentStep === 3 }"
      >
        <div class="step-circle">3</div>
        <span class="step-label">결과 확인</span>
      </div>
    </div>

    <!-- Custom Hero Header instead of PageHeader -->
    <header class="hero-header">
      <div class="hero-content">
        <h1 class="hero-title">{{ headerTitle }}</h1>
        <p class="hero-subtitle">{{ headerSubtitle }}</p>
        
        <div class="hero-actions">
          <Button
            v-if="currentStep === 1"
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Large"
            :round="true"
            @click="resetDummyData"
          >
            <v-icon class="mr-1" size="small">mdi-refresh</v-icon>
            초기화
          </Button>
          <Button
            v-if="currentStep === 1"
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Large"
            :round="true"
            @click="startAiAnalysis"
          >
            <v-icon class="mr-1" size="small">mdi-robot</v-icon>
            AI 분석하기
          </Button>
          
          <Button
            v-if="currentStep === 3"
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Large"
            :round="true"
            @click="goToSignup"
          >
            <v-icon class="mr-1" size="small">mdi-login</v-icon>
            회원가입하고 내 경험 분석하기
          </Button>
        </div>
      </div>
    </header>

    <div class="content-wrapper">
      <div v-if="isLoading" class="loading-container">
        <div class="loading-content">
          <v-progress-circular 
            indeterminate 
            color="primary" 
            size="64" 
            width="6"
          />
          <p class="mt-6 text-h5 font-weight-bold">AI가 경험을 분석하고 있습니다...</p>
          <p class="text-body-1 text-medium-emphasis">Professional한 표현과 역량 키워드를 도출하고 있어요.</p>
        </div>
      </div>

      <ExperienceForm
        v-else
        v-model="formData"
        :mode="formMode"
        :ai-analysis="aiAnalysisData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import ExperienceForm from '@/components/page/ExperienceForm.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { ExperienceFormMode } from '@/types/experience-types';
import type { TExperienceFormData } from '@/types/experience-types';
import type { TExperienceAIAnalysisResponse } from '@/api/experience/types';
import { INITIAL_DUMMY_DATA, MOCK_AI_ANALYSIS } from './data';

definePageMeta({
  layout: 'full-page', 
});

const currentStep = ref(1);
const isLoading = ref(false);

const headerTitle = computed(() => {
  if (currentStep.value === 1) return '나의 경험을 등록해보세요';
  if (currentStep.value === 3) return '분석된 결과를 확인하세요';
  return 'AI가 분석 중입니다';
});

const headerSubtitle = computed(() => {
  if (currentStep.value === 1) return '작성하신 경험이 어떻게 Professional하게 바뀔 수 있는지 미리 체험해보세요.';
  if (currentStep.value === 3) return 'AI가 추천하는 핵심 역량과 개선된 문장들을 통해 나만의 커리어를 빌드업하세요.';
  return '당신의 소중한 경험에서 핵심 가치를 추출하고 있습니다.';
});

const formMode = computed(() => {
  return currentStep.value === 3 ? ExperienceFormMode.EDIT_WITH_AI : ExperienceFormMode.VIEW;
});

const formData = ref<TExperienceFormData>(JSON.parse(JSON.stringify(INITIAL_DUMMY_DATA)));

const aiAnalysisData = ref<TExperienceAIAnalysisResponse | undefined>(undefined);

const resetDummyData = () => {
  formData.value = JSON.parse(JSON.stringify(INITIAL_DUMMY_DATA));
};

const startAiAnalysis = () => {
  currentStep.value = 2; // AI 분석 중 (Loading)
  isLoading.value = true;

  // Simulate API delay
  setTimeout(() => {
    aiAnalysisData.value = MOCK_AI_ANALYSIS;
    
    isLoading.value = false;
    currentStep.value = 3; // 결과 확인
  }, 2500);
};

const goToSignup = () => {
  navigateTo('/welcome/signup');
};
</script>

<style lang="scss" scoped>
@import './welcome-try-career.scss';
</style>
