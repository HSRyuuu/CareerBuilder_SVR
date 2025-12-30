/**
 * 경험(Experience) 관련 타입 정의
 */

/**
 * 경험 블록 (experience_sections 테이블)
 */
export interface ExperienceSection {
  id?: string;
  kind: ExperienceSectionKind | string;
  title: string;
  content: string;
  sortOrder?: number;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 경험 (experiences 테이블)
 */
export interface Experience {
  id?: string;
  userId?: string;
  title: string;
  background?: string;
  periodStart: string;
  periodEnd?: string;
  keyAchievements?: string;
  status?: ExperienceStatus | string;
  tags?: string;
  createdAt?: string;
  updatedAt?: string;
  sections?: ExperienceSection[];
}

/**
 * 경험 등록 폼 데이터
 */
export interface ExperienceFormData {
  title: string;
  background: string;
  periodStart: string;
  periodEnd: string;
  keyAchievements: string;
  tags: string;
  sections: ExperienceSection[];
}

/**
 * 경험 상태
 */
export enum ExperienceStatus {
  INCOMPLETE = 'INCOMPLETE', // 보완 필요
  COMPLETED = 'COMPLETED', // 작성 완료
  MODIFIED = 'MODIFIED', // 수정 완료
  AI_REQUEST = 'AI_REQUEST', // AI 요청
  ANALYZING = 'ANALYZING', // AI 분석 중
  ANALYZED = 'ANALYZED', // 분석 완료
}

/**
 * 경험 상태별 표시 정보
 */
export const STATUS_INFO: Record<ExperienceStatus, { display: string; color: string }> = {
  [ExperienceStatus.INCOMPLETE]: { display: '보완 필요', color: 'status-incomplete' },
  [ExperienceStatus.COMPLETED]: { display: '작성 완료', color: 'status-completed' },
  [ExperienceStatus.MODIFIED]: { display: '수정 완료', color: 'status-completed' },
  [ExperienceStatus.AI_REQUEST]: { display: '분석 요청됨', color: 'status-completed' },
  [ExperienceStatus.ANALYZING]: { display: 'AI 분석 중', color: 'status-analyzing' },
  [ExperienceStatus.ANALYZED]: { display: '분석 완료', color: 'status-analyzed' },
};

/**
 * 경험 통계 요약 타입
 */
export type TExperienceStatsSummary = {
  total: number;
  incomplete: number;
  completed: number;
  analyzing: number;
  analyzed: number;
};

/**
 * 업무 유형
 */
export enum Category {
  PROJECT = 'PROJECT',
  MAINTENANCE = 'MAINTENANCE',
  TROUBLESHOOTING = 'TROUBLESHOOTING',
  R_AND_D = 'R_AND_D',
  LEARNING = 'LEARNING',
  OTHER = 'OTHER',
}

/**
 * 경험 유형별 표시 정보 (IT 직군 공통)
 */
export const CATEGORY_INFO: Record<Category, { display: string; description: string }> = {
  [Category.PROJECT]: {
    display: '프로젝트',
    description: '목표 달성을 위해 초기 기획부터 실행까지 참여한 주요 과업',
  },
  [Category.MAINTENANCE]: {
    display: '운영 및 효율화',
    description: '지속적인 업무 운영과 프로세스 개선 및 품질 향상 활동',
  },
  [Category.TROUBLESHOOTING]: {
    display: '이슈 해결',
    description: '예기치 못한 문제 상황 대응 및 병목 구간 해결 경험',
  },
  [Category.R_AND_D]: {
    display: '리서치 및 설계',
    description: '신규 도입을 위한 조사, 타당성 검토 및 모델링/프로토타이핑',
  },
  [Category.LEARNING]: {
    display: '자기계발 및 교육',
    description: '전문성 강화를 위한 새로운 지식 습득 및 교육 참여',
  },
  [Category.OTHER]: {
    display: '기타',
    description: '기타 활동',
  },
};

/**
 * 기여도/참여도
 */
export enum ContributionLevel {
  OWNER = 'OWNER',
  LEAD = 'LEAD',
  MEMBER = 'MEMBER',
  SUPPORT = 'SUPPORT',
}

/**
 * 기여도별 표시 정보
 */
export const CONTRIBUTION_LEVEL_INFO: Record<
  ContributionLevel,
  { display: string; description: string }
> = {
  [ContributionLevel.OWNER]: {
    display: '오너',
    description: '처음부터 끝까지 책임지고 리드',
  },
  [ContributionLevel.LEAD]: {
    display: '리드',
    description: '팀 리드, 기술 리드',
  },
  [ContributionLevel.MEMBER]: {
    display: '멤버',
    description: '팀의 한 구성원으로 기여',
  },
  [ContributionLevel.SUPPORT]: {
    display: '서포트',
    description: '서포트 및 보조 역할',
  },
};

/**
 * 경험 블록 종류
 */

export enum ExperienceSectionKind {
  NONE = 'NONE',
  SITUATION = 'SITUATION',
  TASK = 'TASK',
  DECISION = 'DECISION',
  TROUBLESHOOTING = 'TROUBLESHOOTING',
  ACHIEVEMENT = 'ACHIEVEMENT',
  LEARNING = 'LEARNING',
  ARTIFACT = 'ARTIFACT',
}
/**
 * 경험 블록 종류별 표시 정보
 */
export const SECTION_KIND_INFO: Record<
  ExperienceSectionKind,
  { display: string; description: string; help: string }
> = {
  [ExperienceSectionKind.NONE]: {
    display: '유형 선택',
    description: '✨ 아직 블록 종류를 선택하지 않은 상태예요',
    help: `블록 종류를 선택하여 경험을 구체화해보세요.
    
추천하는 흐름:
• 🧩 시작할 땐: "배경 및 목표"로 시작해요.
• 🛠️ 구체적인 작업은: "수행 내용"이나 "의사결정"을 활용해요.
• 📊 마무리는: "성과 및 결과"와 "회고"로 정리하면 완벽해요.`,
  },
  [ExperienceSectionKind.SITUATION]: {
    display: '배경 및 목표',
    description: '📍 이 일이 시작된 계기와 해결하려던 과제, 최종 목적지를 정의해요',
    help: `당시의 상황과 도달하고 싶었던 목표를 한 번에 정리해주세요.

• ❓ 왜 이 일을 시작하게 되었나요? (문제 발생, 기획 제안 등)
• 🎯 구체적으로 어떤 상태가 되는 것이 목표였나요?
• 📏 성공을 판단하는 기준은 무엇이었나요?

예) "가입 전환율이 정체된 상황에서 모바일 이탈률 10% 감소를 목표로 프로세스 개편을 시작했습니다."`,
  },
  [ExperienceSectionKind.TASK]: {
    display: '수행 내용',
    description: '🛠️ 목표 달성을 위해 본인이 담당한 핵심 업무와 실행 과정을 정리해요',
    help: `구체적으로 어떤 실무를 진행했는지 적어주세요.

• 📌 본인이 직접 설계하거나 실행한 부분을 강조하세요.
• ⚙️ 활용한 도구(Stack)나 방법론을 포함하면 좋습니다.

예) 
• 가입 단계 축소를 위한 API 신규 설계 및 개발
• 기존 레거시 코드 리팩토링 및 테스트 코드 작성`,
  },
  [ExperienceSectionKind.DECISION]: {
    display: '의사결정 및 근거',
    description: '💡 여러 대안 중 왜 그 방식을 택했는지, 본인의 판단 근거를 기록해요',
    help: `전문성은 '선택의 이유'에서 드러납니다.

• ⚖️ 후보군 중 왜 이 기술/정책을 선택했나요?
• 📅 일정, 비용, 성능 등 어떤 제약 사항을 고려했나요?

예) "실시간성보다 데이터 정합성이 중요했기에 Kafka 대신 RabbitMQ를 선택하여 메시지 유실 없는 안정적인 구조를 택했습니다."`,
  },
  [ExperienceSectionKind.TROUBLESHOOTING]: {
    display: '문제 해결 과정',
    description: '🚧 예상치 못한 난관을 어떻게 분석하고 극복했는지 기록해요',
    help: `문제 해결 역량을 보여줄 수 있는 가장 중요한 섹션입니다.

• 🔍 문제의 근본 원인을 어떻게 파악했나요?
• ✅ 시도했던 방법들과 최종 해결책은 무엇인가요?

예) "배포 후 응답 지연 현상을 발견하여 로컬 캐시 적용 및 DB 인덱스 최적화를 통해 응답 시간을 200ms에서 50ms로 단축했습니다."`,
  },
  [ExperienceSectionKind.ACHIEVEMENT]: {
    display: '성과 및 결과',
    description: '📊 수행 결과로 얻은 수치적 성과나 정성적인 변화를 기록해요',
    help: `이 경험이 팀이나 서비스에 어떤 가치를 주었는지 적어주세요.

• 📈 이전 대비 개선된 지표를 숫자(%)로 표현해보세요.
• 🌟 조직 내부의 긍정적인 평가나 효율화 사례도 좋습니다.

예) "개편 후 가입 완료율 15% 상승, 월평균 CS 유입량 20% 감소라는 성과를 거두었습니다."`,
  },
  [ExperienceSectionKind.LEARNING]: {
    display: '회고 및 성장',
    description: '🧠 경험을 통해 얻은 교훈과 동료의 피드백을 정리하며 마무리해요',
    help: `다음 프로젝트에서 더 잘할 수 있는 인사이트를 기록하세요.

• 🎓 새롭게 배운 기술이나 협업 방식은 무엇인가요?
• 💬 동료나 상사에게 받은 긍정적인 피드백이나 개선점은 무엇인가요?

예) "유관 부서와 초기 요구사항 공유가 늦어 설계 수정이 잦았습니다. 다음엔 킥오프 단계에서 싱크를 맞추는 과정을 필수로 넣으려 합니다."`,
  },
  [ExperienceSectionKind.ARTIFACT]: {
    display: '증빙 자료/링크',
    description: '📎 결과물이나 참고할 수 있는 링크(PR, 문서, 디자인)를 모아둬요',
    help: `경험의 실체를 증명할 수 있는 자료를 기록하세요.

• 🔗 GitHub PR, Notion 기획서, Figma 디자인 링크
• 📁 발표 자료, 기술 블로그 포스팅 링크`,
  },
};
