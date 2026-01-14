<script setup lang="ts">
// Deprecated route - redirect to main detail page with AI panel query param
definePageMeta({
  middleware: (to) => {
    const id = to.params.id as string;
    return navigateTo({
      path: `/experience/${id}`,
      query: {
        ai: 'true'
      }
    }, { redirectCode: 301 });
  }
});
</script>

<template>
  <!-- This page is deprecated and redirects automatically -->
  <div></div>
</template>
