<template>
  <div class="layout-modern">
    <!-- 상단 헤더 -->
    <header class="layout-header">
      <div class="layout-header-left">
        <div class="layout-logo" @click="navigateTo(MENU_URLS.HOME)">
          <img src="/cb-logo.png" alt="CareerBuilder" class="layout-logo-img" />
          <span class="layout-logo-text">CareerBuilder</span>
        </div>
      </div>
      <div class="layout-header-right">
        <div class="header-actions">
          <Button
            v-if="!authStore.isAuthenticated"
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Medium"
            @click="handleMockLogin"
          >
            임시 로그인
          </Button>
          <Button
            v-if="authStore.isAuthenticated"
            :variant="ButtonVariant.Danger"
            :size="CommonSize.Small"
            @click="handleMockLogout"
          >
            임시 로그아웃
          </Button>
          <Button
            v-if="authStore.isAuthenticated"
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Small"
            class="header-help-btn"
            @click="handleOpenServiceHelpDocs"
          >
            <v-icon size="16" color="var(--text-secondary)">mdi-help-circle-outline</v-icon>
            서비스 도움말
          </Button>
          <Button
            v-if="authStore.isAuthenticated"
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Small"
            class="header-help-btn"
            @click="handleOpenAiDocs"
          >
            <v-icon size="16" color="var(--text-secondary)">mdi-text-box-search-outline</v-icon>
            AI 유의사항
          </Button>
          
          <!-- AI 도움말 모달 -->
          <AiHelpDocumentModal v-model="isAiHelpModalOpen" />
          
          <!-- 서비스 도움말 모달 -->
          <ServiceHelpDocumentModal v-model="isServiceHelpModalOpen" />

          <PlanButton v-if="authStore.isAuthenticated" />
          <Button
            :variant="ButtonVariant.Secondary"
            :size="CommonSize.Medium"
            icon-only
            :icon="isDark ? 'mdi-weather-night' : 'mdi-weather-sunny'"
            class="theme-toggle-btn"
            @click="toggleTheme"
          />
          <slot name="header-controls" />
        </div>
      </div>
    </header>

    <!-- 좌측 사이드바 -->
    <aside :class="['layout-sidebar', { 'layout-sidebar--collapsed': isSidebarCollapsed }]">
      <!-- 토글 버튼 -->
      <div class="layout-sidebar-header">
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Small"
          icon-only
          :icon="isSidebarCollapsed ? 'mdi-menu' : 'mdi-menu-open'"
          class="layout-sidebar-toggle"
          @click="toggleSidebar"
        />
      </div>

      <!-- 사용자 정보 -->
      <div
        :class="['layout-sidebar-user', { 'layout-sidebar-user--collapsed': isSidebarCollapsed }]"
        @click="navigateTo(MENU_URLS.SETTING)"
      >
        <div v-if="isSidebarCollapsed" class="layout-sidebar-user-icon-only">
          <v-icon>mdi-account-circle</v-icon>
        </div>
        <template v-else>
          <div class="layout-sidebar-user-avatar">
            <v-icon>mdi-account-circle</v-icon>
          </div>
          <div class="layout-sidebar-user-info">
            <div class="layout-sidebar-user-name">{{ authStore.userName || '사용자' }}</div>
            <div class="layout-sidebar-user-email">{{ authStore.email || 'user@example.com' }}</div>
          </div>
        </template>
      </div>

      <!-- 메뉴 리스트 -->
      <nav class="layout-sidebar-nav">
        <NuxtLink
          v-for="item in menu.menuItems"
          :key="item.key"
          :to="item.path"
          class="layout-sidebar-nav-item"
          :class="{ 'layout-sidebar-nav-item--active': menu.isActive(item.key) }"
          @click="menu.select(item.key)"
        >
          <div class="layout-sidebar-nav-item-icon">
            <v-icon>{{ item.icon }}</v-icon>
          </div>
          <transition name="fade">
            <span v-if="!isSidebarCollapsed" class="layout-sidebar-nav-item-label">
              {{ item.label }}
            </span>
          </transition>
        </NuxtLink>
      </nav>
    </aside>

    <!-- 메인 콘텐츠 -->
    <main :class="['layout-main', { 'layout-main--expanded': isSidebarCollapsed }]">
      <div class="layout-content">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useMenu } from '@/composables/useMenu';
import { useAuthStore } from '@/stores/auth';
import { useUserInfo } from '@/composables/useUserInfo';
import { MENU_URLS } from '~/constants/menus';
import AiHelpDocumentModal from '@/components/page/ai-help/AiHelpDocumentModal.vue';
import ServiceHelpDocumentModal from '@/components/page/ai-help/ServiceHelpDocumentModal.vue';
import UserPlanModal from '@/components/organisms/UserPlanModal/UserPlanModal.vue';
import PlanButton from '@/components/atoms/PlanButton/PlanButton.vue';

const route = useRoute();
const menu = useMenu();
const authStore = useAuthStore();
const { user, planType, planName, fetchAll, clearUserInfo } = useUserInfo();
const colorMode = useColorMode();
const isSidebarCollapsed = ref(false);
const isAiHelpModalOpen = ref(false);
const isServiceHelpModalOpen = ref(false);

const isDark = computed(() => colorMode.value === 'dark');

const toggleTheme = () => {
  colorMode.preference = colorMode.value === 'dark' ? 'light' : 'dark';
};

// 인증 상태 변화 감지 및 프로필 로드
onMounted(async () => {
  if (authStore.isAuthenticated) {
    await fetchAll();
  }
});

// 페이지 이동 시 메뉴 상태 동기화
watch(
  () => route.path,
  (path) => {
    menu.selectByPath(path);
  },
  { immediate: true }
);

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

const handleMockLogin = () => {
  authStore.setAccessToken('mock-token');
  authStore.setUserInfo({
    userId: 'test',
    userName: '임시유저',
    email: 'test@careerbuilder.com',
  });
  navigateTo(MENU_URLS.HOME);
};

const handleOpenServiceHelpDocs = () => {
  isServiceHelpModalOpen.value = true;
};

const handleOpenAiDocs = () => {
  isAiHelpModalOpen.value = true;
};

const handleMockLogout = () => {
  authStore.clearAuth();
  clearUserInfo();
  navigateTo(MENU_URLS.WELCOME);
};
</script>

<style lang="scss" scoped>
@use '@/styles/layouts/default-layout.scss';
</style>
