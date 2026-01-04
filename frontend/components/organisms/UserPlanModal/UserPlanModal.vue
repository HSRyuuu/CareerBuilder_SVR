<template>
  <v-dialog v-model="isVisible" max-width="480" class="user-plan-modal">
    <Card class="modal-card">
      <div class="modal-header">
        <div class="title-area">
          <div class="main-title">
            <v-icon color="var(--primary-color)" class="u-ai-text-gradient">mdi-lightning-bolt</v-icon>
            <h3>나의 구독 및 사용량</h3>
          </div>
          <p class="sub-desc">현재 이용 중인 플랜의 혜택과 잔여 AI 분석 횟수를 확인하세요.</p>
        </div>
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Small"
          icon-only
          icon="mdi-close"
          class="close-btn"
          @click="isVisible = false"
        />
      </div>

      <div class="modal-body">
        <!-- 사용량 카드 (모든 정보 표시) -->
        <UserUsageCard 
          show-experience 
          show-career 
          show-resume 
        />
      </div>

      <div class="modal-footer">
        <Button
          :variant="ButtonVariant.Primary"
          :size="CommonSize.Medium"
          :block="true"
          @click="handleGoToManagePlan"
        >
          플랜 변경
        </Button>
      </div>
    </Card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { MENU_URLS } from '~/constants/menus';

// 로컬 컴포넌트
import Card from '@/components/molecules/Card/Card.vue';
import Button from '@/components/atoms/Button/Button.vue';
import UserUsageCard from '@/components/organisms/UserUsageCard/UserUsageCard.vue';

const isVisible = defineModel<boolean>({ default: false });

const { user, usage, planType, planName, isLoading, fetchAll } = useUserInfo();

// 모살이 열릴 때마다 최신 정보 가져오기
watch(isVisible, (val) => {
  if (val) {
    fetchAll();
  }
});

const handleGoToManagePlan = () => {
  isVisible.value = false;
  navigateTo(MENU_URLS.MANAGE_PLAN);
};
</script>

<style lang="scss" scoped>
@use './UserPlanModal.scss';
</style>
