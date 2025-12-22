// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-11-14',

  // Client Side Rendering 설정
  ssr: false,

  devtools: { enabled: true },

  modules: ['@pinia/nuxt', '@nuxt/eslint', 'pinia-plugin-persistedstate/nuxt'],

  // 환경 변수 설정
  runtimeConfig: {
    // 서버 전용 (private)
    geminiApiKey: process.env.GEMINI_API_KEY || '',
    openaiApiKey: process.env.OPENAI_API_KEY || '',

    // 클라이언트 공개 (public)
    public: {
      // PostHog Analytics
      posthogKey: process.env.NUXT_PUBLIC_POSTHOG_KEY || '',
      posthogHost: process.env.NUXT_PUBLIC_POSTHOG_HOST || 'https://us.i.posthog.com',

      // Mixpanel Analytics (대안)
      mixpanelToken: process.env.NUXT_PUBLIC_MIXPANEL_TOKEN || '',

      // 분석 디버그 모드
      analyticsDebug: process.env.NUXT_PUBLIC_ANALYTICS_DEBUG || 'false',

      // API Base URL
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8080',
    },
  },

  css: ['vuetify/styles'],

  build: {
    transpile: ['vuetify'],
  },

  vite: {
    ssr: {
      noExternal: ['vuetify'],
    },
  },

  typescript: {
    typeCheck: true,
    strict: true,
  },

  app: {
    head: {
      title: 'CareerBuilder',
      htmlAttrs: {
        lang: 'ko',
      },
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: 'AI 기반 커리어 빌더 서비스' },
      ],
    },
  },
});
