<template>
  <div class="welcome-page">
    <!-- Hero Section -->
    <section class="welcome-hero">
      <div class="hero-container">
        <div class="hero-text">
          <div class="hero-badge">AI Powered Career Assistant</div>
          <h1 class="hero-title">
            당신의 커리어를<br />
            <span>AI로 자산화</span>하세요 
          </h1>
          <p class="hero-description">
            파편화된 업무 경험을 체계적으로 관리하고, AI 분석을 통해 당신만의 <b>핵심 경쟁력을 발견</b>하세요.<br/>
            단순한 나열을 넘어 <b>성과 중심의 커리어</b>로 만들어드립니다.
          </p>

          <div class="hero-actions">
            <Button
              class="pulse-btn"
              :variant="ButtonVariant.Primary"
              :size="CommonSize.Large"
              :round="true"
              icon="mdi-rocket-launch"
              @click="handleStartClick"
            >
              지금 시작하기
            </Button>
             <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          @click="handleLoginClick"
        >
        <v-icon size="small">mdi-login</v-icon>
          로그인
        </Button>
             <Button
              class="btn-try"
              :variant="ButtonVariant.Outlined"
              :size="CommonSize.Large"
              :round="true"
              @click="handleTryClick"
            >
              체험해보기
            </Button>
          </div>
        </div>
        <div class="hero-visual">
          <!-- 데코레이션용 플로팅 카드 (4개 사각형 배치) -->
          <div class="floating-card card-1">
            <v-icon color="#60a5fa" size="small">mdi-brain</v-icon>
            <span style="font-size: 12px; font-weight: 600; color: white; margin-left: 8px">
              AI Skill Extraction
            </span>
          </div>
          <div class="floating-card card-2">
            <v-icon color="#a855f7" size="small">mdi-chart-areaspline</v-icon>
            <span style="font-size: 12px; font-weight: 600; color: white; margin-left: 8px">
              Growth Analysis
            </span>
          </div>
          <div class="floating-card card-3">
            <v-icon color="#10b981" size="small">mdi-map-marker-path</v-icon>
            <span style="font-size: 12px; font-weight: 600; color: white; margin-left: 8px">
              Experience Mapping
            </span>
          </div>
          <div class="floating-card card-4">
            <v-icon color="#f59e0b" size="small">mdi-lightbulb-outline</v-icon>
            <span style="font-size: 12px; font-weight: 600; color: white; margin-left: 8px">
              Career Insights
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="welcome-features">
      <div class="features-container">
        <div class="features-header">
          <span class="features-subtitle">Key Features</span>
          <h2 class="features-title">커리어 성장을 위한 완벽한 도구</h2>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(37, 99, 235, 0.1)">
              <v-icon color="#2563eb">mdi-text-box-multiple-outline</v-icon>
            </div>
            <h3 class="feature-name">경험 통합 관리</h3>
            <p class="feature-desc">
              프로젝트, 성과, 스킬 등 흩어져 있는 업무 경험을 한 곳에서 체계적으로 기록하고
              관리합니다.
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(168, 85, 247, 0.1)">
              <v-icon color="#a855f7">mdi-auto-fix</v-icon>
            </div>
            <h3 class="feature-name">AI 역량 분석</h3>
            <p class="feature-desc">
              작성된 내용을 AI가 분석하여 당신의 숨겨진 강점과 보완이 필요한 역량을 명확히
              짚어드립니다.
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(16, 185, 129, 0.1)">
              <v-icon color="#10b981">mdi-file-document-edit-outline</v-icon>
            </div>
            <h3 class="feature-name">맞춤형 이력서 생성</h3>
            <p class="feature-desc">
              잘 관리된 경험 데이터를 바탕으로, 원하는 직무와 상황에 맞는 이력서를 클릭 한 번으로
              생성합니다.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="welcome-cta">
      <div class="cta-container">
        <h2 class="cta-title">준비되셨나요?</h2>
        <p class="cta-description">
          지금 바로 CareerBuilder를 통해 당신의 성장을 기록하세요.<br />
          당신의 노력은 데이터가 되고, 데이터는 강력한 커리어가 됩니다.
        </p>
        <Button
          class="cta-btn"
          :variant="ButtonVariant.Primary"
          :size="CommonSize.Large"
          :round="true"
          style="background: white; color: #2563eb"
          @click="handleSignupClick"
        >
          무료로 시작하기
        </Button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
// 1. 외부 라이브러리 import
import { onBeforeMount } from 'vue';

// 2. 프로젝트 내부 import
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

// 3. API/Composables import
import { useAuthStore } from '@/stores/auth';

// 5. 로컬 컴포넌트 import
import Button from '@/components/atoms/Button/Button.vue';

// 9. Ref/Reactive 선언
definePageMeta({
  layout: 'full-page',
});
const authStore = useAuthStore();
// 11. 함수 선언
const handleStartClick = () => {
  navigateTo('/welcome/signup');
};

const handleTryClick = () => {
  navigateTo('/welcome/try/career');
};

const handleLoginClick = () => {
  navigateTo('/welcome/login');
};

const handleSignupClick = () => {
  navigateTo('/welcome/signup');
};

// 페이지 접근 시 로그인 상태라면 대시보드로 리다이렉트
onBeforeMount(() => {
  if (authStore.isAuthenticated) {
    navigateTo('/home');
  }
});
</script>

<style lang="scss" scoped>
@use '@/styles/pages/welcome-page.scss';
</style>
