/**
 * Analytics 사용 예시 Composable
 * 
 * 프론트엔드에서 이벤트 트래킹을 어떻게 사용하는지 보여주는 예시입니다.
 * 개발자가 이 파일을 참고하여 각 컴포넌트에서 트래킹을 구현할 수 있습니다.
 */

import { useAnalytics } from './useAnalytics';

/**
 * Achievement(성과) 관련 이벤트 트래킹 예시
 */
export const useAchievementTracking = () => {
    const { track, identify } = useAnalytics();

    /**
     * 성과 작성 폼 열기 이벤트
     */
    const trackFormOpened = (source: 'dashboard' | 'career_page' | 'onboarding' | 'home_cta' | 'navbar' | 'direct') => {
        track('achievement_form_opened', { source });
    };

    /**
     * 성과 생성 이벤트 (NSM 핵심 이벤트)
     */
    const trackCreated = (params: {
        wordCount: number;
        sectionCount: number;
        hasGoal: boolean;
        hasImpact: boolean;
        creationTimeSec: number;
        isFirst: boolean;
        achievementId?: string;
    }) => {
        track('achievement_created', {
            achievement_id: params.achievementId,
            word_count: params.wordCount,
            section_count: params.sectionCount,
            has_goal: params.hasGoal,
            has_impact: params.hasImpact,
            creation_time_sec: params.creationTimeSec,
            is_first: params.isFirst,
        });
    };

    /**
     * 성과 삭제 이벤트
     */
    const trackDeleted = (achievementId: string) => {
        track('achievement_deleted', { achievement_id: achievementId });
    };

    return {
        trackFormOpened,
        trackCreated,
        trackDeleted,
    };
};

/**
 * AI 관련 이벤트 트래킹 예시
 */
export const useAiTracking = () => {
    const { track } = useAnalytics();

    /**
     * AI 요약 요청 이벤트
     */
    const trackSummaryRequested = (
        provider: 'gemini' | 'openai',
        contentType: 'achievement' | 'career' | 'resume',
        contentLength: number
    ) => {
        track('ai_summary_requested', {
            provider,
            content_type: contentType,
            content_length: contentLength,
        });
    };

    /**
     * AI 요약 완료 이벤트
     */
    const trackSummaryCompleted = (
        provider: 'gemini' | 'openai',
        latencyMs: number,
        success: boolean,
        tokenCount?: number
    ) => {
        track('ai_summary_completed', {
            provider,
            latency_ms: latencyMs,
            success,
            token_count: tokenCount,
        });
    };

    /**
     * AI 요약 실패 이벤트
     */
    const trackSummaryFailed = (
        provider: 'gemini' | 'openai',
        errorType: string,
        errorMessage?: string
    ) => {
        track('ai_summary_failed', {
            provider,
            error_type: errorType,
            error_message: errorMessage,
        });
    };

    return {
        trackSummaryRequested,
        trackSummaryCompleted,
        trackSummaryFailed,
    };
};

/**
 * 온보딩 퍼널 이벤트 트래킹 예시
 */
export const useOnboardingTracking = () => {
    const { track } = useAnalytics();

    let startTime: number | null = null;
    const stepTimes: Map<number, number> = new Map();

    /**
     * 온보딩 시작
     */
    const trackStarted = (entryPoint: 'signup' | 'first_login' | 'manual') => {
        startTime = Date.now();
        track('onboarding_started', { entry_point: entryPoint });
    };

    /**
     * 온보딩 단계 조회
     */
    const trackStepViewed = (step: number, stepName: string) => {
        stepTimes.set(step, Date.now());
        track('onboarding_step_viewed', { step, step_name: stepName });
    };

    /**
     * 온보딩 단계 완료
     */
    const trackStepCompleted = (step: number, stepName: string) => {
        const stepStartTime = stepTimes.get(step);
        const timeSpentSec = stepStartTime ? Math.round((Date.now() - stepStartTime) / 1000) : 0;

        track('onboarding_step_completed', {
            step,
            step_name: stepName,
            time_spent_sec: timeSpentSec,
        });
    };

    /**
     * 온보딩 완료
     */
    const trackCompleted = (totalSteps: number, skippedSteps: number[] = []) => {
        const totalTimeSec = startTime ? Math.round((Date.now() - startTime) / 1000) : 0;

        track('onboarding_completed', {
            total_steps: totalSteps,
            total_time_sec: totalTimeSec,
            skipped_steps: skippedSteps,
        });
    };

    /**
     * 온보딩 이탈
     */
    const trackDropped = (lastStep: number, lastStepName: string, dropReason?: string) => {
        track('onboarding_dropped', {
            last_step: lastStep,
            last_step_name: lastStepName,
            drop_reason: dropReason,
        });
    };

    return {
        trackStarted,
        trackStepViewed,
        trackStepCompleted,
        trackCompleted,
        trackDropped,
    };
};

/**
 * 사용자 인증 이벤트 트래킹 예시
 */
export const useAuthTracking = () => {
    const { track, identify, reset } = useAnalytics();

    /**
     * 회원가입 시작
     */
    const trackSignupStarted = (method: 'email' | 'google' | 'kakao' | 'naver') => {
        track('user_signup_started', { method });
    };

    /**
     * 회원가입 완료
     */
    const trackSignupCompleted = (
        userId: string,
        method: 'email' | 'google' | 'kakao' | 'naver',
        signupTimeSec: number
    ) => {
        // 사용자 식별
        identify(userId, { signup_method: method });

        track('user_signup_completed', {
            method,
            signup_time_sec: signupTimeSec,
        });
    };

    /**
     * 로그인
     */
    const trackLogin = (userId: string, method: 'email' | 'google' | 'kakao' | 'naver') => {
        identify(userId);
        track('user_login', { method });
    };

    /**
     * 로그아웃
     */
    const trackLogout = () => {
        track('user_logout', {});
        reset(); // 사용자 데이터 초기화
    };

    return {
        trackSignupStarted,
        trackSignupCompleted,
        trackLogin,
        trackLogout,
    };
};
