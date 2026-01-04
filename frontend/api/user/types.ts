import type { TPlanType, TAiProcessType } from '~/types/ai-plan-types';

export type TSubscriptionStatus = 'ACTIVE' | 'EXPIRED' | 'PAUSED';

export type TPlan = {
  name: string;
  planType: TPlanType;
  price: number;
  experienceAnalysisModel: string;
  experienceAnalysisLimitPerDay: number;
  careerAnalysisModel: string;
  careerAnalysisLimitPerMonth: number;
  resumeModel: string;
  resumeLimitPerMonth: number;
};

export type TSubscription = {
  status: TSubscriptionStatus;
  startedAt: string;
  expiredAt: string | null;
  plan: TPlan;
};

export type TUser = {
  id: string;
  email: string;
  username: string;
  subscription: TSubscription;
};

export type TPeriod = 'DAY' | 'WEEK' | 'MONTH' | 'YEAR';

export type TUsageDetail = {
  limit: number;
  current: number;
  period: TPeriod;
};

export type TUserUsage = {
  usageSummary: Partial<Record<TAiProcessType, TUsageDetail>>;
};
