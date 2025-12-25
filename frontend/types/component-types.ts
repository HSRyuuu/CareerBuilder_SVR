/**
 * 컴포넌트 Props 타입 정의
 */

import type {
  ButtonVariant,
  CommonSize,
  FormSize,
  FormVariant,
} from '@/constants/enums/style-enum';

// Button 컴포넌트 Props
export interface ButtonProps {
  variant?: ButtonVariant;
  size?: CommonSize;
  disabled?: boolean;
  icon?: string;
  iconOnly?: boolean;
  iconPosition?: 'left' | 'right';
  iconSize?: string;
  type?: 'button' | 'submit' | 'reset';
  block?: boolean;
  round?: boolean;
}

// Input 컴포넌트 Props
export interface InputProps {
  modelValue: string | number;
  label?: string;
  placeholder?: string;
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'month' | 'date';
  size?: CommonSize;
  disabled?: boolean;
  readonly?: boolean;
  required?: boolean;
  error?: boolean;
}

// TextArea 컴포넌트 Props
export interface TextAreaProps {
  modelValue: string;
  label?: string;
  placeholder?: string;
  size?: CommonSize;
  disabled?: boolean;
  readonly?: boolean;
  required?: boolean;
  error?: boolean;
  rows?: number;
}

// DatePicker 컴포넌트 Props
export type TDatePickerProps = {
  modelValue: string | string[] | Date | Date[] | null;
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
};

// Select 컴포넌트
export type TModelValue = string | number | null | Array<string | number>;

export type TSelectItem = {
  title: string | number;
  value: TModelValue;
  disabled?: boolean;
  subtitle?: string;
};

export type TSelectProps<T extends TModelValue = TModelValue> = {
  modelValue: T;
  items: TSelectItem[];
  placeholder?: string;
  size?: FormSize;
  variant?: FormVariant;
  multiple?: boolean;
  disabled?: boolean;
  readonly?: boolean;
  menu?: boolean;
};

// DescriptionBox 컴포넌트
export type DescriptionBoxVariant = 'info' | 'warning' | 'success' | 'neutral';

export interface DescriptionBoxProps {
  text?: string;
  variant?: DescriptionBoxVariant;
}
