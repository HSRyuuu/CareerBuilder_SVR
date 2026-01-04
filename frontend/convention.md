# 프론트엔드 개발 컨벤션

## 📋 목차

1. [Vue 파일 구조](#1-vue-파일-구조)
2. [API 호출 구조](#2-api-호출-구조)
3. [컴포넌트 구조](#3-컴포넌트-구조)
4. [타입 정의](#4-타입-정의)
5. [네이밍 컨벤션](#5-네이밍-컨벤션)
6. [Composables](#6-composables)

---

## 1. Vue 파일 구조

### 1.1 기본 구조

모든 `.vue` 파일은 다음 순서를 따릅니다:

```vue
<template>
  <!-- 템플릿 코드 -->
</template>

<script setup lang="ts">
// 스크립트 코드 (아래 순서 참고)
</script>

<style lang="scss" scoped>
// 스타일 코드
</style>
```

### 1.2 Script 섹션 작성 순서

#### **순서 규칙**

1. **외부 라이브러리 import** (Vue, Vuetify 등)
2. **프로젝트 내부 import** (constants, enums, utils 등)
3. **API import**
4. **Type import** (type 키워드 사용)
5. **로컬 컴포넌트 import**
6. **Type 선언** (Props, Emits용 타입)
7. **Props 선언**
8. **Emits 선언**
9. **Ref/Reactive 선언**
10. **Computed 선언**
11. **함수 선언** (핸들러, 유틸리티 함수)
12. **Validation 스키마** (yup 등)

#### **예시: 복잡한 페이지 컴포넌트**

```vue
<script lang="ts" setup>
// 1. 외부 라이브러리 import
import { ref, computed } from 'vue';
import * as yup from 'yup';

// 2. 프로젝트 내부 import (constants, enums, utils)
import { Color, FormSize, Size, Variant } from '@/constants/enums/style-enum';
import { DacConstants } from '@/constants/enums/dac-enum';
import { defaultCollectSpec } from '@/utils/dac-utils';
import { PATH_DATA_RESOURCE } from '@/routes/path';

// 3. API import
import { createDataSource } from '@/api/resource/api';

// 4. Type import (type 키워드 사용)
import type { TDataResourceCommon, TDataResourceDetail } from '@/api/resource/types';
import type { TDataSchemaColumn } from '@/constants/types/dac-types';
import type { TCollectSpec, TLoadTable } from '@/constants/types/dac-entity';

// 5. 로컬 컴포넌트 import
import CommonData from '@/pages/data/resource/_components/detail/CommonData.vue';
import TabFileUpload from '@/pages/data/resource/_components/regist/TabFileUpload.vue';
import TabDb from '@/pages/data/resource/_components/regist/TabDb.vue';
import TabApi from '@/pages/data/resource/_components/regist/TabApi.vue';

// 6. Ref 선언 (컴포넌트 참조)
const commonDataRef = ref<InstanceType<typeof CommonData>>();
const tabFileUploadRef = ref<InstanceType<typeof TabFileUpload>>();
const tabDbRef = ref<InstanceType<typeof TabDb>>();
const tabApiRef = ref<InstanceType<typeof TabApi>>();

// 7. 상수 선언
const tabOptions = [
  {
    title: '파일 업로드(수동)',
    value: DacConstants.Dataset,
  },
  {
    title: 'DB 연동(자동)',
    value: DacConstants.DB,
  },
  {
    title: 'API 연동(자동)',
    value: DacConstants.API,
  },
];

// 8. Ref/Reactive 선언 (상태)
const currentTab = ref<string>(tabOptions[0].value);

// 9. 함수 선언 (핸들러)
const handleTabChange = (tab: string | any) => {
  if (tab === DacConstants.Dataset) {
    tabFileUploadRef.value?.refreshData();
  } else if (tab === DacConstants.DB) {
    tabDbRef.value?.refreshData();
  } else if (tab === DacConstants.API) {
    tabApiRef.value?.refreshData();
  }
};

const handleCancel = async () => {
  const { confirm } = useConfirm();
  const isConfirmed = await confirm({
    title: '데이터 등록 취소',
    message: '데이터 등록을 취소하시겠습니까? 입력값이 초기화됩니다.',
  });
  if (!isConfirmed) return;
  await navigateTo(PATH_DATA_RESOURCE);
};

const handleSave = async () => {
  // 데이터 공통 정보
  const commonData = commonDataRef.value?.getCommonData() as TDataResourceCommon;
  commonData.type = currentTab.value;

  let type = '';
  let collectSpec: TCollectSpec = defaultCollectSpec;
  let loadTable: TLoadTable = {};
  let loadColumnList: TDataSchemaColumn[] = [];
  let sourceId: string | undefined;

  // 탭 별 데이터 조회
  if (currentTab.value === DacConstants.Dataset) {
    const tabFileUploadData = tabFileUploadRef.value?.getTabFileUploadData();
    if (tabFileUploadData) {
      type = tabFileUploadData.type;
      collectSpec = tabFileUploadData.collectSpec;
      loadTable = tabFileUploadData.table;
      loadColumnList = tabFileUploadData.columns;
    }
  }

  // 데이터 리소스 생성 API body
  const body: TDataResourceDetail = {
    sourceId: sourceId,
    type: type,
    dataResource: commonData,
    collectSpec: collectSpec,
    loadTable: loadTable,
    loadColumnList: loadColumnList,
  };

  // 생성 요청
  const { error } = await createDataSource(body);
  if (error) {
    return;
  }

  // 성공
  useSnackbar().success('저장되었습니다.');
  await navigateTo(PATH_DATA_RESOURCE);
};
</script>
```

#### **예시: Atom 컴포넌트 (Button)**

```vue
<script setup lang="ts">
// 1. 외부 라이브러리 import
import { VBtn, VIcon } from 'vuetify/lib/components/index.mjs';

// 2. 프로젝트 내부 import
import { Color, Size, Variant } from '@/constants/enums/style-enum';

// 3. Type 선언
export type TButtonProps = {
  size?: Size;
  color?: Color | string;
  variant?: Variant;
  disabled?: boolean;
  readonly?: boolean;
  useRipple?: boolean;
  useOnlyIcon?: boolean | string;
};

// 4. Props 선언 (기본값 포함)
const {
  size = Size.M,
  color = Color.Primary,
  variant = Variant.Flat,
  disabled = false,
  readonly = false,
  useRipple = false,
  useOnlyIcon = false,
} = defineProps<TButtonProps>();
</script>
```

#### **예시: Molecule 컴포넌트 (Pagination)**

```vue
<script setup lang="ts">
// 1. 외부 라이브러리 import
import { computed } from 'vue';

// 2. 프로젝트 내부 import
import { Variant } from '@/constants/enums/style-enum';

// 3. Type 선언
export type TPaginationProps = {
  currentPage: number;
  totalCount: number;
  pageSize: number;
};

// 4. Props 선언
const { currentPage, totalCount, pageSize } = defineProps<TPaginationProps>();

// 5. Emits 선언
const emit = defineEmits<{
  'update:currentPage': [number];
}>();

// 6. Computed 선언
const lastPage = computed(() => Math.ceil(totalCount / pageSize));

const visiblePages = computed(() => {
  if (totalCount <= 0 || pageSize <= 0) return [];

  const groupSize = 10;
  const last = lastPage.value;

  const start = Math.floor((currentPage - 1) / groupSize) * groupSize;
  const end = Math.min(start + groupSize, last);

  return Array.from({ length: end - start }, (_, i) => start + i + 1);
});

// 7. 함수 선언
const handleClickPrev = () => {
  if (currentPage === 1) return;
  changePage(currentPage - 1);
};

const handleClickNext = () => {
  if (currentPage === lastPage.value) return;
  changePage(currentPage + 1);
};

const changePage = (page: number) => {
  emit('update:currentPage', page);
};
</script>
```

### 1.3 주요 규칙

#### **Props 선언**

```typescript
// ✅ 좋은 예: destructuring + 기본값
const { size = Size.M, color = Color.Primary } = defineProps<TButtonProps>();

// ❌ 나쁜 예: withDefaults 사용
const props = withDefaults(defineProps<TButtonProps>(), {
  size: Size.M,
});
```

#### **Emits 선언**

```typescript
// ✅ 좋은 예: 타입 안전하게 선언
const emit = defineEmits<{
  'update:currentPage': [number];
  change: [string, boolean];
  submit: [TFormData];
}>();

// 사용
emit('update:currentPage', 5);
emit('change', 'value', true);
```

#### **Ref 타입 선언**

```typescript
// ✅ 좋은 예: 타입 명시
const count = ref<number>(0);
const data = ref<TDataResource | null>(null);
const list = ref<TDataResource[]>([]);
const componentRef = ref<InstanceType<typeof MyComponent>>();

// ❌ 나쁜 예: 타입 생략
const count = ref(0); // 타입 추론되지만 명시적이지 않음
```

---

## 2. API 호출 구조

API 관련 코드는 `/api/{도메인}/` 폴더에 2개 파일(`api.ts`, `types.ts`)로 구성됩니다.

### 2.1 디렉터리 구조

```
api/
├── resource/              # 도메인별 폴더
│   ├── api.ts            # 순수 API 호출 함수
│   ├── types.ts          # 타입 정의
├── category/
│   ├── api.ts
│   └── types.ts
└── license/
    ├── api.ts
    └── types.ts
```

### 2.2 파일별 역할 및 작성 규칙

#### **api.ts - 순수 API 호출 함수**

```typescript
// 1. Type import
import type { TFetchOptions } from '@/constants/types/api';
import { Method } from '@/constants/enums/common';
import type {
  TDataResource,
  TDataResourceCommon,
  TDataResourceDetail,
  TDistribution,
  TGroup,
} from './types';

// 2. API 함수 정의

// GET - 목록 조회
export const fetchExperiences = (params?: TExperienceListParams) => {
  return useApi<TPageResponse<TExperience>>({
    url: '/api/experiences',
    method: HttpMethod.GET,
    params,
  });
};

// GET - 단건 조회
export const fetchExperience = (id: string) => {
  return useApi<TExperience>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.GET,
  });
};

// POST - 생성
export const updateExperience = (id: string, body: TExperienceUpdate) => {
  return useApi<TExperience>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.PUT,
    body,
  });
};

// PUT - 수정
export const updateDataResource = (id: string, body: TDataResourceCommon): Promise<void> => {
  return useApi({
    url: `/api/data/resources/${id}`,
    method: Method.Put,
    body,
  });
};

// DELETE - 삭제
export const deleteExperience = (id: string) => {
  return useApi<null>({
    url: `/api/experiences/${id}`,
    method: HttpMethod.DELETE,
  });
};

// Blob 다운로드
export const downloadDistribution = (distributionId: string): Promise<Blob> => {
  return useApi({
    url: `/api/data/distributions/${distributionId}/download`,
    method: Method.Get,
    fetchOptions: {
      responseType: 'blob',
    },
  });
};
```

**작성 규칙:**

- 함수명 규칙:
  - 조회(GET): `fetch{Resource}`, `fetch{Resource}s`
  - 생성(POST): `create{Resource}`
  - 수정(PUT): `update{Resource}`
  - 삭제(DELETE): `delete{Resource}`
  - 다운로드: `download{Resource}`
- 파라미터 순서: `id`, `body`, `fetchOptions`
- 반환 타입: `Promise<T>` 명시 필수
- `useApi` 사용
- Method enum 사용 (문자열 직접 사용 금지)

#### **types.ts - 타입 정의**

```typescript
// 1. 공통 타입 import
import type { YN } from '@/constants/enums/common';
import type { TCollectSpec } from '@/constants/types/dac-entity';

// 2. 도메인 타입 정의

// API 응답 타입 (Response)
export type TDataResource = {
  id?: string;
  title?: string;
  type?: string;
  accessRights?: YN;
  keywords?: string;
  description?: string;
  viewCnt?: number;
  creator?: string;
  issued?: string;
  modified?: string;
  // 관계 ID
  categoryId?: string;
  organizationId?: string;
  licenseId?: string;
  // 메타 정보 (조인 결과)
  groupName?: string;
  categoryName?: string;
  organizationName?: string;
  licenseName?: string;
};

// API 요청 타입 (Request)
export type TDataResourceCommon = {
  type?: string;
  title?: string;
  accessRights?: YN;
  keyword?: string;
  contactPoint?: string;
  organizationId?: string;
  licenseId?: string;
  groupId?: string;
  categoryId?: string;
  description?: string;
  username?: string;
};

// 복합 타입
export type TDataResourceDetail = {
  resourceId?: string;
  sourceId?: string;
  type?: string;
  dataResource?: TDataResourceCommon;
  collectSpec: TCollectSpec;
  loadTable?: TLoadTable;
  loadColumnList?: TLoadColumn[];
};

// 관련 타입
export type TDistribution = {
  id?: string;
  partition?: number;
  step: string;
  status: string;
  downloadTitle?: string;
  downloadUrl?: string;
  format?: string;
  byteSize?: number;
  modified?: string;
};

export type TGroup = {
  id: string;
  name: string;
  path: string;
  subGroupCount: number;
};
```

**작성 규칙:**

- Type 이름: `T` prefix + PascalCase
  - 예: `TDataResource`, `TUserInfo`
- Optional 필드: `?` 사용
- 필수 필드는 `?` 없이 선언
- 공통 타입은 `/constants/types/`에서 import
- Request/Response 구분
  - Response: 기본 타입명
  - Request: `{Type}Common`, `{Type}Create`, `{Type}Update`

---

## 3. 컴포넌트 구조

### 3.1 Atomic Design 패턴

컴포넌트는 **Atomic Design** 패턴을 따릅니다.

```
components/
├── atoms/              # 가장 작은 단위
├── molecules/          # atoms 조합
├── organisms/          # molecules 조합
├── templates/          # 페이지 레이아웃
└── pages/              # 페이지별 특화 컴포넌트
```

### 3.2 각 레벨별 정의

#### **Atoms (원자)**

**정의**: 더 이상 분해할 수 없는 최소 단위 컴포넌트

**특징**:

- 재사용성이 매우 높음
- 비즈니스 로직 없음
- Props로만 동작
- 단일 HTML 요소 또는 UI 라이브러리 컴포넌트 래핑

**예시**: `Button`, `Input`, `Select`, `Checkbox`, `Radio`, `Tooltip`, `TextArea`

```
atoms/
├── Button/
│   ├── Button.vue          # 컴포넌트
│   ├── Button.scss         # 스타일 (선택사항)
│   └── Button.stories.tsx  # Storybook (선택사항)
├── Input/
│   ├── Input.vue
│   └── Input.stories.tsx
├── Select/
│   ├── Select.vue
│   └── Select.stories.tsx
└── Checkbox/
    └── Checkbox.vue
```

**코드 특징**:

```vue
<template>
  <VBtn class="button" :size="size" :color="color" :variant="variant" :disabled="disabled">
    <slot></slot>
  </VBtn>
</template>

<script setup lang="ts">
import { VBtn } from 'vuetify/lib/components/index.mjs';
import { Color, Size, Variant } from '@/constants/enums/style-enum';

export type TButtonProps = {
  size?: Size;
  color?: Color;
  variant?: Variant;
  disabled?: boolean;
};

const {
  size = Size.M,
  color = Color.Primary,
  variant = Variant.Flat,
  disabled = false,
} = defineProps<TButtonProps>();
</script>
```

#### **Molecules (분자)**

**정의**: 2개 이상의 Atoms를 조합한 컴포넌트

**특징**:

- 특정 기능을 수행하는 작은 단위
- 간단한 상태 관리 가능
- 재사용 가능
- Emit 이벤트 사용

**예시**: `Pagination`, `SearchBar`, `Tabs`, `BreadCrumb`, `CheckboxGroup`, `KeyValueInput`

```
molecules/
├── Pagination/
│   ├── Pagination.vue
│   └── Pagination.scss
├── Tabs/
│   └── Tabs.vue
├── BreadCrumb/
│   ├── BreadCrumb.vue
│   ├── BreadCrumb.scss
│   └── BreadCrumb.ts     # 타입 분리 (선택사항)
└── CheckboxGroup/
    ├── CheckboxGroup.vue
    └── CheckboxGroup.scss
```

**코드 특징**:

```vue
<template>
  <nav class="pagination">
    <ul class="pagination__list">
      <li>
        <Button @click="handleClickFirst">First</Button>
      </li>
      <li v-for="page in visiblePages" :key="page">
        <Button
          :variant="currentPage === page ? Variant.Flat : Variant.Text"
          @click="changePage(page)"
        >
          {{ page }}
        </Button>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Variant } from '@/constants/enums/style-enum';

export type TPaginationProps = {
  currentPage: number;
  totalCount: number;
  pageSize: number;
};

const { currentPage, totalCount, pageSize } = defineProps<TPaginationProps>();

const emit = defineEmits<{
  'update:currentPage': [number];
}>();

const lastPage = computed(() => Math.ceil(totalCount / pageSize));

const changePage = (page: number) => {
  emit('update:currentPage', page);
};
</script>
```

#### **Organisms (유기체)**

**정의**: Molecules와 Atoms를 조합한 복잡한 컴포넌트

**특징**:

- 비즈니스 로직 포함 가능
- 복잡한 상태 관리
- 특정 도메인에 종속적일 수 있음
- API 호출 가능

**예시**: `Table`, `Form`, `Dialog`, `Tree`, `Layout`

```
organisms/
├── Table/
│   ├── Table.vue
│   └── Table.scss
├── dialog/
│   ├── DialogAlert.vue
│   ├── DialogConfirm.vue
│   ├── DialogBase.vue
│   ├── dialog.scss
│   └── dialog-base.scss
├── form/
│   ├── FormContainer.vue
│   ├── FormItem.vue
│   ├── form-container.scss
│   └── form-item.scss
└── layout/
    ├── LayoutHeader.vue
    ├── LayoutSidebar.vue
    ├── LayoutFooter.vue
    └── layout.scss
```

#### **Templates (템플릿)**

**정의**: 페이지 레이아웃 구조를 정의하는 컴포넌트

**특징**:

- 전체 페이지 구조 정의
- 재사용 가능한 레이아웃
- 에러 처리, 로딩 처리 등

**예시**: `ErrorBoundary`, `Loading`

```
templates/
├── ErrorBoundary/
│   ├── ErrorBoundary.vue
│   └── ErrorBoundary.scss
└── Loading/
    ├── Loading.vue
    └── Loading.scss
```

#### **Pages (페이지 컴포넌트)**

**정의**: 특정 페이지에서만 사용되는 컴포넌트

**특징**:

- 해당 페이지 전용
- 재사용되지 않음
- 복잡한 비즈니스 로직 포함

**위치**: `components/pages/{페이지명}/` 또는 `pages/{경로}/_components/`

```
components/pages/
└── data-resource/
    ├── ResourceTable.vue
    └── ResourceFilter.vue

또는

pages/data/resource/
├── _components/
│   ├── detail/
│   │   └── CommonData.vue
│   └── regist/
│       ├── TabFileUpload.vue
│       ├── TabDb.vue
│       └── TabApi.vue
├── index.vue
├── create/
│   └── index.vue
└── [id]/
    └── index.vue
```

### 3.3 컴포넌트 네이밍 규칙

```
✅ 좋은 예:
- Button.vue
- UserProfile.vue
- DataTable.vue
- SearchInput.vue

❌ 나쁜 예:
- button.vue (소문자)
- user-profile.vue (kebab-case)
- Btn.vue (축약)
```

---

## 4. 타입 정의

### 4.1 타입 파일 위치

```
types/
└── index.ts              # 프로젝트 전역 타입

constants/types/
├── api.ts                # API 관련 공통 타입
├── common.ts             # 공통 타입
├── dac-entity.ts         # 엔티티 타입
└── dac-types.ts          # 도메인 특화 타입

api/{domain}/types.ts     # 도메인별 API 타입
```

### 4.2 타입 네이밍 규칙

```typescript
// Type: T prefix
export type TUser = {
  id: string;
  name: string;
};

export type TUserProfile = {
  user: TUser;
  bio: string;
};

// Interface: I prefix (사용 지양, Type 선호)
export interface IUser {
  id: string;
  name: string;
}

// Enum: E prefix 없이 PascalCase
export enum UserRole {
  Admin = 'ADMIN',
  User = 'USER',
}

// Const Enum 객체 (선호)
export const UserRole = {
  Admin: 'ADMIN',
  User: 'USER',
} as const;

export type TUserRole = (typeof UserRole)[keyof typeof UserRole];
```

### 4.3 공통 타입 예시

```typescript
// constants/types/api.ts
export type TFetchOptions = {
  headers?: Record<string, string>;
  params?: Record<string, any>;
  responseType?: 'json' | 'blob' | 'text';
};

export type TAsyncData<T> = {
  data: Ref<T | null>;
  pending: Ref<boolean>;
  error: Ref<Error | null>;
  refresh: () => Promise<void>;
};

export type TClientFetch<T> = {
  data: T | null;
  error: NuxtError | null;
};

// constants/types/common.ts
export type YN = 'Y' | 'N';

export type TOption<T = string> = {
  label: string;
  value: T;
  disabled?: boolean;
};

export type TPaginationParams = {
  page: number;
  size: number;
  sort?: string;
};

export type TPageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};
```

---

## 5. 네이밍 컨벤션

### 5.1 파일 및 폴더

```
✅ 파일명
- PascalCase: Button.vue, UserProfile.vue, DataTable.vue
- kebab-case: index.vue, [id].vue, _components/

✅ 폴더명
- kebab-case: data-resource/, user-profile/
- camelCase: api/, composables/, utils/

❌ 나쁜 예
- snake_case: user_profile/ (사용 금지)
- SCREAMING: DATA_RESOURCE/ (사용 금지)
```

### 5.2 변수 및 함수

```typescript
// ✅ 좋은 예

// 변수: camelCase
const userName = 'John';
const isActive = true;
const userList = [];

// 함수: camelCase (동사로 시작)
const fetchUser = () => {};
const handleClick = () => {};
const validateForm = () => {};

// 상수: SCREAMING_SNAKE_CASE
const API_BASE_URL = 'https://api.example.com';
const MAX_RETRY_COUNT = 3;

// Boolean: is, has, should prefix
const isLoading = ref(false);
const hasError = computed(() => error.value !== null);
const shouldShowModal = computed(() => isOpen.value && !isLoading.value);

// Handler 함수: handle prefix
const handleSubmit = () => {};
const handleChange = (value: string) => {};
const handleClickDelete = () => {};

// Composable: use prefix
const useUserData = () => {};
const useFetchUsers = () => {};

// ❌ 나쁜 예
const UserName = 'John'; // PascalCase 변수
const click = () => {}; // 동사 없음
const api_base_url = ''; // snake_case
```

### 5.3 컴포넌트 Props/Emits

```vue
<script setup lang="ts">
// Props 타입: T + 컴포넌트명 + Props
export type TButtonProps = {
  size?: Size;
  color?: Color;
};

export type TUserProfileProps = {
  userId: string;
  showAvatar?: boolean;
};

// Emit 이벤트명: kebab-case
const emit = defineEmits<{
  'update:model-value': [string]; // ✅
  'update:modelValue': [string]; // ❌ (camelCase)
  'click-submit': [void]; // ✅
  clickSubmit: [void]; // ❌ (camelCase)
}>();
</script>

<template>
  <!-- 이벤트 핸들러: @event-name -->
  <Button @click-submit="handleSubmit" />

  <!-- v-model -->
  <Input v-model="value" @update:model-value="handleUpdate" />
</template>
```

---

## 6. Composables

### 6.1 Composables 위치

```
composables/
├── useApi.ts              # API 호출 공통 로직
├── useClientFetch.ts      # Client-side fetch
├── useSpringApi.ts        # Spring API 호출
├── useEtlApi.ts           # ETL API 호출
├── useConfirm.ts          # Confirm 다이얼로그
├── useSnackbar.ts         # 스낵바 알림
├── useLoading.ts          # 로딩 상태 관리
└── useBase64.ts           # Base64 인코딩/디코딩
```

### 6.2 Composable 작성 규칙

```typescript
// useClientFetch.ts
import type { TClientFetch } from '@/constants/types/api';
import type { NuxtError } from 'nuxt/app';

export const useClientFetch = async <T>(fetch: () => Promise<T>): TClientFetch<T> => {
  try {
    const response = await fetch();

    return {
      data: response,
      error: null,
    };
  } catch (error) {
    const errorData = error as NuxtError;

    const { error: snackbarError } = useSnackbar();
    snackbarError(errorData.statusMessage ?? '오류가 발생했습니다.');

    return {
      data: null,
      error: errorData as NuxtError,
    };
  }
};
```

**규칙:**

- 함수명: `use` prefix 필수
- 파일명과 함수명 일치
- 반환 타입 명시
- Generic 타입 활용
- 에러 처리 포함

### 6.3 사용 예시

```vue
<script setup lang="ts">
import { createDataSource } from '@/api/resource/api';
import type { TDataResourceDetail } from '@/api/resource/types';

const handleSave = async () => {
  const body: TDataResourceDetail = {
    // ... 데이터
  };

  const { data, error } = await createDataSource(body);

  if (error) {
    // 에러는 useApi 내부에서 처리됨
    return;
  }

  // 성공 처리
  useSnackbar().success('저장되었습니다.');
  await navigateTo('/data/resource');
};
</script>
```

---

## 7. 기타 규칙

### 7.1 Import 순서

```typescript
// 1. 외부 라이브러리
import { ref, computed } from 'vue';
import * as yup from 'yup';

// 2. Nuxt/Vue 관련
import { navigateTo } from '#app';

// 3. 프로젝트 내부 (절대 경로)
import { Color, Size } from '@/constants/enums/style-enum';
import { PATH_DATA_RESOURCE } from '@/routes/path';

// 4. API/Composables
import { useFetchDataResources } from '@/api/resource/composables';

// 5. Type import (type 키워드 사용)
import type { TDataResource } from '@/api/resource/types';

// 6. 로컬 컴포넌트
import MyComponent from './MyComponent.vue';
```

### 7.2 주석 규칙

```typescript
// ✅ 좋은 예: 코드 블록 구분
// ===== 데이터 조회 =====
const { data } = await useFetchDataResources();

// ===== Validation =====
const validateForm = () => {
  // 유효성 검사 로직
};

// ✅ 좋은 예: 복잡한 로직 설명
// 현재 페이지 그룹의 시작/끝 페이지 계산
// 예: 1-10, 11-20, 21-30
const start = Math.floor((currentPage - 1) / groupSize) * groupSize;
const end = Math.min(start + groupSize, lastPage.value);

// ❌ 나쁜 예: 불필요한 주석
const count = 0; // count 변수 선언
```

### 7.3 Enum vs Const Object

```typescript
// ✅ 권장: Const Object (Tree-shaking 가능)
export const Color = {
  Primary: 'primary',
  Secondary: 'secondary',
  Error: 'error',
} as const;

export type TColor = (typeof Color)[keyof typeof Color];

// ❌ 비권장: Enum (사용하지만 const object 선호)
export enum Color {
  Primary = 'primary',
  Secondary = 'secondary',
  Error = 'error',
}
```

---

## 📌 체크리스트

새로운 기능 개발 시 다음 사항을 확인하세요:

- [ ] Vue 파일 작성 순서를 따랐는가?
- [ ] Props는 destructuring + 기본값으로 선언했는가?
- [ ] Emits는 타입 안전하게 선언했는가?
- [ ] API 파일 2개 (api.ts, types.ts)를 생성했는가?
- [ ] Type 이름은 `T` prefix를 사용했는가?
- [ ] 컴포넌트는 Atomic Design 패턴을 따랐는가?
- [ ] 파일명은 PascalCase를 사용했는가?
- [ ] 변수/함수명은 camelCase를 사용했는가?
- [ ] 상수는 SCREAMING_SNAKE_CASE를 사용했는가?

