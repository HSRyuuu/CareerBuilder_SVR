<template>
  <div class="analysis-exp-page">
    <PageHeader
      title="AI 경험 분석 요청"
      subtitle="분석을 원하는 경험을 선택해주세요."
    >
      <template #actions>
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          @click="handleBack"
        >
          <v-icon size="small">mdi-arrow-left</v-icon>
          뒤로가기
        </Button>
      </template>
    </PageHeader>

    <div class="content-wrapper">
      <!-- AI 설명 블록 -->
      <div class="ai-intro-banner">
        <div class="banner-content">
          <div class="banner-icon">
            <v-icon color="white" size="32">mdi-auto-fix</v-icon>
          </div>
          <div class="banner-text">
            <h2 class="banner-title">더 강력해진 AI 경험 분석</h2>
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
import ExperienceTable from '@/components/organisms/ExperienceTable/ExperienceTable.vue';
import ExperienceAnalysisModal from '@/components/organisms/ExperienceAnalysisModal/ExperienceAnalysisModal.vue';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { FormSize, FormVariant, ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import type { TExperienceTableFilters } from '@/components/organisms/ExperienceTable/ExperienceTable.vue';

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
  navigateTo(`/career/${row.id}`);
};

const handleSelect = (row: TExperience) => {
  selectedExperience.value = row;
  showAnalysisModal.value = true;
};

const handleAnalysisRequest = (experience: TExperience, options: any) => {
  toast.success(`'${experience.title}'에 대한 AI 분석을 시작합니다.`);
  console.log('Analysis Options:', options);
  // TODO: API 연동
};

const handleBack = () => {
  navigateTo('/career');
};
</script>

<style lang="scss" scoped>
.analysis-exp-page {
  padding: 26px;
  animation: fadeIn 0.5s ease;
}

.content-wrapper {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.ai-intro-banner {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.05) 0%, rgba(59, 130, 246, 0.05) 100%);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 24px;

  .banner-content {
    display: flex;
    align-items: center;
    gap: 20px;

    .banner-icon {
      width: 64px;
      height: 64px;
      background: linear-gradient(135deg, #8b5cf6 0%, #3b82f6 100%);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 8px 16px rgba(139, 92, 246, 0.25);
    }

    .banner-text {
      .banner-title {
        font-size: 22px;
        font-weight: 800;
        letter-spacing: -0.5px;
        background: linear-gradient(135deg, #8b5cf6 0%, #3b82f6 100%);
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 6px;
      }

      .banner-description {
        font-size: 14px;
        color: var(--text-secondary);
        font-weight: 500;
      }
    }
  }

  .feature-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    padding-top: 24px;
    border-top: 1px solid rgba(139, 92, 246, 0.1);

    .feature-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;

      .feature-info {
        .feature-title {
          font-size: 14px;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        .feature-desc {
          font-size: 12px;
          color: var(--text-tertiary);
          line-height: 1.5;
        }
      }
    }
  }
}

.table-section {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 13px;
  padding: 26px;
  position: relative;
}

.filter-bar {
  display: flex;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
}

.status-select {
  min-width: 120px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(var(--bg-primary-rgb), 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  backdrop-filter: blur(2px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>