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
      <ExperienceForm
        v-if="!isLoading"
        v-model="formData"
        v-model:is-edit-mode="isEditMode"
        :is-new="false"
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
import type { TExperienceFormData } from '@/components/page/ExperienceForm.vue';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { fetchExperience, updateExperience } from '~/api/experience/api';
import type { TExperienceUpdate } from '~/api/experience/types';

const route = useRoute();
const toast = useToast();

definePageMeta({
  layout: 'default',
});

const experienceId = computed(() => route.params.id as string);
const isEditMode = ref(false);
const isLoading = ref(true);
const pageTitle = ref('');

const pageTitleView = computed(() => {
  const prefix = isEditMode.value ? '경험 수정' : '경험 상세';
  return `${prefix} - ${pageTitle.value}`;
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
        data.sections?.map((section) => ({
          ...section,
          isEditingTitle: false,
          tempTitle: '',
          showHelp: false,
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
    sections: formData.value.sections.map((section, index) => ({
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
  await loadExperienceData();
};

const handleCancel = async () => {
  await loadExperienceData();
  isEditMode.value = false;
};

const handleBack = () => {
  navigateTo('/career');
};

onMounted(() => {
  loadExperienceData();
});
</script>

<style lang="scss" scoped>
.career-detail-page {
  min-height: 100vh;
  margin: 0;
  padding: 26px; // Apply padding to all sides
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
