<template>
  <DocumentModal
    v-model="isOpen"
    name="AI 가이드라인"
    title="AI 분석 서비스 이용 가이드 및 유의사항"
    max-width="700"
  >
    <div class="ai-help-content">
      <div class="intro-section">
        <p>
          성공적인 커리어 정리를 위해 AI가 제안하는 분석 결과는 강력한 도구이지만, 생성형 AI의 특성을 이해하고 활용하는 것이 무엇보다 중요합니다. 
          <br>분석 결과를 경험 및 이력서에 반영하시기 전, 아래 내용을 반드시 확인해 주세요.
        </p>
      </div>

      <section class="help-section">
        <h3>1. AI 분석의 특성: "매번 다른 제안을 합니다"</h3>
        <p>AI는 고정된 정답을 출력하는 계산기가 아니라, 확률에 기반해 문장을 생성하는 모델입니다.</p>
        <ul>
          <li><strong>비확정성:</strong> 동일한 경험 내용을 입력하더라도 요청할 때마다 문장의 구조, 단어 선택, 강조점이 달라질 수 있습니다.</li>
          <li><strong>다양한 관점:</strong> 여러 번 시도하여 본인의 가치관과 가장 잘 맞는 표현을 선택하는 것이 좋습니다.</li>
        </ul>
      </section>

      <section class="help-section">
        <h3>2. 사용자의 역할: "최종 편집자는 당신입니다"</h3>
        <p>AI 분석 결과의 최종 수용 여부는 전적으로 사용자 본인에게 있습니다.</p>
        <ul>
          <li><strong>사실 중심 기록:</strong> AI가 화려한 미사여구를 제안하더라도, 오직 본인의 실제 경험과 사실에 기반한 내용만 이력서에 담으시기 바랍니다.</li>
          <li><strong>주체적 편집:</strong> AI의 제안은 '초안'일 뿐입니다. 이를 바탕으로 본인의 목소리를 입혀 다듬을 때 가장 진정성 있는 이력서가 완성됩니다.</li>
        </ul>
      </section>

      <section class="help-section">
        <h3>3. 환각(Hallucination) 현상</h3>
        <p>AI는 문맥상 자연스러운 문장을 만들기 위해, 때때로 실제 사실과 다른 내용을 마치 사실인 것처럼 그럴듯하게 꾸며낼 수 있습니다.</p>
        <ul>
          <li>존재하지 않는 기술 스택을 언급하거나, 프로젝트의 성격을 오해하여 설명하는 경우가 발생할 수 있습니다.</li>
          <li>AI의 답변 중 본인의 실제 업무 내용과 다른 부분이 있는지 세밀한 검토가 필요합니다.</li>
        </ul>
         <div class="ai-warning-box">
          <v-icon size="20" class="mr-2" color="warning">mdi-alert</v-icon>
          <span>
            <strong>권장사항:</strong> 필수값(제목, 목표, 성과, 역할 등)을 정확히 입력해서 AI에게 정확한 정보를 전달하는게 중요합니다.
          </span>
        </div>
      </section>

      <section class="help-section">
        <h3 class="text-alert">4. 수치화 및 정량적 지표에 대한 경고 (중요!)</h3>
        <p>본 서비스의 AI는 정량적 지표를 기반으로 하는 <strong>'수치(Metrics) 기반 설명'</strong>을 시도하도록 설계되어 있습니다. <br>이 과정에서 다음과 같은 오류가 발생할 수 있습니다.</p>
        <ul>
          <li><strong>억지 끼워 맞추기:</strong> 실제 데이터가 없음에도 "성능 20% 향상", "비용 10% 절감"과 같이 임의의 수치를 생성할 가능성이 있습니다.</li>
          <li><strong>수치의 왜곡:</strong> 사용자가 입력한 기초 데이터를 잘못 계산하여 엉뚱한 결과값을 제시할 수 있습니다.</li>
        </ul>
        <div class="ai-warning-box">
          <v-icon size="20" class="mr-2" color="warning">mdi-alert</v-icon>
          <span>
            <strong>주의:</strong> AI가 제안한 수치가 본인의 실제 성과와 일치하는지 반드시 확인하세요. 
            근거가 없거나 확인되지 않은 수치를 이력서에 기재하는 것은 신뢰도에 치명적일 수 있습니다.
          </span>
        </div>
      </section>
    </div>

    <template #actions>
      <Button
        :variant="ButtonVariant.Primary"
        :size="CommonSize.Medium"
        @click="isOpen = false"
      >
        확인했습니다
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
.ai-help-content {
  .intro-section {
    padding: 16px;
    background: var(--bg-tertiary);
    border-radius: 12px;
    margin-bottom: 32px;
    
    p {
      color: var(--text-secondary);
      line-height: 1.6;
      font-size: 14px;
      margin: 0;
    }
  }

  .help-section {
    margin-bottom: 32px;

    h3 {
      font-size: 17px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 12px !important;
      
      &.text-alert {
        color: #ef4444; // Red color for warning title
      }
    }

    p {
      color: var(--text-secondary);
      line-height: 1.7;
      margin-bottom: 12px;
    }
  }

  ul {
    list-style-type: disc;
    padding-left: 20px;
    
    li {
      margin-bottom: 8px;
      color: var(--text-secondary);
      line-height: 1.6;
      
      strong {
        color: var(--text-primary);
        font-weight: 600;
      }
    }
  }

  .ai-warning-box {
    margin-top: 16px;
    padding: 16px;
    background: rgba(245, 158, 11, 0.08);
    border: 1px solid rgba(245, 158, 11, 0.4);
    border-radius: 12px;
    display: flex;
    align-items: flex-start;
    color: #b45309; // Darker warning text color
    font-size: 13.5px;
    line-height: 1.6;

    .v-icon {
      flex-shrink: 0;
      margin-top: 2px;
    }
    
    strong {
      font-weight: 700;
    }
  }
}

// Dark mode adjustments
:global(.dark-mode) {
  .ai-warning-box {
    background: rgba(245, 158, 11, 0.15);
    color: #fbbf24;
    border-color: rgba(245, 158, 11, 0.3);
  }
}
</style>