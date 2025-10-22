<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 36 36"
    class="progress-ring"
  >
    <circle
      cx="18"
      cy="18"
      r="16"
      fill="none"
      stroke="#eee3d2"
      stroke-width="4"
    />
    <circle
      cx="18"
      cy="18"
      r="16"
      fill="none"
      stroke="var(--accent, #d6976b)"
      stroke-width="4"
      :stroke-dasharray="circumference"
      :stroke-dashoffset="dash"
      stroke-linecap="round"
      transform="rotate(-90 18 18)"
    />
    <text
      x="18"
      y="20"
      text-anchor="middle"
      font-size="8"
      fill="var(--ink, #333)"
    >
      {{ pct }}%
    </text>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  value: { type: Number, default: 0 },
  size: { type: Number, default: 92 },
})

const pct = computed(() =>
  Math.round(Math.max(0, Math.min(100, Number.isFinite(props.value) ? props.value : 0)))
)

const circumference = 2 * Math.PI * 16
const dash = computed(() => circumference * (1 - pct.value / 100))
</script>

<style scoped>
.progress-ring {
  display: block;
  overflow: visible;
}
</style>
