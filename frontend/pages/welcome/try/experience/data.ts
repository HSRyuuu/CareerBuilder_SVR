import { ExperienceSectionKind } from '@/types/experience-types';
import type { TExperienceFormData } from '@/types/experience-types';
import type { TExperienceAIAnalysisResponse } from '@/api/experience/types';

// 60점짜리 초기 데이터 (IT 주니어 개발자 - 이커머스 프로젝트 예시)
export const INITIAL_DUMMY_DATA: TExperienceFormData = {
  title: '오픈마켓 쇼핑몰 프로젝트 개발',
  periodStart: '2023-09',
  periodEnd: '2023-12',
  background: '부트캠프 팀 프로젝트',
  role: '백엔드 개발자',
  category: 'PROJECT',
  contributionLevel: 'MEMBER',
  goalSummary: '기존에 배웠던 기술들을 활용해서 실제 운영 가능한 수준의 쇼핑몰을 만드는 것이 목표였습니다. 특히 상품 주문이 몰릴 때 서버가 안 죽게 만드는 것을 중요하게 생각했습니다.',
  keyAchievements: '팀원들과 협력해서 기안 내에 프로젝트를 완성했습니다. 기능 테스트도 다 통과했고, 발표 때 멘토님한테 칭찬도 받았습니다. 대용량 트래픽 처리에 대해 많이 배울 수 있었습니다.',
  skills: 'Java, Spring Boot, MySQL, Redis, AWS',
  sections: [
    {
      id: 'dummy_section_1',
      kind: ExperienceSectionKind.TROUBLESHOOTING,
      title: '선착순 쿠폰 이벤트 동시성 문제 해결',
      content: '선착순 쿠폰 이벤트를 개발했는데, 테스트를 해보니 쿠폰이 수량보다 더 많이 발급되는 문제가 있었습니다. DB에 락을 걸어서 해결하려고 했는데 성능이 너무 느려졌습니다. 그래서 Redis를 써서 문제를 해결했고 속도도 빨라졌습니다.',
      sortOrder: 0,
      isEditingTitle: false,
      tempTitle: '',
      showHelp: false,
    }
  ],
};

// 100점짜리 AI 분석 결과 (완벽한 개선안 - IT 전문성 강화)
export const MOCK_AI_ANALYSIS: TExperienceAIAnalysisResponse = {
  experience: {
    id: 'dummy_exp_id',
    userId: 'dummy_user',
    title: '오픈마켓 쇼핑몰 프로젝트 개발',
    periodStart: '2023-09',
    periodEnd: '2023-12',
    status: 'DRAFT',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    sections: []
  },
  analysis: {
    id: 'dummy_analysis_id',
    totalScore: 72, 
    scoreMetrics: {
      specificity: 65,
      resultOriented: 60,
      logicalFlow: 75,
      jobRelevance: 85,
    },
    overallSummary: '기술 스택 활용 능력은 보이나 구체적인 성능 지표와 기술적 의사결정 과정이 부족합니다.',
    overallFeedback: '사용하신 기술(Redis, DB Lock 등)에 대한 이해도는 훌륭하지만, 단순히 "빨라졌다", "해결했다"는 표현만으로는 역량을 충분히 증명하기 어렵습니다. 트래픽 규모(RPS), 응답 속도 변화(ms), 동시성 제어 방식의 장단점 비교 등 엔지니어링 관점의 구체적인 수치와 논리를 보완한다면 훨씬 더 매력적인 포트폴리오가 될 것입니다.',
    recommendedKeywords: ['대용량 트래픽 처리', '동시성 제어', '분산 락(Distributed Lock)', '성능 최적화', '시스템 설계'],
    
    goalFeedback: '학습 목표보다는 비즈니스 및 기술적 목표(SLA, 성능 목표 등)를 구체적으로 제시하는 것이 좋습니다.',
    goalImprovedContent: 'MAU 1만 명 규모의 트래픽을 감당할 수 있는 고가용성 이커머스 플랫폼 구축을 목표로 했습니다. 특히 선착순 이벤트와 같은 고부하 상황에서도 평균 응답 속도 200ms 이내를 유지하고, 데이터 정합성을 100% 보장하는 안정적인 백엔드 시스템 설계를 핵심 KPI로 설정했습니다.',
    
    achievementFeedback: '정성적인 "칭찬", "완성"보다는 정량적인 성능 개선 수치를 강조해보세요.',
    achievementImprovedContent: 'JMeter 부하 테스트 결과, Redis 분산 락 도입을 통해 최대 1,000 TPS 상황에서도 재고 오차율 0%를 달성했습니다. 또한 캐싱 전략을 적용하여 메인 페이지 조회 API의 평균 응답 시간을 500ms에서 50ms로 90% 단축시키는 등, 사용자 경험과 시스템 안정성을 동시에 확보했습니다.',
  },
  sections: [
    {
      section: {
        id: 'dummy_section_1',
        kind: ExperienceSectionKind.TROUBLESHOOTING,
        content: '',
        title: '',
        sortOrder: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      analysis: {
        id: 'dummy_section_analysis_1',
        sectionId: 'dummy_section_1',
        suggestedKind: ExperienceSectionKind.TROUBLESHOOTING,
        method: 'STAR',
        reasoning: '기술적 난제(Race Condition)와 이를 해결하기 위한 기술적 의사결정 과정이 명확하므로 STAR 기법이 적합합니다.',
        feedback: '문제 현상은 잘 기술되었으나, "왜" Redis를 선택했는지에 대한 기술적 근거와 구체적인 개선 수치가 빠져있습니다. DB Lock(비관적/낙관적)의 한계점과 Redis 분산 락의 이점을 비교 설명하고, 최종적인 성능 향상 수치를 포함하여 작성해보세요.',
        improvedContent: '선착순 쿠폰 발급 로직에서 100명의 동시 요청 시 재고가 음수가 되는 Race Condition이 발생했습니다. 초기에는 DB의 Pessimistic Lock을 적용하여 정합성은 확보했으나, TPS가 50 이하로 급락하는 성능 병목을 확인했습니다.\\n\\n이를 해결하기 위해 In-memory 기반의 Redis 분산 락(Redisson)을 도입했습니다. 스핀 락 방식 대신 Pub/Sub 방식을 활용하여 Redis 부하를 최소화했고, 트랜잭션 범위를 최소한으로 줄여 락 점유 시간을 단축했습니다. 그 결과, 데이터 정합성을 완벽하게 보장하면서도 처리량을 500 TPS까지 10배 향상시킬 수 있었습니다.',
        methodBreakdown: {
          situation: '선착순 쿠폰 이벤트 시 동시성 이슈로 인한 재고 초과 발급 문제 발생',
          task: '데이터 정합성 보장 및 고트래픽 상황에서의 시스템 성능 유지',
          action: 'DB Lock의 성능 한계를 분석하고 Redis 분산 락(Redisson Pub/Sub) 도입 및 트랜잭션 최적화',
          result: '재고 오차율 0% 달성 및 처리량(TPS) 10배(50 -> 500) 성능 향상'
        }
      }
    }
  ]
};
