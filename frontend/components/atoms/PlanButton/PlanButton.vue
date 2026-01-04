<template>
  <div class="plan-button-wrapper">
    <div
      class="u-plan-chip"
      :class="planType.toLowerCase()"
      @click="isModalOpen = true"
    >
      <v-icon size="16">mdi-lightning-bolt</v-icon>
      <span class="chip-text">{{ planName }}</span>
    </div>

    <!-- 플랜 상세 모달 -->
    <UserPlanModal v-model="isModalOpen" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useUserInfo } from '~/composables/useUserInfo';
import UserPlanModal from '@/components/organisms/UserPlanModal/UserPlanModal.vue';

const { planType, planName } = useUserInfo();
const isModalOpen = ref(false);

const openModal = () => {
  isModalOpen.value = true;
};

defineExpose({
  openModal,
});
</script>

<style lang="scss" scoped>
// 공통 스타일은 _ai-ui.scss에 정의되어 있으므로 여기서는 추가 정렬만 처리
.u-plan-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  // 필요 시 명시적으로 스타일 보정
}
</style>
