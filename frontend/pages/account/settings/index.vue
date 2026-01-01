<template>
  <div class="settings-page">
    <PageHeader title="사용자 설정" subtitle="개인 정보 및 애플리케이션 설정을 관리합니다." />

    <div class="settings-container">
      <!-- 사용자 프로필 섹션 -->
      <section class="settings-section">
        <h3 class="section-title">계정 정보</h3>
        <Card class="profile-card">
          <div class="profile-header">
            <div class="profile-avatar">
              <v-icon size="48">mdi-account-circle</v-icon>
            </div>
            <div class="profile-info">
              <h4 class="profile-name">{{ authStore.userName }}</h4>
              <p class="profile-email">{{ authStore.email }}</p>
            </div>
            <Button
              :variant="ButtonVariant.Secondary"
              :size="CommonSize.Small"
              @click="handleEditProfile"
            >
              프로필 수정
            </Button>
          </div>
        </Card>
      </section>

      <!-- 설정 항목 섹션 -->
      <section class="settings-section">
        <h3 class="section-title">애플리케이션 설정</h3>
        <Card class="options-card">
          <div class="setting-item">
            <div class="setting-item-info">
              <h4 class="setting-item-title">알림 설정</h4>
              <p class="setting-item-desc">AI 분석 결과가 나오면 알림을 받습니다.</p>
            </div>
            <v-switch
              v-model="isNotify"
              color="primary"
              hide-details
              inset
              @update:model-value="handleToggleNotify"
            ></v-switch>
          </div>
          <div class="setting-divider"></div>
          <div class="setting-item">
            <div class="setting-item-info">
              <h4 class="setting-item-title">다크 모드</h4>
              <p class="setting-item-desc">화면 테마를 어둡게 설정합니다.</p>
            </div>
            <v-switch v-model="isDark" color="primary" hide-details inset></v-switch>
          </div>
        </Card>
      </section>

      <!-- 구독 정보 섹션 -->
      <section class="settings-section">
        <h3 class="section-title">구독 정보</h3>
        <Card class="options-card">
          <div class="setting-item clickable" @click="navigateTo(MENU_URLS.MANAGE_PLAN)">
            <div class="setting-item-info">
              <h4 class="setting-item-title">플랜 관리</h4>
              <p class="setting-item-desc">현재 이용 중인 플랜 정보를 확인하고 업그레이드합니다.</p>
            </div>
            <div class="plan-badge">Basic</div>
            <v-icon color="var(--text-tertiary)">mdi-chevron-right</v-icon>
          </div>
        </Card>
      </section>

      <!-- 약관 및 정책 섹션 -->
      <section class="settings-section">
        <h3 class="section-title">약관 및 정책</h3>
        <Card class="options-card">
          <div class="setting-item clickable" @click="showTermsModal = true">
            <div class="setting-item-info">
              <h4 class="setting-item-title">서비스 이용약관</h4>
              <p class="setting-item-desc">서비스 이용에 관한 약관을 확인합니다.</p>
            </div>
            <v-icon color="var(--text-tertiary)">mdi-chevron-right</v-icon>
          </div>
          <div class="setting-divider"></div>
          <div class="setting-item clickable" @click="showPrivacyModal = true">
            <div class="setting-item-info">
              <h4 class="setting-item-title">개인정보 처리방침</h4>
              <p class="setting-item-desc">개인정보 수집 및 이용에 관한 정책을 확인합니다.</p>
            </div>
            <v-icon color="var(--text-tertiary)">mdi-chevron-right</v-icon>
          </div>
        </Card>
      </section>

      <!-- 위험 구역 -->
      <section class="settings-section danger-zone">
        <h3 class="section-title">계정 관리</h3>
        <Card class="danger-card">
          <div class="danger-item">
            <div class="danger-info">
              <h4 class="danger-title">로그아웃</h4>
              <p class="danger-desc">현재 기기에서 로그아웃합니다.</p>
            </div>
            <Button
              :variant="ButtonVariant.Secondary"
              :size="CommonSize.Medium"
              @click="handleLogout"
            >
              로그아웃
            </Button>
          </div>
          <div class="setting-divider"></div>
          <div class="danger-item">
            <div class="danger-info">
              <h4 class="danger-title">계정 탈퇴</h4>
              <p class="danger-desc">모든 데이터를 삭제하고 계정을 영구히 폐쇄합니다.</p>
            </div>
            <Button
              :variant="ButtonVariant.Danger"
              :size="CommonSize.Medium"
              @click="handleDeleteAccount"
            >
              계정 탈퇴
            </Button>
          </div>
        </Card>
      </section>
    </div>

    <!-- 이용약관 모달 -->
    <DocumentModal
      v-model="showTermsModal"
      :name="TERMS_OF_SERVICE.name"
      :title="TERMS_OF_SERVICE.title"
      :max-width="700"
    >
      <div class="terms-content">
        <template v-for="(section, index) in TERMS_OF_SERVICE.sections" :key="index">
          <h3>{{ section.title }}</h3>
          <p v-if="section.content">{{ section.content }}</p>
          <ul v-if="section.list">
            <li v-for="(item, idx) in section.list" :key="idx">{{ item }}</li>
          </ul>
        </template>
      </div>

      <template #actions>
        <Button :variant="ButtonVariant.Primary" @click="showTermsModal = false">
          확인
        </Button>
      </template>
    </DocumentModal>

    <!-- 개인정보 처리방침 모달 -->
    <DocumentModal
      v-model="showPrivacyModal"
      :name="PRIVACY_POLICY.name"
      :title="PRIVACY_POLICY.title"
      :max-width="700"
    >
      <div class="terms-content">
        <template v-for="(section, index) in PRIVACY_POLICY.sections" :key="index">
          <h3>{{ section.title }}</h3>
          <p v-if="section.content">{{ section.content }}</p>
          <ul v-if="section.list">
            <li v-for="(item, idx) in section.list" :key="idx">
              <template v-if="typeof item === 'string'">{{ item }}</template>
              <template v-else>
                <strong>{{ item.label }}:</strong> {{ item.value }}
              </template>
            </li>
          </ul>
        </template>
      </div>

      <template #actions>
        <Button :variant="ButtonVariant.Primary" @click="showPrivacyModal = false">
          확인
        </Button>
      </template>
    </DocumentModal>
  </div>
</template>

<script setup lang="ts">
// 1. 외부 라이브러리 import
import { ref, computed } from 'vue';

// 2. 프로젝트 내부 import
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import { TERMS_OF_SERVICE, PRIVACY_POLICY } from '@/constants/legal-documents';

// 3. API/Composables import
import { useAuthStore } from '@/stores/auth';
import { logout } from '@/api/auth/api';

// 5. 로컬 컴포넌트 import
import PageHeader from '@/components/organisms/PageHeader/PageHeader.vue';
import Card from '@/components/molecules/Card/Card.vue';
import Button from '@/components/atoms/Button/Button.vue';
import DocumentModal from '@/components/organisms/DocumentModal/DocumentModal.vue';
import { MENU_URLS } from '~/constants/menus';

// 9. Ref/Reactive 선언
definePageMeta({
  layout: 'default',
});

const authStore = useAuthStore();
const toast = useToast();
const colorMode = useColorMode();

const isNotify = ref(false);
const showTermsModal = ref(false);
const showPrivacyModal = ref(false);

const isDark = computed({
  get: () => colorMode.value === 'dark',
  set: (val) => {
    colorMode.preference = val ? 'dark' : 'light';
  },
});

// 11. 함수 선언
const handleToggleNotify = (val: boolean | null) => {
  if (val) {
    toast.info('준비중입니다.');
    // 준비 중이므로 다시 꺼둠
    nextTick(() => {
      isNotify.value = false;
    });
  }
};

const handleEditProfile = () => {
  toast.info('프로필 수정 기능은 개발 중입니다.');
};

const handleLogout = async () => {
  try {
    await logout();
    authStore.clearAuth();
    colorMode.preference = 'light';
    toast.success('로그아웃 되었습니다.');
    navigateTo(MENU_URLS.WELCOME);
  } catch (error) {
    console.error('Logout error:', error);
    // 에러가 나더라도 클라이언트 상태는 지워주는 것이 좋음
    authStore.clearAuth();
    colorMode.preference = 'light';
    navigateTo(MENU_URLS.WELCOME);
  }
};

const handleDeleteAccount = () => {
  toast.error('계정 탈퇴 기능은 현재 사용할 수 없습니다.');
};
</script>

<style lang="scss" scoped>
.settings-page {
  padding: 26px;
}

.settings-container {
  max-width: 800px;
  margin: 32px auto;
  display: flex;
  flex-direction: column;
  gap: 40px;
  padding: 0 24px 64px;
}

.settings-section {
  .section-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 16px;
    padding-left: 4px;
  }
}

.profile-card {
  .profile-header {
    display: flex;
    align-items: center;
    gap: 20px;

    @media (max-width: 640px) {
      flex-direction: column;
      text-align: center;
    }
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
    background: var(--bg-tertiary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-tertiary);
  }

  .profile-info {
    flex: 1;

    .profile-name {
      font-size: 20px;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .profile-email {
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
}

.options-card, .danger-card {
  display: flex;
  flex-direction: column;
}

.setting-item, .danger-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 0;
  
  &:first-child { padding-top: 8px; }
  &:last-child { padding-bottom: 8px; }

  &.clickable {
    cursor: pointer;
    border-radius: 8px;
    margin: 0 -12px;
    padding: 16px 12px;
    transition: all 0.2s ease;

    &:first-child { padding-top: 16px; margin-top: -8px; }
    &:last-child { padding-bottom: 16px; margin-bottom: -8px; }

    &:hover {
      background: var(--bg-tertiary);
    }
  }
}

.setting-item-info, .danger-info {
  flex: 1;
  padding-right: 24px;

  .setting-item-title, .danger-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 4px;
  }

  .setting-item-desc, .danger-desc {
    font-size: 14px;
    color: var(--text-secondary);
    line-height: 1.5;
  }
}

.plan-badge {
  padding: 4px 12px;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border-radius: 100px;
  font-size: 12px;
  font-weight: 700;
  margin-right: 8px;
}

.setting-divider {
  height: 1px;
  background: var(--border-color);
}

.danger-zone {
  .section-title {
    color: #ef4444;
  }
  
  .danger-title {
    color: var(--text-primary);
  }
}
</style>
