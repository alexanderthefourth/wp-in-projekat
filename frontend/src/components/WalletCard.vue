<template>
  <UiCard>
    <div class="flex-between">
      <strong>{{ wallet.name }}</strong>
      <span style="color: var(--ink-muted)">{{ wallet.currency }}</span>
    </div>
    <div style="margin-top: 8px; font-size: 28px; font-weight: 700">
      {{ money(wallet.balance, wallet.currency) }}
    </div>

    <div class="flex" style="margin-top: 12px">
      <button class="btn" @click="$emit('edit', wallet)">Uredi</button>
      <button class="btn" @click="$emit('archive', wallet)" :disabled="wallet.archived">
        Arhiviraj
      </button>
      <button class="btn" @click="$emit('delete', wallet)">Obriši</button>
    </div>
  </UiCard>
</template>

<script setup>
import UiCard from './UiCard.vue'
const props = defineProps({ wallet: { type: Object, required: true } })

const money = (val, cur) => {
  const n = Number(val ?? 0)
  const symbol = cur === 'EUR' ? '€' : cur === 'USD' ? '$' : ''
  const tail = cur === 'EUR' || cur === 'USD' ? '' : ` ${cur || ''}`
  return `${symbol} ${n.toLocaleString('sr-RS')}${tail}`
}
</script>
