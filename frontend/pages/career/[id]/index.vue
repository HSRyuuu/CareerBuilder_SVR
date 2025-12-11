<template>
  <div class="career-register-page">
    <!-- 페이지 헤더 -->
    <div class="page-header">
      <div class="page-header-left">
        <button class="back-button" @click="handleBack">
          <v-icon>mdi-arrow-left</v-icon>
          목록으로
        </button>
      </div>
      <div class="page-header-center">
        <h1 class="page-title">{{ isEditMode ? '성과 수정' : '성과 상세' }}</h1>
        <p class="page-subtitle">
          {{ isEditMode ? '성과 정보를 수정하세요' : '성과 정보를 확인하세요' }}
        </p>
      </div>
      <div class="page-header-right">
        <button v-if="!isEditMode" class="edit-mode-button" @click="handleEnterEditMode">
          <v-icon>mdi-pencil</v-icon>
          수정 모드
        </button>
        <template v-else>
          <button class="cancel-button" @click="handleCancelEdit">
            <v-icon>mdi-close</v-icon>
            취소
          </button>
          <button class="save-button-primary" @click="handleSave">
            <v-icon>mdi-check</v-icon>
            저장
          </button>
        </template>
      </div>
    </div>

    <div class="page-layout">
      <!-- 왼쪽: 메인 폼 영역 -->
      <div class="form-container">
        <!-- 기본 정보 블록 -->
        <section class="form-section">
          <div class="section-header">
            <div
              class="section-icon"
              style="background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%)"
            >
              <v-icon color="white" size="small">mdi-trophy</v-icon>
            </div>
            <h2 class="section-title">기본 정보</h2>
          </div>
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="field-label">제목 *</label>
              <Input
                v-model="formData.title"
                placeholder="성과 제목을 입력하세요"
                :size="CommonSize.Medium"
                :disabled="!isEditMode"
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
                    :disabled="!isEditMode"
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">종료일</label>
                  <DatePicker
                    v-model="formData.durationEnd"
                    type="date"
                    placeholder="종료일은 선택하지 않아도 괜찮아요."
                    :disabled="!isEditMode"
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
                    :disabled="!isEditMode"
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">역할/직책</label>
                  <Input
                    v-model="formData.roleTitle"
                    placeholder="담당한 역할이나 직책 또는 직무"
                    :size="CommonSize.Medium"
                    :disabled="!isEditMode"
                  />
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 업무 정보 블록 -->
        <section class="form-section">
          <div class="section-header">
            <div
              class="section-icon"
              style="background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)"
            >
              <v-icon color="white" size="small">mdi-briefcase-variant</v-icon>
            </div>
            <h2 class="section-title">업무 정보</h2>
          </div>
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="field-label">업무 유형</label>
              <Select
                v-model="formData.workType"
                :items="workTypeOptions"
                placeholder="선택"
                :size="FormSize.Compact"
                :disabled="!isEditMode"
              />
            </div>

            <div class="form-field full-width">
              <label class="field-label">기여도/참여도</label>
              <Select
                v-model="formData.contributionLevel"
                :items="contributionLevelOptions"
                placeholder="선택"
                :size="FormSize.Compact"
                :disabled="!isEditMode"
              />
            </div>

            <div class="form-field full-width">
              <label class="field-label">스킬/기술</label>
              <Input
                v-model="formData.skills"
                placeholder="사용한 기술, 도구, 방법론 (쉼표로 구분)"
                :size="CommonSize.Medium"
                :disabled="!isEditMode"
              />
            </div>
          </div>
        </section>

        <!-- 핵심 요약 블록 -->
        <section class="form-section">
          <div class="section-header">
            <div
              class="section-icon"
              style="background: linear-gradient(135deg, #10b981 0%, #059669 100%)"
            >
              <v-icon color="white" size="small">mdi-star</v-icon>
            </div>
            <h2 class="section-title">핵심 요약</h2>
          </div>
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="field-label">목표 요약</label>
              <TextArea
                v-model="formData.goalSummary"
                placeholder="이 성과를 통해 달성하고자 했던 목표를 간단히 요약해주세요"
                :rows="3"
                :disabled="!isEditMode"
              />
            </div>

            <div class="form-field full-width">
              <label class="field-label">핵심 성과 요약</label>
              <TextArea
                v-model="formData.impactSummary"
                placeholder="달성한 핵심 성과와 impact를 요약해주세요"
                :rows="3"
                :disabled="!isEditMode"
              />
            </div>
          </div>
        </section>

        <!-- 상세 블록들 -->
        <section
          v-for="(section, index) in formData.sections"
          :key="section.tempId"
          class="form-section"
        >
          <div class="section-header">
            <div
              class="section-icon"
              style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
            >
              <v-icon color="white" size="small">mdi-text-box-multiple</v-icon>
            </div>

            <!-- 제목과 편집 버튼 그룹 -->
            <div class="section-title-group">
              <!-- 편집 모드가 아닐 때 -->
              <template v-if="!section.isEditingTitle || !isEditMode">
                <div class="section-title-wrapper">
                  <div class="section-title-row">
                    <h2 class="section-title">
                      {{ section.title || `블록 ${index + 1}` }}
                    </h2>
                    <button
                      v-if="isEditMode"
                      class="edit-btn-header"
                      @click="startEditSectionTitle(index)"
                    >
                      <v-icon size="small">mdi-pencil</v-icon>
                    </button>
                  </div>
                  <div
                    v-if="isDefaultTitle(section, index) && isEditMode"
                    class="section-title-hint"
                  >
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

            <button v-if="isEditMode" class="delete-btn-header" @click="removeSection(index)">
              <v-icon size="small">mdi-delete</v-icon>
            </button>
          </div>

          <div class="form-grid">
            <div class="form-field full-width">
              <div class="section-kind-group">
                <Select
                  v-model="section.kind"
                  :items="sectionKindOptions"
                  placeholder="블록 유형을 선택하세요"
                  :size="FormSize.Compact"
                  :disabled="!isEditMode"
                  @update:model-value="onSectionKindChange(index)"
                />
                <button
                  v-if="isEditMode"
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
                <div v-if="section.showHelp && isEditMode" class="section-help-detail">
                  {{ getSectionHelp(section.kind) }}
                </div>
              </Transition>
            </div>

            <div class="form-field full-width">
              <TextArea
                v-model="section.content"
                placeholder="Help 버튼을 눌러서 작성 가이드를 확인하세요"
                :rows="8"
                :disabled="!isEditMode"
              />
            </div>
          </div>
        </section>

        <!-- 빈 상태 (수정 모드일 때만 표시) -->
        <div v-if="formData.sections.length === 0 && isEditMode" class="empty-state-standalone">
          <v-icon size="large" color="#9ca3af">mdi-text-box-plus</v-icon>
          <p class="empty-state-text">블록을 추가하여 구체적인 내용을 추가해주세요.</p>
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            class="empty-state-add-btn"
            @click="addSection"
          >
            <v-icon size="small">mdi-plus</v-icon>
            블록 추가
          </Button>
        </div>
      </div>

      <!-- 오른쪽: 액션 영역 -->
      <!-- 읽기 모드: 간단한 액션 패널 -->
      <div v-if="!isEditMode" class="action-panel">
        <div class="action-card">
          <h3 class="action-card-title">액션</h3>
          <div class="action-buttons">
            <button class="action-button primary" @click="handleEnterEditMode">
              <v-icon size="small">mdi-pencil</v-icon>
              수정하기
            </button>
            <button class="action-button ghost" @click="handleBack">
              <v-icon size="small">mdi-arrow-left</v-icon>
              목록으로
            </button>
          </div>
        </div>
      </div>

      <!-- 수정 모드: 편집 사이드바 -->
      <aside v-else class="action-sidebar">
        <!-- 블록 관리 영역 -->
        <div class="sidebar-section">
          <div class="sidebar-header">
            <h3 class="sidebar-title">편집</h3>
          </div>

          <!-- 블록 추가 버튼 -->
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
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
                <v-icon size="small" color="#10b981">mdi-star</v-icon>
                <span class="sidebar-section-title">핵심 요약</span>
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
                :key="section.tempId"
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

        <!-- 액션 버튼 -->
        <div class="sidebar-section sidebar-actions">
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Large"
            class="sidebar-action-btn"
            @click="handleSave"
          >
            <v-icon size="small">mdi-check</v-icon>
            저장
          </Button>
          <Button
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Large"
            class="sidebar-action-btn"
            @click="handleCancelEdit"
          >
            <v-icon size="small">mdi-close</v-icon>
            취소
          </Button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { VueDraggableNext } from 'vue-draggable-next';
import {
  type AchievementSection,
  AchievementSectionKind,
  SECTION_KIND_INFO,
  WORK_TYPE_INFO,
  CONTRIBUTION_LEVEL_INFO,
} from '@/types/achievement-types';
import { ButtonVariant, CommonSize, FormSize } from '@/constants/enums/style-enum';
import { fetchAchievement, updateAchievement } from '~/api/achievement/api';
import Button from '@/components/atoms/Button/Button.vue';
import Input from '@/components/atoms/Input/Input.vue';
import TextArea from '@/components/atoms/TextArea/TextArea.vue';
import DatePicker from '@/components/atoms/DatePicker/DatePicker.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import DescriptionBox from '@/components/atoms/DescriptionBox/DescriptionBox.vue';

definePageMeta({
  layout: 'default',
});

const route = useRoute();
const toast = useToast();

const achievementId = route.params.id as string;

// 읽기/수정 모드 전환
const isEditMode = ref(false);

// 섹션 인터페이스
interface FormSection extends AchievementSection {
  tempId: string; // 프론트엔드에서 임시로 사용할 ID
  isEditingTitle?: boolean; // 제목 편집 모드 여부
  tempTitle?: string; // 편집 중인 임시 제목
  showHelp?: boolean; // help description 표시 여부
}

// 폼 데이터
interface FormData {
  title: string;
  orgName: string;
  roleTitle: string;
  durationStart: string;
  durationEnd: string;
  workType: string;
  contributionLevel: string;
  skills: string;
  goalSummary: string;
  impactSummary: string;
  sections: FormSection[];
}

const formData = ref<FormData>({
  title: '',
  orgName: '',
  roleTitle: '',
  durationStart: '',
  durationEnd: '',
  workType: '',
  contributionLevel: '',
  skills: '',
  goalSummary: '',
  impactSummary: '',
  sections: [],
});

// 원본 데이터 백업 (취소 시 복원용)
const originalData = ref<FormData | null>(null);

// 섹션 카운터
let sectionCounter = 0;

// Select 옵션
const sectionKindOptions = computed<TSelectItem[]>(() => {
  return Object.entries(SECTION_KIND_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

const workTypeOptions = computed<TSelectItem[]>(() => {
  return Object.entries(WORK_TYPE_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

const contributionLevelOptions = computed<TSelectItem[]>(() => {
  return Object.entries(CONTRIBUTION_LEVEL_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

// 섹션 관련 함수
const addSection = () => {
  formData.value.sections.push({
    tempId: `section_${Date.now()}_${sectionCounter++}`,
    kind: AchievementSectionKind.NONE,
    title: '',
    content: '',
    sortOrder: formData.value.sections.length,
    isEditingTitle: false,
    tempTitle: '',
    showHelp: false,
  });
};

const removeSection = (index: number) => {
  formData.value.sections.splice(index, 1);
  updateSortOrder();
};

const updateSortOrder = () => {
  formData.value.sections.forEach((section, index) => {
    section.sortOrder = index;
  });
};

const isDefaultTitle = (section: FormSection, index: number): boolean => {
  return !section.title || section.title === `블록 ${index + 1}`;
};

const startEditSectionTitle = (index: number) => {
  const section = formData.value.sections[index];
  section.tempTitle = section.title || `블록 ${index + 1}`;
  section.isEditingTitle = true;
};

const applySectionTitle = (index: number) => {
  const section = formData.value.sections[index];
  section.title = section.tempTitle || '';
  section.tempTitle = '';
  section.isEditingTitle = false;
};

const cancelSectionTitleEdit = (index: number) => {
  const section = formData.value.sections[index];
  section.tempTitle = '';
  section.isEditingTitle = false;
};

const getSectionHelp = (kind: string): string => {
  const kindKey = kind as AchievementSectionKind;
  return SECTION_KIND_INFO[kindKey]?.help || '내용을 입력하세요';
};

const getSectionDescription = (kind: string): string => {
  const kindKey = kind as AchievementSectionKind;
  return SECTION_KIND_INFO[kindKey]?.description || '';
};

const toggleSectionHelp = (index: number) => {
  formData.value.sections[index].showHelp = !formData.value.sections[index].showHelp;
};

const onSectionKindChange = (index: number) => {
  console.log(`Section ${index} kind changed to:`, formData.value.sections[index].kind);
};

// 데이터 로드
onMounted(async () => {
  const { data, error } = await fetchAchievement(achievementId);

  if (error) {
    console.error('성과 조회 실패:', error);
    toast.error('성과를 불러오는데 실패했습니다.');
    return;
  }

  if (data) {
    // 폼 데이터 초기화
    formData.value = {
      title: data.title,
      orgName: data.orgName || '',
      roleTitle: data.roleTitle || '',
      durationStart: data.durationStart,
      durationEnd: data.durationEnd || '',
      workType: data.workType || '',
      contributionLevel: data.contributionLevel || '',
      skills: data.skills || '',
      goalSummary: data.goalSummary || '',
      impactSummary: data.impactSummary || '',
      sections:
        data.sections?.map((section) => ({
          ...section,
          tempId: `section_${section.id || Date.now()}_${sectionCounter++}`,
          isEditingTitle: false,
          tempTitle: '',
          showHelp: false,
        })) || [],
    };
  }
});

// 핸들러 함수
const handleBack = () => {
  navigateTo('/career');
};

const handleEnterEditMode = () => {
  // 현재 데이터 백업
  originalData.value = { ...formData.value };
  isEditMode.value = true;
};

const handleCancelEdit = () => {
  // 원본 데이터 복원
  if (originalData.value) {
    formData.value = { ...originalData.value };
  }
  isEditMode.value = false;
  originalData.value = null;
};

const handleSave = async () => {
  // 유효성 검증
  if (!formData.value.title.trim()) {
    toast.error('제목을 입력해주세요.');
    return;
  }
  if (!formData.value.durationStart) {
    toast.error('시작일을 입력해주세요.');
    return;
  }

  // API 요청 데이터 변환
  const requestBody = {
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
  const { error } = await updateAchievement(achievementId, requestBody);

  if (error) {
    console.error('성과 수정 실패:', error);
    return;
  }

  toast.success('저장되었습니다!');
  isEditMode.value = false;
  originalData.value = null;
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/career-register-page.scss';

// 추가 스타일
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  gap: 24px;
}

.page-header-left {
  flex-shrink: 0;
}

.page-header-center {
  flex: 1;
  text-align: center;
}

.page-header-right {
  flex-shrink: 0;
  display: flex;
  gap: 12px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: transparent;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #f9fafb;
    border-color: #9ca3af;
    color: #1a1d1f;
  }
}

.edit-mode-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #2563eb;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #1e40af;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  }
}

.cancel-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: transparent;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #f9fafb;
    border-color: #9ca3af;
  }
}

.save-button-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #10b981;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #059669;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  }
}
</style>
