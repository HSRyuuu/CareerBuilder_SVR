/**
 * Mixpanel Analytics Plugin
 * 
 * Mixpanel SDK를 초기화하고 Nuxt 앱에 통합합니다.
 * PostHog의 대안 또는 보조 분석 도구로 사용할 수 있습니다.
 */

import mixpanel from 'mixpanel-browser';

export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig();
    const mixpanelToken = config.public.mixpanelToken as string;

    // Token이 없으면 초기화하지 않음
    if (!mixpanelToken) {
        console.warn('[Mixpanel] Token not configured. Skipping initialization.');
        return {
            provide: {
                mixpanel: null,
            },
        };
    }

    // Mixpanel 초기화
    mixpanel.init(mixpanelToken, {
        // 페이지뷰 자동 추적
        track_pageview: 'full-url',
        // 로컬 스토리지 사용 (쿠키 대신)
        persistence: 'localStorage',
        // EU 데이터 레지던시 사용 시 변경
        api_host: 'https://api.mixpanel.com',
        // 디버그 모드
        debug: config.public.analyticsDebug === 'true',
        // IP 기반 위치 추적
        ip: true,
        // 속성 이름 스네이크 케이스 유지
        property_blacklist: [],
    });

    // 개발 환경 로그
    if (process.env.NODE_ENV === 'development') {
        console.log('[Mixpanel] Initialized successfully');
    }

    // window 객체에 mixpanel 할당 (useAnalytics에서 사용)
    if (typeof window !== 'undefined') {
        window.mixpanel = mixpanel;
    }

    return {
        provide: {
            mixpanel,
        },
    };
});
