<template>
  <div class="career-register-page">
    <!-- 페이지 헤더 -->
    <PageHeader title="경험 등록" subtitle="새로운 경험을 등록하고 관리하세요">
      <template #actions>
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          @click="handleCancel"
        >
          <v-icon size="small">mdi-arrow-left</v-icon>
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

    <div class="page-layout">
      <!-- 왼쪽: 메인 폼 영역 (4) -->
      <div class="form-container">
        <!-- 기본 정보 블록 -->
        <Card
          title="기본 정보"
          icon="mdi-trophy"
          icon-color="linear-gradient(135deg, #2563eb 0%, #1e40af 100%)"
        >
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="field-label">제목 *</label>
              <Input
                v-model="formData.title"
                placeholder="경험을 한마디로 정의해주세요."
                :size="CommonSize.Medium"
              />
            </div>

            <div class="form-field full-width">
              <div class="form-row">
                <div class="form-field-inline">
                  <label class="field-label">시작일 *</label>
                  <DatePicker
                    v-model="formData.durationStart"
                    type="date"
                    placeholder="시작일을 선택해주세요."
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">종료일</label>
                  <DatePicker
                    v-model="formData.durationEnd"
                    type="date"
                    placeholder="종료일은 선택하지 않아도 괜찮아요."
                  />
                </div>
              </div>
            </div>

            <div class="form-field full-width">
              <div class="form-row">
                <div class="form-field-inline">
                  <label class="field-label">소속</label>
                  <Input
                    v-model="formData.orgName"
                    placeholder="회사명, 조직명 또는 개인"
                    :size="CommonSize.Medium"
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">역할/직책</label>
                  <Input
                    v-model="formData.roleTitle"
                    placeholder="담당한 역할이나 직책 또는 직무"
                    :size="CommonSize.Medium"
                  />
                </div>
              </div>
            </div>
          </div>
        </Card>

        <!-- 업무 정보 블록 -->
        <Card
          title="업무 정보"
          icon="mdi-briefcase-variant"
          icon-color="linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)"
        >
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="field-label">업무 유형</label>
              <div class="select-with-description">
                <Select
                  v-model="formData.workType"
                  :items="workTypeOptions"
                  placeholder="선택"
                  :size="FormSize.Compact"
                />
                <DescriptionBox
                  :text="
                    formData.workType
                      ? getWorkTypeDescription(formData.workType)
                      : '업무 유형을 선택해주세요'
                  "
                />
              </div>
            </div>
            <div class="form-field full-width">
              <label class="field-label">기여도/참여도</label>
              <div class="select-with-description">
                <Select
                  v-model="formData.contributionLevel"
                  :items="contributionLevelOptions"
                  placeholder="선택"
                  :size="FormSize.Compact"
                />
                <DescriptionBox
                  :text="
                    formData.contributionLevel
                      ? getContributionLevelDescription(formData.contributionLevel)
                      : '기여도를 선택해주세요'
                  "
                />
              </div>
            </div>
          </div>
        </Card>

        <!-- 목표 블록 -->
        <Card
          title="목표"
          icon="mdi-flag-checkered"
          icon-color="linear-gradient(135deg, #10b981 0%, #059669 100%)"
        >
          <div class="form-grid">
            <div class="form-field full-width">
              <TextArea
                v-model="formData.goalSummary"
                placeholder="달성하고자 했던 목표를 작성하세요"
                :rows="3"
              />
            </div>
          </div>
        </Card>

        <!-- 핵심 경험 블록 -->
        <Card
          title="핵심 경험"
          icon="mdi-star-circle"
          icon-color="linear-gradient(135deg, #f59e0b 0%, #d97706 100%)"
        >
          <div class="form-grid">
            <div class="form-field full-width">
              <TextArea
                v-model="formData.impactSummary"
                placeholder="이 경험을 통해 얻은 경험와 영향을 간략히 설명하세요"
                :rows="3"
              />
            </div>
          </div>
        </Card>

        <!-- 스킬 블록 -->
        <Card
          title="스킬"
          icon="mdi-code-tags"
          icon-color="linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)"
        >
          <div class="form-grid">
            <div class="form-field full-width">
              <Input
                v-model="formData.skills"
                placeholder="관련 스킬을 쉼표로 구분하여 입력하세요 (예: Vue.js, TypeScript, Node.js)"
                :size="CommonSize.Medium"
              />
            </div>
          </div>
        </Card>

        <!-- 상세 블록들 (동적) -->
        <Card
          v-for="(section, index) in formData.sections"
          :key="section.id"
          icon="mdi-text-box-multiple"
          icon-color="linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
        >
          <template #header>
            <div class="card-custom-header">
              <div
                class="card-icon-wrapper"
                style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
              >
                <v-icon color="white" size="20">mdi-text-box-multiple</v-icon>
              </div>

              <!-- 제목과 편집 버튼 그룹 -->
              <div class="section-title-group">
                <!-- 편집 모드가 아닐 때 -->
                <template v-if="!section.isEditingTitle">
                  <div class="section-title-wrapper">
                    <div class="section-title-row">
                      <h2 class="section-title">
                        {{ section.title || `블록 ${index + 1}` }}
                      </h2>
                      <button class="edit-btn-header" @click="startEditSectionTitle(index)">
                        <v-icon size="small">mdi-pencil</v-icon>
                      </button>
                    </div>
                    <div v-if="isDefaultTitle(section, index)" class="section-title-hint">
                      블록 이름을 입력해주세요
                    </div>
                  </div>
                </template>

                <!-- 편집 모드일 때 -->
                <template v-else>
                  <div class="section-title-edit">
                    <Input
                      v-model="section.tempTitle!"
                      placeholder="블록 제목을 입력하세요"
                      :size="CommonSize.Small"
                      class="section-title-input"
                    />
                    <button class="edit-action-btn apply" @click="applySectionTitle(index)">
                      <v-icon size="small">mdi-check</v-icon>
                    </button>
                    <button class="edit-action-btn cancel" @click="cancelSectionTitleEdit(index)">
                      <v-icon size="small">mdi-close</v-icon>
                    </button>
                  </div>
                </template>
              </div>

              <button class="delete-btn-header" @click="removeSection(index)">
                <v-icon size="small">mdi-delete</v-icon>
              </button>
            </div>
          </template>

          <div class="form-grid">
            <div class="form-field full-width">
              <div class="section-kind-group">
                <Select
                  v-model="section.kind"
                  :items="sectionKindOptions"
                  placeholder="블록 유형을 선택하세요"
                  :size="FormSize.Compact"
                  @update:model-value="onSectionKindChange(index)"
                />
                <button
                  class="section-help-btn"
                  :class="{ active: section.showHelp }"
                  @click="toggleSectionHelp(index)"
                >
                  <v-icon size="small">mdi-help-circle-outline</v-icon>
                  <span class="help-btn-text">Help</span>
                </button>
                <DescriptionBox :text="getSectionDescription(section.kind)" />
              </div>
              <Transition name="fade">
                <div v-if="section.showHelp" class="section-help-detail">
                  {{ getSectionHelp(section.kind) }}
                </div>
              </Transition>
            </div>

            <div class="form-field full-width">
              <TextArea
                v-model="section.content"
                placeholder="Help 버튼을 눌러서 작성 가이드를 확인하세요"
                :rows="8"
              />
            </div>
          </div>
        </Card>

        <!-- 빈 상태 -->
        <div v-if="formData.sections.length === 0" class="empty-state-standalone">
          <v-icon size="large" color="#9ca3af">mdi-text-box-plus</v-icon>
          <p class="empty-state-text">블록을 추가하여 구체적인 내용을 추가해주세요.</p>
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            class="empty-state-add-btn"
            @click="addSection"
          >
            <v-icon size="small">mdi-plus</v-icon>
            블록 추가
          </Button>
        </div>
      </div>

      <!-- 오른쪽: 액션 사이드바 (1) -->
      <aside class="action-sidebar">
        <!-- 블록 관리 영역 -->
        <div class="sidebar-section">
          <div class="sidebar-header">
            <h3 class="sidebar-title">편집</h3>
          </div>

          <!-- 블록 추가 버튼 -->
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            :round="true"
            class="sidebar-add-btn"
            @click="addSection"
          >
            <v-icon size="small">mdi-plus</v-icon>
            블록 추가
          </Button>

          <!-- 블록 목록 -->
          <div class="sidebar-sections-list">
            <!-- 고정 블록 (수정 불가) -->
            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#2563eb">mdi-trophy</v-icon>
                <span class="sidebar-section-title">기본 정보</span>
              </div>
            </div>

            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#8b5cf6">mdi-briefcase-variant</v-icon>
                <span class="sidebar-section-title">업무 정보</span>
              </div>
            </div>

            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#10b981">mdi-flag-checkered</v-icon>
                <span class="sidebar-section-title">목표</span>
              </div>
            </div>

            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#f59e0b">mdi-star-circle</v-icon>
                <span class="sidebar-section-title">핵심 경험</span>
              </div>
            </div>

            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#3b82f6">mdi-code-tags</v-icon>
                <span class="sidebar-section-title">스킬</span>
              </div>
            </div>

            <!-- 구분선 -->
            <div v-if="formData.sections.length > 0" class="sidebar-divider" />

            <!-- 동적 블록 (순서 변경 가능) -->
            <VueDraggableNext
              v-model="formData.sections"
              tag="div"
              handle=".sidebar-section-drag-handle"
              :animation="200"
              ghost-class="sidebar-section-ghost"
              chosen-class="sidebar-section-chosen"
              drag-class="sidebar-section-drag"
              @end="updateSortOrder"
            >
              <div
                v-for="(section, index) in formData.sections"
                :key="section.id"
                class="sidebar-section-item"
              >
                <div class="sidebar-section-drag-handle">
                  <v-icon size="small" color="#6b7280">mdi-drag-vertical</v-icon>
                </div>
                <div class="sidebar-section-info">
                  <span class="sidebar-section-number">{{ index + 1 }}</span>
                  <span class="sidebar-section-title">
                    {{ section.title || `블록 ${index + 1}` }}
                  </span>
                </div>
                <button class="sidebar-section-delete" @click="removeSection(index)">
                  <v-icon size="small">mdi-delete-outline</v-icon>
                </button>
              </div>
            </VueDraggableNext>
          </div>
        </div>

        <!-- 미래 확장 공간 -->
        <!-- <div class="sidebar-section">
          <div class="sidebar-placeholder">
            <v-icon size="small" color="#d1d5db">mdi-dots-horizontal</v-icon>
            <p class="sidebar-placeholder-text">추가 기능 예정</p>
          </div>
        </div> -->
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { VueDraggableNext } from 'vue-draggable-next';
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Card from '@/components/molecules/Card/Card.vue';
import Button from '@/components/atoms/Button/Button.vue';
import Input from '@/components/atoms/Input/Input.vue';
import TextArea from '@/components/atoms/TextArea/TextArea.vue';
import DatePicker from '@/components/atoms/DatePicker/DatePicker.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import DescriptionBox from '@/components/atoms/DescriptionBox/DescriptionBox.vue';
import { ButtonVariant, CommonSize, FormSize } from '@/constants/enums/style-enum';
import {
  type ExperienceSection,
  ExperienceSectionKind,
  SECTION_KIND_INFO,
  WORK_TYPE_INFO,
  CONTRIBUTION_LEVEL_INFO,
} from '@/types/experience-types';
import { createExperience } from '~/api/experience/api';
import type { TExperienceCreate } from '~/api/experience/types';

const toast = useToast();

definePageMeta({
  layout: 'default',
});

interface FormSection extends ExperienceSection {
  isEditingTitle?: boolean; // 제목 편집 모드 여부
  tempTitle?: string; // 편집 중인 임시 제목 (수정 전 원본 저장)
  showHelp?: boolean; // help description 표시 여부
}

// 블록 유형 옵션 생성
const sectionKindOptions = computed<TSelectItem[]>(() => {
  return Object.entries(SECTION_KIND_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

// 업무 유형 옵션 생성
const workTypeOptions = computed<TSelectItem[]>(() => {
  return Object.entries(WORK_TYPE_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

// 기여도 옵션 생성
const contributionLevelOptions = computed<TSelectItem[]>(() => {
  return Object.entries(CONTRIBUTION_LEVEL_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

interface FormData {
  title: string;
  orgName: string;
  durationStart: string;
  durationEnd: string;
  roleTitle: string;
  workType: string | null;
  contributionLevel: string | null;
  goalSummary: string;
  impactSummary: string;
  skills: string;
  sections: FormSection[];
}

const formData = ref<FormData>({
  title: '',
  orgName: '',
  durationStart: '',
  durationEnd: '',
  roleTitle: '',
  workType: null,
  contributionLevel: null,
  goalSummary: '',
  impactSummary: '',
  skills: '',
  sections: [],
});

let sectionCounter = 0;

const addSection = () => {
  formData.value.sections.push({
    id: `new_section_${Date.now()}_${sectionCounter++}`,
    kind: ExperienceSectionKind.NONE,
    title: '',
    content: '',
    sortOrder: formData.value.sections.length,
    isEditingTitle: false,
    tempTitle: '',
    showHelp: false,
  });
};

// 블록 유형에 따른 help 가져오기
const getSectionHelp = (kind: string): string => {
  const kindKey = kind as ExperienceSectionKind;
  return SECTION_KIND_INFO[kindKey]?.help || '내용을 입력하세요';
};

// 블록 유형에 따른 description 가져오기
const getSectionDescription = (kind: string): string => {
  const kindKey = kind as ExperienceSectionKind;
  return SECTION_KIND_INFO[kindKey]?.description || '';
};

// 업무 유형에 따른 description 가져오기
const getWorkTypeDescription = (workType: string | null): string => {
  if (!workType) return '';
  return WORK_TYPE_INFO[workType as keyof typeof WORK_TYPE_INFO]?.description || '';
};

// 기여도에 따른 description 가져오기
const getContributionLevelDescription = (level: string | null): string => {
  if (!level) return '';
  return CONTRIBUTION_LEVEL_INFO[level as keyof typeof CONTRIBUTION_LEVEL_INFO]?.description || '';
};

// 블록 help 토글
const toggleSectionHelp = (index: number) => {
  formData.value.sections[index].showHelp = !formData.value.sections[index].showHelp;
};

// 블록 유형 변경 시 처리
const onSectionKindChange = (index: number) => {
  // 유형 변경 시 추가 로직이 필요한 경우 여기에 구현
  console.log(`Section ${index} kind changed to:`, formData.value.sections[index].kind);
};

const removeSection = (index: number) => {
  formData.value.sections.splice(index, 1);
  updateSortOrder();
};

// 블록 순서 업데이트
const updateSortOrder = () => {
  formData.value.sections.forEach((section, index) => {
    section.sortOrder = index;
  });
};

// 제목이 기본값인지 확인
const isDefaultTitle = (section: FormSection, index: number): boolean => {
  // 제목이 비어있거나 기본 패턴("블록 1", "블록 2" 등)과 일치하면 true
  return !section.title || section.title === `블록 ${index + 1}`;
};

// 블록 제목 편집 시작
const startEditSectionTitle = (index: number) => {
  const section = formData.value.sections[index];
  section.tempTitle = section.title || `블록 ${index + 1}`; // 수정 전 원본 저장
  section.isEditingTitle = true;
};

// 블록 제목 적용
const applySectionTitle = (index: number) => {
  const section = formData.value.sections[index];
  section.title = section.tempTitle || ''; // tempTitle을 실제 title로 적용
  section.tempTitle = '';
  section.isEditingTitle = false;
};

// 블록 제목 편집 취소
const cancelSectionTitleEdit = (index: number) => {
  const section = formData.value.sections[index];
  section.tempTitle = '';
  section.isEditingTitle = false;
};

const handleSave = async () => {
  // 필수 필드 검증 (최소한의 필수 블록만)
  if (!formData.value.title.trim()) {
    toast.error('제목을 입력해주세요.');
    return;
  }
  if (!formData.value.durationStart) {
    toast.error('시작일을 입력해주세요.');
    return;
  }

  // API 요청 데이터 변환
  const requestBody: TExperienceCreate = {
    title: formData.value.title,
    orgName: formData.value.orgName || undefined,
    roleTitle: formData.value.roleTitle || undefined,
    durationStart: formData.value.durationStart,
    durationEnd: formData.value.durationEnd || undefined,
    workType: formData.value.workType || undefined,
    contributionLevel: formData.value.contributionLevel || undefined,
    goalSummary: formData.value.goalSummary || undefined,
    impactSummary: formData.value.impactSummary || undefined,
    skills: formData.value.skills || undefined,
    sections: formData.value.sections.map((section, index) => ({
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
  // 폼 초기화 또는 이전 페이지로 이동
  window.history.back();
};
</script>

<style lang="scss" scoped>
.career-register-page {
  min-height: 100vh;
  margin: -32px;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.page-layout {
  padding: 40px 48px;
  display: grid;
  grid-template-columns: 4fr 1fr;
  gap: 24px;
  align-items: start;
  flex: 1;

  @media (max-width: 768px) {
    padding: 24px;
    grid-template-columns: 1fr;
  }
}

// 동적 블록의 커스텀 헤더
.card-custom-header {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.card-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}
</style>
<style lang="scss">
@use '@/styles/pages/career-register-page.scss';
</style>
