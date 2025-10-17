<template>
  <UiCard>
    <div class="header">
      <strong>{{ title }}</strong>
      <span class="muted" v-if="items.length">Top {{ items.length }}</span>
    </div>

    <div v-if="items.length" class="bars">
      <div v-for="(row, i) in items" :key="i" class="barrow">
        <div class="label" :title="row.label">{{ row.label }}</div>
        <div class="barwrap">
          <div
            class="bar"
            :style="{ width: pct(row.value), background: colors[i % colors.length] }"
          ></div>
        </div>
        <div class="val">{{ fmt(row.value) }}</div>
      </div>
    </div>

    <div v-else class="muted">Nema podataka za prikaz.</div>
  </UiCard>
</template>

<script setup>
import { computed } from 'vue'
import UiCard from './UiCard.vue'

const props = defineProps({
  title: { type: String, default: 'Stavke' },
  data: { type: Array, default: () => [] }, // [{label, value}]
})

const items = computed(() => (props.data || []).filter((d) => Number(d?.value) > 0))
const max = computed(() => Math.max(0, ...items.value.map((d) => Number(d?.value) || 0)))
const colors = [
  '#c9a97e',
  '#b38d6e',
  '#e3c9a6',
  '#a67f5a',
  '#e8d5bd',
  '#8f6e51',
  '#dcc6a6',
  '#9f7f63',
]

function pct(v) {
  const x = Number(v) || 0
  return max.value ? `${((x / max.value) * 100).toFixed(1)}%` : '0%'
}
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
.bars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.barrow {
  display: grid;
  grid-template-columns: 1fr 4fr auto;
  gap: 10px;
  align-items: center;
}
.label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #473d33;
}
.barwrap {
  background: #f1e8dd;
  border-radius: 12px;
  height: 18px;
  overflow: hidden;
}
.bar {
  height: 100%;
  border-radius: 12px;
}
.val {
  font-weight: 600;
  text-align: right;
}
</style>
