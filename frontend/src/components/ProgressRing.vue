<template>
  <svg :width="size" :height="size" viewBox="0 0 36 36">
    <circle cx="18" cy="18" r="16" fill="none" stroke="#eee3d2" stroke-width="4" />
    <circle
      cx="18"
      cy="18"
      r="16"
      fill="none"
      stroke="var(--accent)"
      stroke-width="4"
      :stroke-dasharray="circumference"
      :stroke-dashoffset="dash"
      stroke-linecap="round"
      transform="rotate(-90 18 18)"
    />
    <text x="18" y="20" text-anchor="middle" font-size="8" fill="var(--ink)">{{ pct }}%</text>
  </svg>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  value: { type: Number, default: 0 },
  size: { type: Number, default: 92 },
})
const pct = computed(() => Math.round(Math.min(100, Math.max(0, props.value))))
const circumference = 2 * Math.PI * 16
const dash = computed(() => circumference * (1 - pct.value / 100))
</script>
