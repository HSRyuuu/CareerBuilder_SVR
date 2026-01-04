<template>
  <div class="auth-page login-page">
    <div class="auth-card-wrapper">
      <Card class="auth-card">
        <div class="auth-header">
          <div class="auth-logo" @click="navigateTo(MENU_URLS.WELCOME)">
            <div class="auth-logo-icon">CB</div>
            <span class="auth-logo-text">CareerBuilder</span>
          </div>
        </div>

        <div class="auth-content">
          <h1 class="auth-title">환영합니다 &#128075;</h1>
          <p class="auth-desc">계정에 로그인하여 커리어 빌더를 이용해보세요.</p>
        </div>
        <br>

        <form class="auth-form" @submit.prevent="handleLogin">
          <div class="form-field">
            <label class="field-label">사용자 아이디</label>
            <Input
              v-model="loginForm.username"
              placeholder="아이디를 입력하세요"
              :size="CommonSize.Medium"
              required
            />
          </div>

          <div class="form-field">
            <label class="field-label">비밀번호</label>
            <Input
              v-model="loginForm.password"
              type="password"
              placeholder="비밀번호를 입력하세요"
              :size="CommonSize.Medium"
              required
            />
          </div>

          <div class="auth-options">
            <div class="remember-me">
              <!-- Checkbox component if available, or just raw html for now -->
              <input type="checkbox" id="remember" />
              <label for="remember">로그인 상태 유지</label>
            </div>
            <a href="#" class="forgot-password">비밀번호를 잊으셨나요?</a>
          </div>

          <Button
            type="submit"
            :variant="ButtonVariant.Primary"
            :size="CommonSize.Large"
            :round="true"
            class="auth-submit-btn"
            :loading="isLoading"
          >
            로그인
          </Button>
        </form>

        <div class="auth-divider">
          <span>또는</span>
        </div>

        <div class="social-auth-grid">
          <button class="social-btn kakao" @click="handleSocialLogin('kakao')">
            <v-icon color="#3C1E1E">mdi-comment</v-icon>
            <span>카카오 로그인</span>
          </button>
          <button class="social-btn google" @click="handleSocialLogin('google')">
            <v-icon color="#EA4335">mdi-google</v-icon>
            <span>구글 로그인</span>
          </button>
        </div>

        <div class="auth-footer">
          계정이 없으신가요?
          <NuxtLink to="/welcome/signup">회원가입</NuxtLink>
        </div>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
// 1. 외부 라이브러리 import
import { ref, reactive } from 'vue';

// 2. 프로젝트 내부 import
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';

// 3. API/Composables import
import { login } from '@/api/auth/api';
import { useAuthStore } from '@/stores/auth';

// 4. Type import
import type { TLoginRequest } from '@/api/auth/types';

// 5. 로컬 컴포넌트 import
import Card from '@/components/molecules/Card/Card.vue';
import Button from '@/components/atoms/Button/Button.vue';
import Input from '@/components/atoms/Input/Input.vue';
import { MENU_URLS } from '~/constants/menus';

// 9. Ref/Reactive 선언
definePageMeta({
  layout: 'full-page',
});

const authStore = useAuthStore();
const toast = useToast();
const isLoading = ref(false);

const loginForm = reactive<TLoginRequest>({
  username: '',
  password: '',
});

// 11. 함수 선언
const handleLogin = async () => {
  if (isLoading.value) return;
  
  isLoading.value = true;
  try {
    const { data, error } = await login(loginForm);
    
    if (error) {
      toast.error('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
      return;
    }
    
    if (data) {
      authStore.setAccessToken(data.accessToken);
      authStore.setUserInfo({
        userId: data.userInfo.id,
        userName: data.userInfo.username,
        email: data.userInfo.email,
      });
      navigateTo(MENU_URLS.HOME);
    }
  } catch (e) {
    console.error('Login Error:', e);
    toast.error('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
  } finally {
    isLoading.value = false;
  }
};

const handleSocialLogin = (platform: string) => {
  toast.info(`${platform} 로그인은 준비 중입니다.`);
};
</script>

<style lang="scss" scoped>
@use '@/styles/pages/auth-page.scss';
</style>
