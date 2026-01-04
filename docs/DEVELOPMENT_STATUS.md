# 🚧 개발 현황 및 미진한 기능 (Development Status)

현재 코드 분석을 기반으로 파악된 구현 현황과 향후 개발이 필요한 기능 목록입니다.

## ✅ 구현 완료 (Implemented)

### Backend
- [x] **API 기본 구조**: Controller, Service, Repository 레이어 구축 완료
- [x] **Auth**: 회원가입, 로그인, 중복 체크, 로그아웃 API
- [x] **Experience**: 경험 CRUD, 페이징 검색, 필터링, 정렬
- [x] **AI Facade**: AI 분석 요청 흐름 (Facade 패턴 적용됨)
- [x] **Statistics**: 대시보드용 요약 통계 API
- [x] **Logging**: 사용자 행동 로그 수집

### Frontend
- [x] **랜딩 페이지**: Welcome Hero 섹션, 기능 소개, 인터랙티브 UI
- [x] **대시보드 UI**: 통계 카드, 경험 목록 테이블, 상태별 필터링
- [x] **경험 관리 UI**: 목록 조회, 상세 페이지 연결
- [x] **AI 분석 트리거**: 'AI 분석 시작하기' 버튼 및 플로우
- [x] **Atomic Components**: Button, Input, Card 등 공통 컴포넌트 라이브러리화

---

## ⚠️ 개발 미진 / 진행 중 (In Progress / Todo)

코드 내 `FIXME`, `TODO` 주석 및 "준비중" 메시지를 기반으로 파악된 항목입니다.

### 1. 이력서 생성 (Resume Creation)
- **상태**: ❌ 미구현
- **증거**: `frontend/pages/career/index.vue` 내 `handleResumeCreate` 함수에서 `'이력서 생성 기능 준비중입니다.'` 토스트 메시지 출력.
- **계획**: 축적된 경험 데이터를 바탕으로 PDF 또는 웹 뷰 형태의 이력서 자동 생성 기능 필요.

### 2. 전체 커리어 피드백 (Career Feedback)
- **상태**: ❌ 미구현
- **증거**: `handleFeedback` 함수에서 `'전체 커리어 피드백 기능 준비중입니다.'` 토스트 메시지 출력.
- **계획**: 개별 경험이 아닌, 전체 커리어 패스에 대한 AI 종합 조언/피드백 기능.

### 3. AI 분석 결과 상세 UI 고도화
- **상태**: ⚠️ 부분 구현 / 개선 필요
- **내용**: `ExperienceWithAnalysisResponse` DTO는 존재하나, 프론트엔드에서 AI 분석 결과를 시각적으로 풍부하게 보여주는 전용 리포트 페이지(`start/index.vue` 등)의 완성이 필요해 보임.

### 4. 결제/구독 모델 (Subscription)
- **상태**: ⚠️ 구조만 존재
- **내용**: `SubscriptionUsageDto` 및 `UsageService`가 존재하지만(Backend), 실제 결제 연동이나 플랜 업그레이드 UI는 확인되지 않음. 사용량 제한 로직 검증 필요.

### 5. 테스트 코드 (Tests)
- **상태**: ❓ 확인 필요
- **내용**: 주요 비즈니스 로직(AI 분석 Facade, 경험 통계 등)에 대한 단위/통합 테스트 커버리지 확충 권장.

---

## 📅 향후 개선 제안 (Future Improvements)

1. **소셜 로그인 연동**: OAuth2 (Google, Github, Kakao) 도입으로 접근성 향상
2. **다크 모드 완벽 지원**: 현재 SCSS 일부에만 적용된 다크 모드를 시스템 전반으로 확장
3. **모바일 최적화**: 테이블 뷰 등의 모바일 반응형 레이아웃 개선
