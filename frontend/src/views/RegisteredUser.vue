<template>
  <div class="top-row">
  <UiCard class="tile balans" v-if="wallet">
    <h2 style="margin: 0">
      <strong>{{ money(saved, currency) }}</strong>
      <span v-if="targetAmount"> / {{ money(targetAmount, currency) }}</span>
    </h2>
    <div style="color: var(--ink-muted); margin-top: 8px">
      <div><strong>{{ wallet.name }}</strong> — #{{ wallet.id }}</div>
      <div>Valuta: {{ wallet?.currency?.name || '—' }} · Tip: {{ wallet?.savings ? 'Štednja' : 'Standard' }}</div>
    </div>
  </UiCard>

  <UiCard v-else class="tile balans">
    <h2 style="margin: 0"><strong>—</strong></h2>
    <div style="color: var(--ink-muted); margin-top: 8px">
      Izaberite novčanik iz liste da prikažete detalje.
    </div>
    <LogoutButton />
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

<UiCard v-if="wallet" style="margin-top: 12px;">
  <div class="flex" style="gap: 8px; align-items:center;">
    <select v-model="period">
      <option value="all">Sve</option>
      <option value="day">Dan</option>
      <option value="week">Nedelja</option>
      <option value="month">Mesec</option>
      <option value="quarter">Kvartal</option>
    </select>
    <input v-if="period !== 'all'" type="date" v-model="anchor" />
  </div>
</UiCard>

<UiCard v-if="wallet" style="margin-top: 16px;">
  <div class="flex-between" style="margin-bottom: 8px;">
    <strong>Transakcije</strong>
    <span class="muted" v-if="txRows?.length">{{ txRows.length }} stavki</span>
  </div>

  <div v-if="txRows && txRows.length">
    <table class="tx-table">
      <thead>
        <tr>
          <th>Datum</th>
          <th>Naziv</th>
          <th>Tip</th>
          <th>Iznos</th>
          <th>Izvor</th>
          <th>Cilj</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="t in txRows" :key="t.id">
          <td>{{ (t.dateOfExecution || t.createdAt || '').toString().slice(0,10) }}</td>
          <td>{{ t.transactionName || '—' }}</td>
          <td>{{ t.type === 'INCOME' ? 'Prihod' : t.type === 'EXPENSE' ? 'Rashod' : (t.type || '—') }}</td>
          <td>{{ money(Number(t.amount || 0), currency) }}</td>
          <td>#{{ t.sourceWalletId ?? t.sourceId ?? '—' }}</td>
          <td>#{{ t.targetWalletId ?? t.targetId ?? '—' }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div v-else class="muted">Nema transakcija za ovaj novčanik.</div>
</UiCard>

  <UiCard style="margin-top: 8px;">
  <div class="flex-between" style="margin-bottom: 8px;">
    <strong>Moji novčanici</strong>
    <button class="btn" @click="showCreateWallet = !showCreateWallet">
      {{ showCreateWallet ? 'Zatvori' : '+ Kreiraj novčanik' }}
    </button>
  </div>

  <div class="wallet-list">
    <div
      v-for="w in activeWallets"
      :key="w.id"
      class="wallet-row"
      :class="{ active: sourceWalletId === w.id }"
      @click="selectSource(w.id)"
    >
      <div class="wallet-main">
        <input
          type="radio"
          :checked="sourceWalletId === w.id"
          @change="selectSource(w.id)"
        />

        <div v-if="editingWalletId !== w.id" class="wallet-title">
          <strong>{{ w.name }}</strong>
          <span class="muted">#{{ w.id }}</span>
        </div>

        <div v-else class="wallet-edit-grid" @click.stop>
          <input v-model="walletEditForm.name" placeholder="Naziv" />
          <select v-model="walletEditForm.currency">
            <option value="RSD">RSD</option>
            <option value="EUR">EUR</option>
            <option value="USD">USD</option>
          </select>
          <label class="check tiny">
            <input type="checkbox" v-model="walletEditForm.savings" />
            Štednja
          </label>
        </div>
      </div>

      <div class="wallet-meta">
        <span class="pill">{{ w.currencyName || w.currency?.name || '—' }}</span>
        <span v-if="w.savings" class="pill pill-soft">Štednja</span>

        <div class="wallet-actions" @click.stop>
          <template v-if="editingWalletId !== w.id">
            <button class="btn subtle" @click="beginEdit(w)">Uredi</button>
            <button
              class="btn warn"
              @click="toggleArchive(w)"
              :disabled="archivingId === w.id"
            >
              {{ archivingId === w.id ? '...' : 'Arhiviraj' }}
            </button>
            <button class="btn danger" @click="confirmDelete(w)">Obriši</button>
          </template>

          <template v-else>
            <button class="btn accent" :disabled="savingWallet" @click="saveWallet(w)">Sačuvaj</button>
            <button class="btn" :disabled="savingWallet" @click="cancelEdit">Otkaži</button>
          </template>
        </div>
      </div>
    </div>

    <div v-if="!activeWallets.length" class="muted">
      Nema aktivnih novčanika.
    </div>
  </div>

  <div v-if="walletEditConversionPreview" class="muted conversion-preview">
    {{ walletEditConversionPreview }}
  </div>
</UiCard>

<UiCard v-if="archivedWallets.length" style="margin-top: 16px;">
  <div class="flex-between" style="margin-bottom: 8px;">
    <strong>Arhivirani novčanici</strong>
  </div>

  <div class="wallet-list">
    <div
      v-for="w in archivedWallets"
      :key="w.id"
      class="wallet-row archived"
    >
      <div class="wallet-main">
        <input type="radio" disabled />
        <div class="wallet-title">
          <strong>{{ w.name }}</strong>
          <span class="muted">#{{ w.id }}</span>
        </div>
      </div>

      <div class="wallet-meta">
        <span class="pill">{{ w.currencyName || w.currency?.name || '—' }}</span>
        <span v-if="w.savings" class="pill pill-soft">Štednja</span>

        <button
          class="btn"
          style="margin-left: 8px;"
          @click.stop="toggleArchive(w)"
          :disabled="archivingId === w.id"
        >
          {{ archivingId === w.id ? '...' : 'Vrati' }}
        </button>
      </div>
    </div>
  </div>
</UiCard>


    <UiCard v-if="showCreateWallet" style="margin-top: 20px;">
      <h3>Kreiraj novi novčanik</h3>
      <div class="form-grid">
        <input v-model="walletForm.name" placeholder="Naziv novčanika" />
        <input
          type="number"
          v-model.number="walletForm.initBal"
          min="0"
          step="0.01"
          placeholder="Početni balans"
        />
        <select v-model="walletForm.currency">
          <option disabled value="">Valuta</option>
          <option value="RSD">RSD</option>
          <option value="EUR">EUR</option>
          <option value="USD">USD</option>
        </select>
        <label class="check">
          <input type="checkbox" v-model="walletForm.savings" /> Štedni novčanik
        </label>
        <div class="form-actions">
          <button class="btn accent" :disabled="walletSaving" @click="submitWallet">
            {{ walletSaving ? 'Čuvanje…' : 'Kreiraj' }}
          </button>
          <span v-if="walletError" class="err">{{ walletError }}</span>
        </div>
      </div>
    </UiCard>

    <div class="top-row">
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

  <div v-if="showTransfer" class="form-grid" style="margin-top: 8px;">
    <div class="form-row" style="grid-column: 1 / -1;">
      <label class="muted">Početni novčanik:</label>
      <input type="text" :value="sourceWalletName || '—'" readonly />
    </div>

    <select v-model.number="transferForm.targetWalletId">
      <option disabled value="">Odaberite odredišni novčanik</option>
      <option
        v-for="opt in activeWallets"
        :key="opt.id"
        :value="opt.id"
        :disabled="opt.id === sourceWalletId"
      >
        {{ opt.name }} ({{ opt.currencyName || opt.currency?.name || '—' }}) — #{{ opt.id }}
      </option>
    </select>

    <input
      type="number"
      v-model.number="transferForm.amount"
      min="0"
      step="0.01"
      placeholder="Iznos"
    />
    <input v-model="transferForm.transactionName" placeholder="Naziv transakcije" />
    <select v-model="transferForm.type">
      <option value="INCOME">Prihod</option>
      <option value="EXPENSE">Rashod</option>
    </select>
    <input type="date" v-model="transferForm.dateOfExecution" />

    <label class="check">
      <input type="checkbox" v-model="transferForm.repeatable" /> Ponavljajuća
    </label>
    <label class="check" :style="{ opacity: transferForm.repeatable ? 1 : 0.5 }">
      <input type="checkbox" v-model="transferForm.activeRepeat" :disabled="!transferForm.repeatable" />
      Aktivna
    </label>
    <input v-model="transferForm.frequency" :disabled="!transferForm.repeatable" placeholder="Frekvencija (npr. P1M)" />

    <div class="form-actions">
      <button class="btn" :disabled="transferSaving" @click="submitTransfer">
        {{ transferSaving ? 'Prebacujem…' : 'Prebaci' }}
      </button>
      <span v-if="transferError" class="err">{{ transferError }}</span>
    </div>
  </div>
</UiCard>

  <UiCard style="margin-top: 16px;">
  <div class="flex-between" style="margin-bottom: 8px;">
    <strong>Statistika i pregled</strong>
    <div class="flex" style="gap:8px;align-items:center;">
      <select v-model="granularity">
        <option value="DAY">Dan</option>
        <option value="WEEK">Nedelja</option>
        <option value="MONTH">Mesec</option>
        <option value="YEAR">Godina</option>
      </select>
      <input type="date" v-model="from" />
      <input type="date" v-model="to" />
      <input type="number" v-model.number="minAmount" placeholder="Min iznos" style="width:120px;">
      <input type="number" v-model.number="maxAmount" placeholder="Max iznos" style="width:120px;">
      <button class="btn" @click="loadStats">Primeni</button>
    </div>
  </div>

  <div>
    <canvas id="seriesChart" height="140"></canvas>
  </div>

  <div class="grid2" style="margin-top:12px;">
    <div>
      <canvas id="catChart" height="160"></canvas>
    </div>
    <div>
      <table class="tx-table">
        <thead>
          <tr><th colspan="4">Top 10 troškova</th></tr>
          <tr>
            <th>Datum</th><th>Naziv</th><th>Kategorija</th><th>Iznos</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in topExpensesRows" :key="t.id">
            <td>{{ (t.dateOfExecution||'').toString().slice(0,10) }}</td>
            <td>{{ t.name }}</td>
            <td>{{ t.categoryName || '—' }}</td>
            <td>{{ money(t.amount, currency) }}</td>
          </tr>
          <tr v-if="!topExpensesRows.length"><td colspan="4" class="muted">Nema stavki.</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</UiCard>

</template>

<script setup>
import LogoutButton from '@/components/LogoutButton.vue'
import { ref, onMounted, computed, watch } from 'vue'
import UiCard from '../components/UiCard.vue'
import { Stats } from '../services/api'
import ProgressRing from '../components/ProgressRing.vue'
import { Wallets, Transactions } from '../services/api'
import Chart from 'chart.js/auto'

const showTransfer = ref(false)
const showCreateWallet = ref(false)
const today = new Date().toISOString().slice(0, 10)
const walletForm = ref({name: '', initBal: '', currency: '', savings: false,})
const walletSaving = ref(false)
const walletError = ref('')
const transferForm = ref({
  targetWalletId: null,
  amount: '',
  transactionName: '',
  type: 'EXPENSE',
  dateOfExecution: today,
  repeatable: false,
  activeRepeat: false,
  frequency: '',
})
const userWallets = ref([])
const sourceWalletId = ref(null)
const wallet = ref(null)
const balance = ref(null)
const txRows = ref([])
const loading = ref(false)
const error = ref('')
const transferSaving = ref(false)
const transferError = ref('')
const period = ref('all')
const anchor = ref(new Date().toISOString().slice(0,10))
const sourceWalletName = computed(() => {
  const w = userWallets.value.find(x => x.id === sourceWalletId.value)
  return w?.name || ''
})
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userId = user?.id
const currency = computed(() => wallet.value?.currency?.name || 'RSD')
const saved = computed(() => Number(balance.value ?? wallet.value?.currBal ?? 0))
const targetAmount = computed(() => Number(wallet.value?.goal?.targetAmount ?? 0))
const progressPct = computed(() =>
  !targetAmount.value || targetAmount.value <= 0
    ? 0
    : Math.min(100, Math.round((saved.value / targetAmount.value) * 100))
)

watch([sourceWalletId, period, anchor], async () => {
  if (sourceWalletId.value) {
    await loadWalletBundle(sourceWalletId.value)
  }
})

const granularity = ref('MONTH')
const from = ref('')
const to = ref('')
const minAmount = ref(null)
const maxAmount = ref(null)
const categoryId = ref(null)
const archivingId = ref(null)

const activeWallets = computed(() => userWallets.value.filter(w => !w.archived))
const archivedWallets = computed(() => userWallets.value.filter(w => w.archived))
const walletEditForm   = ref({ name: '', currency: '', savings: false })
const savingWallet     = ref(false)
const walletSaveError  = ref('')

function selectSource(id) {
  const w = userWallets.value.find(x => x.id === id)
  if (w?.archived) return
  sourceWalletId.value = id
}

function coerceFlags(list) {
  return (list || []).map(w => ({
    ...w,
    archived: w.archived === true || w.archived === 1 || w.archived === 'true' || w.archived === 'TRUE',
    savings:  w.savings  === true || w.savings  === 1 || w.savings  === 'true' || w.savings  === 'TRUE',
  }))
}


const editingWalletId = ref(null)

const FX = { RSD: 1, EUR: 117.0, USD: 108.0 }

function convertAmount(amount, from, to) {
  if (from === to) return amount
  const inRsd = amount * (FX[from] || 1)
  return inRsd / (FX[to] || 1)
}

function beginEdit(w) {
  editingWalletId.value = w.id
  walletEditForm.value = {
    name: w.name ?? '',
    currency: w.currency?.name ?? w.currencyName ?? 'RSD',
    savings: !!w.savings,
  }
}

const walletEditConversionPreview = computed(() => {
  if (!editingWalletId.value) return ''
  const w = userWallets.value.find(x => x.id === editingWalletId.value)
  if (!w) return ''
  const oldCur = (w.currency?.name || w.currencyName || 'RSD')
  const newCur = walletEditForm.value.currency
  if (oldCur === newCur) return ''
  const currentBal = Number(w.currBal ?? w.currentBalance ?? 0)
  const converted = convertAmount(currentBal, oldCur, newCur)
  return `Konverzija prikaza: ${money(currentBal, oldCur)} → ${money(converted, newCur)}`
})

function cancelEdit() {
  editingWalletId.value = null
  walletEditForm.value = { name: '', currency: 'RSD', savings: false }
}

async function toggleArchive(w) {
  try {
    archivingId.value = w.id

    w.archived = !w.archived

    await Wallets.setArchived?.(w.id, w.archived)

    if (w.archived && sourceWalletId.value === w.id) {
      sourceWalletId.value = activeWallets.value[0]?.id || null
    }
  } catch (e) {
    console.error('archive toggle failed', e)
    w.archived = !w.archived
  } finally {
    archivingId.value = null
  }
}

async function deleteWalletApi(id) {
  if (Wallets.remove) {
    return Wallets.remove(id)
  }
  const res = await fetch(`/api/wallets/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('DELETE /api/wallets failed')
}

async function saveWallet(w) {
  walletSaveError.value = ''
  if (!w?.id) return

  try {
    savingWallet.value = true

    const oldName = w.name ?? ''
    const oldCur  = w.currency?.name ?? w.currencyName ?? ''
    const oldSav  = !!w.savings

    const newName = (walletEditForm.value.name ?? '').trim()
    const newCur  = walletEditForm.value.currency ?? ''
    const newSav  = !!walletEditForm.value.savings

    const ops = []
    if (newName && newName !== oldName) {
      ops.push(Wallets.updateName(w.id, newName))
    }
    if (newCur && newCur !== oldCur) {
      ops.push(Wallets.updateCurrency(w.id, newCur))
    }
    if (newSav !== oldSav) {
      ops.push(Wallets.updateSavings(w.id, newSav))
    }

    if (ops.length) {
      await Promise.all(ops)
      await loadUserWallets()
      const updated = userWallets.value.find(x => x.id === w.id)
      if (updated) {
        wallet.value = updated
        sourceWalletId.value = updated.id
        await loadWalletBundle(updated.id)
      }
    }

    editingWalletId.value = null
  } catch (e) {
    console.error('saveWallet failed:', e?.response?.data || e)
    walletSaveError.value = 'Greška pri čuvanju izmena.'
  } finally {
    savingWallet.value = false
  }
}

async function confirmDelete(w) {
  if (!confirm(`Obrisati novčanik "${w.name}"? Ova akcija je trajna.`)) return
  try {
    await deleteWalletApi(w.id)
    userWallets.value = userWallets.value.filter(x => x.id !== w.id)
    if (sourceWalletId.value === w.id) {
      const firstActive = userWallets.value.find(x => !x.archived)
      sourceWalletId.value = firstActive?.id ?? null
      if (firstActive) await loadWalletBundle(firstActive.id)
      else {
        wallet.value = null
        balance.value = 0
        txRows.value = []
      }
    }
  } catch (e) {
    console.error('delete wallet error:', e)
    alert('Greška pri brisanju novčanika.')
  }
}

let seriesChart, catChart
const topExpensesRows = ref([])

function chartOrCreate(ctxId, cfg) {
  const el = document.getElementById(ctxId)
  if (!el) return null
  if (ctxId === 'seriesChart') { if (seriesChart) seriesChart.destroy(); seriesChart = new Chart(el, cfg); return seriesChart }
  if (ctxId === 'catChart')    { if (catChart) catChart.destroy();     catChart    = new Chart(el, cfg);   return catChart }
  return null
}

async function loadStats() {
  if (!userId) return
  const params = {
    userId,
    granularity: granularity.value,
    from: from.value || undefined,
    to: to.value || undefined,
    categoryId: categoryId.value || undefined,
    minAmount: minAmount.value || undefined,
    maxAmount: maxAmount.value || undefined,
  }

  const [{ data: series }, { data: cats }, { data: tops }] = await Promise.all([
    Stats.series(params),
    Stats.byCategory(params),
    Stats.topExpenses({ ...params, limit: 10 }),
  ])

  const labels = series.map(p => p.bucketDate)
  const income = series.map(p => Number(p.income || 0))
  const expense = series.map(p => Number(p.expense || 0))

  chartOrCreate('seriesChart', {
    type: 'line',
    data: { labels, datasets: [
      { label: 'Prihod', data: income, tension: 0.25 },
      { label: 'Rashod', data: expense, tension: 0.25 }
    ]},
    options: { responsive: true, maintainAspectRatio: false }
  })

  const catLabels = cats.map(c => c.categoryName || 'Bez kategorije')
  const catExpense = cats.map(c => Number(c.expense || 0))
  chartOrCreate('catChart', {
    type: 'doughnut',
    data: { labels: catLabels, datasets: [{ label: 'Rashodi po kategorijama', data: catExpense }] },
    options: { responsive: true, maintainAspectRatio: false }
  })

  topExpensesRows.value = tops || []
}

function isoDate(dt) { return dt.toISOString().slice(0,10) }

function getRange(period, anchorStr) {
  if (period === 'all') return null
  const d = new Date(anchorStr);
  const start = new Date(d); const end = new Date(d);

  if (period === 'day') {
  } else if (period === 'week') {
    const day = (d.getDay() + 6) % 7;
    start.setDate(d.getDate() - day);
    end.setDate(start.getDate() + 6);
  } else if (period === 'month') {
    start.setDate(1);
    end.setMonth(start.getMonth() + 1, 0);
  } else if (period === 'quarter') {
    const q = Math.floor(d.getMonth() / 3);
    start.setMonth(q * 3, 1);
    end.setMonth(q * 3 + 3, 0);
  }
  return { from: isoDate(start), to: isoDate(end) }
}

async function submitTransfer() {
  transferError.value = ''
  const targetId = Number(transferForm.value.targetWalletId)
  const sourceId = Number(sourceWalletId.value)
  const amt = toNumber(transferForm.value.amount)
  transferError.value = ''

  const source = userWallets.value.find(x => x.id === Number(sourceWalletId.value))
  const target = userWallets.value.find(x => x.id === Number(transferForm.value.targetWalletId))

  if (!source || source.archived) {
    transferError.value = 'Početni novčanik je arhiviran ili nije izabran.'
    return
  }
  if (!target || target.archived) {
    transferError.value = 'Odredišni novčanik je arhiviran ili nije izabran.'
    return
  }

  if (!sourceId) {
    transferError.value = 'Odaberite početni novčanik.'
    return
  }
  if (!targetId || !amt || amt <= 0) {
    transferError.value = 'Unesite validne vrednosti: odredišni novčanik i pozitivan iznos.'
    return
  }
  if (targetId === sourceId) {
    transferError.value = 'Izvorni i ciljni novčanik ne mogu biti isti.'
    return
  }

  try {
    transferSaving.value = true
    const payload = {
      sourceWalletId: sourceId,
      targetWalletId: targetId,
      amount: amt,
      transactionName: transferForm.value.transactionName,
      type: transferForm.value.type,
      dateOfExecution: transferForm.value.dateOfExecution,
      repeatable: !!transferForm.value.repeatable,
      activeRepeat: !!transferForm.value.activeRepeat,
      frequency: transferForm.value.frequency || null,
    }
    await Transactions.move(payload)
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
    transferError.value = 'Greška pri transferu.'
  } finally {
    transferSaving.value = false
  }
}

async function submitWallet() {
  walletError.value = ''

  if (!walletForm.value.name || !walletForm.value.initBal || !walletForm.value.currency) {
    walletError.value = 'Popunite naziv, balans i valutu.'
    return
  }

  try {
    walletSaving.value = true

    const payload = {
      name: walletForm.value.name,
      initBal: walletForm.value.initBal,
      currBal: walletForm.value.initBal,
      currency: { name: walletForm.value.currency },
      creatingDate: today,
      archived: false,
      savings: walletForm.value.savings,
      goal: null,
    }

    await Wallets.create(userId, payload)

    await loadUserWallets()

    if (userWallets.value.length) {
      sourceWalletId.value = userWallets.value[userWallets.value.length - 1].id
    }

    walletForm.value = { name: '', initBal: '', currency: '', savings: false }
    showCreateWallet.value = false
  } catch (e) {
    console.error('create wallet failed:', e?.response?.status, e?.response?.data || e)
    walletError.value = 'Greška pri kreiranju novčanika.'
  } finally {
    walletSaving.value = false
  }
}

function toNumber(n) {
  const x = Number(n)
  return isNaN(x) ? 0 : x
}

async function loadUserWallets() {
  try {
    const { data } = await Wallets.getAll(userId)
    userWallets.value = coerceFlags(data)
    if (!sourceWalletId.value && userWallets.value.length) {
      sourceWalletId.value = userWallets.value[0].id
    }
    console.log('[wallets] active:', userWallets.value.filter(x => !x.archived).length,
                'archived:', userWallets.value.filter(x =>  x.archived).length)
  } catch (e) {
    console.error('getAll wallets failed:', e?.response?.status, e?.response?.data || e)
    userWallets.value = []
  }
}

async function loadWalletBundle(id) {
  if (!id) return
  loading.value = true;
  error.value = '';
  try {
    const range = getRange(period.value, anchor.value);

    const [{ data: w }, { data: bal }, { data: tr }] = await Promise.all([
      Wallets.get(id),
      Wallets.viewCurrBal(id),
      Wallets.viewTransactions(id, range || undefined),
    ])

    wallet.value = w;
    balance.value = bal;
    txRows.value = tr || [];

    console.log('bundle:', { id, period: period.value, range, txCount: txRows.value.length })
  } catch (e) {
    console.error('loadWalletBundle failed:', e?.response?.status, e?.response?.data || e)
    error.value = 'Greška pri učitavanju novčanika.';
  } finally {
    loading.value = false;
  }
}

watch([period, anchor, sourceWalletId], async () => {
  if (sourceWalletId.value) await loadWalletBundle(sourceWalletId.value);
})

onMounted(async () => {
  await loadUserWallets()
  if (userWallets.value.length) {
    sourceWalletId.value = userWallets.value[0].id;
    await loadWalletBundle(sourceWalletId.value);
    const d = new Date()
    const start = new Date(d.getFullYear(), d.getMonth(), 1)
    const end   = new Date(d.getFullYear(), d.getMonth()+1, 0)
    from.value = start.toISOString().slice(0,10)
    to.value   = end.toISOString().slice(0,10)
    await loadStats()
  }
})

function money(val, cur) {
  const n = Number(val ?? 0)
  const symbol = cur === 'EUR' ? '€' : cur === 'USD' ? '$' : ''
  const tail = cur === 'EUR' || cur === 'USD' ? '' : ` ${cur || ''}`
  return `${symbol} ${n.toLocaleString('sr-RS')}${tail}`
}
</script>

<style scoped>
.page-xl {
  font-size: 18px;
  line-height: 1.6;
}

.page-xl h3 {
  margin-bottom: 12px;
}

.page-xl input,
.page-xl select,
.page-xl button {
  font-size: 18px;
  padding: 14px 16px;
  border-radius: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(220px, 1fr));
  gap: 12px;
  margin: 16px 0 8px;
}

.check {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 8px;
  border: 1px dashed var(--line);
  border-radius: 12px;
  background: #fffaf3;
}

.check.tiny { padding: 6px 8px; font-size: 14px; }

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: 12px;
}
.err {
  color: #b00020;
}

.wallet-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.wallet-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--line);
  border-radius: 12px;
  cursor: pointer;
  background: #fff;
}

.wallet-row.active {
  outline: 2px solid var(--accent, #4f46e5);
  background: #f8faff;
}

.wallet-main {
  display: flex;
  gap: 10px;
  align-items: center;
}

.wallet-title {
  display: flex;
  gap: 8px;
  align-items: baseline;
}

.wallet-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.wallet-row.archived {
  opacity: 0.65;
}

.wallet-row.archived .wallet-actions,
.wallet-row.archived .wallet-actions .btn {
  pointer-events: all;
  cursor: pointer;
}


.wallet-edit-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr auto;
  gap: 8px;
  align-items: center;
}

.pill {
  border: 1px solid var(--line);
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
}

.pill-soft {
  background: #fffaf3;
  border-color: #f6e3b4;
}

.muted {
  color: var(--ink-muted);
  font-size: 14px;
}

.btn{
  background-color: #d6976b;
  font-family: Verdana, Geneva, Tahoma, sans-serif;
  font-weight: bold;
}

.grid2 { display: grid; grid-template-columns: 1.2fr 1fr; gap: 16px; }

.conversion-preview { margin-top: 6px; }
</style>
