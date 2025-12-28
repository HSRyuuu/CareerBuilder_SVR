<template>
  <div class="career-list-page">
    <!-- Part1: 카드 그리드 -->
    <div class="career-list-cards-grid">
      <div class="career-list-info-card" @click="handleAiAnalysis">
        <div
          class="career-list-card-icon"
          style="background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)"
        >
          <v-icon color="white">mdi-brain</v-icon>
        </div>
        <h3 class="career-list-card-title">AI 커리어 분석</h3>
        <p class="career-list-card-description">
          AI가 당신의 커리어를 분석하고 인사이트를 제공합니다
        </p>
        <div class="career-list-card-action-link">분석 시작 →</div>
      </div>

      <div class="career-list-info-card" @click="handleDashboard">
        <div
          class="career-list-card-icon"
          style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
        >
          <v-icon color="white">mdi-chart-box</v-icon>
        </div>
        <h3 class="career-list-card-title">현재 분석 결과 대시보드</h3>
        <p class="career-list-card-description">커리어 분석 결과를 한눈에 확인하세요</p>
        <div class="career-list-card-action-link">대시보드 →</div>
      </div>

      <div class="career-list-info-card" @click="handleExport">
        <div
          class="career-list-card-icon"
          style="background: linear-gradient(135deg, #10b981 0%, #059669 100%)"
        >
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
        <div class="career-list-stat-label">완료</div>
      </div>
      <div class="career-list-stat-divider" />
      <div class="career-list-stat-item">
        <div class="career-list-stat-number">{{ stats.needsImprovement }}</div>
        <div class="career-list-stat-label">보완 필요</div>
      </div>
      <div class="career-list-stat-divider" />
      <div class="career-list-stat-item">
        <div class="career-list-stat-number">{{ stats.aiAnalyzed }}</div>
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
      <div class="career-list-sort-wrapper">
        <label class="career-list-sort-label">정렬:</label>
        <Select
          v-model="sortOption"
          :items="sortOptions"
          :size="FormSize.Compact"
          :variant="FormVariant.Outlined"
          class="career-list-sort-select-component"
        />
        <button
          :title="sortDirection === 'DESC' ? '내림차순' : '오름차순'"
          class="career-list-sort-direction-button"
          @click="toggleSortDirection"
        >
          <v-icon>
            {{ sortDirection === 'DESC' ? 'mdi-sort-descending' : 'mdi-sort-ascending' }}
          </v-icon>
        </button>
      </div>

      <Table
        :columns="experienceColumns"
        :rows="experiences"
        row-key="id"
        row-class="career-list-table-row"
        @row-click="handleRowClick"
      >
        <!-- 상태 셀만 커스텀 렌더링 -->
        <template #cell(status)="{ value }">
          <span :class="['career-list-status-chip', getStatusClass(value)]">
            {{ getStatusDisplay(value) }}
          </span>
        </template>
      </Table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ExperienceStatus } from '@/types/experience-types';
import type {
  TExperience,
  TExperienceListParams,
  SortDirection,
  ExperienceSortKey,
} from '~/api/experience/types';
import { fetchExperiences } from '~/api/experience/api';
import Table from '@/components/organisms/Table/Table.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { FormSize, FormVariant, ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { experienceColumns } from '@/columns/experience';

definePageMeta({
  layout: 'default',
});

// 통계 데이터 (하드코딩)
const stats = ref({
  total: 0,
  completed: 2,
  needsImprovement: 5,
  aiAnalyzed: 1,
});

// Sort 옵션
const sortOption = ref<ExperienceSortKey>('UPDATED_AT');
const sortDirection = ref<SortDirection>('DESC');

// Select 옵션 목록
const sortOptions: TSelectItem[] = [
  { title: '수정일', value: 'UPDATED_AT' },
  { title: '프로젝트 기간', value: 'DURATION_START' },
];

// 경험 목록 데이터
const experiences = ref<TExperience[]>([]);
const isLoading = ref(false);

// API 파라미터 computed
const apiParams = computed<TExperienceListParams>(() => {
  return {
    p: 1,
    size: 100, // 전체 조회
    sortKey: sortOption.value, // Java enum 값 직접 사용
    sortDir: sortDirection.value,
  };
});

// 정렬 순서 토글
const toggleSortDirection = () => {
  sortDirection.value = sortDirection.value === 'DESC' ? 'ASC' : 'DESC';
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
      stats.value.total = data.total;
    }
  } finally {
    isLoading.value = false;
  }
};

// 초기 로드
loadExperiences();

// sortOption, sortDirection 변경 시 재조회
watch([sortOption, sortDirection], () => {
  loadExperiences();
});

const handleRegister = () => {
  navigateTo('/career/register');
};

const handleAiAnalysis = () => {
  console.log('AI 커리어 분석 클릭');
  // TODO: AI 분석 페이지로 이동
};

const handleDashboard = () => {
  console.log('현재 분석 결과 대시보드 클릭');
  // TODO: 대시보드 페이지로 이동
};

const handleExport = () => {
  console.log('내 전체 커리어 Export 클릭');
  // TODO: Export 기능 구현
};

// Part3 테이블 관련 함수
const handleRowClick = (row: TExperience) => {
  navigateTo(`/career/${row.id}`);
};

const getStatusDisplay = (status: string) => {
  if (status === ExperienceStatus.DRAFT) return '작성중';
  if (status === ExperienceStatus.PUBLISHED) return '완료';
  if (status === ExperienceStatus.ARCHIVED) return '보관';
  return status;
};

const getStatusClass = (status: string) => {
  if (status === ExperienceStatus.DRAFT) return 'status-draft';
  if (status === ExperienceStatus.PUBLISHED) return 'status-published';
  if (status === ExperienceStatus.ARCHIVED) return 'status-archived';
  return '';
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/career-list-page.scss';
</style>
