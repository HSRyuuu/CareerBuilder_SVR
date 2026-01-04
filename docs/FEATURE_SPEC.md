# 📋 프로젝트 기능 명세서 (Project Feature Specification)

Career Builder 서비스의 주요 기능 명세입니다.

## 1. 회원 시스템 (User System)

### 1.1 인증 (Authentication)
- **회원가입 (`/api/auth/signup`)**
  - 아이디(Username), 이메일 중복 체크 기능 제공
  - 비밀번호 암호화 저장
- **로그인 (`/api/auth/login`)**
  - JWT 기반 인증 (추정) 또는 세션 기반 인증
- **로그아웃 (`/api/auth/logout`)**
  - 안전한 로그아웃 처리
- **내 정보 조회 (`/api/users/me`)**
  - 사용자 기본 프로필 정보 조회
  - AI 사용량(Subscription Usage) 조회

### 1.2 알림 (Notification)
- **알림 목록 조회**
  - 읽음/안읽음 필터링 지원
  - 페이징 처리 지원
- **알림 읽음 처리**
  - 개별 알림 확인 상태 변경

---

## 2. 경험 관리 (Experience Management)

### 2.1 경험 기록 (Experience CRUD)
- **경험 생성 (`POST /api/experiences`)**
  - S-T-A-R 기법 등 구조화된 폼을 통한 경험 입력
  - 기간, 역할, 사용 기술 등 메타데이터 저장
- **경험 조회 및 검색**
  - **목록 조회**: 페이징, 정렬(최신순 등) 지원
  - **검색**: 키워드 검색, 상태별(작성중, 완료, AI 분석됨) 필터링
  - **상세 조회**: 개별 경험의 상세 내용 확인
- **경험 수정/삭제**
  - 기존 기록된 경험의 내용 업데이트 및 삭제

### 2.2 통계 (Statistics)
- **통계 요약 (`/api/experiences/stats/summary`)**
  - 전체 경험 수
  - '완성된 경험', '보완 필요 경험' 카운트
  - AI 분석 완료 건수 집계

---

## 3. AI 커리어 분석 (AI Analysis)

### 3.1 경험 분석
- **AI 분석 요청 (`/api/v1/ai/experiences/{id}/analyze`)**
  - 작성된 경험 내용을 바탕으로 AI 분석 트리거
  - 비동기 처리 (Facade 패턴 사용 확인됨)
- **분석 결과 조회**
  - AI가 추출한 핵심 역량 확인
  - 성과 중심으로 재구성된 문장(Result) 확인
- **분석 상태 관리**
  - 분석 중, 분석 완료 등 상태 추적

---

## 4. 프론트엔드 페이지 구조 (Frontend Map)

| 페이지 경로 | 설명 | 주요 기능 |
|------------|------|----------|
| `/welcome` | 랜딩 페이지 | 서비스 소개 및 시작하기/체험하기 유도 |
| `/auth/login` | 로그인 | 사용자 로그인 |
| `/auth/signup` | 회원가입 | 신규 계정 생성 |
| `/career` | 경험 목록 (메인) | 경험 카드/테이블 리스트, 통계 대시보드 |
| `/career/{id}` | 경험 상세 | 경험 상세 조회 및 수정 |
| `/career/register` | 경험 등록 | 새로운 경험 작성 폼 |
| `/ai/experience` | AI 분석 | (경험 목록에서 트리거) AI 분석 결과를 별도로 확인/관리 |

---

## 5. 시스템/기타 (System)
- **사용자 행동 로그 (`/api/logs`)**
  - 주요 액션에 대한 사용자 로그 수집 (User Agent, IP 포함)
- **Swagger UI**
  - API 명세 자동화 및 테스트 환경 제공
