<template>
  <div class="career-ai-edit-page">
    <PageHeader
      v-if="!isLoading"
      :title="`AI와 함께 수정 [ ${pageTitle} ]`"
      subtitle="AI의 제안을 참고하여 경험을 더 구체적으로 만들어보세요"
    >
      <template #actions>
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
      </template>
    </PageHeader>

    <div class="content-wrapper">
      <ExperienceForm
        v-if="!isLoading"
        v-model="formData"
        :mode="ExperienceFormMode.EDIT_WITH_AI"
        :ai-analysis="aiAnalysisData"
      />
      <div v-else class="loading-container">
        <v-progress-circular indeterminate color="primary" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import ExperienceForm from '@/components/page/ExperienceForm.vue';
import { ExperienceFormMode } from '@/types/experience-types';
import type { TExperienceFormData, TExperienceFormSection } from '@/types/experience-types';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { fetchExperience, updateExperience, fetchExperienceAIAnalysis } from '~/api/experience/api';
import type { TExperienceUpdate, TExperienceAIAnalysisResponse } from '~/api/experience/types';

const route = useRoute();
const toast = useToast();

definePageMeta({
  layout: 'default',
});

const experienceId = computed(() => route.params.id as string);
const isLoading = ref(true);
const pageTitle = ref('');
const aiAnalysisData = ref<TExperienceAIAnalysisResponse | undefined>();

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

const loadInitialData = async () => {
  isLoading.value = true;
  
  // 경험 상사 정보와 AI 분석 정보를 동시에 가져옴
  const [expRes, aiRes] = await Promise.all([
    fetchExperience(experienceId.value),
    fetchExperienceAIAnalysis(experienceId.value)
  ]);

  isLoading.value = false;

  if (expRes.error) {
    toast.error('경험 정보를 불러오는데 실패했습니다.');
    navigateTo(`/career/${experienceId.value}`);
    return;
  }

  if (aiRes.error) {
    toast.error('AI 분석 정보를 불러오는데 실패했습니다.');
  }

  if (expRes.data) {
    formData.value = {
      id: expRes.data.id,
      title: expRes.data.title,
      background: expRes.data.background || '',
      periodStart: expRes.data.periodStart,
      periodEnd: expRes.data.periodEnd || '',
      role: expRes.data.role || '',
      category: expRes.data.category || null,
      contributionLevel: expRes.data.contributionLevel || null,
      goalSummary: expRes.data.goalSummary || '',
      keyAchievements: expRes.data.keyAchievements || '',
      skills: expRes.data.skills || '',
      sections: expRes.data.sections?.map((s: any) => ({
        ...s,
        isEditingTitle: false,
        tempTitle: '',
        showHelp: false
      })) || [],
    };
    pageTitle.value = expRes.data.title;
  }

  if (aiRes.data) {
    aiAnalysisData.value = aiRes.data;
  }
};

const handleSave = async () => {
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
    sections: formData.value.sections.map((section: TExperienceFormSection, index: number) => ({
      id: section.id?.startsWith('new_section_') ? undefined : section.id,
      kind: section.kind,
      title: section.title || `블록 ${index + 1}`,
      content: section.content,
      sortOrder: section.sortOrder ?? index,
    })),
  };

  const { error } = await updateExperience(experienceId.value, requestBody);

  if (error) {
    console.error('저장 실패:', error);
    return;
  }

  toast.success('저장되었습니다!');
  navigateTo(`/career/${experienceId.value}`);
};

const handleCancel = () => {
  navigateTo(`/career/${experienceId.value}`);
};

onMounted(() => {
  loadInitialData();
});
</script>

<style lang="scss" scoped>
.career-ai-edit-page {
  min-height: 100vh;
  margin: 0;
  padding: 26px;
  display: flex;
  flex-direction: column;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}
</style>
