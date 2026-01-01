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
          <div class="visual-container">
            <!-- 연결 선 (Network Lines) -->
            <svg class="visual-connectors" viewBox="0 0 500 400">
              <line class="line dash" x1="120" y1="80" x2="380" y2="320" />
              <line class="line dash" x1="380" y1="80" x2="120" y2="320" />
            </svg>

            <div class="visual-grid">
              <div class="floating-card card-1">
                <v-icon color="#22d3ee" size="small">mdi-robot</v-icon>
                <span class="card-text">AI Skill Extraction</span>
              </div>
              <div class="floating-card card-2">
                <v-icon color="#a78bfa" size="small">mdi-trending-up</v-icon>
                <span class="card-text">Growth Analysis</span>
              </div>
              <div class="floating-card card-3">
                <v-icon color="#4ade80" size="small">mdi-database-arrow-up</v-icon>
                <span class="card-text">Collect Experience</span>
              </div>
              <div class="floating-card card-4">
                <v-icon color="#fbbf24" size="small">mdi-lightbulb-on-10</v-icon>
                <span class="card-text">Career Insights</span>
              </div>
            </div>

            <!-- 중앙 AI 코어 효과 -->
            <div class="ai-core">
              <div class="core-ring"></div>
              <Transition name="fade-scale" mode="out-in">
                <div :key="currentCoreStage" class="core-content">
                  <v-icon color="white" size="large">{{ coreStages[currentCoreStage].icon }}</v-icon>
                </div>
              </Transition>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="welcome-features">
      <div class="features-container">
        <div class="features-header">
          <span class="features-subtitle">Key Features</span>
          <h2 class="features-title">AI가 리드하는 커리어 브랜딩</h2>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(37, 99, 235, 0.1)">
              <v-icon color="#2563eb">mdi-auto-fix</v-icon>
            </div>
            <h3 class="feature-name">AI 가이드 기반 기록</h3>
            <p class="feature-desc">
              무엇을 적을지 막막하신가요? 각 섹션별 AI 가이드와 힌트 상자가 전문적인 기록을 완성할 수 있도록 돕습니다.
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(168, 85, 247, 0.1)">
              <v-icon color="#a855f7">mdi-text-search-variant</v-icon>
            </div>
            <h3 class="feature-name">성과 중심 문장 교정</h3>
            <p class="feature-desc">
              파편화된 업무 내용을 AI가 분석하여, 정량적인 성과와 핵심 역량이 드러나는 전문적인 문장으로 정교하게 다듬어 드립니다.
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper" style="background: rgba(16, 185, 129, 0.1)">
              <v-icon color="#10b981">mdi-chart-timeline-variant</v-icon>
            </div>
            <h3 class="feature-name">직무 역량 정밀 맵핑</h3>
            <p class="feature-desc">
              기존의 경험들이 실제 어떤 직무 역량으로 연결되는지 통계 데이터와 시각화 지표를 통해 한눈에 확인할 수 있습니다.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- AI Workflow Section -->
    <section class="ai-process">
      <div class="process-container">
        <div class="process-text">
          <h2 class="section-title">투박한 초안이<br/><span>완벽한 커리어 자산</span>으로</h2>
          <p class="section-desc">
            당신이 입력한 단순한 사실들을 CareerBuilder AI가 분석하여<br/>
            채용 담당자의 눈을 사로잡는 강력한 성과 데이터로 재탄생시킵니다.
          </p>
          
          <ul class="process-steps">
            <li>
              <v-icon color="#2563eb">mdi-check-circle</v-icon>
              <span>가이드 박스를 활용한 핵심 정보 입력</span>
            </li>
            <li>
              <v-icon color="#2563eb">mdi-check-circle</v-icon>
              <span>AI 기반의 문맥 분석 및 핵심 역량 추출</span>
            </li>
            <li>
              <v-icon color="#2563eb">mdi-check-circle</v-icon>
              <span>성과 중심의 매력적인 커리어 블록 완성</span>
            </li>
          </ul>
        </div>
        
        <div class="process-visual">
          <div class="visual-card before">
            <div class="card-label">Before</div>
            <div class="card-content">
              "새로운 결제 기능을 개발했습니다. 버그 없이 잘 작동했고 고객들도 좋아했어요."
            </div>
          </div>
          <div class="visual-arrow">
            <v-icon>mdi-chevron-double-right</v-icon>
          </div>
          <div class="visual-card after">
            <div class="card-label">After with AI</div>
            <div class="card-content">
              "결제 전환율 15% 향상을 목표로 신규 결제 자동화 엔진을 설계 및 개발하여, 결제 오류 발생률을 0.01%로 낮추고 일 평균 결제 처리량을 30% 증대시켰습니다."
            </div>
            <div class="ai-glow"></div>
          </div>
        </div>
      </div>
    </section>

    <section class="welcome-cta">
      <div class="cta-container">
        <!-- 배경 데코레이션 -->
        <div class="cta-bg-glow glow-1"></div>
        <div class="cta-bg-glow glow-2"></div>
        
        <div class="cta-content">
          <div class="cta-icon">
            <v-icon color="white" size="30">mdi-robot</v-icon>
          </div>
          <h2 class="cta-title">지금 바로 시작하세요</h2>
          <p class="cta-description">
            무의미하게 흘려보냈던 업무 시간들이 당신의 강력한 커리어 경쟁력이 됩니다.<br />
            CareerBuilder AI와 함께 준비된 인재로 거듭나세요.
          </p>
          <div class="cta-buttons">
            <Button
              class="cta-btn primary-white"
              :variant="ButtonVariant.Primary"
              :size="CommonSize.Large"
              :round="true"
              @click="handleSignupClick"
            >
              무료로 시작하기
            </Button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
// 1. 외부 라이브러리 import
import { onBeforeMount, onMounted, onUnmounted, ref } from 'vue';

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

// AI 코어 순환 로직
const coreStages = [
  { label: '축적', icon: 'mdi-database-outline' },
  { label: '분석', icon: 'mdi-chart-timeline-variant-shimmer' },
  { label: 'AI 피드백', icon: 'mdi-auto-fix' },
];
const currentCoreStage = ref(0);
let coreTimer: any = null;

onMounted(() => {
  coreTimer = setInterval(() => {
    currentCoreStage.value = (currentCoreStage.value + 1) % coreStages.length;
  }, 2500); // 2.5초 간격
});

onUnmounted(() => {
  if (coreTimer) clearInterval(coreTimer);
});
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
