<template>
  <button
    :class="['toggle-button', { active: modelValue }]"
    @click="emit('update:modelValue', !modelValue)"
    :disabled="disabled"
    :aria-label="modelValue ? labelOn : labelOff"
    :aria-pressed="modelValue"
    type="button"
  >
    <slot v-if="!modelValue" name="icon-off"></slot>
    <slot v-else name="icon-on"></slot>
    <span class="toggle-label">{{ modelValue ? labelOn : labelOff }}</span>
  </button>
</template>

<script setup lang="ts">
interface Props {
  modelValue: boolean;
  labelOff: string;
  labelOn: string;
  disabled?: boolean;
}

withDefaults(defineProps<Props>(), {
  disabled: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
}>();
</script>

<style scoped lang="scss">
.toggle-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.6);

  &:hover:not(:disabled) {
    border-color: rgba(0, 0, 0, 0.24);
    background: rgba(0, 0, 0, 0.02);
  }

  &.active {
    border-color: rgb(var(--v-theme-primary));
    background: rgba(var(--v-theme-primary), 0.08);
    color: rgb(var(--v-theme-primary));
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  &:focus-visible {
    outline: 2px solid rgb(var(--v-theme-primary));
    outline-offset: 2px;
  }

  .toggle-label {
    user-select: none;
  }
}
</style>
