<template>
  <div class="page-xl">
    <UiCard>
      <div
        style="
          display: flex;
          gap: 8px;
          align-items: center;
          justify-content: center;
          flex-wrap: wrap;
        "
      >
        <span style="color: var(--ink-muted)">Wallet ID:</span>
        <input type="number" v-model.number="currentWalletId" style="max-width: 120px" />
        <button class="btn accent" @click="loadWalletBundle(currentWalletId)">Load</button>
        <span v-if="loading" style="color: var(--ink-muted)">Loading…</span>
        <span v-if="error" style="color: #b00020">{{ error }}</span>
      </div>
    </UiCard>

    <div class="top-row">
      <!-- Balance card becomes a UiCard so both cards style/size match -->
      <UiCard class="tile balans">
        <h2 style="margin: 0">
          <strong>{{ money(saved, currency) }}</strong>
          <span v-if="targetAmount"> / {{ money(targetAmount, currency) }}</span>
        </h2>
        <div style="color: var(--ink-muted); margin-top: 8px">
          Novčanik: {{ wallet?.savings ? 'Štednja' : 'Standard' }}
        </div>
      </UiCard>

      <UiCard v-if="wallet?.goal" class="tile goal-card">
        <strong>{{ wallet.goal.name }}</strong>
        <div class="flex" style="align-items: center; gap: 18px; margin-top: 10px">
          <ProgressRing class="progress-ring" :value="progressPct" />
          <div>
            <div>
              <strong>{{ money(saved, currency) }}</strong>
              <span> / {{ money(targetAmount, currency) }}</span>
            </div>
            <div style="color: var(--ink-muted)">
              Novčanik: {{ wallet?.savings ? 'Štednja' : 'Standard' }}
            </div>
          </div>
        </div>
      </UiCard>
    </div>

    <UiCard>
      <div class="flex-between">
        <strong>2.2 Lične transakcije i transferi</strong>
        <div class="flex" style="gap: 8px">
          <button class="btn" @click="showTransfer = !showTransfer">
            {{ showTransfer ? 'Zatvori' : 'Novo plaćanje' }}
          </button>
        </div>
      </div>

      <!-- Add Transaction form -->
      <div v-if="showAddTx" class="form-grid">
        <input v-model="txForm.name" placeholder="Naziv" />
        <input
          v-model.number="txForm.amount"
          type="number"
          min="0"
          step="0.01"
          placeholder="Iznos"
        />
        <select v-model="txForm.type">
          <option value="INCOME">Prihod</option>
          <option value="EXPENSE">Rashod</option>
        </select>
        <input v-model="txForm.dateOfExecution" type="date" />
        <label class="check">
          <input type="checkbox" v-model="txForm.repeatable" /> Ponavljajuća
        </label>
        <label class="check" :style="{ opacity: txForm.repeatable ? 1 : 0.5 }">
          <input type="checkbox" v-model="txForm.activeRepeat" :disabled="!txForm.repeatable" />
          Aktivna
        </label>
        <input
          v-model="txForm.frequency"
          :disabled="!txForm.repeatable"
          placeholder="Frekvencija (npr. P1M)"
        />
        <div class="form-actions">
          <button class="btn accent" :disabled="txSaving" @click="submitTx">
            {{ txSaving ? 'Čuvanje…' : 'Sačuvaj' }}
          </button>
          <span v-if="txError" class="err">{{ txError }}</span>
        </div>
      </div>

      <!-- Transfer form (TransactionMoveDTO) -->
      <div v-if="showTransfer" class="form-grid">
        <input
          type="number"
          v-model.number="transferForm.targetWalletId"
          placeholder="ID odredišnog novčanika"
        />
        <input
          type="number"
          v-model.number="transferForm.amount"
          min="0"
          step="0.01"
          placeholder="Iznos"
        />
        <input
          v-model="transferForm.transactionName"
          placeholder="Naziv transakcije (npr. Transfer sredstava)"
        />
        <select v-model="transferForm.type">
          <option value="INCOME">Prihod</option>
          <option value="EXPENSE">Rashod</option>
        </select>
        <input type="date" v-model="transferForm.dateOfExecution" />

        <label class="check">
          <input type="checkbox" v-model="transferForm.repeatable" /> Ponavljajuća
        </label>
        <label class="check" :style="{ opacity: transferForm.repeatable ? 1 : 0.5 }">
          <input
            type="checkbox"
            v-model="transferForm.activeRepeat"
            :disabled="!transferForm.repeatable"
          />
          Aktivna
        </label>
        <input
          v-model="transferForm.frequency"
          :disabled="!transferForm.repeatable"
          placeholder="Frekvencija (npr. P1M)"
        />

        <div class="form-actions">
          <button class="btn" :disabled="transferSaving" @click="submitTransfer">
            {{ transferSaving ? 'Prebacujem…' : 'Prebaci' }}
          </button>
          <span v-if="transferError" class="err">{{ transferError }}</span>
        </div>
      </div>

      <TransactionList
        :items="filteredTxRows"
        :page="txPage"
        :hasMore="txHasMore"
        @page="onTxPageChange"
      >
        <template #filters>
          <FilterBar @apply="onFilterApply" />
        </template>
      </TransactionList>
    </UiCard>

    <UiCard>
      <div class="flex-between">
        <strong>Statistika i pregled</strong>
        <FilterBar />
      </div>
      <section class="grid" style="grid-template-columns: 1fr 1fr; margin-top: 12px">
        <PiePlaceholder title="Po kategorijama" :data="pieData" />
        <BarPlaceholder title="Top 10 troškova" :data="topExpenses" />
      </section>
      <div style="margin-top: 12px"><TransactionList /></div>
    </UiCard>

    <UiCard>
      <strong>Podešavanje i profil</strong>
      <div class="grid" style="grid-template-columns: 160px 1fr; gap: 16px; margin-top: 10px">
        <div
          style="width: 160px; height: 160px; border-radius: 16px; background: var(--accent)"
        ></div>
        <div class="grid" style="grid-template-columns: 1fr 1fr; gap: 12px">
          <input placeholder="Ime" value="" />
          <input placeholder="Prezime" value="" />
          <input placeholder="Korisničko ime" value="" />
          <input placeholder="Email" value="" />
          <select>
            <option>RSD</option>
            <option>EUR</option>
            <option>USD</option>
          </select>
          <input placeholder="Datum rođenja" type="date" value="2000-01-01" />
        </div>
      </div>
      <div class="flex" style="justify-content: flex-end; margin-top: 12px">
        <button class="btn accent" disabled>Sačuvaj (demo)</button>
      </div>
    </UiCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import UiCard from '../components/UiCard.vue'
import WalletCard from '../components/WalletCard.vue'
import TransactionList from '../components/TransactionList.vue'
import FilterBar from '../components/FilterBar.vue'
import PiePlaceholder from '../components/PiePlaceholder.vue'
import BarPlaceholder from '../components/BarPlaceholder.vue'
import ProgressRing from '../components/ProgressRing.vue'
import { Wallets, Transactions } from '../services/api'
import { computed } from 'vue'

// UI toggles
const showAddTx = ref(false)
const showTransfer = ref(false)
const today = new Date().toISOString().slice(0, 10)

const txForm = ref({
  name: '',
  amount: '',
  type: 'EXPENSE', // Spring enum (likely INCOME / EXPENSE)
  dateOfExecution: today,
  repeatable: false,
  activeRepeat: false,
  frequency: '', // leave empty if not used
})

// --- TRANSFER FORM (TransactionMoveDTO) ---

const transferForm = ref({
  targetWalletId: null,
  amount: '',
  transactionName: '',
  type: 'EXPENSE', // must match your Java enum (e.g., INCOME / EXPENSE)
  dateOfExecution: today,
  repeatable: false,
  activeRepeat: false,
  frequency: '', // leave empty if not used
})

async function submitTransfer() {
  transferError.value = ''

  const targetId = Number(transferForm.value.targetWalletId)
  const amt = toNumber(transferForm.value.amount)

  if (!targetId || !amt || amt <= 0) {
    transferError.value = 'Unesite validne vrednosti: cilj novčanika i pozitivan iznos.'
    return
  }
  if (targetId === Number(currentWalletId.value)) {
    transferError.value = 'Izvorni i ciljni novčanik ne mogu biti isti.'
    return
  }
  if (!transferForm.value.transactionName) {
    transferError.value = 'Unesite naziv transakcije.'
    return
  }

  try {
    transferSaving.value = true

    const payload = {
      sourceWalletId: Number(currentWalletId.value),
      targetWalletId: targetId,
      amount: amt,
      transactionName: transferForm.value.transactionName,
      type: transferForm.value.type, // 'INCOME' or 'EXPENSE' (exact enum)
      dateOfExecution: transferForm.value.dateOfExecution,
      repeatable: !!transferForm.value.repeatable,
      activeRepeat: !!transferForm.value.activeRepeat,
      frequency: transferForm.value.frequency || null,
    }

    await Transactions.move(payload)

    // Refresh UI
    await loadWalletBundle(currentWalletId.value)

    // Reset
    transferForm.value = {
      targetWalletId: null,
      amount: '',
      transactionName: '',
      type: 'EXPENSE',
      dateOfExecution: today,
      repeatable: false,
      activeRepeat: false,
      frequency: '',
    }
    showTransfer.value = false
  } catch (e) {
    console.error('Transfer error:', e?.response?.data || e)
    transferError.value =
      e?.response?.data?.message ||
      (typeof e?.response?.data === 'string' ? e.response.data : '') ||
      `Greška pri transferu. (status ${e?.response?.status ?? '???'})`
  } finally {
    transferSaving.value = false
  }
}

// Feedback
const txSaving = ref(false)
const txError = ref('')
const transferSaving = ref(false)
const transferError = ref('')

// Helpers
function toNumber(n) {
  const x = Number(n)
  return isNaN(x) ? 0 : x
}

// Submit: CreateTransactionDTO
async function submitTx() {
  txError.value = ''
  if (!txForm.value.name || !txForm.value.amount || !txForm.value.dateOfExecution) {
    txError.value = 'Unesite naziv, iznos i datum.'
    return
  }
  try {
    txSaving.value = true
    await Transactions.create({
      name: txForm.value.name,
      amount: toNumber(txForm.value.amount),
      type: txForm.value.type, // 'INCOME' or 'EXPENSE'
      dateOfExecution: txForm.value.dateOfExecution,
      repeatable: !!txForm.value.repeatable,
      activeRepeat: !!txForm.value.activeRepeat,
      frequency: txForm.value.frequency || null,
      walletId: currentWalletId.value,
    })
    // refresh UI
    await loadWalletBundle(currentWalletId.value)
    // reset form
    txForm.value = {
      name: '',
      amount: '',
      type: 'EXPENSE',
      dateOfExecution: today,
      repeatable: false,
      activeRepeat: false,
      frequency: '',
    }
    showAddTx.value = false
  } catch (e) {
    txError.value = `Greška pri kreiranju transakcije. (${e?.response?.status ?? '???'})`
    console.error(e)
  } finally {
    txSaving.value = false
  }
}

const currency = computed(() => wallet.value?.currency?.name || 'RSD')
const saved = computed(() => Number(balance.value ?? wallet.value?.currBal ?? 0))
const targetAmount = computed(() => Number(wallet.value?.goal?.targetAmount ?? 0))

const progressPct = computed(() => {
  if (!targetAmount.value || targetAmount.value <= 0) return 0
  return Math.min(100, Math.round((saved.value / targetAmount.value) * 100))
})

function money(val, cur) {
  const n = Number(val ?? 0)
  const symbol = cur === 'EUR' ? '€' : cur === 'USD' ? '$' : ''
  const tail = cur === 'EUR' || cur === 'USD' ? '' : ` ${cur || ''}`
  return `${symbol} ${n.toLocaleString('sr-RS')}${tail}`
}
const currentWalletId = ref(1)

const wallet = ref(null)
const balance = ref(null)
const transfers = ref([])
const loading = ref(false)
const error = ref('')

const txRows = ref([])
const txPage = ref(1)
const txHasMore = ref(false)

function onTxPageChange() {}

async function loadWalletBundle(id) {
  loading.value = true
  error.value = ''
  try {
    const [{ data: w }, { data: bal }, { data: tr }] = await Promise.all([
      Wallets.get(id),
      Wallets.viewCurrBal(id),
      Wallets.viewTransfers(id),
    ])

    wallet.value = w
    balance.value = bal
    transfers.value = tr || []

    txRows.value = (tr || []).map((t, i) => ({
      id: t.id ?? i,
      name: t.name ?? t.description ?? 'Transfer',
      type: typeof t.amount === 'number' && t.amount < 0 ? 'EXPENSE' : 'INCOME',
      categoryName: t.categoryName ?? 'Transfer',
      walletName: w?.name ?? 'Novčanik',
      amount: t.amount,
      currency: w?.currency?.name ?? 'RSD',
      date: (t.dateOfExecution ?? t.date ?? t.createdAt ?? '').toString().slice(0, 10),
    }))
    txHasMore.value = false
  } catch (e) {
    error.value = 'Greška pri učitavanju novčanika.'
  } finally {
    loading.value = false
  }
}

async function onCreateWallet() {
  /* add a form and call Wallets.create(dto) */
}
async function onEditWallet(w) {
  /* call Wallets.updateName / updateCurrency / ... */
}
async function onDeleteWallet(w) {
  /* call Wallets.remove(w.id) */
}
async function onArchiveWallet(w) {
  /* call Wallets.updateArchived(w.id,true) */
}

async function onAddTransaction() {
  /* call Transactions.create(dto from a form) */
}
async function onTransfer() {
  /* call Transactions.move(dto from a form) */
}

onMounted(() => loadWalletBundle(currentWalletId.value))

// ---------- FILTERS ----------
const filters = ref({
  from: '',
  to: '',
  type: '', // 'INCOME' | 'EXPENSE' | ''
  min: '',
  max: '',
  q: '', // search in name/category
})

function onFilterApply(f) {
  filters.value = { ...filters.value, ...f }
}

// helpers for comparing dates/numbers safely
function asNum(n) {
  const x = Number(n)
  return Number.isFinite(x) ? x : null
}
function asDateStr(d) {
  return d ? String(d).slice(0, 10) : ''
}
function inDateRange(d, from, to) {
  const ds = asDateStr(d)
  if (!ds) return true
  if (from && ds < from) return false
  if (to && ds > to) return false
  return true
}

// Use txRows (your raw mapped transfers) -> filtered list for table & charts
const filteredTxRows = computed(() => {
  const from = filters.value.from || ''
  const to = filters.value.to || ''
  const type = filters.value.type || ''
  const min = asNum(filters.value.min)
  const max = asNum(filters.value.max)
  const q = (filters.value.q || '').toLowerCase()

  return (txRows.value || []).filter((t) => {
    // date: prefer explicit transaction date fields you mapped
    const date = t.date
    if (!inDateRange(date, from, to)) return false

    // type filter
    if (type && t.type !== type) return false

    // amount filter
    const amt = asNum(t.amount)
    if (min != null && (amt == null || amt < min)) return false
    if (max != null && (amt == null || amt > max)) return false

    // free search on name/category
    if (q) {
      const hay = `${t.name || ''} ${t.categoryName || ''}`.toLowerCase()
      if (!hay.includes(q)) return false
    }
    return true
  })
})

// ---------- STATS FROM FILTERED ----------
const pieData = computed(() => {
  // sum by category for EXPENSE, use absolute expense amount (positive slices)
  const map = new Map()
  for (const t of filteredTxRows.value) {
    if (t.type !== 'EXPENSE') continue
    const key = t.categoryName || 'Ostalo'
    const amt = Math.abs(Number(t.amount) || 0)
    map.set(key, (map.get(key) || 0) + amt)
  }
  // convert to array of {label, value}
  return Array.from(map, ([label, value]) => ({ label, value }))
})

// top 10 expenses by amount
const topExpenses = computed(() => {
  const rows = filteredTxRows.value
    .filter((t) => t.type === 'EXPENSE')
    .map((t) => ({
      label: t.name || t.categoryName || '—',
      value: Math.abs(Number(t.amount) || 0),
    }))
    .sort((a, b) => b.value - a.value)
  return rows.slice(0, 10)
})
</script>

<style scoped>
.page-xl {
  /* Bigger overall UI for desktop-only usage */
  font-size: 18px; /* base text bigger */
  line-height: 1.6;
}

.page-xl h2 {
  font-size: 32px;
}
.page-xl strong {
  font-weight: 700;
}

.page-xl input,
.page-xl select,
.page-xl button {
  font-size: 18px;
  padding: 14px 16px;
  border-radius: 14px;
}

/* Two cards side-by-side, equal height */
.top-row {
  display: grid;
  grid-template-columns: 1fr 1fr; /* same width columns */
  align-items: stretch; /* make items fill the row height */
  margin-bottom: 32px;
}

/* Make both cards fill available height and look roomy */
.tile {
  height: 100%;
  min-height: 260px; /* adjust to taste (280–320px also works) */
  padding: 28px; /* larger inner spacing */
  display: flex;
  flex-direction: column;
  justify-content: center; /* vertically center content */
}

/* (Optional) Stack on small screens just in case */
@media (max-width: 1024px) {
  .top-row {
    grid-template-columns: 1fr;
  }
}

.progress-ring {
  width: 140px;
  height: 140px;
  align-items: center;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(220px, 1fr));
  gap: 12px;
  margin: 16px 0 8px;
}
.form-grid .check {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 8px;
  border: 1px dashed var(--line);
  border-radius: 12px;
  background: #fffaf3;
}
.form-actions {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: 12px;
}
.err {
  color: #b00020;
}
@media (max-width: 1100px) {
  .form-grid {
    grid-template-columns: 1fr 1fr;
  }
}
@media (max-width: 700px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
