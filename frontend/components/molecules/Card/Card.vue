<template>
  <div class="card">
    <div v-if="$slots.header || title" class="card__header">
      <slot name="header">
        <div v-if="icon" class="card__icon-wrapper" :style="iconStyle">
          <v-icon color="white" size="20">{{ icon }}</v-icon>
        </div>
        <h2 v-if="title" class="card__title">{{ title }}</h2>
      </slot>
    </div>
    <div class="card__body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

export type TCardProps = {
  title?: string;
  icon?: string;
  iconColor?: string;
};

const { title, icon, iconColor } = defineProps<TCardProps>();

const iconStyle = computed(() => {
  if (iconColor) {
    return { background: iconColor };
  }
  return { background: 'linear-gradient(135deg, #007aff 0%, #0051d5 100%)' };
});
</script>

<style scoped lang="scss" src="./Card.scss"></style>
