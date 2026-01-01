<template>
  <div class="experience-form-component">
    <div class="page-layout">
      <!-- AI 종합 분석 영역 -->
      <div v-if="hasAiAnalysis" class="ai-overall-container">
        <Card
          title="AI 분석 결과"
          icon="mdi-robot"
          icon-color="linear-gradient(135deg, #10b981 0%, #3b82f6 100%)"
        >
          <div class="ai-analysis-header">
            <div class="score-summary">
              <div class="score-circle">
                <span class="score-value">{{ aiAnalysis?.analysis.totalScore }}</span>
                <span class="score-label">점</span>
              </div>
              <div class="score-metrics">
                <div v-for="(value, key) in aiAnalysis?.analysis.scoreMetrics" :key="key" class="metric-item">
                  <span class="metric-label">{{
                    key === 'specificity' ? '구체성' :
                    key === 'resultOriented' ? '성과중심' :
                    key === 'logicalFlow' ? '논리성' : '직무적합성'
                  }}</span>
                  <v-progress-linear :model-value="value" color="primary" height="6" rounded></v-progress-linear>
                  <span class="metric-value">{{ value }}</span>
                </div>
              </div>
            </div>
            <div class="overall-feedback-box">
              <h4 class="feedback-title">종합 의견</h4>
              <p class="feedback-text">{{ aiAnalysis?.analysis.overallFeedback }}</p>
              <div class="keyword-tags">
                <v-chip
                  v-for="keyword in aiAnalysis?.analysis.recommendedKeywords"
                  :key="keyword"
                  size="small"
                  variant="flat"
                  color="blue-lighten-4"
                  class="mr-1 mb-1"
                >
                  # {{ keyword }}
                </v-chip>
              </div>
            </div>
          </div>
        </Card>
      </div>

      <!-- 왼쪽: 메인 폼 영역 (4) -->
      <div class="form-container" :class="{ 'with-ai': hasAiAnalysis }">
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
                v-model="modelValue.title"
                placeholder="경험을 한마디로 정의해주세요."
                :size="CommonSize.Medium"
                :disabled="!localIsEditMode"
              />
            </div>

            <div class="form-field full-width">
              <div class="form-row">
                <div class="form-field-inline">
                  <label class="field-label">시작 시점*</label>
                  <DatePicker
                    v-model="modelValue.periodStart"
                    type="month"
                    placeholder="시작월을 선택해주세요."
                    :disabled="!localIsEditMode"
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">종료 시점</label>
                  <DatePicker
                    v-model="modelValue.periodEnd"
                    type="month"
                    placeholder="종료월을 선택해주세요."
                    :disabled="!localIsEditMode"
                  />
                </div>
              </div>
            </div>

            <div class="form-field full-width">
              <div class="form-row">
                <div class="form-field-inline">
                  <label class="field-label">배경 / 소속 / 단체</label>
                  <Input
                    v-model="modelValue.background"
                    placeholder="프로젝트명, 회사명, 학교, 소모임 등 활동 배경"
                    :size="CommonSize.Medium"
                    :disabled="!localIsEditMode"
                  />
                </div>
                <div class="form-field-inline">
                  <label class="field-label">주요 역할</label>
                  <Input
                    v-model="modelValue.role"
                    placeholder="담당한 역할이나 직책 또는 직무"
                    :size="CommonSize.Medium"
                    :disabled="!localIsEditMode"
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

            <!-- 사용자 입력 영역 -->
            <div class="user-side">
              <div class="form-field full-width">
                <label class="field-label">업무 유형</label>
                <div class="select-with-description">
                  <Select
                    v-model="modelValue.category"
                    :items="categoryOptions"
                    placeholder="선택"
                    :size="FormSize.Compact"
                    :disabled="!localIsEditMode"
                  />
                  <DescriptionBox
                    :text="
                      modelValue.category
                        ? getcategoryDescription(modelValue.category)
                        : '업무 유형을 선택해주세요'
                    "
                  />
                </div>
              </div>
              <div class="form-field full-width">
                <label class="field-label">기여도/참여도</label>
                <div class="select-with-description">
                  <Select
                    v-model="modelValue.contributionLevel"
                    :items="contributionLevelOptions"
                    placeholder="선택"
                    :size="FormSize.Compact"
                    :disabled="!localIsEditMode"
                  />
                  <DescriptionBox
                    :text="
                      modelValue.contributionLevel
                        ? getContributionLevelDescription(modelValue.contributionLevel)
                        : '기여도를 선택해주세요'
                    "
                  />
                </div>
              </div>
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
                v-model="modelValue.skills"
                placeholder="관련 스킬을 쉼표로 구분하여 입력하세요 (예: Vue.js, TypeScript, Node.js)"
                :size="CommonSize.Medium"
                :disabled="!localIsEditMode"
              />
            </div>
          </div>
        </Card>

        <!-- 목표 블록 -->
        <Card
          title="목표"
          icon="mdi-flag-checkered"
          icon-color="linear-gradient(135deg, #10b981 0%, #059669 100%)"
        >
          <div class="form-grid" :class="{ 'split-view': hasAiAnalysis }">
            <!-- AI 영역 -->
            <div v-if="hasAiAnalysis" class="ai-side">
              <div class="ai-content-box">
                <div class="ai-badge">AI 제안</div>
                <div class="ai-feedback-box">{{ aiAnalysis?.analysis.goalFeedback }}</div>
                <div class="ai-improved-content highlight">{{ aiAnalysis?.analysis.goalImprovedContent }}</div>
              </div>
            </div>

            <!-- 사용자 입력 영역 -->
            <div class="user-side">
              <div v-if="localIsEditMode" class="form-field full-width">
                <div  class="section-kind-group">
                  <button
                    class="section-help-btn"
                    :class="{ active: showGoalHelp }"
                    @click="toggleGoalHelp"
                  >
                    <v-icon size="small">mdi-help-circle-outline</v-icon>
                    <span class="help-btn-text">Help</span>
                  </button>
                  <DescriptionBox v-if="!hasAiAnalysis" :text="GOAL_INFO.description" />
                </div>
                <Transition name="fade">
                  <div v-if="showGoalHelp && localIsEditMode" class="section-help-detail">
                    <div class="help-detail-icon">
                      <v-icon size="18">mdi-lightbulb-on-outline</v-icon>
                    </div>
                    <div class="help-detail-content">
                      {{ GOAL_INFO.help }}
                    </div>
                  </div>
                </Transition>
              </div>
              <div class="form-field full-width">
                <TextArea
                  v-model="modelValue.goalSummary"
                  placeholder="달성하고자 했던 목표를 작성하세요"
                  :rows="7"
                  :disabled="!localIsEditMode"
                />
              </div>
            </div>
          </div>
        </Card>

        <!-- 핵심 성과 블록 -->
        <Card
          title="핵심 성과"
          icon="mdi-star-circle"
          icon-color="linear-gradient(135deg, #f59e0b 0%, #d97706 100%)"
        >
          <div class="form-grid" :class="{ 'split-view': hasAiAnalysis }">
            <!-- AI 영역 -->
            <div v-if="hasAiAnalysis" class="ai-side">
              <div class="ai-content-box">
                <div class="ai-badge">AI 제안</div>
                <div class="ai-feedback-box">{{ aiAnalysis?.analysis.achievementFeedback }}</div>
                <div class="ai-improved-content highlight">{{ aiAnalysis?.analysis.achievementImprovedContent }}</div>
              </div>
            </div>

            <!-- 사용자 입력 영역 -->
            <div class="user-side">
              <div v-if="localIsEditMode" class="form-field full-width">
                <div class="section-kind-group" >
                  <button
                    class="section-help-btn"
                    :class="{ active: showAchievementsHelp }"
                    @click="toggleAchievementsHelp"
                  >
                    <v-icon size="small">mdi-help-circle-outline</v-icon>
                    <span class="help-btn-text">Help</span>
                  </button>
                  <DescriptionBox v-if="!hasAiAnalysis" :text="KEY_ACHIEVEMENTS_INFO.description" />
                </div>
                <Transition name="fade">
                  <div v-if="showAchievementsHelp && localIsEditMode" class="section-help-detail">
                    <div class="help-detail-icon">
                      <v-icon size="18">mdi-lightbulb-on-outline</v-icon>
                    </div>
                    <div class="help-detail-content">
                      {{ KEY_ACHIEVEMENTS_INFO.help }}
                    </div>
                  </div>
                </Transition>
              </div>
              <div class="form-field full-width">
                <TextArea
                  v-model="modelValue.keyAchievements"
                  placeholder="이 경험을 통해 얻은 성과와 영향을 간략히 설명하세요"
                  :rows="7"
                  :disabled="!localIsEditMode"
                />
              </div>
            </div>
          </div>
        </Card>

        <!-- 상세 블록들 (동적) -->
        <Card
          v-for="(section, index) in modelValue.sections"
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
                      <button
                        v-if="localIsEditMode"
                        class="edit-btn-header"
                        @click="startEditSectionTitle(index)"
                      >
                        <v-icon size="small">mdi-pencil</v-icon>
                      </button>
                    </div>
                    <div
                      v-if="isDefaultTitle(section, index) && localIsEditMode"
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
                      v-model="section.tempTitle"
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

              <button
                v-if="localIsEditMode"
                class="delete-btn-header"
                @click="removeSection(index)"
              >
                <v-icon size="small">mdi-delete</v-icon>
              </button>
            </div>
          </template>

          <div class="form-grid" :class="{ 'split-view': hasAiAnalysis && getAiSectionAnalysis(section.id) }">
            <!-- AI 영역 -->
            <div v-if="hasAiAnalysis && getAiSectionAnalysis(section.id)" class="ai-side">
              <div class="ai-content-box">
                <div class="ai-badge">AI 제안</div>
                <div class="ai-feedback-box">{{ getAiSectionAnalysis(section.id)?.feedback }}</div>
                <div class="ai-improved-content highlight">{{ getAiSectionAnalysis(section.id)?.improvedContent }}</div>
                
                <!-- STAR/PAR Breakdown -->
                <div
                  class="method-breakdown-container"
                  v-if="getAiSectionAnalysis(section.id)?.methodBreakdown"
                >
                  <button
                    class="breakdown-toggle-btn"
                    @click="toggleMethodBreakdown(index)"
                  >
                    <span>분석 항목 상세 보기</span>
                    <v-icon size="x-small">
                      {{ section.showMethodBreakdown ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                    </v-icon>
                  </button>
                  <Transition name="fade">
                    <div class="method-breakdown" v-if="section.showMethodBreakdown">
                      <div v-for="(v, k) in getAiSectionAnalysis(section.id)?.methodBreakdown" :key="k">
                        <div v-if="v" class="breakdown-item">
                          <span class="breakdown-label">{{ k.toUpperCase() }}:</span>
                          <span class="breakdown-value">{{ v }}</span>
                        </div>
                      </div>
                    </div>
                  </Transition>
                </div>

              </div>
            </div>

            <!-- 사용자 입력 영역 -->
            <div class="user-side">
              <div class="form-field full-width">
                <div class="section-kind-group">
                  <Select
                    v-model="section.kind"
                    :items="sectionKindOptions"
                    placeholder="블록 유형을 선택하세요"
                    :size="FormSize.Compact"
                    :disabled="!localIsEditMode"
                    @update:model-value="onSectionKindChange(index)"
                  />
                  <button
                    v-if="localIsEditMode"
                    class="section-help-btn"
                    :class="{ active: section.showHelp }"
                    @click="toggleSectionHelp(index)"
                  >
                    <v-icon size="small">mdi-help-circle-outline</v-icon>
                    <span class="help-btn-text">Help</span>
                  </button>
                  <DescriptionBox v-if="!hasAiAnalysis" :text="getSectionDescription(section.kind)" />
                </div>
                <Transition name="fade">
                  <div v-if="section.showHelp && localIsEditMode" class="section-help-detail">
                    <div class="help-detail-icon">
                      <v-icon size="18">mdi-lightbulb-on-outline</v-icon>
                    </div>
                    <div class="help-detail-content">
                      {{ getSectionHelp(section.kind) }}
                    </div>
                  </div>
                </Transition>
              </div>

              <div class="form-field full-width">
                <TextArea
                  v-model="section.content"
                  placeholder="Help 버튼을 눌러서 작성 가이드를 확인하세요"
                  :rows="7"
                  :disabled="!localIsEditMode"
                />
              </div>
            </div>
          </div>
        </Card>

        <!-- 빈 상태 -->
        <div
          v-if="modelValue.sections.length === 0 && localIsEditMode"
          class="empty-state-standalone"
        >
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
            <h3 class="sidebar-title">{{ localIsEditMode ? '편집' : '목차' }}</h3>
          </div>

          <!-- 블록 추가 버튼 -->
          <Button
            v-if="localIsEditMode"
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
                <span class="sidebar-section-title">핵심 성과</span>
              </div>
            </div>

            <div class="sidebar-section-item sidebar-section-fixed">
              <div class="sidebar-section-info">
                <v-icon size="small" color="#3b82f6">mdi-code-tags</v-icon>
                <span class="sidebar-section-title">스킬</span>
              </div>
            </div>

            <!-- 구분선 -->
            <div v-if="modelValue.sections.length > 0" class="sidebar-divider" />

            <!-- 동적 블록 목록 (Edit/View 모드 통합) -->
            <VueDraggableNext
              v-model="modelValue.sections"
              tag="div"
              handle=".sidebar-section-drag-handle"
              :animation="200"
              :disabled="!localIsEditMode"
              ghost-class="sidebar-section-ghost"
              chosen-class="sidebar-section-chosen"
              drag-class="sidebar-section-drag"
              @end="updateSortOrder"
            >
              <div
                v-for="(section, index) in modelValue.sections"
                :key="section.id"
                class="sidebar-section-item"
                :class="{ 'sidebar-section-readonly': !localIsEditMode }"
              >
                <div
                  class="sidebar-section-drag-handle"
                  :class="{ 'drag-disabled': !localIsEditMode }"
                >
                  <v-icon size="small" :color="localIsEditMode ? '#6b7280' : 'transparent'">
                    mdi-drag-vertical
                  </v-icon>
                </div>
                <div class="sidebar-section-info">
                  <span class="sidebar-section-number">{{ index + 1 }}</span>
                  <span class="sidebar-section-title">
                    {{ section.title || `블록 ${index + 1}` }}
                  </span>
                </div>
                <button
                  v-if="localIsEditMode"
                  class="sidebar-section-delete"
                  @click="removeSection(index)"
                >
                  <v-icon size="small">mdi-delete-outline</v-icon>
                </button>
              </div>
            </VueDraggableNext>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { VueDraggableNext } from 'vue-draggable-next';
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
  ExperienceSectionKind,
  SECTION_KIND_INFO,
  CATEGORY_INFO,
  CONTRIBUTION_LEVEL_INFO,
  Category,
  ExperienceFormMode,
  GOAL_INFO,
  KEY_ACHIEVEMENTS_INFO,
} from '@/types/experience-types';
import type { TExperienceFormSection, TExperienceFormData } from '@/types/experience-types';
import type { TExperienceAIAnalysisResponse } from '@/api/experience/types';

interface Props {
  modelValue: TExperienceFormData;
  mode?: ExperienceFormMode | string;
  aiAnalysis?: TExperienceAIAnalysisResponse;
}

const {
  modelValue,
  mode = ExperienceFormMode.EDIT,
  aiAnalysis = undefined,
} = defineProps<Props>();

const emit = defineEmits<{
  'update:modelValue': [TExperienceFormData];
}>();

const toast = useToast();
const localIsEditMode = computed(() => [ExperienceFormMode.REGISTER, ExperienceFormMode.EDIT, ExperienceFormMode.EDIT_WITH_AI].includes(mode as ExperienceFormMode));
const hasAiAnalysis = computed(() => !!aiAnalysis && mode === ExperienceFormMode.EDIT_WITH_AI);

// 목표/핵심 성과 Help 토글 상태
const showGoalHelp = ref(false);
const showAchievementsHelp = ref(false);

const toggleGoalHelp = () => {
  showGoalHelp.value = !showGoalHelp.value;
};

const toggleAchievementsHelp = () => {
  showAchievementsHelp.value = !showAchievementsHelp.value;
};

// AI 섹션 매핑
const getAiSectionAnalysis = (sectionId?: string) => {
  if (!aiAnalysis || !sectionId) return null;
  return aiAnalysis.sections.find((s) => s.section.id === sectionId)?.analysis;
};



// 블록 유형 옵션 생성
const sectionKindOptions = computed<TSelectItem[]>(() => {
  return Object.entries(SECTION_KIND_INFO).map(([key, value]) => ({
    title: value.display,
    value: key,
  }));
});

// 업무 유형 옵션 생성
const categoryOptions = computed<TSelectItem[]>(() => {
  return Object.entries(CATEGORY_INFO).map(([key, value]) => ({
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

let sectionCounter = 0;

const addSection = () => {
  if (modelValue.sections.length >= 10) {
    toast.error('추가 블록은 최대 10개까지만 등록 가능합니다.');
    return;
  }

  modelValue.sections.push({
    id: `new_section_${Date.now()}_${sectionCounter++}`,
    kind: ExperienceSectionKind.NONE,
    title: '',
    content: '',
    sortOrder: modelValue.sections.length,
    isEditingTitle: false,
    tempTitle: '',
    showHelp: false,
  });
};

const removeSection = (index: number) => {
  modelValue.sections.splice(index, 1);
  updateSortOrder();
};

const updateSortOrder = () => {
  modelValue.sections.forEach((section, index) => {
    section.sortOrder = index;
  });
};

const getSectionHelp = (kind: string): string => {
  const kindKey = kind as ExperienceSectionKind;
  return SECTION_KIND_INFO[kindKey]?.help || '내용을 입력하세요';
};

const getSectionDescription = (kind: string): string => {
  const kindKey = kind as ExperienceSectionKind;
  return SECTION_KIND_INFO[kindKey]?.description || '';
};

const getcategoryDescription = (category: string | null): string => {
  if (!category) return '';
  return CATEGORY_INFO[category as keyof typeof CATEGORY_INFO]?.description || '';
};

const getContributionLevelDescription = (level: string | null): string => {
  if (!level) return '';
  return CONTRIBUTION_LEVEL_INFO[level as keyof typeof CONTRIBUTION_LEVEL_INFO]?.description || '';
};

const toggleSectionHelp = (index: number) => {
  modelValue.sections[index].showHelp = !modelValue.sections[index].showHelp;
};

const toggleMethodBreakdown = (index: number) => {
  modelValue.sections[index].showMethodBreakdown = !modelValue.sections[index].showMethodBreakdown;
};

const onSectionKindChange = (index: number) => {
  // logic can be added here if needed
};

const isDefaultTitle = (section: TExperienceFormSection, index: number): boolean => {
  return !section.title || section.title === `블록 ${index + 1}`;
};

const startEditSectionTitle = (index: number) => {
  const section = modelValue.sections[index];
  section.tempTitle = section.title || `블록 ${index + 1}`;
  section.isEditingTitle = true;
};

const applySectionTitle = (index: number) => {
  const section = modelValue.sections[index];
  section.title = section.tempTitle || '';
  section.tempTitle = '';
  section.isEditingTitle = false;
};

const cancelSectionTitleEdit = (index: number) => {
  const section = modelValue.sections[index];
  section.tempTitle = '';
  section.isEditingTitle = false;
};
</script>

<style lang="scss">
@use './ExperienceForm.scss';
</style>
