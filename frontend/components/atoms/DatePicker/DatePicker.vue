<template>
  <DatePicker
    :id="id"
    :model-value="modelValue"
    :range="range"
    :locale="locale"
    :multi-calendars="range"
    :disabled="disabled"
    :readonly="readonly"
    :enable-time-picker="useTimePicker"
    :time-picker-inline="useTimePickerInline"
    :action-row="{ showNow: useToday }"
    :cancel-text="customLocale.cancel"
    :select-text="customLocale.select"
    :now-button-label="customLocale.today"
    :format="computedFormat"
    :placeholder="placeholder"
    :dark="isDark"
    :month-picker="type === 'month' || monthPicker"
    auto-apply
    @update:model-value="handleUpdate"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import DatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

export type TDatePickerProps = {
  modelValue: string | string[] | Date | Date[] | any | null;
  locale?: string;
  customLocale?: {
    cancel: string;
    select: string;
    today: string;
  };
  useToday?: boolean;
  id?: string;
  format?: string;
  type?: string;
  range?: boolean;
  disabled?: boolean;
  readonly?: boolean;
  placeholder?: string;
  useTimePicker?: boolean;
  useTimePickerInline?: boolean;
  monthPicker?: boolean;
};

const colorMode = useColorMode();
const isDark = computed(() => colorMode.value === 'dark');

const {
  locale = 'ko',
  customLocale = {
    cancel: '취소',
    select: '선택',
    today: '오늘',
  },
  useToday = false,
  id = 'datePicker',
  format = 'yyyy-MM-dd',
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  type = 'date',
  range = false,
  disabled = false,
  readonly = false,
  placeholder = '',
  useTimePicker = false,
  useTimePickerInline = false,
  monthPicker = false,
} = defineProps<TDatePickerProps>();

const computedFormat = computed(() => {
  if (format !== 'yyyy-MM-dd') return format;
  if (type === 'month' || monthPicker) return 'yyyy-MM';
  return format;
});

const emit = defineEmits<{
  'update:modelValue': [any];
}>();

const handleUpdate = (value: any) => {
  if (!value) {
    emit('update:modelValue', '');
    return;
  }

  const formatDate = (date: any) => {
    // month-picker가 켜져있을 때 { month: 1, year: 2025 } 형태의 객체가 올 수 있음
    if (date && typeof date === 'object' && 'month' in date && 'year' in date) {
      return `${date.year}-${String(date.month + 1).padStart(2, '0')}`;
    }

    if (date instanceof Date) {
      if (type === 'month' || monthPicker) {
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      }
      if (useTimePicker) {
        return date.toISOString().slice(0, 16); // YYYY-MM-ddTHH:mm 형식
      }
      return date.toISOString().split('T')[0]; // YYYY-MM-dd 형식
    }
    return date;
  };

  if (Array.isArray(value)) {
    const formattedDates = value.map(formatDate);
    emit('update:modelValue', formattedDates);
  } else {
    emit('update:modelValue', formatDate(value));
  }
};
</script>

<style lang="scss" scoped>
@use './DatePicker.scss';
</style>
