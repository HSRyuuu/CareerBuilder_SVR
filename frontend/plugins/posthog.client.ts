/**
 * PostHog Analytics Plugin
 * 
 * PostHog SDK를 초기화하고 Nuxt 앱에 통합합니다.
 * 클라이언트 사이드에서만 실행됩니다.
 */

import posthog from 'posthog-js';

export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig();
    const posthogKey = config.public.posthogKey as string;
    const posthogHost = (config.public.posthogHost as string) || 'https://us.i.posthog.com';

    // API Key가 없으면 초기화하지 않음
    if (!posthogKey) {
        console.warn('[PostHog] API key not configured. Skipping initialization.');
        return {
            provide: {
                posthog: null,
            },
        };
    }

    // PostHog 초기화
    posthog.init(posthogKey, {
        api_host: posthogHost,
        // 자동 페이지뷰 추적
        capture_pageview: true,
        // 페이지 이탈 추적
        capture_pageleave: true,
        // 자동 이벤트 캡처 (클릭, 입력 등)
        autocapture: true,
        // 세션 녹화 (무료 플랜: 5,000 세션/월)
        disable_session_recording: false,
        // 디버그 모드
        debug: config.public.analyticsDebug === 'true',
        // 한국어 로케일
        loaded: (ph) => {
            // 개발 환경에서는 로그 출력
            if (process.env.NODE_ENV === 'development') {
                console.log('[PostHog] Initialized successfully');
            }
        },
    });

    // window 객체에 posthog 할당 (useAnalytics에서 사용)
    if (typeof window !== 'undefined') {
        window.posthog = posthog;
    }

    return {
        provide: {
            posthog,
        },
    };
});
