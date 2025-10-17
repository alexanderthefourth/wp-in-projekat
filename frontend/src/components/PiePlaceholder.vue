<template>
  <UiCard>
    <div class="header">
      <strong>{{ title }}</strong>
      <span class="muted">{{ totalLabel }}</span>
    </div>

    <div class="piewrap" v-if="series.length">
      <div class="pie" :style="conic"></div>
      <ul class="legend">
        <li v-for="s in series" :key="s.label">
          <span class="dot" :style="{ background: s.color }"></span>
          <span class="lab">{{ s.label }}</span>
          <span class="val">{{ fmt(s.value) }}</span>
        </li>
      </ul>
    </div>

    <div v-else class="muted">Nema podataka za prikaz.</div>
  </UiCard>
</template>

<script setup>
import { computed } from 'vue'
import UiCard from './UiCard.vue'

const props = defineProps({
  title: { type: String, default: 'Grafikon' },
  data: { type: Array, default: () => [] }, // [{label, value}]
})

const palette = [
  '#c9a97e',
  '#e3c9a6',
  '#b38d6e',
  '#d6b890',
  '#a67f5a',
  '#e8d5bd',
  '#8f6e51',
  '#dcc6a6',
]

const total = computed(() => (props.data || []).reduce((s, x) => s + (Number(x?.value) || 0), 0))
const totalLabel = computed(() => (total.value ? `Ukupno: ${fmt(total.value)}` : ''))

const series = computed(() => {
  if (!total.value) return []
  return (props.data || []).map((d, i) => ({
    label: d?.label ?? '—',
    value: Number(d?.value) || 0,
    pct: total.value ? (Number(d?.value) || 0) / total.value : 0,
    color: palette[i % palette.length],
  }))
})

const conic = computed(() => {
  let acc = 0
  const stops = series.value
    .map((s) => {
      const start = (acc * 360).toFixed(2)
      acc += s.pct
      const end = (acc * 360).toFixed(2)
      return `${s.color} ${start}deg ${end}deg`
    })
    .join(', ')
  return { background: `conic-gradient(${stops})` }
})

function fmt(n) {
  return Number(n || 0).toLocaleString('sr-RS')
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.muted {
  color: var(--ink-muted);
}
.piewrap {
  display: flex;
  gap: 16px;
  align-items: center;
}
.pie {
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: #eee;
}
.legend {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 6px;
}
.legend li {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 8px;
  align-items: center;
}
.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}
.lab {
  color: #473d33;
}
.val {
  font-weight: 600;
}
</style>
