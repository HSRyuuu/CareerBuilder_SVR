<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-visual">
        <h1 class="error-code">404</h1>
        <div class="error-circle u-ai-bg-soft">
          <v-icon size="64" class="u-ai-text-gradient">mdi-robot-confused-outline</v-icon>
        </div>
      </div>
      
      <div class="error-content">
        <h2 class="error-title">페이지를 찾을 수 없습니다</h2>
        <p class="error-description">
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.<br>
          입력하신 주소가 정확한지 다시 한번 확인해주세요.
        </p>
        
        <div class="error-actions">
          <Button
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Large"
            :round="true"
            icon="mdi-home"
            class="home-btn"
            @click="handleError"
          >
            홈으로 돌아가기
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useAuthStore } from '@/stores/auth';
import { MENU_URLS } from '~/constants/menus';

const props = defineProps({
  error: Object
});

const authStore = useAuthStore();

const handleError = () => {
  const redirectPath = authStore.isAuthenticated ? MENU_URLS.HOME : MENU_URLS.WELCOME;
  clearError({ redirect: redirectPath });
};
</script>

<style lang="scss" scoped>
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  padding: 20px;
  text-align: center;
}

.error-container {
  max-width: 500px;
  width: 100%;
  animation: fadeIn 0.6s ease-out;
}

.error-visual {
  position: relative;
  margin-bottom: 40px;
  
  .error-code {
    font-size: 120px;
    font-weight: 900;
    line-height: 1;
    color: var(--border-color);
    margin: 0;
    letter-spacing: -5px;
    opacity: 0.5;
  }
  
  .error-circle {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 140px;
    height: 140px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: var(--card-shadow);
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    
    .v-icon {
      animation: rotate-shake 3s infinite ease-in-out;
    }
  }
}

.error-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -1px;
}

.error-description {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 40px;
  word-break: keep-all;
}

.error-actions {
  display: flex;
  justify-content: center;
  
  .home-btn {
    min-width: 200px;
    font-size: 15px;
    font-weight: 700;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes rotate-shake {
  0% { transform: rotate(0deg); }
  25% { transform: rotate(10deg); }
  50% { transform: rotate(-10deg); }
  75% { transform: rotate(5deg); }
  100% { transform: rotate(0deg); }
}
</style>
