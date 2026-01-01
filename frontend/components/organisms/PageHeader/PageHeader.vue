<template>
  <div class="page-header">
    <div class="page-header__content">
      <div class="page-header__text">
        <h1 class="page-header__title">{{ title }}</h1>
        <p v-if="subtitle" class="page-header__subtitle">{{ subtitle }}</p>
      </div>

      <div v-if="$slots.actions" class="page-header__actions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Type 선언
export type TPageHeaderProps = {
  title: string;
  subtitle?: string;
};

// Props 선언
const { title, subtitle } = defineProps<TPageHeaderProps>();
</script>

<style scoped lang="scss">
@use './PageHeader.scss';
</style>
