/**
 * Google Analytics 4 Plugin
 *
 * GA4 SDK (gtag.js)를 초기화하고 Nuxt 앱에 통합합니다.
 */

export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig();
    const measurementId = config.public.ga4MeasurementId as string;

    // Measurement ID가 없으면 초기화하지 않음
    if (!measurementId) {
        if (import.meta.dev) {
            console.warn('[GA4] Measurement ID not configured. Skipping initialization.');
        }
        return;
    }

    // 클라이언트 사이드에서만 실행
    if (typeof window === 'undefined') {
        return;
    }

    // gtag.js 스크립트 동적 로드
    const script = document.createElement('script');
    script.async = true;
    script.src = `https://www.googletagmanager.com/gtag/js?id=${measurementId}`;
    document.head.appendChild(script);

    // gtag 초기화
    window.dataLayer = window.dataLayer || [];
    function gtag(...args: unknown[]) {
        window.dataLayer.push(args);
    }
    gtag('js', new Date());
    gtag('config', measurementId, {
        // 디버그 모드
        debug_mode: config.public.analyticsDebug === 'true',
        // 자동 페이지뷰 비활성화 (수동 제어)
        send_page_view: false,
    });

    // window 객체에 gtag 할당 (useAnalytics에서 사용)
    window.gtag = gtag;

    // 개발 환경 로그
    if (import.meta.dev) {
        console.log('[GA4] Initialized successfully with ID:', measurementId);
    }
});

// Global Type Declarations
declare global {
    interface Window {
        dataLayer: unknown[];
        gtag: (...args: unknown[]) => void;
    }
}
