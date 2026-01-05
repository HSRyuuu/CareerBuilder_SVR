# CareerBuilder 이벤트 설계 가이드

## 📋 이벤트 네이밍 규칙

### 기본 원칙
1. **snake_case** 사용 (예: `user_signup_completed`)
2. **명사_동사** 또는 **카테고리_액션** 형식
3. 과거형 동사 사용 (completed, created, viewed)
4. 명확하고 일관된 이름 사용

### 카테고리 접두사
| 접두사 | 설명 | 예시 |
|--------|------|------|
| `page_` | 페이지 관련 | `page_view`, `page_leave` |
| `user_` | 사용자 계정 관련 | `user_signup_completed`, `user_login` |
| `achievement_` | 성과 관련 | `achievement_created`, `achievement_viewed` |
| `ai_` | AI 기능 관련 | `ai_summary_requested`, `ai_summary_completed` |
| `onboarding_` | 온보딩 관련 | `onboarding_started`, `onboarding_completed` |
| `career_` | 경력/이력서 관련 | `career_register_started` |

---

## 📊 이벤트 카탈로그

### 1. 페이지 이벤트

#### `page_view`
페이지 조회 시 자동 발생

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `page_name` | string | ✅ | 페이지 이름 (home, career_register 등) |
| `page_path` | string | ✅ | URL 경로 |
| `referrer` | string | ❌ | 이전 페이지 URL |

#### `page_leave`
페이지 이탈 시 발생

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `page_name` | string | ✅ | 페이지 이름 |
| `time_spent_sec` | number | ✅ | 체류 시간(초) |

---

### 2. 사용자 이벤트

#### `user_signup_started`
회원가입 폼 진입 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `method` | enum | ✅ | 가입 방법: `email`, `google`, `kakao`, `naver` |

#### `user_signup_completed`
회원가입 완료 시 (Primary KPI)

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `method` | enum | ✅ | 가입 방법 |
| `signup_time_sec` | number | ✅ | 가입 소요 시간(초) |

#### `user_login`
로그인 성공 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `method` | enum | ✅ | 로그인 방법 |

#### `user_logout`
로그아웃 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| (없음) | - | - | - |

---

### 3. 성과(Achievement) 이벤트 ⭐ 핵심

#### `career_register_started`
성과 등록 페이지 진입 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `source` | enum | ✅ | 진입 경로: `home_cta`, `navbar`, `direct` |

#### `achievement_form_step_completed`
성과 등록 폼 단계 완료 시 (퍼널 분석용)

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `step` | number | ✅ | 단계 번호 (1-5) |
| `step_name` | string | ✅ | 단계 이름 |
| `time_spent_sec` | number | ❌ | 해당 단계 소요 시간 |

**단계 정의:**
1. `basic_info` - 기본 정보 (제목, 기간, 소속)
2. `work_info` - 업무 정보 (유형, 기여도)
3. `goal` - 목표
4. `impact` - 핵심 성과
5. `skills` - 스킬

#### `achievement_created` ⭐ NSM 핵심 이벤트
성과 저장 완료 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `achievement_id` | string | ✅ | 성과 ID |
| `word_count` | number | ✅ | 총 글자 수 |
| `section_count` | number | ✅ | 블록 개수 |
| `has_goal` | boolean | ✅ | 목표 입력 여부 |
| `has_impact` | boolean | ✅ | 핵심 성과 입력 여부 |
| `creation_time_sec` | number | ✅ | 작성 소요 시간 |
| `is_first` | boolean | ✅ | 첫 번째 성과 여부 (Activation 측정) |

#### `achievement_updated`
성과 수정 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `achievement_id` | string | ✅ | 성과 ID |
| `updated_fields` | string[] | ✅ | 수정된 필드 목록 |

#### `achievement_deleted`
성과 삭제 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `achievement_id` | string | ✅ | 성과 ID |

---

### 4. AI 이벤트 (구독 전환 선행지표)

#### `ai_summary_requested`
AI 요약 요청 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `provider` | enum | ✅ | AI 제공자: `gemini`, `openai` |
| `content_type` | enum | ✅ | 콘텐츠 유형: `achievement`, `career`, `resume` |
| `content_length` | number | ✅ | 입력 콘텐츠 길이 |

#### `ai_summary_completed`
AI 요약 완료 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `provider` | enum | ✅ | AI 제공자 |
| `latency_ms` | number | ✅ | 응답 시간(ms) |
| `success` | boolean | ✅ | 성공 여부 |
| `token_count` | number | ❌ | 사용된 토큰 수 |

#### `ai_summary_failed`
AI 요약 실패 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `provider` | enum | ✅ | AI 제공자 |
| `error_type` | string | ✅ | 에러 유형 |
| `error_message` | string | ❌ | 에러 메시지 |

---

### 5. CTA 클릭 이벤트

#### `cta_clicked`
주요 CTA 버튼 클릭 시

| 속성 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `cta_name` | string | ✅ | CTA 이름 (start_register, view_analysis 등) |
| `cta_location` | string | ✅ | CTA 위치 (home_hero, navbar, sidebar) |
| `destination` | string | ❌ | 이동할 페이지 |

---

## 🛠️ 구현 예시

### Vue 컴포넌트에서 이벤트 발생

```typescript
import { useAnalytics } from '@/composables/useAnalytics';

const { track } = useAnalytics();

// 성과 등록 시작
track('career_register_started', {
  source: 'home_cta'
});

// 성과 저장 완료
track('achievement_created', {
  achievement_id: 'ach_123',
  word_count: 500,
  section_count: 3,
  has_goal: true,
  has_impact: true,
  creation_time_sec: 180,
  is_first: true
});
```

---

## ✅ 체크리스트

이벤트 추가 시 다음을 확인하세요:

- [ ] 이벤트 이름이 snake_case인가?
- [ ] 필수 속성이 모두 포함되었는가?
- [ ] event-schema.ts에 타입이 정의되었는가?
- [ ] 이 문서에 이벤트가 기록되었는가?
- [ ] 테스트 환경에서 이벤트 발생 확인했는가?
