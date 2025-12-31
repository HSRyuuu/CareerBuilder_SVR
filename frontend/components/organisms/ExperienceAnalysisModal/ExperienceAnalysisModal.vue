<template>
  <v-dialog
    v-model="isVisible"
    max-width="500px"
    class="experience-analysis-modal"
    @click:outside="handleClose"
  >
    <v-card class="modal-card">
      <div class="modal-header">
        <div class="header-icon">
          <v-icon color="#8b5cf6" size="28">mdi-auto-fix</v-icon>
        </div>
        <div class="header-text">
          <h3 class="modal-title">AI 분석 요청</h3>
          <p class="modal-subtitle">AI가 당신의 경험을 심층 분석합니다.</p>
        </div>
        <v-btn icon="mdi-close" variant="text" size="small" class="close-btn" @click="handleClose" />
      </div>

      <v-card-text class="modal-body">
        <div class="experience-info">
          <div class="info-label">대상 경험</div>
          <div class="info-value">{{ experience?.title }}</div>
        </div>

        <!-- 멤버십/제한 정보 섹션 (더미) -->
        <div class="usage-status-card">
          <div class="status-header">
            <div class="status-badge" :class="membershipGrade.toLowerCase()">
              {{ membershipGrade }} 멤버십
            </div>
            <div class="usage-count">
              이번 달 잔여 <span>{{ remainingCount }}</span> / {{ totalLimit }}회
            </div>
          </div>
          
          <v-progress-linear
            :model-value="(remainingCount / totalLimit) * 100"
            color="#8b5cf6"
            height="6"
            rounded
            class="usage-progress"
          />

          <div v-if="membershipGrade === 'FREE'" class="upgrade-info">
            <v-icon size="small" color="#8b5cf6">mdi-information-outline</v-icon>
            고급 분석을 위해 <span>PREMIUM</span>으로 업그레이드 해보세요!
          </div>
        </div>

        <div class="analysis-options">
          <div class="option-item">
            <div class="checkbox-area">
              <v-checkbox-btn v-model="options.deepAnalysis" color="#8b5cf6" density="compact" hide-details />
            </div>
            <div class="option-text">
              <div class="option-title">심층 역량 키워드 추출</div>
              <div class="option-desc">경험 속 숨겨진 역량을 정밀하게 분석합니다.</div>
            </div>
          </div>
          <div class="option-item" :class="{ 'disabled': membershipGrade === 'FREE' }">
            <div class="checkbox-area">
              <v-checkbox-btn v-model="options.suggestImprovedContent" color="#8b5cf6" :disabled="membershipGrade === 'FREE'" density="compact" hide-details />
            </div>
            <div class="option-text">
              <div class="option-title">AI 문장 자동 교정 <span class="premium-tag" v-if="membershipGrade === 'FREE'">PREMIUM</span></div>
              <div class="option-desc">더 전문적인 표현으로 문장을 자동 재구성합니다.</div>
            </div>
          </div>
        </div>
      </v-card-text>

      <v-card-actions class="modal-actions">
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          :round="true"
          block
          class="cancel-btn"
          @click="handleClose"
        >
          취소
        </Button>
        <Button
          :variant="ButtonVariant.Primary"
          :size="CommonSize.Medium"
          :round="true"
          block
          :disabled="remainingCount <= 0"
          class="confirm-btn"
          @click="handleRequest"
        >
          분석 시작하기
        </Button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import type { TExperience } from '~/api/experience/types';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

const isVisible = defineModel<boolean>({ default: false });

interface Props {
  experience: TExperience | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'request': [experience: TExperience, options: any];
}>();

// 더미 데이터: 멤버십 정보
const membershipGrade = ref('FREE'); // or 'PREMIUM'
const remainingCount = ref(3);
const totalLimit = ref(5);

const options = reactive({
  deepAnalysis: true,
  suggestImprovedContent: false,
});

const handleClose = () => {
  isVisible.value = false;
};

const handleRequest = () => {
  if (props.experience) {
    emit('request', props.experience, { ...options });
    handleClose();
  }
};
</script>

<style lang="scss" scoped>
@use './ExperienceAnalysisModal.scss';
</style>
