<template>
  <div class="experience-table-container">
    <!-- 통합 필터 바 -->
    <div class="experience-table-filter-bar">
      <!-- 검색어 -->
      <div class="filter-item search-group">
        <Input
          v-model="filters.q"
          placeholder="검색어를 입력하세요 (경험명, 역할 검색 가능)"
          :size="CommonSize.Small"
          class="experience-search-input"
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
      :columns="experienceColumns"
      :rows="rows"
      row-key="id"
      row-class="experience-table-row"
      @row-click="handleRowClick"
    >
      <!-- 상태 셀 커스텀 렌더링 -->
      <template #cell(status)="{ value }">
        <span :class="['experience-status-chip', getStatusClass(value)]">
          {{ getStatusDisplay(value) }}
        </span>
      </template>
    </Table>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue';
import { experienceColumns } from '@/columns/experience';
import Table from '@/components/organisms/Table/Table.vue';
import Input from '@/components/atoms/Input/Input.vue';
import Select from '@/components/atoms/Select/Select.vue';
import type { TSelectItem } from '@/components/atoms/Select/Select.vue';
import { ExperienceStatus, STATUS_INFO } from '@/types/experience-types';
import type { TExperience, ExperienceSortKey, SortDirection } from '~/api/experience/types';
import { FormSize, FormVariant, CommonSize } from '@/constants/enums/style-enum';

export type TExperienceTableFilters = {
  q: string;
  status: ExperienceStatus | 'ALL';
  sortKey: ExperienceSortKey;
  sortDir: SortDirection;
};

interface Props {
  rows: TExperience[];
}

defineProps<Props>();

const emit = defineEmits<{
  'row-click': [TExperience];
  'update:filters': [TExperienceTableFilters];
}>();

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
  { title: 'AI 분석 중', value: ExperienceStatus.ANALYZING },
  { title: '분석 완료', value: ExperienceStatus.ANALYZED },
];

const sortOptions: TSelectItem[] = [
  { title: '수정일', value: 'UPDATED_AT' },
  { title: '프로젝트 기간', value: 'DURATION_START' },
];

const toggleSortDirection = () => {
  filters.sortDir = filters.sortDir === 'DESC' ? 'ASC' : 'DESC';
};

// 필터 변경 감지
watch(filters, (newFilters) => {
  emit('update:filters', { ...newFilters });
}, { deep: true });

const handleRowClick = (row: TExperience) => {
  emit('row-click', row);
};

const getStatusDisplay = (status: any) => {
  return STATUS_INFO[status as ExperienceStatus]?.display || status;
};

const getStatusClass = (status: any) => {
  return STATUS_INFO[status as ExperienceStatus]?.color || '';
};
</script>

<style lang="scss" scoped>
.experience-table-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.experience-table-filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;

  &.search-group {
    position: relative;
    flex: 1;
    max-width: 400px;
    min-width: 250px;

    .experience-search-input {
      width: 100%;
      :deep(input) {
        padding-left: 38px;
      }
    }

    .search-icon {
      position: absolute;
      left: 12px;
      top: 50%;
      transform: translateY(-50%);
      color: var(--text-tertiary);
      pointer-events: none;
    }
  }
}

.filter-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.filter-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
  white-space: nowrap;
}

.status-select, .sort-select {
  min-width: 110px;
}

.sort-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-direction-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--primary-color);
    color: var(--primary-color);
    background: var(--bg-tertiary);
  }

  &:active {
    transform: scale(0.95);
  }
}

.experience-table-row {
  cursor: pointer;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: var(--bg-tertiary) !important;
  }
}

.experience-status-chip {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11.5px;
  font-weight: 600;

  &.status-incomplete {
    background-color: rgba(241, 115, 115, 0.1);
    color: #f17373;
  }

  &.status-completed {
    background-color: rgba(61, 183, 131, 0.1);
    color: #3db783;
  }

  &.status-analyzing {
    background-color: rgba(59, 130, 246, 0.1);
    color: #3b82f6;
  }

  &.status-analyzed {
    background-color: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
  }
}

.dark-mode {
  .experience-status-chip {
    &.status-incomplete {
      background-color: rgba(241, 115, 115, 0.2);
    }
    &.status-completed {
      background-color: rgba(61, 183, 131, 0.2);
    }
    &.status-analyzing {
      background-color: rgba(59, 130, 246, 0.2);
    }
    &.status-analyzed {
      background-color: rgba(139, 92, 246, 0.2);
    }
  }
}
</style>
