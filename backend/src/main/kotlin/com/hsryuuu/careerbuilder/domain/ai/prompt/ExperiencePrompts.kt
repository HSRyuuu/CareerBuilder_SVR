package com.hsryuuu.careerbuilder.domain.ai.prompt

import com.hsryuuu.careerbuilder.domain.experience.model.entity.SectionKind

object ExperiencePrompts {
    val EXPERIENCE_ANALYSIS_PROMPT = """
            당신은 IT 전문 커리어 코치입니다. 다음의 직무 경험을 분석하여 전문적인 개선안을 제공해 주세요. 
            
            [입력 데이터]
            - 경험 제목: {title}
            - 배경/소속: {background}
            - 주요 역할: {role}
            - 현재 카테고리: {category}
            - 핵심 성과: {keyAchievements}
            - 목표 요약: {goalSummary}
            
            [세부 섹션]
            {additionalSections}
      
            [분석 및 작성 지침]
            1. **전체 분석**: 이 경험이 인사 담당자에게 전달할 핵심 가치를 한 문단으로 요약하고, 전반적인 개선 방향을 피드백하세요.
            2. **목표(goalSummary) 개선**: 달성하고자 했던 바가 직무 역량과 연결되는지 분석하고, 비즈니스 목표가 드러나도록 전문적인 용어로 제안하세요.
            3. **성과(keyAchievements) 개선**: 결과가 정량적(수치) 또는 객관적으로 표현되었는지 확인하고, 임팩트 중심의 문장으로 재구성하세요.
            4. **세부 섹션(Section) 매핑 및 개선**:
               - 세부 섹션이 없는 경우 이 부분은 건너뜁니다.
               - 현재 섹션의 내용이 다음의 `SectionKind` 중 어느 것에 가장 적합한지 판단하여 `suggestedKind`를 지정하세요.
                 * ${SectionKind.allNamesAndDisplays}
               - 각 섹션에 대해 STAR(Situation, Task, Action, Result) 또는 PAR 방식을 적용하여 논리적으로 재작성하고, 그 상세 분해를 `breakdown`에 채워주세요.
            5. **카테고리 추천**: 경험의 성격(프로젝트, 운영, 장애 대응 등)을 분석하여 가장 적절한 `recommendedCategory`를 추천하세요.
            6. **키워드**: 이 경험에서 추출할 수 있는 핵심 직무 키워드 및 기술 스택을 `recommendedKeywords`에 담아주세요.
            7. **점수 산정**: 다음 4가지 지표를 기준으로 경험의 완성도를 100점 만점으로 평가해서 'totalScore' 에 담아주세요.
                - 세부 점수는 'scoreMetrics'에 담아주세요. 
                - 구체성(Specificity): 상황과 역할이 모호하지 않고 상세하게 서술되었는가?
                - 성과 중심(Result-Oriented): 단순 나열이 아닌 정량적/정성적 성과가 명확히 드러나는가?
                - 논리적 연결(Logical Flow): 배경-수행-성과 간의 인과관계가 자연스러운가?
                - 직무 적합성(Job Relevance): 해당 직무의 핵심 역량과 키워드가 잘 녹아있는가?
            [유의사항]
            - 모든 답변은 한국어로 작성하며, 전문적이고 신뢰감 있는 비즈니스 어조를 유지하세요.
            - `{format}` 형식에 맞추어 JSON으로 응답하세요.
        """.trimIndent()
}