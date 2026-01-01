<template>
  <div class="career-register-page">
    <PageHeader title="경험 등록" subtitle="새로운 경험을 등록하고 관리하세요">
      <template #actions>
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          @click="handleCancel"
        >
          <v-icon size="small">mdi-close</v-icon>
          뒤로가기
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
        v-model="formData"
        :mode="ExperienceFormMode.REGISTER"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ExperienceForm from '@/components/page/ExperienceForm.vue';
import { ExperienceFormMode } from '@/types/experience-types';
import type { TExperienceFormData, TExperienceFormSection } from '@/types/experience-types';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { createExperience } from '~/api/experience/api';
import type { TExperienceCreate } from '~/api/experience/types';

import { ExperienceSectionKind } from '@/types/experience-types';

const toast = useToast();

definePageMeta({
  layout: 'default',
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
  sections: [
    {
      id: `default_section_${Date.now()}`,
      kind: ExperienceSectionKind.SITUATION,
      title: '배경 및 목표',
      content: '',
      sortOrder: 0,
      isEditingTitle: false,
      tempTitle: '',
      showHelp: false,
    },
  ],
});

const handleSave = async () => {
  // 필수 필드 검증
  if (!formData.value.title.trim()) {
    toast.error('제목을 입력해주세요.');
    return;
  }
  if (!formData.value.periodStart) {
    toast.error('시작일을 입력해주세요.');
    return;
  }

  // API 요청 데이터 변환
  const requestBody: TExperienceCreate = {
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
      kind: section.kind,
      title: section.title || `블록 ${index + 1}`,
      content: section.content,
      sortOrder: section.sortOrder ?? index,
    })),
  };

  // API 호출
  const { error } = await createExperience(requestBody);

  if (error) {
    console.error('API 에러:', error);
    return;
  }

  toast.success('저장되었습니다!');

  // 목록 페이지로 이동
  await navigateTo('/career');
};

const handleCancel = () => {
  window.history.back();
};
</script>

<style lang="scss" scoped>
.career-register-page {
  min-height: 100vh;
  margin: 0;
  padding: 26px; // Apply padding to all sides
  display: flex;
  flex-direction: column;
}

/* ExperienceForm 내부에서 스타일을 관리하므로 여기서는 최소한의 스타일만 유지하거나 제거 가능 */
</style>

<style lang="scss">
/* 전역 스타일은 필요시 유지 */
</style>
