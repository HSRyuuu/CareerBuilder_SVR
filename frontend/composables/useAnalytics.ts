/**
 * Analytics Composable
 * 
 * PostHog과 Mixpanel을 모두 지원하는 이벤트 트래킹 시스템
 * 타입 안전한 이벤트 추적을 제공합니다.
 */

import type {
    AnalyticsEvents,
    EventName,
    EventProperties,
    AnalyticsProvider,
    CommonEventProperties,
} from '@/constants/analytics/event-schema';

// ============================================
// Analytics Provider Interface
// ============================================
interface IAnalyticsProvider {
    name: AnalyticsProvider;
    init(config: AnalyticsConfig): void;
    track<T extends EventName>(event: T, properties: EventProperties<T>): void;
    identify(userId: string, traits?: Record<string, unknown>): void;
    reset(): void;
    setUserProperties(properties: Record<string, unknown>): void;
}

// ============================================
// Configuration
// ============================================
export type AnalyticsConfig = {
    posthog?: {
        apiKey: string;
        apiHost?: string;
    };
    mixpanel?: {
        token: string;
    };
    ga4?: {
        measurementId: string;
    };
    debug?: boolean;
};

// ============================================
// PostHog Provider
// ============================================
class PostHogProvider implements IAnalyticsProvider {
    name: AnalyticsProvider = 'posthog';
    private posthog: typeof window.posthog | null = null;
    private debug: boolean = false;

    init(config: AnalyticsConfig): void {
        if (!config.posthog?.apiKey) return;
        this.debug = config.debug || false;

        if (typeof window !== 'undefined' && window.posthog) {
            this.posthog = window.posthog;
            this.posthog.init(config.posthog.apiKey, {
                api_host: config.posthog.apiHost || 'https://us.i.posthog.com',
                capture_pageview: true,
                capture_pageleave: true,
                autocapture: true,
            });
        }
    }

    track<T extends EventName>(event: T, properties: EventProperties<T>): void {
        if (this.debug) {
            console.log('[PostHog] Track:', event, properties);
        }
        this.posthog?.capture(event, properties as Record<string, unknown>);
    }

    identify(userId: string, traits?: Record<string, unknown>): void {
        if (this.debug) {
            console.log('[PostHog] Identify:', userId, traits);
        }
        this.posthog?.identify(userId, traits);
    }

    reset(): void {
        this.posthog?.reset();
    }

    setUserProperties(properties: Record<string, unknown>): void {
        this.posthog?.setPersonProperties(properties);
    }
}

// ============================================
// Mixpanel Provider
// ============================================
class MixpanelProvider implements IAnalyticsProvider {
    name: AnalyticsProvider = 'mixpanel';
    private mixpanel: typeof window.mixpanel | null = null;
    private debug: boolean = false;

    init(config: AnalyticsConfig): void {
        if (!config.mixpanel?.token) return;
        this.debug = config.debug || false;

        if (typeof window !== 'undefined' && window.mixpanel) {
            this.mixpanel = window.mixpanel;
            this.mixpanel.init(config.mixpanel.token, {
                track_pageview: true,
                persistence: 'localStorage',
            });
        }
    }

    track<T extends EventName>(event: T, properties: EventProperties<T>): void {
        if (this.debug) {
            console.log('[Mixpanel] Track:', event, properties);
        }
        this.mixpanel?.track(event, properties as Record<string, unknown>);
    }

    identify(userId: string, traits?: Record<string, unknown>): void {
        if (this.debug) {
            console.log('[Mixpanel] Identify:', userId, traits);
        }
        this.mixpanel?.identify(userId);
        if (traits) {
            this.mixpanel?.people.set(traits);
        }
    }

    reset(): void {
        this.mixpanel?.reset();
    }

    setUserProperties(properties: Record<string, unknown>): void {
        this.mixpanel?.people.set(properties);
    }
}

// ============================================
// GA4 Provider
// ============================================
class GA4Provider implements IAnalyticsProvider {
    name: AnalyticsProvider = 'ga4';
    private debug: boolean = false;
    private measurementId: string = '';

    init(config: AnalyticsConfig): void {
        if (!config.ga4?.measurementId) return;
        this.debug = config.debug || false;
        this.measurementId = config.ga4.measurementId;

        // GA4 plugin에서 이미 초기화됨
        if (this.debug) {
            console.log('[GA4] Provider initialized with ID:', this.measurementId);
        }
    }

    track<T extends EventName>(event: T, properties: EventProperties<T>): void {
        if (typeof window === 'undefined' || !window.gtag) return;

        if (this.debug) {
            console.log('[GA4] Track:', event, properties);
        }

        // GA4 이벤트 전송
        window.gtag('event', event, properties as Record<string, unknown>);
    }

    identify(userId: string, traits?: Record<string, unknown>): void {
        if (typeof window === 'undefined' || !window.gtag) return;

        if (this.debug) {
            console.log('[GA4] Identify:', userId, traits);
        }

        // GA4 사용자 ID 설정
        window.gtag('config', this.measurementId, {
            user_id: userId,
            ...traits,
        });
    }

    reset(): void {
        // GA4는 사용자 리셋 기능이 제한적
        if (this.debug) {
            console.log('[GA4] Reset called (limited support in GA4)');
        }
    }

    setUserProperties(properties: Record<string, unknown>): void {
        if (typeof window === 'undefined' || !window.gtag) return;

        window.gtag('set', 'user_properties', properties);
    }
}

// ============================================
// Analytics Manager
// ============================================
class AnalyticsManager {
    private providers: IAnalyticsProvider[] = [];
    private initialized: boolean = false;
    private debug: boolean = false;
    private commonProperties: CommonEventProperties = {};

    init(config: AnalyticsConfig): void {
        if (this.initialized) return;
        this.debug = config.debug || false;

        // PostHog 초기화
        if (config.posthog?.apiKey) {
            const posthog = new PostHogProvider();
            posthog.init(config);
            this.providers.push(posthog);
        }

        // Mixpanel 초기화
        if (config.mixpanel?.token) {
            const mixpanel = new MixpanelProvider();
            mixpanel.init(config);
            this.providers.push(mixpanel);
        }

        // GA4 초기화
        if (config.ga4?.measurementId) {
            const ga4 = new GA4Provider();
            ga4.init(config);
            this.providers.push(ga4);
        }

        this.initialized = true;

        if (this.debug) {
            console.log('[Analytics] Initialized with providers:', this.providers.map((p) => p.name));
        }
    }

    track<T extends EventName>(event: T, properties: EventProperties<T>): void {
        const enrichedProperties = {
            ...this.commonProperties,
            ...properties,
            timestamp: new Date().toISOString(),
        };

        // 모든 프로바이더에 이벤트 전송
        this.providers.forEach((provider) => {
            provider.track(event, enrichedProperties as EventProperties<T>);
        });

        // 디버그 모드: 콘솔 로그
        if (this.debug && this.providers.length === 0) {
            console.log('[Analytics Debug] Track:', event, enrichedProperties);
        }
    }

    identify(userId: string, traits?: Record<string, unknown>): void {
        this.providers.forEach((provider) => {
            provider.identify(userId, traits);
        });

        if (this.debug) {
            console.log('[Analytics] Identify:', userId, traits);
        }
    }

    reset(): void {
        this.providers.forEach((provider) => {
            provider.reset();
        });
        this.commonProperties = {};
    }

    setUserProperties(properties: Record<string, unknown>): void {
        this.providers.forEach((provider) => {
            provider.setUserProperties(properties);
        });
    }

    setCommonProperties(properties: CommonEventProperties): void {
        this.commonProperties = { ...this.commonProperties, ...properties };
    }
}

// ============================================
// Singleton Instance
// ============================================
const analyticsManager = new AnalyticsManager();

// ============================================
// Composable
// ============================================
export const useAnalytics = () => {
    const config = useRuntimeConfig();

    // 초기화 (한 번만 실행)
    const initAnalytics = () => {
        analyticsManager.init({
            posthog: config.public.posthogKey
                ? {
                    apiKey: config.public.posthogKey as string,
                    apiHost: (config.public.posthogHost as string) || 'https://us.i.posthog.com',
                }
                : undefined,
            mixpanel: config.public.mixpanelToken
                ? {
                    token: config.public.mixpanelToken as string,
                }
                : undefined,
            ga4: config.public.ga4MeasurementId
                ? {
                    measurementId: config.public.ga4MeasurementId as string,
                }
                : undefined,
            debug: config.public.analyticsDebug === 'true',
        });
    };

    // 이벤트 트래킹 (타입 안전)
    const track = <T extends EventName>(event: T, properties: EventProperties<T>): void => {
        analyticsManager.track(event, properties);
    };

    // 사용자 식별
    const identify = (userId: string, traits?: Record<string, unknown>): void => {
        analyticsManager.identify(userId, traits);
    };

    // 사용자 로그아웃 시 초기화
    const reset = (): void => {
        analyticsManager.reset();
    };

    // 사용자 속성 설정
    const setUserProperties = (properties: Record<string, unknown>): void => {
        analyticsManager.setUserProperties(properties);
    };

    // 공통 속성 설정 (모든 이벤트에 포함)
    const setCommonProperties = (properties: CommonEventProperties): void => {
        analyticsManager.setCommonProperties(properties);
    };

    return {
        initAnalytics,
        track,
        identify,
        reset,
        setUserProperties,
        setCommonProperties,
    };
};

// ============================================
// Global Type Declarations
// ============================================
declare global {
    interface Window {
        posthog?: {
            init(apiKey: string, config?: Record<string, unknown>): void;
            capture(event: string, properties?: Record<string, unknown>): void;
            identify(userId: string, traits?: Record<string, unknown>): void;
            reset(): void;
            setPersonProperties(properties: Record<string, unknown>): void;
        };
        mixpanel?: {
            init(token: string, config?: Record<string, unknown>): void;
            track(event: string, properties?: Record<string, unknown>): void;
            identify(userId: string): void;
            reset(): void;
            people: {
                set(properties: Record<string, unknown>): void;
            };
        };
    }
}
