<template>
  <UiCard>
    <slot name="filters"></slot>
    <table class="table" style="margin-top: 10px">
      <thead>
        <tr>
          <th>Naziv</th>
          <th>Tip</th>
          <th>Kategorija</th>
          <th>Novčanik</th>
          <th>Iznos</th>
          <th>Datum</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="t in items" :key="t.id">
          <td>{{ t.name }}</td>
          <td>
            <span :style="t.type === 'Prihod' ? 'color:green' : 'color:#a33'">{{ t.type }}</span>
          </td>
          <td>{{ t.categoryName }}</td>
          <td>{{ t.walletName }}</td>
          <td>{{ t.amount }} {{ t.currency }}</td>
          <td>{{ t.date }}</td>
          <td><button class="btn ghost" @click="$emit('open', t)">⋯</button></td>
        </tr>
      </tbody>
    </table>
    <div class="flex" style="justify-content: flex-end; margin-top: 12px">
      <button class="btn" :disabled="page === 1" @click="$emit('page', page - 1)">Prev</button>
      <button class="btn accent">{{ page }}</button>
      <button class="btn" :disabled="!hasMore" @click="$emit('page', page + 1)">Next</button>
    </div>
  </UiCard>
</template>

<script setup>
import UiCard from './UiCard.vue'
const props = defineProps({
  items: { type: Array, default: () => [] },
  page: { type: Number, default: 1 },
  hasMore: { type: Boolean, default: false },
})
</script>
