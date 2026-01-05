/**
 * CareerBuilder 이벤트 트래킹 스키마
 * 
 * 데이터 분석가가 사용자 여정, 퍼널 분석을 수행할 수 있도록
 * 체계적인 이벤트 구조를 정의합니다.
 */

// ============================================
// 이벤트 카테고리
// ============================================
export enum EventCategory {
    PAGE = 'page',
    USER = 'user',
    ACHIEVEMENT = 'achievement',
    CAREER = 'career',
    AI = 'ai',
    ONBOARDING = 'onboarding',
}

// ============================================
// Analytics Provider 타입
// ============================================
export type AnalyticsProvider = 'posthog' | 'mixpanel' | 'ga4';

// ============================================
// 공통 이벤트 속성
// ============================================
export type CommonEventProperties = {
    timestamp?: string;
    session_id?: string;
    user_id?: string;
    page_url?: string;
    referrer?: string;
    user_agent?: string;
};

// ============================================
// 이벤트 정의
// ============================================

// 페이지 관련 이벤트
export type PageEvents = {
    page_view: {
        page_name: string;
        page_path: string;
        referrer?: string;
        time_on_page_sec?: number;
    };
    page_leave: {
        page_name: string;
        time_spent_sec: number;
    };
};

// 사용자 관련 이벤트
export type UserEvents = {
    user_signup_started: {
        method: 'email' | 'google' | 'kakao' | 'naver';
    };
    user_signup_completed: {
        method: 'email' | 'google' | 'kakao' | 'naver';
        signup_time_sec: number;
    };
    user_login: {
        method: 'email' | 'google' | 'kakao' | 'naver';
    };
    user_logout: Record<string, never>;
    user_profile_updated: {
        updated_fields: string[];
    };
};

// 성과(Achievement) 관련 이벤트
export type AchievementEvents = {
    achievement_form_opened: {
        source: 'dashboard' | 'career_page' | 'onboarding' | 'home_cta' | 'navbar' | 'direct';
    };
    // 퍼널 분석용: 폼 단계별 완료
    achievement_form_step_completed: {
        step: number;
        step_name: 'basic_info' | 'work_info' | 'goal' | 'impact' | 'skills';
        time_spent_sec?: number;
    };
    // NSM 핵심 이벤트: 성과 생성
    achievement_created: {
        achievement_id?: string;
        word_count: number;
        section_count: number;
        has_goal: boolean;
        has_impact: boolean;
        creation_time_sec: number;
        is_first: boolean; // Activation 측정용
    };
    achievement_updated: {
        achievement_id: string;
        updated_fields: string[];
    };
    achievement_deleted: {
        achievement_id: string;
    };
    achievement_viewed: {
        achievement_id: string;
        view_duration_sec?: number;
    };
};

// AI 관련 이벤트
export type AiEvents = {
    ai_summary_requested: {
        provider: 'gemini' | 'openai';
        content_type: 'achievement' | 'career' | 'resume';
        content_length: number;
    };
    ai_summary_completed: {
        provider: 'gemini' | 'openai';
        latency_ms: number;
        success: boolean;
        token_count?: number;
    };
    ai_summary_failed: {
        provider: 'gemini' | 'openai';
        error_type: string;
        error_message?: string;
    };
    ai_keywords_extracted: {
        provider: 'gemini' | 'openai';
        keyword_count: number;
    };
};

// 온보딩 관련 이벤트 (퍼널 분석용)
export type OnboardingEvents = {
    onboarding_started: {
        entry_point: 'signup' | 'first_login' | 'manual';
    };
    onboarding_step_viewed: {
        step: number;
        step_name: string;
    };
    onboarding_step_completed: {
        step: number;
        step_name: string;
        time_spent_sec: number;
    };
    onboarding_step_skipped: {
        step: number;
        step_name: string;
    };
    onboarding_completed: {
        total_steps: number;
        total_time_sec: number;
        skipped_steps: number[];
    };
    onboarding_dropped: {
        last_step: number;
        last_step_name: string;
        drop_reason?: string;
    };
};

// 경력 관련 이벤트
export type CareerEvents = {
    career_register_started: {
        source: 'home_cta' | 'navbar' | 'direct' | 'onboarding';
    };
    career_register_completed: {
        career_count: number;
        total_years: number;
    };
    resume_generated: {
        format: 'pdf' | 'word' | 'web';
        template_id?: string;
    };
    resume_downloaded: {
        format: 'pdf' | 'word';
    };
};

// CTA 클릭 이벤트
export type CTAEvents = {
    cta_clicked: {
        cta_name: string;
        cta_location: 'home_hero' | 'navbar' | 'sidebar' | 'card' | 'footer';
        destination?: string;
    };
};

// ============================================
// 통합 이벤트 타입
// ============================================
export type AnalyticsEvents = PageEvents &
    UserEvents &
    AchievementEvents &
    AiEvents &
    OnboardingEvents &
    CareerEvents &
    CTAEvents;

// 이벤트 이름 유니온 타입
export type EventName = keyof AnalyticsEvents;

// 특정 이벤트의 속성 타입 추출
export type EventProperties<T extends EventName> = AnalyticsEvents[T];

// ============================================
// 이벤트 메타데이터 (분석용)
// ============================================
export const EVENT_METADATA: Record<EventName, { category: EventCategory; description: string }> = {
    // Page
    page_view: { category: EventCategory.PAGE, description: '페이지 조회' },
    page_leave: { category: EventCategory.PAGE, description: '페이지 이탈' },

    // User
    user_signup_started: { category: EventCategory.USER, description: '회원가입 시작' },
    user_signup_completed: { category: EventCategory.USER, description: '회원가입 완료' },
    user_login: { category: EventCategory.USER, description: '로그인' },
    user_logout: { category: EventCategory.USER, description: '로그아웃' },
    user_profile_updated: { category: EventCategory.USER, description: '프로필 수정' },

    // Achievement
    achievement_form_opened: { category: EventCategory.ACHIEVEMENT, description: '성과 작성 폼 열기' },
    achievement_form_step_completed: { category: EventCategory.ACHIEVEMENT, description: '성과 작성 단계 완료' },
    achievement_created: { category: EventCategory.ACHIEVEMENT, description: '성과 생성 (NSM 핵심)' },
    achievement_updated: { category: EventCategory.ACHIEVEMENT, description: '성과 수정' },
    achievement_deleted: { category: EventCategory.ACHIEVEMENT, description: '성과 삭제' },
    achievement_viewed: { category: EventCategory.ACHIEVEMENT, description: '성과 조회' },

    // AI
    ai_summary_requested: { category: EventCategory.AI, description: 'AI 요약 요청' },
    ai_summary_completed: { category: EventCategory.AI, description: 'AI 요약 완료' },
    ai_summary_failed: { category: EventCategory.AI, description: 'AI 요약 실패' },
    ai_keywords_extracted: { category: EventCategory.AI, description: 'AI 키워드 추출' },

    // Onboarding
    onboarding_started: { category: EventCategory.ONBOARDING, description: '온보딩 시작' },
    onboarding_step_viewed: { category: EventCategory.ONBOARDING, description: '온보딩 단계 조회' },
    onboarding_step_completed: { category: EventCategory.ONBOARDING, description: '온보딩 단계 완료' },
    onboarding_step_skipped: { category: EventCategory.ONBOARDING, description: '온보딩 단계 스킵' },
    onboarding_completed: { category: EventCategory.ONBOARDING, description: '온보딩 완료' },
    onboarding_dropped: { category: EventCategory.ONBOARDING, description: '온보딩 이탈' },

    // Career
    career_register_started: { category: EventCategory.CAREER, description: '경력 등록 시작' },
    career_register_completed: { category: EventCategory.CAREER, description: '경력 등록 완료' },
    resume_generated: { category: EventCategory.CAREER, description: '이력서 생성' },
    resume_downloaded: { category: EventCategory.CAREER, description: '이력서 다운로드' },

    // CTA
    cta_clicked: { category: EventCategory.PAGE, description: 'CTA 버튼 클릭' },
};
