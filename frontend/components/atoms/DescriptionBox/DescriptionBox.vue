<template>
  <div class="description-box" :class="[variantClass, { 'has-icon': displayIcon }]">
    <div v-if="displayIcon" class="description-icon">
      <v-icon :icon="displayIcon" size="18" />
    </div>
    <div class="description-content">
      <slot>{{ text }}</slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

export type DescriptionBoxVariant = 'info' | 'warning' | 'success' | 'neutral' | 'gradient';

export interface DescriptionBoxProps {
  text?: string;
  variant?: DescriptionBoxVariant;
  icon?: string;
}

const props = withDefaults(defineProps<DescriptionBoxProps>(), {
  text: '',
  variant: 'info',
  icon: '',
});

const displayIcon = computed(() => {
  if (props.icon) return props.icon;
  switch (props.variant) {
    case 'info': return 'mdi-information-outline';
    case 'warning': return 'mdi-alert-circle-outline';
    case 'success': return 'mdi-check-circle-outline';
    case 'gradient': return 'mdi-sparkles';
    default: return '';
  }
});

const variantClass = computed(() => {
  return `description-box--${props.variant}`;
});
</script>

<style lang="scss" scoped>
@use './DescriptionBox.scss';
</style>
