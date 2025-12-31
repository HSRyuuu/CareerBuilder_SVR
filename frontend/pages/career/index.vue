<template>
  <div class="career-list-page">
    <div class="content-wrapper">
      <!-- Part1: 카드 그리드 -->
      <div class="career-list-cards-grid">
      
        <div class="career-list-info-card ai-card" @click="handleAiAnalysisRequest">
          <div class="career-list-card-icon ai-analysis">
            <v-icon color="white">mdi-brain</v-icon>
          </div>
          <h3 class="career-list-card-title">AI 경험 분석 요청</h3>
          <p class="career-list-card-description">
            AI가 당신의 경험을 분석하여 전문성을 강화할 수 있는 가이드를 가이드해 드립니다.
          </p>
          <div class="career-list-card-action-link">분석 요청하기 →</div>
        </div>

        <div class="career-list-info-card" @click="handleDashboard">
          <div class="career-list-card-icon dashboard">
            <v-icon color="white">mdi-chart-box</v-icon>
          </div>
          <h3 class="career-list-card-title">현재 분석 결과 대시보드</h3>
          <p class="career-list-card-description">커리어 분석 결과를 한눈에 확인하세요</p>
          <div class="career-list-card-action-link">대시보드 →</div>
        </div>

        <div class="career-list-info-card" @click="handleExport">
          <div class="career-list-card-icon export">
            <v-icon color="white">mdi-download</v-icon>
          </div>
          <h3 class="career-list-card-title">내 전체 커리어 Export</h3>
          <p class="career-list-card-description">전체 커리어 데이터를 다운로드 받으세요</p>
          <div class="career-list-card-action-link">Export →</div>
        </div>
      </div>

      <!-- Part2: 통계 섹션 -->
      <div class="career-list-stats-section">
        <div class="career-list-stat-item">
          <div class="career-list-stat-number">{{ stats.total }}</div>
          <div class="career-list-stat-label">전체 경험</div>
        </div>
        <div class="career-list-stat-divider" />
        <div class="career-list-stat-item">
          <div class="career-list-stat-number">{{ stats.completed }}</div>
          <div class="career-list-stat-label">작성 완료</div>
        </div>
        <div class="career-list-stat-divider" />
        <div class="career-list-stat-item">
          <div class="career-list-stat-number">{{ stats.incomplete }}</div>
          <div class="career-list-stat-label">보완 필요</div>
        </div>
        <div class="career-list-stat-divider" />
        <div class="career-list-stat-item">
          <div class="career-list-stat-number">{{ stats.analyzed }}</div>
          <div class="career-list-stat-label">AI 분석 완료</div>
        </div>
      </div>

      <!-- Part3: 경험 목록 테이블 -->
      <div class="career-list-table-section">
        <div class="career-list-table-header">
          <h2 class="career-list-section-title">내 경험 목록</h2>
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            @click="handleRegister"
          >
            <v-icon size="small">mdi-plus</v-icon>
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

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ExperienceStatus, STATUS_INFO } from '@/types/experience-types';
import type { TExperienceStatsSummary } from '@/types/experience-types';
import type {
  TExperience,
  TExperienceListParams,
  SortDirection,
  ExperienceSortKey,
} from '~/api/experience/types';
import { fetchExperiences, fetchExperienceStatsSummary } from '~/api/experience/api';
import Table from '@/components/organisms/Table/Table.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { FormSize, FormVariant, ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import ExperienceTable from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import type { TExperienceTableFilters } from '@/components/organisms/ExperienceTable/ExperienceTable.vue';

definePageMeta({
  layout: 'default',
});

// 통계 데이터
const stats = ref<TExperienceStatsSummary>({
  total: 0,
  incomplete: 0,
  completed: 0,
  analyzing: 0,
  analyzed: 0,
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
watch(filters, () => {
  loadExperiences();
}, { deep: true });

const handleRegister = () => {
  navigateTo('/career/register');
};

const handleAiAnalysisRequest = () => {
  navigateTo('/career/analysis/exp');
};

const handleDashboard = () => {
  console.log('현재 분석 결과 대시보드 클릭');
};

const handleExport = () => {
  console.log('내 전체 커리어 Export 클릭');
};

// 테이블 관련 함수
const handleRowClick = (row: TExperience) => {
  navigateTo(`/career/${row.id}`);
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/career-list-page.scss';
</style>
