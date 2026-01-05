<template>
  <div class="home-page">
    <div class="content-wrapper">
      <!-- 상단 환영 문구 및 퀵 액션 -->
      <div class="home-hero-section">
        <div class="hero-bg-overlay">
          <div class="glow-blob blob-1"></div>
          <div class="glow-blob blob-2"></div>
          <div class="grid-pattern"></div>
        </div>

        <div class="hero-content">
          <div class="hero-badge"># Build Your Career</div>
          <h1 class="home-page-title">
            <span class="gradient-text">Career Builder</span>
          </h1>
          <p class="home-page-subtitle">
            당신의 커리어 경험을 자산으로 만드세요. AI 가이드가 당신의 폭발적인 성장을 지원합니다.
          </p>
          <div class="hero-actions">
            <Button
              :round="true"
              :size="CommonSize.Medium"
              :variant="ButtonVariant.Primary"
              class="pulse-btn"
              icon="mdi-rocket-launch"
              @click="navigateTo(MENU_URLS.EXPERIENCE_REGISTER)"
            >
              지금 등록하기
            </Button>
            <Button
              :round="true"
              :size="CommonSize.Medium"
              :variant="ButtonVariant.Secondary"
              @click="navigateTo(MENU_URLS.EXPERIENCE)"
            >
              내 경험 둘러보기
            </Button>
          </div>
        </div>
      </div>

      <!-- 대시보드 통계 섹션 -->
      <div class="dashboard-stats-grid">
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(37, 99, 235, 0.1)">
            <v-icon color="#2563eb">mdi-trophy-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">총 경험 수</span>
            <h2 class="stat-value">{{ stats?.total || 0 }}</h2>
          </div>
        </Card>
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(16, 185, 129, 0.1)">
            <v-icon color="#10b981">mdi-check-circle-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">완성된 경험</span>
            <h2 class="stat-value">{{ (stats?.completed || 0) + (stats?.modified || 0) }}</h2>
          </div>
        </Card>
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(245, 158, 11, 0.1)">
            <v-icon color="#f59e0b">mdi-alert-circle-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">보완 필요</span>
            <h2 class="stat-value">{{ stats?.incomplete || 0 }}</h2>
          </div>
        </Card>
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(139, 92, 246, 0.1)">
            <v-icon color="#8b5cf6">mdi-auto-fix</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">AI 분석 완료</span>
            <h2 class="stat-value">{{ stats?.aiAnalyzed || 0 }}</h2>
          </div>
        </Card>
      </div>

      <div class="home-main-grid">
        <!-- 왼쪽: 최근 경험 리스트 -->
        <div class="recent-activities">
          <div class="section-header">
            <h3 class="section-title">최근 등록한 경험</h3>
            <NuxtLink class="view-all" :to="MENU_URLS.EXPERIENCE">전체보기</NuxtLink>
          </div>
          <div v-if="recentExperiences.length > 0" class="activities-list">
            <div
              v-for="item in recentExperiences"
              :key="item.id"
              class="activity-item"
              @click="navigateTo(`${MENU_URLS.EXPERIENCE}/${item.id}`)"
            >
              <div class="activity-date">{{ formatDate(item.updatedAt) }}</div>
              <div class="activity-content">
                <h4 class="activity-title">{{ item.title }}</h4>
                <p class="activity-org">{{ item.background || '소속 없음' }}</p>
              </div>
              <v-icon color="#9ca3af" size="small">mdi-chevron-right</v-icon>
            </div>
          </div>
          <div v-else class="empty-activities">
            <v-icon color="#e5e7eb" size="48">mdi-text-box-plus-outline</v-icon>
            <p>아직 등록된 경험이 없어요</p>
            <Button
              :size="CommonSize.Small"
              :variant="ButtonVariant.Ghost"
              @click="navigateTo(MENU_URLS.EXPERIENCE_REGISTER)"
            >
              첫번째 경험을 등록하고 AI 분석을 받아보세요 →
            </Button>
          </div>
        </div>

        <!-- 오른쪽: AI 서비스 프리뷰 -->
        <div class="ai-services-preview">
          <h3 class="section-title">AI 커리어 엔진</h3>
          <div class="ai-cards">
            <div class="ai-card" @click="handleAiAnalysis">
              <v-icon class="ai-icon" color="#2563eb">mdi-brain</v-icon>
              <div class="ai-card-info">
                <h4 class="ai-card-title">AI 경험 분석</h4>
                <p class="ai-card-desc">
                  등록된 경험을 분석하여 당신의 강점과 핵심 역량을 추출합니다.
                </p>
              </div>
            </div>
            <div class="ai-card" @click="handleAiResume">
              <div class="ai-card-tag">Coming Soon</div>
              <v-icon class="ai-icon" color="#8b5cf6">mdi-file-document-edit-outline</v-icon>
              <div class="ai-card-info">
                <h4 class="ai-card-title">AI 이력서 생성</h4>
                <p class="ai-card-desc">
                  경험 데이터를 기반으로 직무 맞춤형 이력서를 자동 생성합니다.
                </p>
              </div>
            </div>
            <div class="ai-card" @click="handleAiFeedback">
              <div class="ai-card-tag">Coming Soon</div>
              <v-icon class="ai-icon" color="#10b981">mdi-comment-check-outline</v-icon>
              <div class="ai-card-info">
                <h4 class="ai-card-title">전체 커리어 피드백</h4>
                <p class="ai-card-desc">
                  내 전체 경험 데이터를 바탕으로 커리어 방향성과 개선점을 제안합니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
// 1. 외부 라이브러리 import
import { onMounted, ref } from 'vue';

// 2. 프로젝트 내부 import
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

// 3. API import
import { fetchExperiences, fetchExperienceStatsSummary } from '~/api/experience/api';

// 4. Type import
import type { TExperience, TExperienceStatsSummary } from '~/api/experience/types';
// 5. 로컬 컴포넌트 import
import Button from '@/components/atoms/Button/Button.vue';
import Card from '@/components/molecules/Card/Card.vue';
import { MENU_URLS } from '~/constants/menus';

// 9. Ref/Reactive 선언
definePageMeta({
  layout: 'default',
});

const stats = ref<TExperienceStatsSummary | null>(null);
const recentExperiences = ref<TExperience[]>([]);

// 11. 함수 선언
const loadDashboardData = async () => {
  const [statsRes, listRes] = await Promise.all([
    fetchExperienceStatsSummary(),
    fetchExperiences({ size: 5, sortKey: 'UPDATED_AT', sortDir: 'DESC' }),
  ]);

  if (statsRes.data) {
    stats.value = statsRes.data;
  }

  if (listRes.data) {
    recentExperiences.value = listRes.data.list;
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(
    date.getDate()
  ).padStart(2, '0')}`;
};

onMounted(() => {
  loadDashboardData();
});

const handleAiAnalysis = () => {
  navigateTo(MENU_URLS.AI_EXPERIENCE);
};

const handleAiResume = () => {
  useToast().warning('AI 이력서 생성 기능 준비중입니다.');
};

const handleAiFeedback = () => {
  useToast().warning('전체 커리어 피드백 기능 준비중입니다.');
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/home-page.scss';
</style>
