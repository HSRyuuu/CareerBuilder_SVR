<template>
  <div class="experience-table-container">
    <!-- 통합 필터 바 -->
    <div class="experience-table-filter-bar">
      <!-- 검색어 -->
      <div class="filter-item search-group">
        <Input
          v-model="filters.q"
          :size="CommonSize.Small"
          class="experience-search-input"
          placeholder="검색어를 입력하세요 (경험명, 역할 검색 가능)"
        />
        <v-icon class="search-icon">mdi-magnify</v-icon>
      </div>

      <div class="filter-controls">
        <!-- 상태 필터 -->
        <div class="filter-item">
          <label class="filter-label">상태:</label>
          <Select
            v-model="filters.status"
            :items="statusOptions"
            :size="FormSize.Compact"
            :variant="FormVariant.Outlined"
            class="status-select"
          />
        </div>

        <!-- 정렬 -->
        <div class="filter-item sort-group">
          <label class="filter-label">정렬:</label>
          <Select
            v-model="filters.sortKey"
            :items="sortOptions"
            :size="FormSize.Compact"
            :variant="FormVariant.Outlined"
            class="sort-select"
          />
          <button
            :title="filters.sortDir === 'DESC' ? '내림차순' : '오름차순'"
            class="sort-direction-btn"
            @click="toggleSortDirection"
          >
            <v-icon size="small">
              {{ filters.sortDir === 'DESC' ? 'mdi-sort-descending' : 'mdi-sort-ascending' }}
            </v-icon>
          </button>
        </div>
      </div>
    </div>

    <Table
      :columns="computedColumns"
      :rows="rows"
      row-class="experience-table-row"
      row-key="id"
      @row-click="handleRowClick"
    >
      <!-- 상태 셀 커스텀 렌더링 -->
      <template #cell(status)="{ value }">
        <span :class="['experience-status-chip', getStatusClass(value)]">
          {{ getStatusDisplay(value) }}
        </span>
      </template>

      <!-- 선택 버튼 셀 커스텀 렌더링 -->
      <template #cell(select)="{ row }">
        <Button
          :round="true"
          :size="CommonSize.Small"
          :variant="ButtonVariant.Outlined"
          class="row-select-btn"
          @click.stop="handleSelect(row)"
        >
          {{ selectButtonLabel }}
        </Button>
      </template>
    </Table>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, watch } from 'vue';
import { experienceColumns } from '@/columns/experience';
import Table from '@/components/organisms/Table/Table.vue';
import Input from '@/components/atoms/Input/Input.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import Select from '@/components/atoms/Select/Select.vue';
import Button from '@/components/atoms/Button/Button.vue';
import { ExperienceStatus, STATUS_INFO } from '@/types/experience-types';
import type { ExperienceSortKey, SortDirection, TExperience } from '~/api/experience/types';
import { ButtonVariant, CommonSize, FormSize, FormVariant } from '@/constants/enums/style-enum';

export type TExperienceTableFilters = {
  q: string;
  status: ExperienceStatus | 'ALL';
  sortKey: ExperienceSortKey;
  sortDir: SortDirection;
};

interface Props {
  rows: TExperience[];
  showSelectButton?: boolean;
  selectButtonLabel?: string;
}

const { rows, showSelectButton = false, selectButtonLabel = '선택' } = defineProps<Props>();

const emit = defineEmits<{
  'row-click': [TExperience];
  select: [TExperience];
  'update:filters': [TExperienceTableFilters];
}>();

// 컬럼 정의 (선택 버튼 여부에 따라 동적 생성)
const computedColumns = computed(() => {
  const cols = [...experienceColumns];
  if (showSelectButton) {
    cols.push({
      field: 'select',
      headerName: selectButtonLabel,
      flex: 1.5,
      width: '80px',
      align: 'center',
    });
  }
  return cols;
});

// 내부 필터 상태
const filters = reactive<TExperienceTableFilters>({
  q: '',
  status: 'ALL',
  sortKey: 'UPDATED_AT',
  sortDir: 'DESC',
});

// Select 옵션 목록
const statusOptions: TSelectItem[] = [
  { title: '전체', value: 'ALL' },
  { title: '보완 필요', value: ExperienceStatus.INCOMPLETE },
  { title: '작성 완료', value: ExperienceStatus.COMPLETED },
  { title: 'AI 분석 완료', value: ExperienceStatus.AI_ANALYZED },
];

const sortOptions: TSelectItem[] = [
  { title: '수정일시', value: 'UPDATED_AT' },
  { title: '경험 시작일', value: 'DURATION_START' },
];

const toggleSortDirection = () => {
  filters.sortDir = filters.sortDir === 'DESC' ? 'ASC' : 'DESC';
};

// 필터 변경 감지
watch(
  filters,
  (newFilters) => {
    emit('update:filters', { ...newFilters });
  },
  { deep: true }
);

const handleRowClick = (row: TExperience) => {
  emit('row-click', row);
};

const handleSelect = (row: TExperience) => {
  emit('select', row);
};

const getStatusDisplay = (status: any) => {
  return STATUS_INFO[status as ExperienceStatus]?.display || status;
};

const getStatusClass = (status: any) => {
  return STATUS_INFO[status as ExperienceStatus]?.color || '';
};
</script>

<style lang="scss" scoped>
@use './ExperienceTable.scss';
</style>
