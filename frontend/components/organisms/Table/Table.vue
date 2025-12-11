<template>
  <div class="table-container">
    <table class="table">
      <thead>
        <tr>
          <th
            v-for="column in columns"
            :key="column.field"
            :style="getColumnStyle(column)"
            :class="column.headerClass"
          >
            {{ column.headerName }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(row, index) in rows"
          :key="getRowKey(row, index)"
          :class="rowClass"
          @click="handleRowClick(row)"
        >
          <td
            v-for="column in columns"
            :key="column.field"
            :class="column.cellClass"
            :style="{ textAlign: column.align || 'center' }"
          >
            <!-- Scoped Slot이 있으면 우선 사용 -->
            <slot
              v-if="$slots[`cell(${column.field})`]"
              :name="`cell(${column.field})`"
              :row="row"
              :value="getCellValue(row, column)"
            />
            <!-- valueFormatter가 있으면 사용 -->
            <template v-else-if="column.valueFormatter">
              {{ column.valueFormatter(getCellValue(row, column), row) }}
            </template>
            <!-- 기본: 필드 값 그대로 표시 -->
            <template v-else>
              {{ getCellValue(row, column) }}
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
/**
 * 범용 Table 컴포넌트
 * ag-grid의 컬럼 정의 방식을 참조하여 설계
 */

export type TTableColumn<T = any> = {
  field: string;
  headerName: string;
  width?: string;
  flex?: number; // ag-grid 스타일 flex (비율)
  align?: 'left' | 'center' | 'right'; // 정렬 옵션
  headerClass?: string;
  cellClass?: string;
  valueFormatter?: (value: any, row: T) => string | number;
  valueGetter?: (row: T) => any;
};

export type TTableProps<T = any> = {
  columns: TTableColumn<T>[];
  rows: T[];
  rowKey?: string | ((row: T) => string | number);
  rowClass?: string;
};

const props = withDefaults(defineProps<TTableProps>(), {
  rowKey: 'id',
  rowClass: '',
});

const emit = defineEmits<{
  'row-click': [row: any];
}>();

const getRowKey = (row: any, index: number): string | number => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(row);
  }
  return row[props.rowKey] ?? index;
};

const getCellValue = (row: any, column: TTableColumn) => {
  if (column.valueGetter) {
    return column.valueGetter(row);
  }
  return row[column.field];
};

const handleRowClick = (row: any) => {
  emit('row-click', row);
};

// flex 비율을 width로 계산 + 정렬 스타일
const getColumnStyle = (column: TTableColumn) => {
  const style: Record<string, string> = {};

  // width 또는 flex
  if (column.width) {
    style.width = column.width;
  } else if (column.flex) {
    const totalFlex = props.columns.reduce((sum, col) => sum + (col.flex || 0), 0);
    if (totalFlex > 0) {
      const percentage = (column.flex / totalFlex) * 100;
      style.width = `${percentage}%`;
    }
  }

  // align
  if (column.align) {
    style.textAlign = column.align;
  }

  return style;
};
</script>

<style lang="scss" scoped>
.table-container {
  overflow-x: auto;
}

.table {
  width: 100%;
  table-layout: fixed; // flex 비율이 제대로 작동하도록
  border-collapse: collapse;

  thead {
    background: #f9fafb;
    border-bottom: 2px solid #e5e7eb;

    th {
      padding: 16px;
      text-align: center; // 기본값 (align 옵션으로 오버라이드 가능)
      font-size: 14px;
      font-weight: 700;
      color: #6b7280;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }

  tbody {
    :deep(tr) {
      border-bottom: 1px solid #e8e9eb;
      transition: all 0.2s ease;
      cursor: pointer;

      &:hover {
        background: #f9fafb;
      }

      &:last-child {
        border-bottom: none;
      }
    }

    :deep(td) {
      padding: 16px;
      font-size: 14px;
      color: #1a1d1f;
    }
  }
}
</style>
