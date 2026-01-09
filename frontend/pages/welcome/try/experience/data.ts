import { ExperienceSectionKind } from '@/types/experience-types';
import type { TExperienceFormData } from '@/types/experience-types';
import type { TExperienceAIAnalysisResponse } from '@/api/experience/types';

// 60점짜리 초기 데이터 (사용자가 작성한 조금 불친절하고 정성적인 예시)
export const INITIAL_DUMMY_DATA: TExperienceFormData = {
  title: '백엔드 시스템 성능 최적화 및 캐싱 도입',
  periodStart: '2024-01',
  periodEnd: '2024-03',
  background: '사내 서비스 메인 페이지 응답 속도 개선 프로젝트',
  role: '백엔드 개발자',
  category: 'IMPROVEMENT',
  contributionLevel: 'LEAD',
  goalSummary: '메인 화면을 들어갈 때마다 너무 느려서 사용자들이 불편해했습니다. 여러 외부 API에서 데이터를 가져오느라 느린 것 같아 캐시를 도입해서 속도를 올리는 것이 목표였습니다.',
  keyAchievements: 'Redis를 써서 데이터를 미리 저장해두니까 확실히 빨라졌습니다. K6라는 도구로 테스트도 해봤는데 지표가 잘 나왔고, 팀원들도 편하게 쓰라고 모듈로 만들어서 배포했습니다.',
  skills: 'Kotlin, Spring Boot, Redis, Grafana K6, Docker',
  sections: [
    {
      id: 'dummy_section_1',
      kind: ExperienceSectionKind.DECISION,
      title: '외부 API 호출 구간 Redis 캐싱 도입',
      content: '메인 화면에서 날씨 정보를 외부 서버에서 매번 가져오는데 이게 너무 오래 걸렸습니다. 그래서 Redis를 도입해서 10분 동안 데이터를 들고 있게 바꿨습니다. 이제 외부 서버가 잠깐 죽어도 우리 서비스는 잘 돌아갑니다.',
      sortOrder: 0,
      isEditingTitle: false,
      tempTitle: '',
      showHelp: false,
    },
    {
      id: 'dummy_section_2',
      kind: ExperienceSectionKind.TROUBLESHOOTING,
      title: 'Grafana K6를 이용한 성능 부하 테스트',
      content: '캐시를 넣고 정말 빨라졌는지 확인하려고 K6로 테스트를 했습니다. 유저가 많이 들어올 때를 가정해서 돌려봤는데 응답 시간이 많이 줄어든 걸 확인했습니다. 덕분에 성능이 좋아졌다는 걸 팀원들한테 보여줄 수 있었습니다.',
      sortOrder: 1,
      isEditingTitle: false,
      tempTitle: '',
      showHelp: false,
    },
    {
      id: 'dummy_section_3',
      kind: ExperienceSectionKind.TASK,
      title: '공통 캐싱 모듈화 및 신뢰도 향상',
      content: '캐시 코드가 여기저기 섞여 있으면 보기 안 좋아서 하나로 합쳤습니다. 어노테이션만 붙이면 캐시가 적용되도록 모듈화했어요. 예외 처리도 넣어서 캐시 서버에 문제가 생겨도 서비스는 안 멈추게 만들었습니다.',
      sortOrder: 2,
      isEditingTitle: false,
      tempTitle: '',
      showHelp: false,
    }
  ],
};

// 100점짜리 AI 분석 결과 (완벽한 개선안 - 수치화, 전문성, 구조화 강조)
export const MOCK_AI_ANALYSIS: TExperienceAIAnalysisResponse = {
  experience: {
    id: 'dummy_exp_id',
    userId: 'dummy_user',
    title: '백엔드 시스템 성능 최적화 및 캐싱 도입',
    periodStart: '2024-01',
    periodEnd: '2024-03',
    status: 'COMPLETED',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    sections: []
  },
  analysis: {
    id: 'dummy_analysis_id',
    totalScore: 65, 
    scoreMetrics: {
      specificity: 55,
      resultOriented: 40,
      logicalFlow: 70,
      jobRelevance: 85,
    },
    overallSummary: '기술적 성과는 뛰어나지만, 수치 기반의 정량적 성과와 아키텍처적 의사결정 근거가 부족하여 전문성이 충분히 드러나지 않습니다.',
    overallFeedback: 'Redis 도입 및 성능 측정이라는 좋은 소재를 가지고 있지만, "느려서", "빨라졌다", "잘 나왔다"와 같은 정성적인 표현이 주를 이루고 있습니다. 엔지니어로서 어떤 데이터를 근거로 문제를 정의했는지, 그리고 개선 후 어떤 지표(TPS, Latency 등)를 통해 이를 검증했는지를 보완한다면 설득력이 획기적으로 높아질 것입니다.',
    recommendedKeywords: ['Redis', 'Load Testing', 'AOP'],
    
    goalFeedback: '단순한 사용자의 "불편함" 보다는 시스템 가용성과 응답 속도에 대한 구체적인 타겟 지표를 제시하여 목표의 선명도를 높여보세요.',
    goalImprovedContent: '메인 페이지 렌더링 시 발생하는 평균 2.5s의 네트워크 지연시간을 0.3s 이내로 90% 이상 단축하고, 외부 API 장애 시에도 중단 없는 서비스를 제공하기 위한 고가용성 캐싱 레이어 구축을 목표로 설정했습니다.',
    
    achievementFeedback: '"확실히 빨라졌다"는 표현 대신, k6 테스트를 통해 도출된 구체적인 퍼센트와 P99 응답 속도 등의 지표를 강조해야 합니다.',
    achievementImprovedContent: 'Redis Look-aside 전략 도입을 통해 메인 화면 응답 속도를 기존 대비 92% 개선(2.5s → 0.2s) 했습니다. Grafana k6 부하 테스트를 통해 1,000 VUs 동시 접속 상황에서도 안정적인 응답 속도를 유지함을 검증했으며, 캐싱 로직을 AOP 기반 모듈로 추상화하여 개발 생산성을 30% 이상 향상시켰습니다.',
  },
  sections: [
    {
      section: {
        id: 'dummy_section_1',
        kind: ExperienceSectionKind.DECISION,
        content: '',
        title: '',
        sortOrder: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      analysis: {
        id: 'dummy_section_analysis_1',
        sectionId: 'dummy_section_1',
        suggestedKind: ExperienceSectionKind.ACHIEVEMENT,
        method: 'STAR',
        reasoning: '외부 의존성이 높은 아키텍처의 문제를 파악하고 해결한 기술적 기여도가 높으므로 상황(S)-해결(A)-성과(R) 구조가 적합합니다.',
        feedback: '캐싱 전략(Look-aside 등)과 데이터 만료 정책(TTL)의 근거, 그리고 외부 API 장애 대응 측면의 수치를 보완하세요.',
        improvedContent: '메인 화면 구성에 필요한 외부 날씨 API 호출이 병목 지점이 되어 전체 응답 속도가 2.5초까지 지연되는 상황이었습니다. 이를 해결하기 위해 Redis 기반의 Look-aside 캐싱 전략을 도입하였습니다. 데이터의 실시간성과 시스템 하중을 고려하여 TTL을 10분으로 최적화하였으며, 캐시 히트율(Cache Hit Rate)을 95% 이상으로 유지하도록 설계했습니다. 결과적으로 외부 API 호출 횟수를 90% 감소시켰으며, 외부 서버 장애 시에도 캐시 데이터를 활용하여 끊김 없는 서비스를 제공함으써 가용성을 확보했습니다.',
        methodBreakdown: {
          situation: '외부 API(날씨) 호출 지연으로 인한 메인 페이지 응답 속도 저하(2.5s)',
          task: '데이터 조회 성능 개선 및 외부 의존성 제거를 통한 가용성 확보',
          action: 'Redis Look-aside 전략 수립, TTL 10분 설정 및 데이터 정합성 보장 로직 구현',
          result: '응답 속도 92% 개선(0.2s) 및 외부 장애 시 무중단 서비스 제공 가능'
        }
      }
    },
    {
      section: {
        id: 'dummy_section_2',
        kind: ExperienceSectionKind.TROUBLESHOOTING,
        content: '',
        title: '',
        sortOrder: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      analysis: {
        id: 'dummy_section_analysis_2',
        sectionId: 'dummy_section_2',
        suggestedKind: ExperienceSectionKind.TROUBLESHOOTING,
        method: 'STAR',
        reasoning: '막연한 추측이 아닌 데이터 기반의 성능 지표를 도출하는 과정을 구체적으로 기술해야 합니다.',
        feedback: 'K6 테스트 시나리오(VUs, Duration)와 구체적인 성능 지표(P95, P99)의 변화를 포함하여 신뢰도를 높이세요.',
        improvedContent: '성능 개선의 객관적 지표를 확보하기 위해 Grafana k6를 활용한 부하 테스트 시나리오를 설계했습니다. 100 VUs부터 시작하여 최대 1,000 VUs까지 점진적으로 부하를 증가시키는 Ramp-up 테스트 결과, 기존 인프라 대비 동시 처리량이 5배 향상됨을 확인했습니다. 특히 응답 시간 P99 지표가 3.5s에서 0.5s로 대폭 개선되었으며, Grafana 대시보드를 통해 Redis의 메모리 사용량과 CPU 부하를 실시간 모니터링하여 병목 현상이 발생하지 않음을 최종 검증했습니다.',
        methodBreakdown: {
          situation: '캐싱 도입 후 성능 개선 수치에 대한 객관적 데이터 및 스트레스 테스트 필요',
          task: '고부하 상황에서의 시스템 안정성 검증 및 성능 리포트 작성',
          action: 'k6 Ramp-up 시나리오 정의 및 Grafana 실시간 대시보드 연동 모니터링 수행',
          result: '동시 처리량 5배 향상 확인 및 P99 지표 85% 개선 성과 도출'
        }
      }
    },
    {
      section: {
        id: 'dummy_section_3',
        kind: ExperienceSectionKind.TASK,
        content: '',
        title: '',
        sortOrder: 2,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      analysis: {
        id: 'dummy_section_analysis_3',
        sectionId: 'dummy_section_3',
        suggestedKind: ExperienceSectionKind.ACHIEVEMENT,
        method: 'STAR',
        reasoning: '단일 기능 구현을 넘어 아키텍처적 추상화와 유지보수성 향상을 강조해야 합니다.',
        feedback: 'AOP 활용 방식이나 서킷 브레이커(Circuit Breaker) 패턴 개념을 포함하여 기술적 깊이를 보여주세요.',
        improvedContent: '반복되는 캐싱 로직으로 인한 코드 비대화를 방지하고 핵심 비즈니스 로직과 기술적 관심사를 분리하기 위해 Spring AOP 기반의 공통 캐싱 모듈을 설계했습니다. 커스텀 어노테이션(@GlobalCache)을 통해 간단하게 캐시를 적용할 수 있도록 구현했으며, Redis 장애 시 DB로 직접 접근하는 Fail-over 로직을 포함하여 시스템 안정성을 강화했습니다. 이를 통해 신규 API 개발 시 캐싱 도입 시간을 80% 단축했으며, 팀 전체의 개발 생산성 향상에 기여했습니다.',
        methodBreakdown: {
          situation: '캐싱 로직의 중복으로 인한 코드 복잡도 증가 및 유지보수성 저하',
          task: '재사용 가능한 캐싱 모듈 설계 및 도트 모드 장애 대응 로직 구현',
          action: 'Spring AOP 기반의 커스텀 어노테이션 개발 및 Fail-back 로직 적용',
          result: '신규 캐시 적용 생산성 80% 향상 및 시스템 내 결함 격리(Fault Tolerance) 확보'
        }
      }
    }
  ]
};
