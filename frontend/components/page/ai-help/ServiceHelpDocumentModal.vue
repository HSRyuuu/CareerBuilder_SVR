<template>
  <DocumentModal
    v-model="isOpen"
    name="서비스 가이드"
    title="CareerBuilder 서비스 이용 안내"
    max-width="800"
  >
    <div class="service-help-content">
      <!-- 서비스 소개 섹션 -->
      <section class="help-section intro-bg">
        <h3>CareerBuilder란?</h3>
        <p>
          당신의 소중한 경험들이 단순히 잊혀지지 않도록 기록하고, 
          <strong>AI 기술</strong>을 통해 직무 역량이 돋보이는 <strong>전문적인 커리어</strong>로 발전시키는 플랫폼입니다.
        </p>
      </section>

      <!-- 핵심 기능 섹션 -->
      <section class="help-section">
        <h3>핵심 기능 소개</h3>
        <div class="features-grid">
          <div class="feature-card">
            <div class="icon-wrapper blue">
              <v-icon color="#2563eb" size="20">mdi-pencil-plus</v-icon>
            </div>
            <h4>경험 기록</h4>
            <p>프로젝트, 업무 성과 등 모든 커리어 경험을 체계적으로 구조화하여 저장합니다.</p>
          </div>
          <div class="feature-card">
            <div class="icon-wrapper purple">
              <v-icon color="#7c3aed" size="20">mdi-robot-excited-outline</v-icon>
            </div>
            <h4>AI 분석 & 코칭</h4>
            <p>기록된 경험을 분석하여 구체성, 성과 중심 등 4대 지표 기반의 피드백을 제공합니다.</p>
          </div>
          <div class="feature-card">
            <div class="icon-wrapper green">
              <v-icon color="#10b981" size="20">mdi-chart-timeline-variant</v-icon>
            </div>
            <h4>커리어 분석</h4>
            <p>나의 성장 추이와 강점을 시각화된 데이터로 확인하고 커리어 방향성을 점검합니다.</p>
          </div>
          <div class="feature-card">
            <div class="icon-wrapper amber">
              <v-icon color="#f59e0b" size="20">mdi-file-document-check-outline</v-icon>
            </div>
            <h4>이력서 완성</h4>
            <p>분석된 경험들을 조합하여 지원 직무에 최적화된 맞춤형 이력서를 생성합니다.</p>
          </div>
        </div>
      </section>

      <!-- 이용 흐름 (Process Flow) -->
      <section class="help-section">
        <h3>서비스 이용 흐름</h3>
        <div class="process-flow-container">
          <!-- Step 1 -->
          <div class="step-item">
            <div class="step-badge">STEP 01</div>
            <div class="step-icon">
              <v-icon size="24">mdi-pencil</v-icon>
            </div>
            <div class="step-content">
              <h5>경험 등록</h5>
              <p>경험 기록</p>
            </div>
          </div>
          
          <div class="step-connector">
            <v-icon>mdi-arrow-right</v-icon>
          </div>

          <!-- Step 2 -->
          <div class="step-item">
            <div class="step-badge">STEP 02</div>
            <div class="step-icon">
              <v-icon size="24">mdi-layers-triple</v-icon>
            </div>
            <div class="step-content">
              <h5>경험 누적</h5>
              <p>데이터베이스화</p>
            </div>
          </div>

          <div class="step-connector">
            <v-icon>mdi-arrow-right</v-icon>
          </div>

          <!-- Step 3 -->
          <div class="step-item">
            <div class="step-badge">STEP 03</div>
            <div class="step-icon">
              <div class="ai-mini-tag">AI</div>
              <v-icon size="24">mdi-auto-fix</v-icon>
            </div>
            <div class="step-content">
              <h5>경험 분석</h5>
              <p>피드백 & 수정</p>
            </div>
          </div>

          <div class="step-connector">
            <v-icon>mdi-arrow-right</v-icon>
          </div>

          <!-- Step 4 -->
          <div class="step-item">
            <div class="step-badge">STEP 04</div>
            <div class="step-icon">
              <div class="ai-mini-tag">AI</div>
              <v-icon size="24">mdi-chart-box-outline</v-icon>
            </div>
            <div class="step-content">
              <h5>커리어 분석</h5>
              <p>성장/강점 파악</p>
            </div>
          </div>

          <div class="step-connector">
            <v-icon>mdi-arrow-right</v-icon>
          </div>

          <!-- Step 5 -->
          <div class="step-item">
            <div class="step-badge">STEP 05</div>
            <div class="step-icon">
              <div class="ai-mini-tag">AI</div>
              <v-icon size="24">mdi-file-document-check-outline</v-icon>
            </div>
            <div class="step-content">
              <h5>이력서 생성</h5>
              <p>맞춤형 완성</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 기대 효과 (Benefits) -->
      <section class="help-section benefit-section">
        <h3>🏆 서비스를 통해 얻을 수 있는 것</h3>
        <div class="benefit-list">
          <div class="benefit-item">
            <div class="benefit-icon">
              <v-icon color="#2563eb">mdi-book-open-page-variant</v-icon>
            </div>
            <div class="benefit-text">
              <strong>경험의 자산화</strong>
              <p>휘발되지 않고 체계적으로 정리된 경험 데이터베이스를 구축합니다.</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <v-icon color="#7c3aed">mdi-head-lightbulb-outline</v-icon>
            </div>
            <div class="benefit-text">
              <strong>객관적 역량 인지</strong>
              <p>AI의 객관적인 분석을 통해 나의 강점과 보완점을 명확히 파악합니다.</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <v-icon color="#10b981">mdi-rocket-launch-outline</v-icon>
            </div>
            <div class="benefit-text">
              <strong>언제든 준비된 상태</strong>
              <p>축적된 데이터를 바탕으로 즉시 최적의 이력서를 완성합니다.</p>
            </div>
          </div>
        </div>
      </section>
    </div>

    <template #actions>
      <Button
        :variant="ButtonVariant.Primary"
        :size="CommonSize.Medium"
        @click="isOpen = false"
      >
        시작하기
      </Button>
    </template>
  </DocumentModal>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import DocumentModal from '@/components/organisms/DocumentModal/DocumentModal.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

interface Props {
  modelValue: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  'update:modelValue': [boolean];
}>();

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});
</script>

<style lang="scss" scoped>
.service-help-content {
  .help-section {
    margin-bottom: 40px;

    &:last-child {
      margin-bottom: 0;
    }

    h3 {
      font-size: 18px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 16px !important;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    p {
      color: var(--text-secondary);
      line-height: 1.6;
      font-size: 14px;
      margin: 0;
    }
  }

  // 인트로 배경 스타일
  .intro-bg {
    background: linear-gradient(135deg, rgba(37, 99, 235, 0.05) 0%, rgba(59, 130, 246, 0.05) 100%);
    padding: 20px;
    border-radius: 12px;
    border: 1px solid rgba(37, 99, 235, 0.1);
  }

  // 기능 그리드
  .features-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    @media (max-width: 600px) {
      grid-template-columns: 1fr;
    }
  }

  .feature-card {
    padding: 20px;
    background: var(--bg-tertiary);
    border-radius: 12px;
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    }

    .icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;

      &.blue { background: rgba(37, 99, 235, 0.1); }
      &.purple { background: rgba(124, 58, 237, 0.1); }
      &.green { background: rgba(16, 185, 129, 0.1); }
      &.amber { background: rgba(245, 158, 11, 0.1); }
    }

    h4 {
      font-size: 15px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 6px;
    }

    p {
      font-size: 13px;
      color: var(--text-secondary);
      line-height: 1.5;
    }
  }

  // 프로세스 플로우
  .process-flow-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 0;
    gap: 4px;

    @media (max-width: 600px) {
      flex-direction: column;
      align-items: stretch;
      gap: 24px;

      .step-connector {
        transform: rotate(90deg);
        justify-content: center;
      }
    }
  }

  .step-connector {
    display: flex;
    align-items: center;
    color: var(--text-tertiary);
    opacity: 0.5;
  }

  .step-item {
    flex: 1;
    flex-basis: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    position: relative;

    .step-badge {
      font-size: 10px;
      font-weight: 800;
      color: var(--text-tertiary);
      margin-bottom: 8px;
    }

    .step-icon {
      width: 48px;
      height: 48px;
      border-radius: 16px;
      background: var(--bg-tertiary);
      border: 1px solid var(--border-color);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
      transition: all 0.3s ease;
      position: relative;
      
      .v-icon {
        color: var(--text-secondary);
      }
    }

    .step-content {
      h5 {
        font-size: 13px;
        font-weight: 700;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      p {
        font-size: 11px;
        color: var(--text-tertiary);
        word-break: keep-all;
      }
    }

    // AI Mini Tag
    .ai-mini-tag {
      position: absolute;
      top: -4px;
      right: -4px;
      background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
      color: white;
      font-size: 8px;
      font-weight: 800;
      padding: 1px 5px;
      border-radius: 100px;
      box-shadow: 0 2px 4px rgba(124, 58, 237, 0.3);
      z-index: 10;
      border: 1.5px solid var(--bg-surface, #fff);
    }
  }

  // Benefit Section
  .benefit-section {
    margin-top: 48px;
    padding-top: 12px;
  }

  .benefit-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .benefit-item {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
    background: var(--bg-primary); // 배경색을 primary로 변경하여 강조
    border: 1px solid var(--border-color);
    border-radius: 12px;
    
    .benefit-icon {
      margin-top: 2px;
      flex-shrink: 0;
    }

    .benefit-text {
      strong {
        display: block;
        font-size: 15px;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      p {
        font-size: 13px;
        color: var(--text-secondary);
        line-height: 1.5;
        margin: 0;
      }
    }
  }
}
// Remove old keyframes as they are not used anymore

</style>
