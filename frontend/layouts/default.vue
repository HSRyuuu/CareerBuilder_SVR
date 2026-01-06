<template>
  <div class="layout-modern">
    <!-- 상단 헤더 -->
    <header class="layout-header">
      <div class="layout-header-left">
        <div class="layout-logo" @click="navigateTo(MENU_URLS.HOME)">
          <img src="/cb-logo.png" alt="CareerBuilder" class="layout-logo-img" />
          <span class="layout-logo-text">CareerBuilder</span>
        </div>
        <Button
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Small"
          class="layout-sidebar-toggle-btn"
          @click="toggleSidebar"
        >
          <img 
            src="/icons/side-bar.png" 
            alt="Toggle Sidebar" 
            :class="['sidebar-toggle-icon', { 'sidebar-toggle-icon--collapsed': isSidebarCollapsed }]"
          />
        </Button>
        <Button 
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          icon-only
          :icon="isDark ? 'mdi-weather-night' : 'mdi-weather-sunny'"
          class="theme-toggle-header-btn"
          @click="toggleTheme"
        />
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
          
          <!-- AI 도움말 모달 -->
          <AiHelpDocumentModal v-model="isAiHelpModalOpen" />
          
          <!-- 서비스 도움말 모달 -->
          <ServiceHelpDocumentModal v-model="isServiceHelpModalOpen" />

          <PlanButton v-if="authStore.isAuthenticated" />
          
          <!-- 알림 영역 -->
          <v-menu
            v-model="isNotificationOpen"
            :close-on-content-click="false"
            location="bottom end"
            offset="10"
          >
            <template #activator="{ props }">
              <Button
                v-bind="props"
                :variant="ButtonVariant.Secondary"
                :size="CommonSize.Medium"
                icon-only
                icon="mdi-bell-outline"
                class="notification-btn"
              />
            </template>

            <v-card width="320" class="notification-dropdown">
              <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
                <span class="text-subtitle-1 font-weight-bold">알림</span>
                <v-btn variant="text" size="small" color="primary" @click="clearAllNotifications">전체 읽음</v-btn>
              </v-card-title>
              <v-divider />
              <v-list class="pa-0" max-height="400">
                <template v-if="notifications.length > 0">
                  <v-list-item
                    v-for="(n, i) in notifications"
                    :key="i"
                    :subtitle="n.time"
                    class="py-3 border-b"
                    @click="handleNotificationClick(n)"
                  >
                    <template #prepend>
                      <v-avatar :color="n.color" size="32" class="mr-3">
                        <v-icon color="white" size="18">{{ n.icon }}</v-icon>
                      </v-avatar>
                    </template>
                    <v-list-item-title class="text-body-2 font-weight-medium text-wrap">
                      {{ n.message }}
                    </v-list-item-title>
                  </v-list-item>
                </template>
                <div v-else class="pa-8 text-center text-medium-emphasis">
                  <v-icon size="48" class="mb-2 opacity-20">mdi-bell-off-outline</v-icon>
                  <div class="text-body-2">새로운 알림이 없습니다.</div>
                </div>
              </v-list>
              <v-divider />
              <v-card-actions class="pa-2">
                <v-btn block variant="text" size="small" @click="navigateTo(MENU_URLS.HOME)">모든 알림 보기</v-btn>
              </v-card-actions>
            </v-card>
          </v-menu>

          <slot name="header-controls" />
        </div>
      </div>
    </header>

    <!-- 좌측 사이드바 -->
    <aside :class="['layout-sidebar', { 'layout-sidebar--collapsed': isSidebarCollapsed }]">

      <!-- 사용자 정보 -->
      <div
        v-if="!isSidebarCollapsed"
        class="layout-sidebar-user"
        @click="navigateTo(MENU_URLS.SETTING)"
      >
        <div class="layout-sidebar-user-avatar">
          <v-icon size="24">mdi-account-circle</v-icon>
        </div>
        <div class="layout-sidebar-user-info">
          <div class="layout-sidebar-user-name">{{ authStore.userName || '사용자' }}</div>
          <div class="layout-sidebar-user-email">{{ authStore.email || 'user@example.com' }}</div>
        </div>
      </div>
      <div 
        v-else 
        class="layout-sidebar-nav-item layout-sidebar-user--active-navy"
        style="margin-bottom: 20px;"
        @click="navigateTo(MENU_URLS.SETTING)"
      >
        <div class="layout-sidebar-nav-item-icon">
          <v-icon size="20">mdi-account-circle</v-icon>
        </div>
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
          <span class="layout-sidebar-nav-item-label">
            {{ item.label }}
          </span>
        </NuxtLink>
      </nav>

      <!-- 사이드바 푸터 -->
      <div v-if="authStore.isAuthenticated" class="layout-sidebar-footer">
        <div 
          class="layout-sidebar-nav-item" 
          @click="handleOpenServiceHelpDocs"
        >
          <div class="layout-sidebar-nav-item-icon">
            <v-icon size="20">mdi-help-circle-outline</v-icon>
          </div>
          <span class="layout-sidebar-nav-item-label">
            서비스 도움말
          </span>
        </div>
        <div 
          class="layout-sidebar-nav-item" 
          @click="handleOpenAiDocs"
        >
          <div class="layout-sidebar-nav-item-icon">
            <v-icon size="20">mdi-text-box-search-outline</v-icon>
          </div>
          <span class="layout-sidebar-nav-item-label">
            AI 유의사항
          </span>
        </div>
      </div>
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
const isNotificationOpen = ref(false);

const notifications = ref([
  {
    message: '새로운 분석 완료! 귀하의 경험이 성공적으로 분석되었습니다.',
    time: '2분 전',
    icon: 'mdi-auto-fix',
    color: 'primary',
  },
  {
    message: '회원가입을 환영합니다! CareerBuilder와 함께 성장해보세요.',
    time: '1시간 전',
    icon: 'mdi-party-popper',
    color: 'success',
  },
  {
    message: '구독 플랜이 곧 만료됩니다. 연장하여 혜택을 계속 누리세요.',
    time: '24시간 전',
    icon: 'mdi-alert-circle-outline',
    color: 'warning',
  },
]);

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

const handleOpenNotifications = () => {
  isNotificationOpen.value = !isNotificationOpen.value;
};

const handleNotificationClick = (notification: any) => {
  console.log('Notification clicked:', notification);
  isNotificationOpen.value = false;
};

const clearAllNotifications = () => {
  notifications.value = [];
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
