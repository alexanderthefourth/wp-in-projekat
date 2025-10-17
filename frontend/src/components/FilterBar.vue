<template>
  <div class="filterbar">
    <input type="date" v-model="local.from" />
    <input type="date" v-model="local.to" />
    <select v-model="local.type">
      <option value="">Sve vrste</option>
      <option value="INCOME">Prihodi</option>
      <option value="EXPENSE">Rashodi</option>
    </select>
    <input type="number" step="0.01" placeholder="Min iznos" v-model.number="local.min" />
    <input type="number" step="0.01" placeholder="Max iznos" v-model.number="local.max" />
    <input type="text" placeholder="Pretraga (naziv/kategorija)" v-model="local.q" />

    <button class="btn" @click="apply">Primeni</button>
    <button class="btn" @click="reset">Reset</button>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['apply'])
const local = reactive({
  from: '',
  to: '',
  type: '',
  min: '',
  max: '',
  q: '',
})

function apply() {
  emit('apply', { ...local })
}
function reset() {
  local.from = ''
  local.to = ''
  local.type = ''
  local.min = ''
  local.max = ''
  local.q = ''
  emit('apply', { ...local })
}
</script>

<style scoped>
.filterbar {
  display: grid;
  grid-template-columns: repeat(7, minmax(140px, 1fr));
  gap: 10px;
  align-items: center;
}
@media (max-width: 1100px) {
  .filterbar {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
