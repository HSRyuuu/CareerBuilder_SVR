<template>
  <div class="home-page">
    <!-- 메인 콘텐츠 -->
    <div class="home-welcome-section">
      <h1 class="home-page-title">안녕하세요 👋</h1>
      <p class="home-page-subtitle">CareerBuilder에 오신 것을 환영합니다</p>
    </div>

    <div class="home-cards-grid">
      <div class="home-info-card">
        <div
          class="home-card-icon"
          style="background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%)"
        >
          <v-icon color="white">mdi-rocket-launch</v-icon>
        </div>
        <h3 class="home-card-title">빠른 시작</h3>
        <p class="home-card-description">이력서를 등록하고 AI 기반 분석을 받아보세요</p>
        <NuxtLink to="/career/register" class="home-card-action-link" @click="handleStartClick"> 시작하기 → </NuxtLink>
      </div>

      <div class="home-info-card">
        <div
          class="home-card-icon"
          style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
        >
          <v-icon color="white">mdi-chart-line</v-icon>
        </div>
        <h3 class="home-card-title">커리어 분석</h3>
        <p class="home-card-description">전문적인 분석으로 경력을 한눈에 파악하세요</p>
        <Button
          :variant="ButtonVariant.Ghost"
          :size="CommonSize.Small"
          class="home-card-action-button"
          disabled
        >
          준비중 →
        </Button>
      </div>

      <div class="home-info-card">
        <div
          class="home-card-icon"
          style="background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)"
        >
          <v-icon color="white">mdi-lightbulb</v-icon>
        </div>
        <h3 class="home-card-title">AI 추천</h3>
        <p class="home-card-description">맞춤형 커리어 조언과 개선 방안을 제공합니다</p>
        <Button
          :variant="ButtonVariant.Ghost"
          :size="CommonSize.Small"
          class="home-card-action-button"
          disabled
        >
          준비중 →
        </Button>
      </div>
    </div>

    <div class="home-stats-section">
      <div class="home-stat-item">
        <div class="home-stat-number">0</div>
        <div class="home-stat-label">등록된 이력서</div>
      </div>
      <div class="home-stat-divider" />
      <div class="home-stat-item">
        <div class="home-stat-number">0</div>
        <div class="home-stat-label">분석 완료</div>
      </div>
      <div class="home-stat-divider" />
      <div class="home-stat-item">
        <div class="home-stat-number">0</div>
        <div class="home-stat-label">AI 추천</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useAnalytics } from '@/composables/useAnalytics';

const { track } = useAnalytics();

definePageMeta({
  layout: 'default',
});

// CTA 클릭 이벤트 핸들러
const handleStartClick = () => {
  // CTA 클릭 이벤트
  track('cta_clicked', {
    cta_name: 'start_register',
    cta_location: 'home_hero',
    destination: '/career/register',
  });
  
  // 성과 등록 시작 이벤트
  track('career_register_started', {
    source: 'home_cta',
  });
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/home-page.scss';
</style>
