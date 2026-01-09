<template>
  <div class="layout-welcome">
    <!-- 상단 헤더 (필요시 추가) -->
    <header class="layout-header welcome-header">
      <div class="layout-header-left">
        <div class="layout-logo" @click="navigateTo('/')">
          <img src="/cb-logo.png" alt="CareerBuilder" class="layout-logo-img" />
          <span class="layout-logo-text">CareerBuilder</span>
        </div>
      </div>
      <div class="layout-header-right">
       
      </div>
    </header>

    <main class="layout-main-full">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useAuthStore } from '@/stores/auth';
import { MENU_URLS } from '~/constants/menus';

const authStore = useAuthStore();

</script>

<style lang="scss" scoped>
.layout-welcome {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

.layout-header {
  height: 64px;
  padding: 0 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 100;

  .layout-header-left {
    display: flex;
    align-items: center;
  }

  .layout-logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;

    .layout-logo-img {
      height: 32px;
      width: auto;
      object-fit: contain;
      border-radius: 8px;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.02);
      }
    }

    .layout-logo-text {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      letter-spacing: -0.5px;
    }
  }
}

.layout-main-full {
  flex: 1;
  width: 100%;
}
</style>
