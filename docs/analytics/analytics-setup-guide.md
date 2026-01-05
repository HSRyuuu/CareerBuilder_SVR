# CareerBuilder Analytics 설정 가이드

## 📋 개요

CareerBuilder는 다음 PA(Product Analytics) 도구를 지원합니다:
- **Google Analytics 4 (GA4)** - 웹 분석 표준
- **Mixpanel** - 제품 분석 및 퍼널 분석

두 도구를 동시에 사용하여 데이터 정합성을 검증하고, 각 도구의 장점을 활용합니다.

---

## 🔧 환경변수 설정

### 필수 환경변수

`.env` 파일에 다음 변수를 설정하세요:

```bash
# Google Analytics 4
NUXT_PUBLIC_GA4_MEASUREMENT_ID=G-XXXXXXXXXX

# Mixpanel
NUXT_PUBLIC_MIXPANEL_TOKEN=your-mixpanel-project-token

# 디버그 모드 (개발 환경에서 true)
NUXT_PUBLIC_ANALYTICS_DEBUG=true
```

### 선택적 환경변수 (PostHog 사용 시)

```bash
# PostHog (선택적)
NUXT_PUBLIC_POSTHOG_KEY=phc_xxxxxxxxxxxxx
NUXT_PUBLIC_POSTHOG_HOST=https://us.i.posthog.com
```

---

## 🛠️ GA4 설정 방법

### 1. GA4 속성 생성

1. [Google Analytics](https://analytics.google.com/) 접속
2. 관리 → 속성 만들기
3. 웹 스트림 추가
4. **Measurement ID** 복사 (G-XXXXXXXXXX 형식)

### 2. 맞춤 이벤트 등록

GA4 관리 → 이벤트 → 이벤트 만들기에서 다음 이벤트를 전환으로 표시:

| 이벤트명 | 전환 표시 |
|----------|-----------|
| `user_signup_completed` | ✅ |
| `achievement_created` | ✅ |
| `ai_summary_completed` | ✅ |

### 3. 맞춤 측정기준 등록

GA4 관리 → 맞춤 정의 → 맞춤 측정기준 만들기:

| 측정기준 이름 | 이벤트 매개변수 | 범위 |
|---------------|-----------------|------|
| 가입 방법 | `method` | 이벤트 |
| 성과 ID | `achievement_id` | 이벤트 |
| 첫 성과 여부 | `is_first` | 이벤트 |
| 단계 이름 | `step_name` | 이벤트 |

---

## 🛠️ Mixpanel 설정 방법

### 1. 프로젝트 토큰 확인

1. [Mixpanel](https://mixpanel.com/) 접속
2. Settings → Project Settings
3. **Project Token** 복사

### 2. 핵심 이벤트 설정

Data Management → Lexicon에서 이벤트 정의:

| 이벤트명 | 표시 이름 | 설명 |
|----------|-----------|------|
| `user_signup_completed` | 회원가입 완료 | 신규 사용자 가입 |
| `achievement_created` | 성과 등록 | 새 성과 저장 완료 |
| `ai_summary_requested` | AI 요약 요청 | AI 기능 사용 |

### 3. 퍼널 생성

Funnels에서 다음 퍼널 생성:

**Activation Funnel:**
1. `user_signup_completed`
2. `achievement_created` (where `is_first` = true)

**Engagement Funnel:**
1. `achievement_created`
2. `ai_summary_requested`

---

## 💻 코드에서 사용하기

### 기본 사용법

```typescript
import { useAnalytics } from '@/composables/useAnalytics';

const { track, identify, initAnalytics } = useAnalytics();

// 앱 시작 시 초기화 (app.vue 또는 레이아웃에서)
onMounted(() => {
  initAnalytics();
});

// 이벤트 트래킹
track('achievement_created', {
  achievement_id: 'ach_123',
  word_count: 500,
  section_count: 3,
  has_goal: true,
  has_impact: true,
  creation_time_sec: 180,
  is_first: true
});

// 사용자 식별 (로그인 후)
identify('user_123', {
  email: 'user@example.com',
  signup_method: 'google',
  created_at: '2024-01-01'
});
```

### 페이지 뷰 자동 추적

레이아웃에서 라우트 변경 시 자동 페이지 뷰:

```typescript
// layouts/default.vue
const { track } = useAnalytics();
const route = useRoute();

watch(() => route.path, (newPath) => {
  track('page_view', {
    page_name: route.name as string,
    page_path: newPath,
    referrer: document.referrer
  });
}, { immediate: true });
```

---

## 🐛 디버깅

### 개발 환경 디버그 모드

`.env`에서 디버그 활성화:
```bash
NUXT_PUBLIC_ANALYTICS_DEBUG=true
```

모든 이벤트가 브라우저 콘솔에 로깅됩니다:
```
[Analytics Debug] Track: achievement_created { achievement_id: 'ach_123', ... }
```

### GA4 DebugView

1. GA4 → 관리 → DebugView
2. Chrome 확장 프로그램 "Google Analytics Debugger" 설치
3. 실시간으로 이벤트 확인

### Mixpanel Live View

1. Mixpanel → Activity → Live View
2. 실시간 이벤트 스트림 확인

---

## ✅ 설정 체크리스트

- [ ] GA4 Measurement ID 환경변수 설정
- [ ] Mixpanel Token 환경변수 설정
- [ ] 개발 환경 디버그 모드 활성화
- [ ] GA4 맞춤 이벤트 전환 표시
- [ ] Mixpanel 퍼널 설정
- [ ] 이벤트 발생 테스트 완료

---

## 🔗 관련 문서

- [이벤트 설계 가이드](./event-design-guide.md)
- [KPI 트래킹 가이드](./kpi-tracking-guide.md)
