# Career Builder

**Career Builder**는 사용자의 파편화된 업무 경험을 체계적으로 관리하고, AI 분석을 통해 커리어 자산으로 전환해주는 서비스입니다.단순한 이력 관리를 넘어, 성과 중심의 커리어 브랜딩을 돕습니다.

## 🚀 프로젝트 소개

Career Builder는 사용자가 입력한 경험을 AI가 분석하여 핵심 역량을 추출하고, 이를 바탕으로 설득력 있는 커리어 문장을 생성해줍니다. 직무 역량 매핑, 성장 추이 분석 등을 통해 사용자는 자신의 커리어를 객관적인 데이터로 관리할 수 있습니다.

## 🛠 기술 스택 (Tech Stack)

### Backend
- **Language**: Kotlin
- **Framework**: Spring Boot 3
- **ORM**: JPA (Hibernate), QueryDSL
- **API Documentation**: Swagger (OpenAPI 3.0)
- **Database**: H2 / MySQL (추정)
- **Build Tool**: Gradle

### Frontend
- **Framework**: Nuxt 3 (Vue 3)
- **UI Library**: Vuetify
- **Language**: TypeScript
- **Styling**: SCSS (Scoped)
- **Design Pattern**: Atomic Design Pattern
- **State Management**: Pinia (Nuxt 3 built-in)

## 📂 프로젝트 구조

```bash
CareerBuilder/
├── backend/    # Spring Boot API 서버
└── frontend/   # Nuxt.js 웹 애플리케이션
```

## ✨ 주요 기능 요약

- **경험 관리**: 업무 경험(Project, Task) 기록 및 관리 (CRUD)
- **AI 커리어 분석**: 입력된 경험을 AI가 분석하여 역량 및 성과 요약 제공
- **대시보드**: 커리어 통계, 성장 그래프, 역량 분포 시각화
- **사용자 관리**: 회원가입, 로그인, 개인화 설정
- **알림 시스템**: 분석 완료 및 주요 이벤트 알림

## 🚦 시작하기 (Getting Started)

각 디렉토리의 `README.md`를 참조하여 서버와 클라이언트를 실행하세요.

- [Backend 실행 방법](./backend/README.md)
- [Frontend 실행 방법](./frontend/README.md)
