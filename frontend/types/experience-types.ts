/**
 * 경험(Experience) 관련 타입 정의
 */

/**
 * 경험 블록 종류
 */
export enum ExperienceSectionKind {
  NONE = 'NONE',
  CONTEXT = 'CONTEXT',
  GOAL = 'GOAL',
  ACTION = 'ACTION',
  RESULT = 'RESULT',
  CHALLENGE = 'CHALLENGE',
  LEARNING = 'LEARNING',
  FEEDBACK = 'FEEDBACK',
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
    help: `아직 블록 종류를 선택하지 않았어요.
위에서 블록 종류를 선택한 후, 해당하는 내용을 채워주세요.

예를 들어,
• 🧩 "배경/상황" 섹션이라면: 이 일을 시작한 이유와 그때의 상황을 적어요.
• 🧑‍🔧 "실행 내용" 섹션이라면: 당신이 한 주요 행동과 기여를 적어요.
• 📊 "결과 및 경험" 섹션이라면: 결과를 숫자나 변화를 위주로 적으면 좋아요.`,
  },
  [ExperienceSectionKind.CONTEXT]: {
    display: '배경/상황',
    description: '📍 이 일을 시작하게 된 이유와 그때의 팀·회사·고객 상황을 정리해요',
    help: `이 일을 시작하게 된 배경과 당시 상황을 자유롭게 적어주세요.

- ❓ 왜 이 일을 시작하게 되었나요? (지시, 문제 발생, 아이디어 제안 등)
• 🧑‍🤝‍🧑 당시 팀/회사/고객은 어떤 상태였나요?
• 🛠️ 관련된 시스템이나 프로세스는 어떤 상황이었나요?

예) "신규 회원 가입률이 몇 달째 정체되어 있어, 마케팅 팀과 함께 가입 플로우를 점검하기로 했습니다. 당시 우리 서비스는 모바일 유입은 늘고 있었지만, 가입 완료율은 40% 중반에 머물러 있었고, 가입 과정에서 이탈하는 사용자가 많다는 의견이 지속적으로 들어오고 있었습니다."`,
  },
  [ExperienceSectionKind.GOAL]: {
    display: '목표/문제',
    description:
      '🎯 해결하려던 문제는 무엇이고, 달성하려던 목표와 성공 기준은 무엇인지 명확히 정리해요',
    help: `이 일을 통해 해결하고 싶었던 문제와 이루고자 했던 목표를 적어주세요.

- 🚨 어떤 문제가 있었나요? (불편함, 병목, 오류, 리소스 낭비 등)
• ✅ 구체적으로 어떤 상태가 되면 "성공"이라고 볼 수 있었나요?
• 📏 가능하면 목표를 숫자나 명확한 기준으로 표현해보면 좋아요.

예) "회원 가입 완료율을 기존 45%에서 55% 이상으로 끌어올리는 것을 목표로 했습니다. 특히 모바일 환경에서의 이탈률을 줄이는 데 초점을 두었고, 사용자가 1분 안에 가입을 완료할 수 있도록 하는 것을 성공 기준으로 삼았습니다."`,
  },
  [ExperienceSectionKind.ACTION]: {
    display: '실행 내용',
    description: '🛠️ 목표를 이루기 위해 당신이 한 주요 행동과 기여를 정리해요',
    help: `이 목표를 위해 당신이 직접 한 일을 위주로 적어주세요.

- 📌 '내가 한 일'을 2~5줄로, 3~5개의 포인트로 나눠 정리해보세요.
• 💡 설계, 구현, 커뮤니케이션, 조율, 의사결정 등에서 본인이 기여한 부분을 강조하면 좋아요.

예)
• 📉 가입 플로우의 주요 이탈 구간를 분석하기 위해 로그 이벤트 설계를 주도하고, 기존 데이터도 함께 수집·정리했습니다.
• 🤝 분석 결과를 바탕으로 UI/UX 디자이너와 협업해 가입 단계를 4단계에서 2단계로 줄이는 개선안을 제안했습니다.
• ⚙️ 백엔드 API 응답 구조를 단순화하고, 중복된 검증 로직을 통합하여 전체 응답 시간을 단축했습니다.
• 📈 변경된 가입 플로우에 대한 A/B 테스트 환경을 구성하고, 실험 결과를 정리하여 팀에 공유했습니다.`,
  },
  [ExperienceSectionKind.RESULT]: {
    display: '결과 및 경험',
    description: '📊 당신이 한 일로 생긴 변화나 경험를, 가능하면 숫자로 표현해요',
    help: `이 일을 통해 어떤 결과와 변화가 있었는지 적어주세요.

- 🔁 이전과 비교해 어떤 지표나 상태가 어떻게 달라졌나요?
• 💼 사용자, 팀, 회사 입장에서 어떤 이득이 있었나요?
• 🧮 가능하다면 숫자, 비율, 기간 등 객관적인 표현을 써보세요.

예)
• 📈 가입 완료율을 45%에서 58%로 약 13%p 증가시켰습니다.
• ⏱️ 모바일 가입 플로우의 평균 소요 시간을 1분 30초에서 50초로 약 40% 단축했습니다.
• ☎️ 멤버십 가입 관련 CS 문의를 월 120건에서 70건으로 줄였습니다.
• 📊 A/B 테스트 결과, 변경된 플로우의 전환율이 기존 대비 통계적으로 유의미하게 높았습니다.`,
  },
  [ExperienceSectionKind.CHALLENGE]: {
    display: '어려웠던 점',
    description: '🚧 이 일을 진행하면서 겪은 어려움이나 제약, 갈등을 정리해요',
    help: `이 일을 진행하면서 가장 어려웠던 점이 무엇이었는지 적어주세요.

- ⏰ 기술적인 제약, 촉박한 일정, 자원 부족, 팀 간 의견 충돌 같은 어려움이 있었나요?
• 🔍 그 문제를 어떻게 파악했고, 어떤 방법으로 해결하거나 완화했나요?

예) "기존 가입 플로우는 여러 팀의 이해관계가 얽혀 있어 단계 축소에 대한 반대가 많았습니다. 특히 마케팅 팀은 추가 정보 수집을, 보안 팀은 추가 인증 단계를 요구했습니다. 각 팀의 우려를 정리한 뒤, 필수 정보와 선택 정보를 분리하고 단계 내 노출 방식을 바꾸는 절충안을 제안했습니다. 또한, 보안 요구사항을 충족하면서도 사용자 경험을 해치지 않는 인증 방식을 찾기 위해 여러 옵션을 비교 테스트했습니다."`,
  },
  [ExperienceSectionKind.LEARNING]: {
    display: '배운 점',
    description: '📚 이 경험을 통해 배운 점과 다음에 비슷한 상황에서 다르게 할 점을 돌아봐요',
    help: `이 경험을 통해 무엇을 배웠고, 다음에 비슷한 일을 할 때는 무엇을 다르게 할지 적어주세요.

- 🧠 일하는 방식, 소통, 기술 선택, 우선순위 결정 등에서 느낀 점은 무엇인가요?
• 🔄 다음에 비슷한 일을 한다면, 무엇을 더 잘하거나 다르게 하고 싶나요?

예) "여러 팀의 이해관계가 얽힌 문제에서는 처음부터 각 팀의 목표와 우려를 빠르게 공유하고 정리하는 것이 중요하다는 걸 배웠습니다. 이번에는 설계가 어느 정도 진행된 뒤에야 논의를 시작해서, 몇 번이나 방향을 수정해야 했습니다. 다음에는 킥오프 단계에서 주요 팀과 함께 공통 목표와 최소·최대 요구사항을 먼저 정리하고, 그 바탕 위에 설계를 시작하는 방식을 시도해보고 싶습니다."`,
  },
  [ExperienceSectionKind.FEEDBACK]: {
    display: '피드백/평가',
    description: '💬 이 일에 대해 받은 평가나 피드백을 기록해요',
    help: `이 일과 관련해 상사, 동료, 타 팀 또는 고객에게서 받은 피드백을 적어주세요.

- 🗣️ 경험 평가나 회고 시간에 들은 말, 칭찬 메일이나 채팅 기록 등 기억나는 내용을 적어보세요.
• ✍️ 받은 말을 그대로 인용해도 좋고, 요약해서 적어도 괜찮아요.

예)
• "분석부터 A/B 테스트까지 전체 플로우를 주도적으로 끌고 가줘서 고맙다"는 피드백을 팀 리드에게 받았습니다.
• 분기 경험 평가에서 '사용자 데이터 기반으로 문제를 정의하고 개선안을 도출하는 역량' 블록에서 상위 평가를 받았습니다.
• CS 팀에서 "가입 관련 문의가 눈에 띄게 줄었다"는 이야기를 들었고, 실제로 문의 통계에도 반영되었습니다.`,
  },
  [ExperienceSectionKind.ARTIFACT]: {
    display: '증빙 자료/링크',
    description: '📎 나중에 참고할 수 있도록 관련 문서, PR, 대시보드 등의 링크를 모아둬요',
    help: `이 경험와 관련된 증빙 자료나 참고 링크를 적어주세요.

- 🔗 Git/PR 링크, Notion/Confluence 문서, 설계서, 회의록, 발표 자료
• 📊 대시보드, BI 리포트, Figma 링크 등
• 📁 파일이 있다면, 어디에 저장되어 있는지도 함께 적어두면 좋아요.

예)
• PR: [https://github.com/…](https://github.com/%E2%80%A6)
• 설계 문서(Notion): [https://www.notion.so/…](https://www.notion.so/%E2%80%A6)
• A/B 테스트 결과 대시보드: [https://looker/…](https://looker/%E2%80%A6)
• 발표 자료(Google Slides): /drive/…`,
  },
};

/**
 * 경험 상태
 */
export enum ExperienceStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED',
}

/**
 * 업무 유형
 */
export enum WorkType {
  PROJECT = 'PROJECT',
  OPERATION = 'OPERATION',
  INCIDENT = 'INCIDENT',
  PERSONAL = 'PERSONAL',
  LEARNING = 'LEARNING',
  OTHER = 'OTHER',
}

/**
 * 업무 유형별 표시 정보
 */
export const WORK_TYPE_INFO: Record<WorkType, { display: string; description: string }> = {
  [WorkType.PROJECT]: {
    display: '프로젝트',
    description: '3개월 이상의 프로젝트성 업무',
  },
  [WorkType.OPERATION]: {
    display: '운영/개선',
    description: '상시 업무 개선 및 운영',
  },
  [WorkType.INCIDENT]: {
    display: '장애 대응',
    description: '긴급 장애 대응 및 해결',
  },
  [WorkType.PERSONAL]: {
    display: '개인 프로젝트',
    description: '사이드 프로젝트, 토이 프로젝트',
  },
  [WorkType.LEARNING]: {
    display: '학습/교육',
    description: '스터디, 교육, 자기계발',
  },
  [WorkType.OTHER]: {
    display: '기타',
    description: '기타 업무 유형',
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
  orgName?: string;
  durationStart: string;
  durationEnd?: string;
  impactSummary?: string;
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
  orgName: string;
  durationStart: string;
  durationEnd: string;
  impactSummary: string;
  tags: string;
  sections: ExperienceSection[];
}
