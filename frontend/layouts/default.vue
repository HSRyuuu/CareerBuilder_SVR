<template>
  <div class="layout-modern">
    <!-- 상단 헤더 -->
    <header class="layout-header">
      <div class="layout-header-left">
        <div class="layout-logo">
          <div class="layout-logo-icon">CB</div>
          <span class="layout-logo-text">CareerBuilder</span>
        </div>
      </div>
      <div class="layout-header-right">
        <slot name="header-controls" />
      </div>
    </header>

    <!-- 좌측 사이드바 -->
    <aside :class="['layout-sidebar', { 'layout-sidebar--collapsed': isSidebarCollapsed }]">
      <!-- 토글 버튼 -->
      <Button
        :variant="ButtonVariant.Secondary"
        :size="CommonSize.Small"
        icon-only
        :icon="isSidebarCollapsed ? 'mdi-menu' : 'mdi-menu-open'"
        class="layout-sidebar-toggle"
        @click="toggleSidebar"
      />

      <!-- 사용자 정보 -->
      <div
        :class="['layout-sidebar-user', { 'layout-sidebar-user--collapsed': isSidebarCollapsed }]"
      >
        <div v-if="isSidebarCollapsed" class="layout-sidebar-user-icon-only">
          <v-icon>mdi-account-circle</v-icon>
        </div>
        <template v-else>
          <div class="layout-sidebar-user-avatar">
            <v-icon>mdi-account-circle</v-icon>
          </div>
          <div class="layout-sidebar-user-info">
            <div class="layout-sidebar-user-name">사용자</div>
            <div class="layout-sidebar-user-email">user@example.com</div>
          </div>
        </template>
      </div>

      <!-- 메뉴 리스트 -->
      <nav class="layout-sidebar-nav">
        <NuxtLink
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="layout-sidebar-nav-item"
          :class="{ 'layout-sidebar-nav-item--active': isActiveRoute(item.path) }"
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
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import Button from '@/components/atoms/Button/Button.vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { useAnalytics } from '@/composables/useAnalytics';

const route = useRoute();
const isSidebarCollapsed = ref(false);

// Analytics 초기화 및 페이지뷰 추적
const { initAnalytics, track } = useAnalytics();

onMounted(() => {
  // Analytics 초기화
  initAnalytics();
  
  // 초기 페이지뷰 트래킹
  trackPageView();
});

// 라우트 변경 시 페이지뷰 자동 추적
watch(() => route.path, () => {
  trackPageView();
});

const trackPageView = () => {
  const pageName = getPageName(route.path);
  track('page_view', {
    page_name: pageName,
    page_path: route.path,
    referrer: typeof document !== 'undefined' ? document.referrer : undefined,
  });
};

const getPageName = (path: string): string => {
  const pathMap: Record<string, string> = {
    '/home': 'home',
    '/career/register': 'career_register',
  };
  return pathMap[path] || path.replace(/\//g, '_').slice(1) || 'home';
};

interface MenuItem {
  path: string;
  label: string;
  icon: string;
}

const menuItems: MenuItem[] = [
  { path: '/home', label: '홈', icon: 'mdi-home' },
  { path: '/career/register', label: '성과 등록', icon: 'mdi-file-document-edit' },
];

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

const isActiveRoute = (path: string) => {
  return route.path === path;
};
</script>

<style lang="scss" scoped>
@use '@/styles/layouts/default-layout.scss';
</style>
