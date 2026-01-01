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
 * 경험 폼 내의 개별 섹션 (편집 상태 포함)
 */
export interface TExperienceFormSection extends ExperienceSection {
  isEditingTitle?: boolean;
  tempTitle: string;
  showHelp?: boolean;
  showMethodBreakdown?: boolean;
}

/**
 * ExperienceForm 컴포넌트에서 사용하는 전체 데이터 구조
 */
export interface TExperienceFormData {
  id?: string;
  title: string;
  background: string;
  periodStart: string;
  periodEnd: string;
  role: string;
  category: string | null;
  contributionLevel: string | null;
  goalSummary: string;
  keyAchievements: string;
  skills: string;
  sections: TExperienceFormSection[];
}

/**
 * ExperienceForm 컴포넌트의 동작 모드
 */
export enum ExperienceFormMode {
  REGISTER = 'REGISTER',
  VIEW = 'VIEW',
  EDIT = 'EDIT',
  EDIT_WITH_AI = 'EDIT_WITH_AI',
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
  AI_ANALYZED = 'AI_ANALYZED', // 분석 완료
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
  [ExperienceStatus.AI_ANALYZED]: { display: '분석 완료', color: 'status-analyzed' },
};

/**
 * 경험 통계 요약 타입
 */
export type TExperienceStatsSummary = {
  total: number;
  incomplete: number;
  completed: number;
  modified: number;
  aiAnalyzed: number;
};

/**
 * 업무 유형
 */
export enum Category {
  PROJECT = 'PROJECT',
  IMPROVEMENT = 'IMPROVEMENT',
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
  [Category.IMPROVEMENT]: {
    display: '개선',
    description: '프로세스 개선 및 품질 향상 활동',
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
 * 목표 필드 정보
 */
export const GOAL_INFO = {
  description: '🎯 이 경험을 통해 달성하고자 했던 구체적인 목표를 정의해요',
  help: `목표는 경험의 방향성과 성공 기준을 보여주는 핵심입니다.
• 🎯 무엇을 달성하고 싶었나요? (정량적 목표가 있다면 더 좋아요)
• 📏 성공을 어떻게 측정할 수 있었나요?

예)
• "신규 고객 유치율 20% 향상"
• "고객 만족도 점수 4.5점 이상 달성"`,
};

/**
 * 핵심 성과 필드 정보
 */
export const KEY_ACHIEVEMENTS_INFO = {
  description: '📊 이 경험을 통해 얻은 가시적인 성과와 비즈니스 임팩트를 기록해요',
  help: `성과는 수치와 구체적인 결과로 표현하면 설득력이 높아집니다.
• 📈 Before/After를 비교할 수 있는 수치가 있나요?
• 🏆 팀이나 조직에 어떤 긍정적인 변화를 가져왔나요?

예)
• "분기 매출 15% 증가에 기여"
• "고객 클레임 건수 40% 감소"`,
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
    description: '📍 이 일이 시작된 계기와 해결하려던 과제, 최종 목표를 정의해요',
    help: `당시의 상황과 도달하고 싶었던 목표를 한 번에 정리해주세요.
• ❓ 왜 이 일을 시작하게 되었나요? (문제 발생, 기획 제안 등)
• 🎯 구체적으로 어떤 상태가 되는 것이 목표였나요?
• 📏 성공을 판단하는 기준은 무엇이었나요?

예) "가입 전환율이 정체된 상황에서 모바일 이탈률 10% 감소를 목표로 프로세스 개편을 시작했습니다."`,
  },
  [ExperienceSectionKind.TASK]: {
    display: '수행 내용',
    description: '🛠️ 목표 달성을 위해 본인이 담당한 핵심 업무와 실행 과정을 정리해요',
    help: `구체적으로 어떤 업무를 수행했는지 적어주세요.
• 📌 '내가 주도적으로 한 일'을 중심으로 3~5개 포인트를 잡아보세요.
• ⚙️ 활용한 도구(Figma, Excel, SQL 등)나 업무 방법론을 포함하면 전문성이 돋보입니다.

예) 
• 고객 이탈률 감소를 위한 사용자 여정(User Journey) 재설계 및 신규 프로모션 기획
• 효율적인 협업을 위한 사내 업무 프로세스 가이드라인 제작 및 전사 배포
• 데이터 분석 툴을 활용한 주간 매출 리포트 자동화 서식 구축`,
  },
  [ExperienceSectionKind.DECISION]: {
    display: '의사결정 및 근거',
    description: '💡 여러 대안 중 왜 그 방식을 택했는지, 본인의 판단 근거를 기록해요',
    help: `전문성은 단순한 실행이 아닌 '선택의 이유'에서 드러납니다.
• ⚖️ 여러 대안 중 왜 이 방법(혹은 툴/정책)을 선택했나요?
• 📅 일정, 예산, 인력 등 당시의 제약 사항 속에서 최선의 선택이었음을 강조하세요.

예) "한정된 마케팅 예산 내에서 브랜드 인지도를 높여야 했기에, 광범위한 유료 광고 대신 타겟 고객층이 뚜렷한 인플루언서 협업 방식을 선택하여 전환율을 높였습니다."`,
  },
  [ExperienceSectionKind.TROUBLESHOOTING]: {
    display: '문제 해결 과정',
    description: '🚧 예상치 못한 난관을 어떻게 분석하고 극복했는지 기록해요',
    help: `위기 관리 능력과 문제 해결 역량을 보여줄 수 있는 가장 중요한 섹션입니다.
• 🔍 갑작스러운 문제나 갈등 상황의 '원인'을 어떻게 파악했나요?
• ✅ 문제를 해결하기 위해 시도했던 구체적인 행동과 최종 결과를 적어주세요.

예) "신규 서비스 런칭 직후 고객 문의가 급증하는 상황에서, 빈번한 질문(FAQ)을 분석하여 즉시 챗봇 시나리오를 업데이트함으로써 고객 대기 시간을 50% 이상 단축하고 상담 업무의 병목을 해결했습니다."`,
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
