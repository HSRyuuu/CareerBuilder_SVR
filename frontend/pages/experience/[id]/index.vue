<template>
  <div class="career-detail-page">
    <PageHeader
      v-if="!isLoading"
      :title="pageTitleView"
      :subtitle="isEditMode ? '경험 정보를 수정하세요' : '경험 정보를 확인하세요'"
    >
      <template #actions>
        <template v-if="isEditMode">
          <Button
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Medium"
            :round="true"
            @click="handleCancel"
          >
            <v-icon size="small">mdi-close</v-icon>
            취소
          </Button>
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            @click="handleSave"
          >
            <v-icon size="small">mdi-check</v-icon>
            저장
          </Button>
          <ToggleButton
            v-if="hasAiAnalysis"
            v-model="showAiPanel"
            label-off="AI 제안 보기"
            label-on="AI 제안 숨기기"
            class="u-ai-btn-outline"
            @update:model-value="handleAiPanelToggle"
          >
            <template #icon-off>
              <v-icon size="small">mdi-robot</v-icon>
            </template>
            <template #icon-on>
              <v-icon size="small">mdi-robot-off</v-icon>
            </template>
          </ToggleButton>
        </template>
        <template v-else>
          <Button
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Medium"
            :round="true"
            @click="handleBack"
          >
            <v-icon size="small">mdi-arrow-left</v-icon>
            목록으로
          </Button>

          <Button
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Medium"
            :round="true"
            class="u-ai-btn-outline"
            @click="handleRequestAiAnalysis"
          >
            <v-icon size="small">mdi-auto-fix</v-icon>
            AI 분석 요청
          </Button>

          <Button
            v-if="hasAiAnalysis"
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            class="u-ai-btn-gradient"
            @click="handleEditWithAi"
          >
            <v-icon size="small">mdi-robot</v-icon>
            수정하기
            <span class="ai-tag">AI</span>
          </Button>
          <Button
            v-else
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            @click="isEditMode = true"
          >
            <v-icon size="small">mdi-pencil</v-icon>
            수정하기
          </Button>
        </template>
      </template>
    </PageHeader>

    <div class="content-wrapper">
      <div class="form-section">
        <ExperienceForm
          v-if="!isLoading"
          v-model="formData"
          :mode="currentMode"
          :ai-analysis="showAiPanel ? aiAnalysisData : undefined"
        />
        <div v-else class="loading-container">
          <v-progress-circular indeterminate color="primary" />
        </div>
      </div>

      <!-- 분석 요청 모달 -->
      <ExperienceAnalysisModal
        v-model="showAnalysisModal"
        :experience="modalExperience"
        @request="handleAnalysisRequest"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import ExperienceForm from '@/components/page/experience/ExperienceForm.vue';
import { ExperienceFormMode } from '@/types/experience-types';
import type { TExperienceFormData } from '@/types/experience-types';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Button from '@/components/atoms/Button/Button.vue';
import ToggleButton from '@/components/atoms/ToggleButton/ToggleButton.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { fetchExperience, updateExperience, fetchAIAnalysisExists, fetchExperienceAIAnalysis } from '~/api/experience/api';
import type { TExperienceAIAnalysisResponse } from '~/api/experience/types';
import { requestAiAnalysis } from '~/api/ai/api';
import ExperienceAnalysisModal from '@/components/organisms/ExperienceAnalysisModal/ExperienceAnalysisModal.vue';
import type { TExperienceUpdate, TExperience } from '~/api/experience/types';
import { MENU_URLS } from '~/constants/menus';

const route = useRoute();
const toast = useToast();

definePageMeta({
  layout: 'default',
});

const experienceId = computed(() => route.params.id as string);
const isEditMode = ref(false);
const isLoading = ref(true);
const pageTitle = ref('');
const hasAiAnalysis = ref(false);
const showAnalysisModal = ref(false);
const rawExperience = ref<TExperience | null>(null);
const showAiPanel = ref(false);
const aiAnalysisData = ref<TExperienceAIAnalysisResponse | undefined>();
const isLoadingAiAnalysis = ref(false);

const modalExperience = computed<TExperience | null>(() => {
  return rawExperience.value;
});

const currentMode = computed(() => {
  if (isEditMode.value) {
    return showAiPanel.value ? ExperienceFormMode.EDIT_WITH_AI : ExperienceFormMode.EDIT;
  }
  return ExperienceFormMode.VIEW;
});

const pageTitleView = computed(() => {
  const prefix = isEditMode.value ? '경험 수정' : '경험 상세';
  return `${prefix} [ ${pageTitle.value} ]`;
});

const formData = ref<TExperienceFormData>({
  title: '',
  background: '',
  periodStart: '',
  periodEnd: '',
  role: '',
  category: null,
  contributionLevel: null,
  goalSummary: '',
  keyAchievements: '',
  skills: '',
  sections: [],
});

const loadExperienceData = async () => {
  isLoading.value = true;
  const { data, error } = await fetchExperience(experienceId.value);
  isLoading.value = false;

  if (error) {
    console.error('경험 조회 실패:', error);
    toast.error('경험을 불러오는데 실패했습니다.');
    return;
  }

  if (data) {
    rawExperience.value = data;
    formData.value = {
      id: data.id,
      title: data.title,
      background: data.background || '',
      periodStart: data.periodStart,
      periodEnd: data.periodEnd || '',
      role: data.role || '',
      category: data.category || null,
      contributionLevel: data.contributionLevel || null,
      goalSummary: data.goalSummary || '',
      keyAchievements: data.keyAchievements || '',
      skills: data.skills || '',
      sections:
        data.sections?.map((section: any) => ({
          ...section,
          isEditingTitle: false,
          tempTitle: '',
          showHelp: false,
          showMethodBreakdown: false,
        })) || [],
    };
    pageTitle.value = data.title;
  }
};

const handleSave = async () => {
  if (!formData.value.title.trim()) {
    toast.error('제목을 입력해주세요.');
    return;
  }
  if (!formData.value.periodStart) {
    toast.error('시작일을 입력해주세요.');
    return;
  }

  const requestBody: TExperienceUpdate = {
    title: formData.value.title,
    background: formData.value.background || undefined,
    role: formData.value.role || undefined,
    periodStart: formData.value.periodStart,
    periodEnd: formData.value.periodEnd || undefined,
    category: formData.value.category || undefined,
    contributionLevel: formData.value.contributionLevel || undefined,
    goalSummary: formData.value.goalSummary || undefined,
    keyAchievements: formData.value.keyAchievements || undefined,
    skills: formData.value.skills || undefined,
    sections: formData.value.sections.map((section: any, index: number) => ({
      id: section.id?.startsWith('new_section_') ? undefined : section.id,
      kind: section.kind,
      title: section.title || `블록 ${index + 1}`,
      content: section.content,
      sortOrder: section.sortOrder ?? index,
    })),
  };

  const { error } = await updateExperience(experienceId.value, requestBody);

  if (error) {
    console.error('경험 수정 실패:', error);
    return;
  }

  toast.success('저장되었습니다!');
  isEditMode.value = false;
  showAiPanel.value = false;
  await loadExperienceData();
};

const handleCancel = async () => {
  await loadExperienceData();
  isEditMode.value = false;
  showAiPanel.value = false;
};


const checkAiAnalysisExists = async () => {
  const { data } = await fetchAIAnalysisExists(experienceId.value);
  if (typeof data === 'boolean') {
    hasAiAnalysis.value = data;
  }
};

const handleRequestAiAnalysis = () => {
  showAnalysisModal.value = true;
};

const handleAnalysisRequest = async (experience: TExperience, options: any) => {
  const { error } = await requestAiAnalysis(experience.id, options);
  if (!error) {
    toast.success(`'${experience.title}'에 대한 AI 분석 요청이 완료되었습니다. 분석이 완료되면 자동으로 알려드려요!`);
    showAnalysisModal.value = false;

    // 약 3초 후에 존재 여부를 다시 확인해봄 (데모용)
    setTimeout(async () => {
      await checkAiAnalysisExists();
      await loadExperienceData();

      // 분석 완료되면 토스트로 알림
      if (hasAiAnalysis.value) {
        toast.success('AI 분석이 완료되었습니다! 수정하기 버튼을 눌러 AI 제안을 확인하세요.');
      }
    }, 3000);
  }
};

// AI 분석 데이터 로드 함수
const loadAiAnalysisData = async (forceRefresh = false) => {
  if (!forceRefresh && aiAnalysisData.value) return;
  if (!hasAiAnalysis.value) return;

  isLoadingAiAnalysis.value = true;
  try {
    const { data, error } = await fetchExperienceAIAnalysis(experienceId.value);
    if (!error && data) {
      aiAnalysisData.value = data;
    } else {
      toast.error('AI 분석 결과를 불러오는데 실패했습니다');
    }
  } catch (err) {
    console.error('Failed to load AI analysis:', err);
    toast.error('AI 분석 결과를 불러올 수 없습니다');
  } finally {
    isLoadingAiAnalysis.value = false;
  }
};

const handleBack = () => {
  navigateTo(MENU_URLS.EXPERIENCE);
};

// AI 제안 토글 핸들러
const handleAiPanelToggle = async (value: boolean) => {
  if (value) {
    // AI 패널 표시 시 데이터 로드
    await loadAiAnalysisData();
  }
};

// AI 분석이 있는 상태에서 수정하기 버튼 클릭
const handleEditWithAi = async () => {
  await loadAiAnalysisData();
  showAiPanel.value = true;
  isEditMode.value = true;
};

// Load AI analysis data when panel is enabled
watch(showAiPanel, async (newValue) => {
  if (newValue && !aiAnalysisData.value && hasAiAnalysis.value) {
    await loadAiAnalysisData();
  }
});

// Check query param for AI panel state - enter edit mode with AI if ai=true
watch(() => route.query.ai, async (aiParam) => {
  if (aiParam === 'true' && hasAiAnalysis.value) {
    await loadAiAnalysisData();
    showAiPanel.value = true;
    isEditMode.value = true;
  }
}, { immediate: true });

// Check if experience has AI analysis
watch(() => rawExperience.value?.status, (status) => {
  if (status === 'AI_ANALYZED') {
    checkAiAnalysisExists();
  }
}, { immediate: true });

onMounted(() => {
  loadExperienceData();
  checkAiAnalysisExists();
});
</script>

<style lang="scss" scoped>
.career-detail-page {
  min-height: 100vh;
  margin: 0;
  padding: 26px;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
  display: flex;
  gap: 24px;

  .form-section {
    flex: 1;
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

// AI 태그 스타일
.ai-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 6px;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #10b981 0%, #3b82f6 100%);
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>
