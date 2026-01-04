/**
 * 플랜 유형
 */
export const PlanType = {
  BASIC: 'BASIC',
  LITE: 'LITE',
  PRO: 'PRO',
  MAX: 'MAX',
} as const;

export type TPlanType = (typeof PlanType)[keyof typeof PlanType];

/**
 * AI 프로세스 유형 (사용량 제한 항목)
 */
export const AiProcessType = {
  EXPERIENCE_ANALYSIS: 'EXPERIENCE_ANALYSIS',
  RESUME: 'RESUME',
  CAREER: 'CAREER',
  INTERVIEW_GEN: 'INTERVIEW_GEN',
} as const;

export type TAiProcessType = (typeof AiProcessType)[keyof typeof AiProcessType];
