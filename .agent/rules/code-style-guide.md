---
trigger: always_on
---

# CareerBuilder 프로젝트 개발 규칙

이 프로젝트는 **Career Builder** 애플리케이션으로, 프론트엔드(Nuxt.js/Vue)와 백엔드(Kotlin/Spring Boot)로 구성되어 있습니다.

## 📁 프로젝트 구조

```
CareerBuilder_SVR/
├── frontend/           # Nuxt.js + Vue 3 + TypeScript + Vuetify
│   ├── components/    # Atomic Design 패턴
│   ├── pages/         # Nuxt 페이지
│   ├── composables/   # Vue Composables
│   ├── constants/     # 상수 및 Enum
│   └── types/         # 타입 정의
├── backend/           # Kotlin + Spring Boot
│   └── src/
└── init/              # Docker 설정
```
---

## 디자인 규칙


---

## 🔧 프론트엔드 규칙 (Vue3 + Nuxt3)


### 프론트엔드 작업 시 반드시 준수

** 다크모드를 고려한 CSS 작성 **

**코드 작성 전 `frontend/convention.md` 파일을 참고하세요.**

이 파일에는 다음 내용이 포함되어 있습니다:
- Vue 파일 구조 및 작성 순서
- API 호출 구조 (api.ts, types.ts, keys.ts)
- 컴포넌트 구조 (Atomic Design)
- 타입 정의 규칙
- 네이밍 컨벤션
- Composables 작성 규칙

## 📋 프론트엔드 주요 규칙

### 1. Vue 파일 작성 순서

**Script 섹션 순서:**
1. 외부 라이브러리 import (Vue, Vuetify 등)
2. 프로젝트 내부 import (constants, enums, utils)
3. API/Composables import
4. Type import (type 키워드 사용)
5. 로컬 컴포넌트 import
6. Type 선언 (Props, Emits용)
7. Props 선언
8. Emits 선언
9. Ref/Reactive 선언
10. Computed 선언
11. 함수 선언
12. Validation 스키마

### 2. API 구조 (필수)

각 도메인별로 4개 파일을 생성:
```
api/{domain}/
├── api.ts            # 순수 API 호출 함수
└──types.ts          # 타입 정의
└── keys.ts           # 쿼리 키 상수
```

**API 함수 네이밍:**
- 조회(GET): `fetch{Resource}`, `fetch{Resource}s`
- 생성(POST): `create{Resource}`
- 수정(PUT): `update{Resource}`
- 삭제(DELETE): `delete{Resource}`

### 3. 컴포넌트 구조 (Atomic Design)

```
components/
├── atoms/         # Button, Input, Select 등
├── molecules/     # Pagination, Tabs, SearchBar 등
├── organisms/     # Table, Form, Dialog 등
└── templates/     # Layout, ErrorBoundary 등
```

**특징:**
- **Atoms**: 최소 단위, 재사용성 높음, 비즈니스 로직 없음
- **Molecules**: Atoms 조합, 간단한 상태 관리
- **Organisms**: 복잡한 로직, API 호출 가능

### 4. 타입 정의 규칙

```typescript
// Type: T prefix
export type TUser = { ... };
export type TButtonProps = { ... };

// Enum: Const Object 선호
export const Color = {
  Primary: 'primary',
  Secondary: 'secondary',
} as const;

export type TColor = (typeof Color)[keyof typeof Color];
```

### 5. 네이밍 컨벤션

```typescript
// 파일명: PascalCase
Button.vue, UserProfile.vue

// 변수/함수: camelCase
const userName = 'John';
const fetchUser = () => {};

// 상수: SCREAMING_SNAKE_CASE
const API_BASE_URL = 'https://api.example.com';
const DATA_RESOURCE_LIST_KEY = 'data-resource-list';

// Boolean: is, has, should prefix
const isLoading = ref(false);
const hasError = computed(() => ...);

// Handler: handle prefix
const handleSubmit = () => {};
const handleClickDelete = () => {};

// Composable: use prefix
const useFetchUsers = () => {};
```

### 6. Props & Emits

```vue
<script setup lang="ts">
// Props: destructuring + 기본값
export type TButtonProps = {
  size?: Size;
  color?: Color;
};

const {
  size = Size.M,
  color = Color.Primary,
} = defineProps<TButtonProps>();

// Emits: 타입 안전하게
const emit = defineEmits<{
  'update:model-value': [string];
  'click-submit': [void];
}>();
</script>
```

## 🔧 백엔드 규칙 (Kotlin + Spring Boot)

### 1. 패키지 구조
```
com.hsryuuu.careerbuilder/
├── domain/           # 도메인별 패키지
│   ├── user/
│   ├── career/
│   └── auth/
├── global/           # 공통 설정
└── common/           # 공통 유틸
```

### 2. 코딩 스타일
- Kotlin 표준 컨벤션 준수
- Data class 활용
- Null safety 고려
- Coroutine 사용 시 적절한 스코프 관리

## ✅ 코드 작성 체크리스트

작업 시작 전:
- [ ] `frontend/convention.md` 파일을 확인했는가?
- [ ] API 구조 3개 파일 (api.ts, types.ts, keys.ts)를 생성했는가?
- [ ] Type 이름은 T prefix를 사용했는가?
- [ ] Composable 함수는 use prefix를 사용했는가?

코드 작성 시:
- [ ] Vue 파일 작성 순서를 따랐는가?
- [ ] Props는 destructuring + 기본값으로 선언했는가?
- [ ] Emits는 타입 안전하게 선언했는가?
- [ ] 컴포넌트는 Atomic Design 패턴을 따랐는가?
- [ ] 네이밍 컨벤션을 준수했는가?

## 💡 추가 지침

### 응답 언어
- 모든 대화는 **한글**로 진행합니다.

### 코드 품질
- TypeScript strict 모드 활성화
- ESLint 규칙 준수
- 불필요한 any 타입 사용 금지
- Props/Emits는 항상 타입 정의

### 에러 처리
- API 에러는 useClientFetch 내부에서 처리
- 사용자 친화적인 에러 메시지 제공
- useSnackbar로 알림 표시

### 성능 최적화
- Computed 적극 활용
- 불필요한 watch 지양
- v-if vs v-show 적절히 구분
- 큰 리스트는 가상 스크롤 고려

## 📚 참고 문서

- **프론트엔드 컨벤션**: `frontend/convention.md` (필수 참고)
- **프로젝트 README**: `README.md`
- **프론트엔드 README**: `frontend/README.md`

---

**중요**: 프론트엔드 코드 작업 시 반드시 `frontend/convention.md` 파일의 상세 규칙을 따르세요.