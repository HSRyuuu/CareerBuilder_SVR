<template>
  <button :class="buttonClasses" :disabled="disabled" :type="type" @click="handleClick">
    <v-icon v-if="icon && iconPosition === 'left'" :size="iconSize">{{ icon }}</v-icon>
    <span v-if="!iconOnly" class="button-label">
      <slot />
    </span>
    <v-icon v-if="icon && iconPosition === 'right'" :size="iconSize">{{ icon }}</v-icon>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ButtonVariant, CommonSize } from '@/constants/enums/style-enum';
import type { ButtonProps } from '@/types/component-types';

const props = withDefaults(defineProps<ButtonProps>(), {
  variant: ButtonVariant.Primary,
  size: CommonSize.Medium,
  disabled: false,
  icon: undefined,
  iconOnly: false,
  iconPosition: 'left',
  iconSize: 'small',
  type: 'button',
  block: false,
  round: false,
});

const emit = defineEmits<{
  click: [event: MouseEvent];
}>();

const buttonClasses = computed(() => {
  return [
    'cb-button',
    `cb-button--${props.variant}`,
    `cb-button--${props.size}`,
    {
      'cb-button--icon-only': props.iconOnly,
      'cb-button--block': props.block,
      'cb-button--disabled': props.disabled,
      'cb-button--round': props.round,
    },
  ];
});

const handleClick = (event: MouseEvent) => {
  if (!props.disabled) {
    emit('click', event);
  }
};
</script>

<style lang="scss">
@use './Button.scss';
</style>
