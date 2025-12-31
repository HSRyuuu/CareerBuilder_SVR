<template>
  <div class="career-list-page">
    <div class="content-wrapper">
      <!-- Part1: 카드 그리드 -->
      <div class="career-list-cards-grid">
        <div class="career-list-info-card ai-card theme-purple" @click="handleAiAnalysisRequest">
          <div class="career-list-card-icon ai-analysis">
            <v-icon color="white">mdi-brain</v-icon>
          </div>
          <h3 class="career-list-card-title">AI 경험 분석 요청</h3>
          <p class="career-list-card-description">
            AI가 당신의 경험을 분석하여 전문성을 강화할 수 있는 가이드를 가이드해 드립니다.
          </p>
          <div class="career-list-card-action-link">분석 요청하기 →</div>
        </div>

        <div class="career-list-info-card ai-card theme-blue" @click="handleFeedback">
          <div class="ai-card-tag">Coming Soon</div>
          <div class="career-list-card-icon feedback">
            <v-icon color="white">mdi-comment-check-outline</v-icon>
          </div>
          <h3 class="career-list-card-title">전체 커리어 피드백</h3>
          <p class="career-list-card-description">
            경험 데이터를 바탕으로 커리어 방향성과 개선점을 제안받으세요
          </p>
          <div class="career-list-card-action-link">피드백 보기 →</div>
        </div>

        <div class="career-list-info-card ai-card theme-emerald" @click="handleResumeCreate">
          <div class="ai-card-tag">Coming Soon</div>
          <div class="career-list-card-icon resume">
            <v-icon color="white">mdi-file-document-edit-outline</v-icon>
          </div>
          <h3 class="career-list-card-title">이력서 생성</h3>
          <p class="career-list-card-description">
            내 경험을 바탕으로 직무 맞춤형 이력서를 자동 생성합니다
          </p>
          <div class="career-list-card-action-link">이력서 생성 →</div>
        </div>
      </div>

      <!-- Part2: 통계 섹션 -->
      <div class="dashboard-stats-grid">
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(37, 99, 235, 0.1)">
            <v-icon color="#2563eb">mdi-trophy-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">총 경험 수</span>
            <h2 class="stat-value">{{ stats.total }}</h2>
          </div>
        </Card>
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(16, 185, 129, 0.1)">
            <v-icon color="#10b981">mdi-check-circle-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">완성된 경험</span>
            <h2 class="stat-value">{{ stats.completed + stats.modified }}</h2>
          </div>
        </Card>
        <Card class="stat-card">
          <div class="stat-icon" style="background: rgba(245, 158, 11, 0.1)">
            <v-icon color="#f59e0b">mdi-alert-circle-outline</v-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">보완 필요</span>
            <h2 class="stat-value">{{ stats.incomplete }}</h2>
          </div>
        </Card>
        <Card 
          class="stat-card" 
          :class="{ 'ai-cta-card': stats.aiAnalyzed === 0 }"
          @click="handleAnalyzedCardClick"
        >
          <template v-if="stats.aiAnalyzed > 0">
            <div class="stat-icon" style="background: rgba(139, 92, 246, 0.1)">
              <v-icon color="#8b5cf6">mdi-auto-fix</v-icon>
            </div>
            <div class="stat-info">
              <span class="stat-label">AI 분석 완료</span>
              <div class="stat-value-group">
                <h2 class="stat-value">{{ stats.aiAnalyzed }}</h2>
                <span class="stat-description">분석 완료된 경험을 수정해보세요 →</span>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="stat-icon" style="background: rgba(139, 92, 246, 0.1)">
              <v-icon color="#8b5cf6">mdi-brain</v-icon>
            </div>
            <div class="stat-info">
              <span class="stat-cta-text">AI 분석 시작하기</span>
              <span class="stat-cta-sub">당신의 커리어 강점을 발견해보세요</span>
            </div>
          </template>
        </Card>
      </div>

      <!-- AI 분석 완료 경험 선택 모달 -->
      <v-dialog v-model="showSelectionModal" max-width="500px">
        <v-card class="selection-modal-card">
          <div class="modal-header">
            <h3 class="modal-title">수정할 경험 선택</h3>
            <v-btn icon="mdi-close" variant="text" size="small" @click="showSelectionModal = false" />
          </div>
          <div class="modal-body">
            <div 
              v-for="exp in analyzedExperiences" 
              :key="exp.id" 
              class="selection-item"
              @click="navigateTo(`/career/${exp.id}/ai`)"
            >
              <div class="item-info">
                <span class="item-title">{{ exp.title }}</span>
                <span class="item-date">{{ new Date(exp.updatedAt).toLocaleDateString() }} 수정됨</span>
              </div>
              <v-icon size="small">mdi-chevron-right</v-icon>
            </div>
          </div>
        </v-card>
      </v-dialog>

      <!-- Part3: 경험 목록 테이블 -->
      <div class="career-list-table-section">
        <div class="career-list-table-header">
          <h2 class="career-list-section-title">내 경험 목록</h2>
          <Button
            :round="true"
            :size="CommonSize.Medium"
            :variant="ButtonVariant.Primary"
            icon="mdi-plus"
            @click="handleRegister"
          >
            경험 등록
          </Button>
        </div>

        <ExperienceTable
          :rows="experiences"
          @row-click="handleRowClick"
          @update:filters="handleFilterUpdate"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import type { TExperienceStatsSummary } from '@/types/experience-types';
import type { TExperience, TExperienceListParams, } from '~/api/experience/types';
import { fetchExperiences, fetchExperienceStatsSummary } from '~/api/experience/api';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import type { TExperienceTableFilters } from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import ExperienceTable from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import Card from '@/components/molecules/Card/Card.vue';
import { ExperienceStatus } from '@/types/experience-types';

definePageMeta({
  layout: 'default',
});

// 통계 데이터
const stats = ref<TExperienceStatsSummary>({
  total: 0,
  incomplete: 0,
  completed: 0,
  modified: 0,
  aiAnalyzed: 0,
});

// Filter & Sort 옵션
const filters = ref<TExperienceTableFilters>({
  q: '',
  status: 'ALL',
  sortKey: 'UPDATED_AT',
  sortDir: 'DESC',
});

// 경험 목록 데이터
const experiences = ref<TExperience[]>([]);

// AI 분석 완료 경험 선택 관련
const showSelectionModal = ref(false);
const analyzedExperiences = ref<TExperience[]>([]);
const isLoading = ref(false);

// API 파라미터 computed
const apiParams = computed<TExperienceListParams>(() => {
  return {
    p: 1,
    size: 100, // 전체 조회
    q: filters.value.q,
    status: filters.value.status === 'ALL' ? undefined : filters.value.status,
    sortKey: filters.value.sortKey,
    sortDir: filters.value.sortDir,
  };
});

const handleFilterUpdate = (newFilters: TExperienceTableFilters) => {
  filters.value = newFilters;
};

// 통계 조회
const loadStats = async () => {
  const { data, error } = await fetchExperienceStatsSummary();
  if (!error && data) {
    stats.value = data;
  }
};

// 경험 목록 조회
const loadExperiences = async () => {
  isLoading.value = true;
  try {
    const { data, error } = await fetchExperiences(apiParams.value);

    if (error) {
      console.error('경험 목록 조회 실패:', error);
      return;
    }

    if (data) {
      experiences.value = data.list;
    }
  } finally {
    isLoading.value = false;
  }
};

// 초기 로드
loadStats();
loadExperiences();

// 파라미터 변경 시 재조회 (디바운스 처리는 생략, 필요시 추가)
watch(
  filters,
  () => {
    loadExperiences();
  },
  { deep: true }
);

const handleRegister = () => {
  navigateTo('/career/register');
};

const handleAiAnalysisRequest = () => {
  navigateTo('/career/analysis/exp');
};

const handleAnalyzedCardClick = async () => {
  if (stats.value.aiAnalyzed === 0) {
    handleAiAnalysisRequest();
    return;
  }

  const { data, error } = await fetchExperiences({
    status: ExperienceStatus.AI_ANALYZED,
    p: 1,
    size: 100,
    sortKey: 'UPDATED_AT',
    sortDir: 'DESC'
  });

  if (!error && data) {
    if (data.list.length === 1) {
      navigateTo(`/career/${data.list[0].id}/ai`);
    } else if (data.list.length > 1) {
      analyzedExperiences.value = data.list;
      showSelectionModal.value = true;
    }
  }
};

const handleFeedback = () => {
  useToast().warning('전체 커리어 피드백 기능 준비중입니다.');
};

const handleResumeCreate = () => {
  useToast().warning('이력서 생성 기능 준비중입니다.');
};

// 테이블 관련 함수
const handleRowClick = (row: TExperience) => {
  navigateTo(`/career/${row.id}`);
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/career-list-page.scss';
</style>
