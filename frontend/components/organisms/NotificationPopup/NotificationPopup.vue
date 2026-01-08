<template>
  <v-menu
    v-model="isOpen"
    :close-on-content-click="false"
    location="bottom end"
    offset="10"
    transition="slide-y-transition"
  >
    <template #activator="{ props }">
      <v-badge
        :model-value="hasUnread"
        color="error"
        dot
        offset-x="3"
        offset-y="3"
      >
        <Button
          v-bind="props"
          :variant="ButtonVariant.Secondary"
          :size="CommonSize.Medium"
          icon-only
          icon="mdi-bell-outline"
          class="notification-btn"
        />
      </v-badge>
    </template>

    <v-card width="380" class="notification-popup" elevation="4">
      <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
        <span class="text-subtitle-1 font-weight-bold">알림</span>
        <Button 
          :variant="ButtonVariant.Ghost" 
          :size="CommonSize.Small"
          :disabled="!hasUnread"
          @click="handleMarkAllAsRead"
        >
          전체 읽음
        </Button>
      </v-card-title>
      <v-divider />
      
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="pa-8 text-center">
        <v-progress-circular indeterminate size="32" color="primary" />
        <div class="text-body-2 mt-2 text-medium-emphasis">불러오는 중...</div>
      </div>
      
      <!-- 알림 목록 -->
      <v-list v-else class="pa-0 notification-list" max-height="480">
        <template v-if="displayNotifications.length > 0">
          <v-list-item
            v-for="notification in displayNotifications"
            :key="notification.id"
            class="notification-item py-4 px-4 border-b"
            :class="{ 
              'notification-item--unread': !notification.isRead,
              'notification-item--read': notification.isRead 
            }"
            @click="handleNotificationClick(notification)"
          >
            <template #prepend>
              <v-avatar 
                :color="notification.isRead ? 'grey-lighten-2' : 'amber-lighten-4'" 
                size="36" 
                class="mr-3 mt-1"
                :class="{ 'opacity-60': notification.isRead }"
              >
                <v-icon 
                  :color="notification.isRead ? 'grey' : 'amber-darken-2'" 
                  size="20"
                >
                  {{ notification.isRead ? 'mdi-bell-check-outline' : 'mdi-bell-alert' }}
                </v-icon>
              </v-avatar>
            </template>
            
            <div class="d-flex flex-column gap-1 overflow-hidden">
              <v-list-item-title 
                class="text-body-2 font-weight-bold text-wrap line-clamp-1"
                :class="{ 'text-medium-emphasis': notification.isRead }"
              >
                {{ notification.title }}
              </v-list-item-title>
              <div 
                class="text-caption text-wrap line-clamp-2 mb-1"
                :class="notification.isRead ? 'text-disabled' : 'text-medium-emphasis'"
              >
                {{ notification.content }}
              </div>
              <div class="text-caption text-disabled">
                {{ formatTime(notification.createdAt) }}
              </div>
            </div>
          </v-list-item>
        </template>
        <div v-else class="pa-12 text-center text-medium-emphasis">
          <v-icon size="48" class="mb-3 opacity-20">mdi-bell-off-outline</v-icon>
          <div class="text-body-2 font-weight-medium">새로운 알림이 없습니다.</div>
          <div class="text-caption mt-1 opacity-60">중요한 소식이 있으면 알려드릴게요.</div>
        </div>
      </v-list>
      <v-divider />
      <v-card-actions class="pa-2">
        <Button 
          block 
          :variant="ButtonVariant.Ghost" 
          :size="CommonSize.Small" 
          @click="handleViewAll"
        >
          모든 알림 보기
        </Button>
      </v-card-actions>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
// 1. 외부 라이브러리 import
import { ref, onMounted, computed, watch } from 'vue';

// 2. 프로젝트 내부 import (constants, enums, utils)
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { MENU_URLS } from '~/constants/menus';
import { NotificationType } from '@/api/notification/types';

// 3. API/Composables import
import { fetchNotifications, markNotificationAsRead, markAllNotificationsAsRead } from '@/api/notification/api';
import { useAuthStore } from '@/stores/auth';

// 4. Type import
import type { TNotification, TNotificationType } from '@/api/notification/types';

// 5. 로컬 컴포넌트 import
import Button from '@/components/atoms/Button/Button.vue';

// 9. Ref/Reactive 선언
const authStore = useAuthStore();
const isOpen = ref(false);
const isLoading = ref(false);
const notifications = ref<TNotification[]>([]);

// 10. Computed 선언
const displayNotifications = computed(() => notifications.value);
const hasUnread = computed(() => notifications.value.some(n => !n.isRead));

// 11. 함수 선언

/**
 * 알림 목록 로드
 */
const loadNotifications = async () => {
  if (!authStore.isAuthenticated) return;
  
  isLoading.value = true;
  try {
    const { data, error } = await fetchNotifications({
      isRead: null,
      page: 1,
      pageSize: 20,
    });
    
    if (data && !error) {
      notifications.value = data.list;
    }
  } catch (e) {
    console.error('Failed to fetch notifications:', e);
  } finally {
    isLoading.value = false;
  }
};


/**
 * 시간 포맷팅
 */
const formatTime = (dateString: string): string => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  
  if (minutes < 1) return '방금 전';
  if (minutes < 60) return `${minutes}분 전`;
  if (hours < 24) return `${hours}시간 전`;
  if (days < 7) return `${days}일 전`;
  
  return date.toLocaleDateString('ko-KR');
};

/**
 * 알림 클릭 핸들러
 */
const handleNotificationClick = async (notification: TNotification) => {
  // 이미 읽은 알림이 아니면 서버에 읽음 처리 요청
  if (!notification.isRead) {
    try {
      await markNotificationAsRead(notification.id);
      // 로컬 데이터 상태 업데이트 (즉각적인 피드백)
      notification.isRead = true;
      notification.readAt = new Date().toISOString();
    } catch (e) {
      console.error('Failed to mark notification as read:', e);
    }
  }

  isOpen.value = false;
  if (notification.url) {
    navigateTo(notification.url);
  }
};

/**
 * 전체 읽음 처리
 */
const handleMarkAllAsRead = async () => {
  try {
    await markAllNotificationsAsRead();
    notifications.value = notifications.value.map(n => ({
      ...n,
      isRead: true,
      readAt: new Date().toISOString(),
    }));
  } catch (e) {
    console.error('Failed to mark all notifications as read:', e);
  }
};

/**
 * 모든 알림 보기 버튼 클릭 핸들러
 */
const handleViewAll = () => {
  isOpen.value = false;
  navigateTo(MENU_URLS.HOME);
};

// Life Cycle & Watchers
onMounted(() => {
  loadNotifications();
});

watch(isOpen, (newVal) => {
  if (newVal) {
    loadNotifications();
  }
});

watch(() => authStore.isAuthenticated, (isAuth) => {
  if (isAuth) {
    loadNotifications();
  } else {
    notifications.value = [];
  }
});
</script>

<style lang="scss" scoped>
@use '@/styles/components/notification-popup.scss';
</style>
