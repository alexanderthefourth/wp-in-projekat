<template>
  <section class="card" :style="{ padding }">
    <slot />
  </section>
</template>

<script setup>
const props = defineProps({
  padding: { type: String, default: '16px' },
})
</script>

<style scoped>
.card {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
</style>
