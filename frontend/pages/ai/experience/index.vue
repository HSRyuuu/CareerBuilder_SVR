<template>
  <div class="analysis-exp-page">
    <div class="content-wrapper">
      <!-- AI 설명 블록 -->
      <div class="ai-intro-banner u-ai-bg-soft">
        <div class="banner-content">
             <div class="banner-icon">
            <v-icon color="white" size="32">mdi-auto-fix</v-icon>
          </div>
          <div class="banner-text">
            <h2 class="banner-title u-ai-text-gradient">더 강력해진 AI 경험 분석</h2>
            <p class="banner-description">
              등록된 경험을 선택하면 AI가 전문성을 극대화할 수 있도록 분석 가이드를 제공합니다.
            </p>
          </div>
        </div>
        <div class="feature-grid">
          <div class="feature-item">
            <v-icon color="#8b5cf6" size="20">mdi-text-search</v-icon>
            <div class="feature-info">
              <div class="feature-title">핵심 역량 추출</div>
              <div class="feature-desc">경험에서 강조할 수 있는 역량 키워드를 찾아냅니다.</div>
            </div>
          </div>
          <div class="feature-item">
            <v-icon color="#3b82f6" size="20">mdi-file-tree</v-icon>
            <div class="feature-info">
              <div class="feature-title">STAR 구조화</div>
              <div class="feature-desc">논리적인 서술 체계에 맞춰 내용을 재구성합니다.</div>
            </div>
          </div>
          <div class="feature-item">
            <v-icon color="#10b981" size="20">mdi-lightbulb-on</v-icon>
            <div class="feature-info">
              <div class="feature-title">구체적 피드백</div>
              <div class="feature-desc">완성도를 높이기 위한 상세한 수정 가이드를 제공합니다.</div>
            </div>
          </div>
        </div>
      </div>

      <div class="table-section">
        <ExperienceTable
          :rows="experiences"
          :show-select-button="true"
          select-button-label="분석 요청"
          @select="handleSelect"
          @update:filters="handleFilterUpdate"
        />

        <!-- 분석 요청 모달 -->
        <ExperienceAnalysisModal
          v-model="showAnalysisModal"
          :experience="selectedExperience"
          @request="handleAnalysisRequest"
        />

        <div v-if="isLoading" class="loading-overlay">
          <v-progress-circular indeterminate color="primary" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { ExperienceStatus } from '@/types/experience-types';
import type { TExperience, TExperienceListParams, SortDirection, ExperienceSortKey } from '~/api/experience/types';
import { fetchExperiences } from '~/api/experience/api';
import { requestAiAnalysis } from '~/api/ai/api';
import ExperienceTable from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import ExperienceAnalysisModal from '@/components/organisms/ExperienceAnalysisModal/ExperienceAnalysisModal.vue';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { FormSize, FormVariant, ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import type { TExperienceTableFilters } from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import { MENU_URLS } from '~/constants/menus';

const toast = useToast();

definePageMeta({
  layout: 'default',
});

// Filter & Sort
const filters = ref<TExperienceTableFilters>({
  q: '',
  status: 'ALL',
  sortKey: 'UPDATED_AT',
  sortDir: 'DESC',
});

const experiences = ref<TExperience[]>([]);
const isLoading = ref(false);

// 모달 상태
const showAnalysisModal = ref(false);
const selectedExperience = ref<TExperience | null>(null);

const apiParams = computed<TExperienceListParams>(() => {
  return {
    p: 1,
    size: 100,
    q: filters.value.q,
    status: filters.value.status === 'ALL' ? undefined : filters.value.status,
    sortKey: filters.value.sortKey,
    sortDir: filters.value.sortDir,
  };
});

const handleFilterUpdate = (newFilters: TExperienceTableFilters) => {
  filters.value = newFilters;
};

const loadExperiences = async () => {
  isLoading.value = true;
  try {
    const { data, error } = await fetchExperiences(apiParams.value);
    if (!error && data) {
      experiences.value = data.list;
    }
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  loadExperiences();
});

watch(filters, () => {
  loadExperiences();
}, { deep: true });

const handleRowClick = (row: TExperience) => {
  navigateTo(`${MENU_URLS.EXPERIENCE}/${row.id}`);
};

const handleSelect = (row: TExperience) => {
  selectedExperience.value = row;
  showAnalysisModal.value = true;
};

const handleAnalysisRequest = async (experience: TExperience, options: any) => {
  const { error } = await requestAiAnalysis(experience.id, options);
  
  if (!error) {
    toast.success(`'${experience.title}'에 대한 AI 분석 요청이 완료되었습니다. 분석이 완료되면 알림을 통해 알려드려요!`);
    console.log('Analysis Options:', options);
  }
};

</script>

<style lang="scss" scoped>
@use '@/styles/pages/ai-experience-page.scss';
</style>