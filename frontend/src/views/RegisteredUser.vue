<template>
  <div class="user-container">
    <div class="user-header">
      <h1>Moj Novčanik</h1>
      <div class="header-actions">
        <LogoutButton />
      </div>
    </div>

    <div class="top-row">
      <div class="card tile balans" v-if="wallet">
        <h2 style="margin: 0">
          <strong>
            {{ money(saved, currency) }}
            <template v-if="wallet.goal && targetAmount">
              / {{ money(targetAmount, currency) }}
            </template>
          </strong>
        </h2>
        <div class="text-muted" style="margin-top: 8px">
          <div><strong>{{ wallet.name }}</strong></div>
          <div>Valuta: {{ wallet?.currency?.name || '—' }} · Tip: {{ (wallet?.savingsWallet ?? wallet?.savings) ? 'Štednja' : 'Standard' }}</div>
        </div>
      </div>

      <div class="card tile balans" v-else>
        <h2 style="margin: 0"><strong>—</strong></h2>
        <div class="text-muted" style="margin-top: 8px">
          Izaberite novčanik iz liste da prikažete detalje.
        </div>
      </div>
    </div>

    <div class="card" v-if="wallet" style="margin-top: 12px;">
      <div class="flex gap-4">
        <select v-model="period">
          <option value="all">Sve</option>
          <option value="day">Dan</option>
          <option value="week">Nedelja</option>
          <option value="month">Mesec</option>
          <option value="quarter">Kvartal</option>
        </select>
        <input v-if="period !== 'all'" type="date" v-model="anchor" />
      </div>
    </div>

    <div class="card" v-if="wallet" style="margin-top: 16px;">
      <div class="flex-between" style="margin-bottom: 8px;">
        <strong>Transakcije</strong>
        <div class="flex gap-4">
          <span class="text-muted" v-if="txRows?.length">{{ txRows.length }} stavki</span>
          <button class="btn btn-warn" :disabled="stopAllBusy" @click="stopAllRepeats">
            {{ stopAllBusy ? 'Zaustavljam…' : 'Zaustavi sva ponavljanja' }}
          </button>
        </div>
      </div>

  <div v-if="txRows && txRows.length">
    <table class="tx-table">
          <thead>
      <tr>
        <th>Datum</th>
        <th>Naziv</th>
        <th>Tip</th>
        <th>Iznos</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="t in txRows" :key="t.id">
        <td>{{ (t.dateOfExecution || t.createdAt || '').toString().slice(0,10) }}</td>
        <td>{{ t.name || t.transactionName || '—' }}</td>
        <td>{{ t.type === 'INCOME' ? 'Prihod' : t.type === 'EXPENSE' ? 'Rashod' : (t.type || '—') }}</td>
        <td>{{ money(Number(t.amount || 0), currency) }}</td>
      </tr>
    </tbody>
    </table>
  </div>

      <div v-else class="text-muted">Nema transakcija za ovaj novčanik.</div>
    </div>

    <div class="card" style="margin-top: 8px;">
      <div class="flex-between" style="margin-bottom: 8px;">
        <strong>Moji novčanici</strong>
        <button class="btn btn-primary" @click="showCreateWallet = !showCreateWallet">
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

          <div
            v-if="isSavingsWallet(w) && hasGoal(w)"
            class="goal-chip"
            @click.stop
          >
            <ProgressRing :value="goalPct(w)" :size="52" />
            <div class="goal-chip-text">
              <div class="text-muted">Cilj</div>
              <div class="tight">
                <strong>{{ money(Number(w.currBal ?? 0), w.currency?.name || w.currencyName || 'RSD') }}</strong>
                <span>
                  /
                  {{ money(Number(w.goal.targetAmount ?? w.goal.target_amount ?? 0), w.currency?.name || w.currencyName || 'RSD') }}
                </span>
              </div>
            </div>
          </div>

          <div class="wallet-actions" @click.stop>
            <template v-if="editingWalletId !== w.id">
              <button class="btn btn-subtle" @click="beginEdit(w)">Uredi</button>
              <button
                class="btn btn-warn"
                @click="toggleArchive(w)"
                :disabled="archivingId === w.id"
              >
                {{ archivingId === w.id ? '...' : 'Arhiviraj' }}
              </button>
              <button class="btn btn-danger" @click="confirmDelete(w)">Obriši</button>
            </template>

            <template v-else>
              <button class="btn btn-accent" :disabled="savingWallet" @click="saveWallet(w)">Sačuvaj</button>
              <button class="btn btn-subtle" :disabled="savingWallet" @click="cancelEdit">Otkaži</button>
            </template>
          </div>
        </div>
      </div>

      <div v-if="!activeWallets.length" class="text-muted">
        Nema aktivnih novčanika.
      </div>

      <div v-if="walletEditConversionPreview" class="text-muted conversion-preview">
        {{ walletEditConversionPreview }}
      </div>
    </div>

    <div class="card" v-if="archivedWallets.length" style="margin-top: 16px;">
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
            </div>
          </div>

          <div class="wallet-meta">
            <span class="pill">{{ w.currencyName || w.currency?.name || '—' }}</span>
            <button
              class="btn btn-primary"
              style="margin-left: 8px;"
              @click.stop="toggleArchive(w)"
              :disabled="archivingId === w.id"
            >
              {{ archivingId === w.id ? '...' : 'Vrati' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="card" v-if="showCreateWallet" style="margin-top: 20px;">
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
          <input type="checkbox" v-model="walletForm.savingsWallet" /> Štedni novčanik
        </label>

        <div v-if="walletForm.savingsWallet" class="form-grid" style="grid-template-columns: repeat(3, minmax(220px, 1fr));">
          <input v-model="walletForm.goalName" placeholder="Naziv cilja" />
          <input type="number" min="0" step="0.01" v-model.number="walletForm.goalTarget" placeholder="Ciljni iznos" />
          <input type="date" v-model="walletForm.goalDeadline" placeholder="Rok" />
        </div>

        <div class="form-actions">
          <button class="btn btn-accent" :disabled="walletSaving" @click="submitWallet">
            {{ walletSaving ? 'Čuvanje…' : 'Kreiraj' }}
          </button>
          <span v-if="walletError" class="error-message">{{ walletError }}</span>
        </div>
      </div>
    </div>

    <div class="top-row">
      <div class="card tile goal-card" v-if="wallet?.goal">
        <strong>{{ wallet.goal.name }}</strong>
        <div class="flex gap-4" style="align-items: center; margin-top: 10px">
          <ProgressRing class="progress-ring" :value="progressPct" />
          <div>
            <div>
              <strong>{{ money(saved, currency) }}</strong>
              <span> / {{ money(targetAmount, currency) }}</span>
            </div>
            <div class="text-muted">
              Novčanik: {{ wallet?.savings ? 'Štednja' : 'Standard' }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card" style="margin-top: 20px;">
      <div class="flex-between">
        <strong>Plaćanja i transferi</strong>
        <div class="flex gap-4">
          <button class="btn btn-primary" @click="showTransfer = !showTransfer">
            {{ showTransfer ? 'Zatvori' : 'Novo plaćanje' }}
          </button>
        </div>
      </div>

      <div v-if="showTransfer" class="form-grid" style="margin-top: 8px;">
        <div class="form-group" style="grid-column: 1 / -1;">
          <label class="text-muted">Početni novčanik:</label>
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
            {{ opt.name }} ({{ opt.currencyName || opt.currency?.name || '—' }})
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

        <select v-model="transferForm.frequency" :disabled="!transferForm.repeatable">
          <option value="">— Odaberite učestalost —</option>
          <option value="DAILY">Svakog dana</option>
          <option value="WEEKLY">Nedeljno</option>
          <option value="MONTHLY">Mesečno</option>
          <option value="EVERY_2_MIN">Svakih 2 min (test)</option>
        </select>

    <div class="form-actions">
      <button class="btn" :disabled="transferSaving" @click="submitTransfer">
        {{ transferSaving ? 'Prebacujem…' : 'Prebaci' }}
      </button>
      <span v-if="transferError" class="err">{{ transferError }}</span>
    </div>

    <select v-model="transferForm.categoryId">
      <option :value="null">— Bez kategorije —</option>
      <option v-for="c in categories" :key="c.id" :value="c.id">
        {{ c.name }} ({{ c.type }})
      </option>
    </select>

    <div class="flex" style="gap:8px; align-items:center;">
      <input v-model="newCatName" placeholder="Nova kategorija" style="min-width: 200px;" />
      <select v-model="newCatType" style="min-width: 140px;">
        <option value="INCOME">Prihod</option>
        <option value="EXPENSE">Rashod</option>
      </select>
      <button class="btn" :disabled="creatingCat" @click.prevent="createCategoryInline">
        {{ creatingCat ? 'Dodajem…' : 'Dodaj kategoriju' }}
      </button>
    </div>
    <div v-if="catLoading" class="muted">Učitavanje kategorija…</div>
    <div v-if="catError" class="err">{{ catError }}</div>
  </div>
  </div>
      <div>
        <canvas id="seriesChart" height="140"></canvas>
      </div>

<UiCard class="centered-card" style="margin-top: 16px;">
  <div style="text-align:center; margin-bottom: 8px;">
    <strong style="font-size: 18px;">Top 10 troškova</strong>
  </div>

  <div class="flex" style="justify-content:center; gap:8px; margin:8px 0;">
    <select v-model="topPeriod">
      <option value="all">Sve</option>
      <option value="day">Dan</option>
      <option value="week">Nedelja</option>
      <option value="month">Mesec</option>
      <option value="quarter">Kvartal</option>
    </select>
    <input v-if="topPeriod !== 'all'" type="date" v-model="topAnchor" />
    <button class="btn" @click="loadTopExpenses">Primeni</button>
  </div>

  <div v-if="topLoading" class="muted" style="text-align:center; padding: 12px;">Učitavanje…</div>
  <div v-else-if="topError" class="err" style="text-align:center;">{{ topError }}</div>

  <div v-else>
    <table class="pretty-table">
      <thead>
        <tr>
          <th>#</th>
          <th>Datum</th>
          <th>Naziv</th>
          <th>Kategorija</th>
          <th style="text-align:right;">Iznos</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(t, i) in topRows" :key="i">
          <td style="text-align:center; width: 44px;">{{ i + 1 }}</td>
          <td style="width: 110px;">{{ (t.dateOfExecution||'').toString().slice(0,10) }}</td>
          <td>{{ t.name }}</td>
          <td>{{ t.categoryName || '—' }}</td>
          <td style="text-align:right; white-space:nowrap;">{{ money(t.amount, currency) }}</td>
        </tr>
        <tr v-if="!topRows.length">
          <td colspan="5" class="muted" style="text-align:center;">Nema stavki za izabrani period.</td>
        </tr>
      </tbody>
    </table>
  </div>
</UiCard>


    <div class="card" style="margin-top: 20px;">
      <h3>Uredi profil</h3>
      <div class="form-grid">
        <input v-model="editUserForm.firstName" placeholder="Ime" />
        <input v-model="editUserForm.lastName" placeholder="Prezime" />
        <input v-model="editUserForm.email" type="email" placeholder="Email" />
        <input v-model="editUserForm.birthDate" type="date" placeholder="Datum rođenja" />
        <div class="form-actions">
          <button class="btn btn-accent" :disabled="savingUser" @click="saveUserProfile">
            {{ savingUser ? 'Čuvanje…' : 'Sačuvaj promene' }}
          </button>
          <span v-if="userError" class="error-message">{{ userError }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LogoutButton from '@/components/LogoutButton.vue'
import { ref, onMounted, computed, watch } from 'vue'
import { Stats } from '../services/api'
import ProgressRing from '../components/ProgressRing.vue'
import { Wallets, Transactions } from '../services/api'
import Chart from 'chart.js/auto'
import { Users } from '../services/api'
import { Categories, Goal } from '../services/api'

const showTransfer = ref(false)
const showCreateWallet = ref(false)
const today = new Date().toISOString().slice(0, 10)
const walletForm = ref({name: '', initBal: '', currency: '', savingsWallet: false, goalEnabled: false, goalName: '', goalAmount: null, goalDeadline: ''})

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
  categoryId: null,
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
const topRows    = ref([])
const topLoading = ref(false)
const topError   = ref('')

const activeWallets = computed(() => userWallets.value.filter(w => !w.archived))
const archivedWallets = computed(() => userWallets.value.filter(w => w.archived))
const walletEditForm   = ref({ name: '', currency: '', savings: false })
const savingWallet     = ref(false)
const walletSaveError  = ref('')
const savingsTogglingId = ref(null)

const categories = ref([])
const categoryLoading = ref(false)
const catError = ref('')
const newCat = ref({ name: '', type: 'EXPENSE' })
const showNewCat = ref(false)

const incomeCategories = computed(() => categories.value.filter(c => c.type === 'INCOME'))
const expenseCategories = computed(() => categories.value.filter(c => c.type === 'EXPENSE'))
const catLoading     = ref(false)
const creatingCat    = ref(false)
const newCatName     = ref('')
const newCatType     = ref('EXPENSE')

const repeatTogglingId = ref(null)

const liveOn = ref(true)
const liveRefreshSec = ref(15)
let liveTimer = null
const stopAllBusy = ref(false)
const topPeriod = ref('month')
const topAnchor = ref(new Date().toISOString().slice(0,10)) 

const topRange = computed(() => {
  if (topPeriod.value === 'all') return null
  return getRange(topPeriod.value, topAnchor.value)
})
const topFrom = computed(() => topRange.value?.from || '')
const topTo   = computed(() => topRange.value?.to   || '')

async function loadTopExpenses() {
  if (!userId) return
  topError.value = ''
  topLoading.value = true
  try {
    const today = new Date().toISOString().slice(0,10)
    const start = topFrom.value || '1970-01-01'
    const end   = topTo.value   || today

    const params = { userId, startDate: start, endDate: end }

    const call = Transactions.topExpenses
      ? () => Transactions.topExpenses(params)
      : async () => {
          const qs = new URLSearchParams(params).toString()
          const res = await fetch(`/api/transactions/top-expenses?${qs}`)
          if (!res.ok) throw new Error(`HTTP ${res.status}`)
          const data = await res.json()
          return { data }
        }

    const { data } = await call()
    topRows.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error('topExpenses failed:', err?.response?.data || err)
    topError.value = 'Greška pri učitavanju Top 10 troškova.'
    topRows.value = []
  } finally {
    topLoading.value = false
  }
}

async function liveTick() {
  if (!liveOn.value) return
  if (!sourceWalletId.value) return
  try {
    await loadWalletBundle(sourceWalletId.value)
    await loadTopExpenses()
  } catch (e) {
    console.debug('liveTick failed:', e?.response?.data || e)
  }
}

function startLive() {
  stopLive()
  liveTimer = setInterval(liveTick, liveRefreshSec.value * 1000)
}

function stopLive() {
  if (liveTimer) {
    clearInterval(liveTimer)
    liveTimer = null
  }
}

function handleVisibility() {
  if (document.hidden) stopLive()
  else if (liveOn.value) startLive()
}

watch(liveOn, (v) => { v ? startLive() : stopLive() })
watch(liveRefreshSec, () => { if (liveOn.value) startLive() })

async function afterDataChangeReload() {
  if (sourceWalletId.value) await loadWalletBundle(sourceWalletId.value)
  await loadTopExpenses()
}

async function stopAllRepeats() {
  if (stopAllBusy.value) return;
  stopAllBusy.value = true;
  try {
    await Transactions.stopAllRepeats({ userId, walletId: sourceWalletId.value || undefined });
    if (sourceWalletId.value) {
      await loadWalletBundle(sourceWalletId.value);
      await loadStats();
      await loadStats();
    }
  } catch (e) {
    console.error('stopAllRepeats failed:', e?.response?.data || e);
    alert('Greška: nije moguće zaustaviti ponavljanja.');
  } finally {
    stopAllBusy.value = false;
  }
}

async function loadCategories() {
  try {
    catLoading.value = true
    const { data } = await Categories.getForUser(userId)
    categories.value = data || []
  } catch (e) {
    console.error('loadCategories failed:', e?.response?.data || e)
    catError.value = 'Greška pri učitavanju kategorija.'
  } finally {
    catLoading.value = false
  }
}

async function createCategoryInline() {
  const name = (newCatName.value || '').trim()
  const type = newCatType.value || 'EXPENSE'
  if (!name) return
  try {
    creatingCat.value = true
    await Categories.create({ userId, name, type })
    newCatName.value = ''
    await loadCategories()
    const created = categories.value.find(c => c.name === name && c.type === type)
    if (created) transferForm.value.categoryId = created.id
  } catch (e) {
    console.error('createCategoryInline failed:', e?.response?.data || e)
    alert('Greška pri kreiranju kategorije.')
  } finally {
    creatingCat.value = false
  }
}

function isSavingsWallet(w) {
  return w?.savingsWallet === true
    || w?.savings_wallet === true
    || w?.savings === true;
}

function readTargetAmount(w) {
  const g = w?.goal;
  const raw =
    g?.targetAmount ??
    g?.target_amount ??
    w?.targetAmount ??
    w?.target_amount ??
    null;
  const n = Number(raw);
  return Number.isFinite(n) ? n : 0;
}

const isSavings = computed(() => isSavingsWallet(wallet.value));
const targetAmount = computed(() => {
  const g = wallet.value?.goal || {}
  const raw =
    g.targetAmount ??
    g.target_amount ??
    wallet.value?.targetAmount ??
    wallet.value?.target_amount ??
    0
  const n = Number(raw)
  return Number.isFinite(n) ? n : 0
})

function goalPct(w) {
  const target = Number(w?.goal?.targetAmount ?? 0)
  const current = Number(w?.currBal ?? 0)
  if (!target || target <= 0) return 0
  return Math.max(0, Math.min(100, Math.round((current / target) * 100)))
}

const editUserForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  birthDate: '',
})

const savingUser = ref(false)
const userError = ref('')

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
    const newName = (walletEditForm.value.name ?? '').trim()
    const newCur  = walletEditForm.value.currency ?? ''

    const ops = []
    if (newName && newName !== oldName) ops.push(Wallets.updateName(w.id, newName))
    if (newCur && newCur !== oldCur)   ops.push(Wallets.updateCurrencyAndRecalc(w.id, newCur))

    if (ops.length) {
      let updatedWalletFromCurrency = null
      for (const op of ops) {
        const res = await op
        if (res?.data?.id) updatedWalletFromCurrency = res.data
      }

      await loadUserWallets()

      const updated = updatedWalletFromCurrency
        ? updatedWalletFromCurrency
        : userWallets.value.find(x => x.id === w.id)

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

async function saveUserProfile() {
  userError.value = ''
  try {
    savingUser.value = true
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await Users.updateProfile(user.id, editUserForm.value)

    localStorage.setItem('user', JSON.stringify(data))

    alert('Profil uspešno ažuriran!')

    editUserForm.value = {
      firstName: '',
      lastName: '',
      email: '',
      birthDate: '',
    }
  } catch (e) {
    console.error('updateProfile failed', e)
    userError.value = 'Greška pri čuvanju profila.'
  } finally {
    savingUser.value = false
  }
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
      categoryId: transferForm.value.categoryId || null,
      userId,
    }

    if (targetId) {
      await Transactions.move(payload)
    } else {
      await Transactions.create({
        walletId: sourceId,
        name: payload.transactionName,
        amount: payload.amount,
        type: payload.type,
        dateOfExecution: payload.dateOfExecution,
        repeatable: payload.repeatable,
        activeRepeat: payload.activeRepeat,
        frequency: payload.frequency,
        categoryId: payload.categoryId,
        userId: payload.userId,
      })
    }
    transferForm.value = {
      targetWalletId: null,
      amount: '',
      transactionName: '',
      type: 'EXPENSE',
      dateOfExecution: today,
      repeatable: false,
      activeRepeat: false,
      frequency: '',
      categoryId: null,
    }
    showTransfer.value = false
  } catch (e) {
    console.error('Transfer error:', e?.response?.data || e)
    transferError.value = 'Greška pri transferu.'
  } finally {
    transferSaving.value = false
  }
  await afterDataChangeReload()
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
      savingsWallet: !!walletForm.value.savingsWallet,
      savings:       !!walletForm.value.savingsWallet,
    }

    const { data: created } = await Wallets.create(userId, payload)
    const newWalletId = created?.id ?? created?.wallet?.id
    if (!newWalletId) {
      throw new Error('Nema ID novčanika u odgovoru.')
    }

    if (walletForm.value.savingsWallet && Number(walletForm.value.goalTarget) > 0) {
      await Goal.create({
        walletId: newWalletId,
        name: walletForm.value.goalName || 'Cilj štednje',
        targetAmount: walletForm.value.goalTarget,
        deadline: walletForm.value.goalDeadline || null,
      })
    }

    await loadUserWallets()
    sourceWalletId.value = newWalletId
    await loadWalletBundle(newWalletId)

    walletForm.value = {
      name: '', initBal: '', currency: '',
      savingsWallet: false, goalName: '', goalTarget: null, goalDeadline: ''
    }
    showCreateWallet.value = false
  } catch (e) {
    console.error('create wallet/goal failed:', e?.response?.status, e?.response?.data || e)
    walletError.value = 'Greška pri kreiranju novčanika ili cilja.'
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
    txRows.value = (tr || []).map(t => ({
      id: t.id ?? t.transactionId ?? t.txId,
      name: t.name ?? t.transactionName ?? '—',
      type: t.type ?? t.type2 ?? null,
      amount: Number(t.amount ?? 0),
      sourceWalletId: t.sourceWalletId ?? t.sourceId ?? null,
      targetWalletId: t.targetWalletId ?? t.targetId ?? null,
      dateOfExecution: t.dateOfExecution ?? t.createdAt ?? null,
      repeatable: !!(t.repeatable ?? t.isRepeatable ?? t.repeat),
      activeRepeat: !!(t.activeRepeat ?? t.isActiveRepeat ?? t.active),
      frequency: t.frequency ?? t.freq ?? null,
    }));

    const g = w?.goal || null
    wallet.value = {
      ...w,
      goal: g
        ? {
          ...g,
          targetAmount: Number(g.targetAmount ?? g.target_amount ?? 0),
          name: g.name ?? g.goal_name ?? 'Cilj',
        }
        : null,
    }

    console.log('bundle:', { id, period: period.value, range, txCount: txRows.value.length })
    console.log('[wallet]', {
      savingsWallet: wallet.value?.savingsWallet ?? wallet.value?.savings_wallet ?? wallet.value?.savings,
      goal: wallet.value?.goal,
      targetAmount: readTargetAmount(wallet.value),
    });
  } catch (e) {
    console.error('loadWalletBundle failed:', e?.response?.status, e?.response?.data || e)
    error.value = 'Greška pri učitavanju novčanika.';
  } finally {
    loading.value = false;
  }
}

watch([from, to], () => { loadTopExpenses() })

watch([period, anchor, sourceWalletId], async () => {
  if (sourceWalletId.value) await loadWalletBundle(sourceWalletId.value);
})

onMounted(async () => {
  await loadUserWallets()
  await loadCategories()
  await loadTopExpenses()
  if (userWallets.value.length) {
    sourceWalletId.value = userWallets.value[0].id
    await loadWalletBundle(sourceWalletId.value)
    const d = new Date()
    const start = new Date(d.getFullYear(), d.getMonth(), 1)
    const end   = new Date(d.getFullYear(), d.getMonth()+1, 0)
    from.value = start.toISOString().slice(0,10)
    to.value   = end.toISOString().slice(0,10)
  }

  document.addEventListener('visibilitychange', handleVisibility)
  if (liveOn.value) startLive()

  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user) {
    editUserForm.value = {
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      email: user.email || '',
      birthDate: user.birthDate || '',
    }
  }
})

async function loadStats () {
  await loadTopExpenses()
}

function hasGoal(w) {
  return !!(w?.goal && Number(w.goal.targetAmount ?? w.goal.target_amount ?? 0) > 0)
}

function money(val, cur) {
  const n = Number(val ?? 0)
  const symbol = cur === 'EUR' ? '€' : cur === 'USD' ? '$' : ''
  const tail = cur === 'EUR' || cur === 'USD' ? '' : ` ${cur || ''}`
  return `${symbol} ${n.toLocaleString('sr-RS')}${tail}`
}
</script>

<style scoped>
.user-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e5e7eb;
}

.user-header h1 {
  color: #1f2937;
  font-size: 32px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.top-row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.wallet-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.wallet-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  background: white;
  transition: all 0.3s ease;
}

.wallet-row:hover {
  border-color: #4f46e5;
  transform: translateY(-2px);
}

.wallet-row.active {
  border-color: #4f46e5;
  background: #f8faff;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.1);
}

.wallet-main {
  display: flex;
  gap: 12px;
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
  opacity: 0.6;
  background: #f9fafb;
}

.wallet-edit-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr auto;
  gap: 8px;
  align-items: center;
}

.wallet-actions {
  display: flex;
  gap: 8px;
}

.pill {
  border: 1px solid #e5e7eb;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  background: white;
}

.pill-soft {
  background: #fffaf3;
  border-color: #f6e3b4;
  color: #92400e;
}

.check {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
}

.check.tiny {
  padding: 6px 8px;
  font-size: 14px;
}

.grid2 {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 24px;
}

.conversion-preview { margin-top: 6px; }

.goal-chip {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: #f8fafc;
}
.goal-chip-text .tight { line-height: 1.2; }

.tx-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}

.tx-table thead th {
  background: #fafafa;
  font-weight: 600;
}

.tx-table th,
.tx-table td {
  border: 1px solid #e5e7eb;
  padding: 10px 12px;
  text-align: left;
  vertical-align: middle;
  white-space: nowrap;
}

.tx-table tbody tr:hover {
  background: #f9fafb;
}

.centered-card {
  max-width: 860px;
  margin-left: auto;
  margin-right: auto;
}

.pretty-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  overflow: hidden;
  border-radius: 12px;
}

.pretty-table thead th {
  background: #fafafa;
  font-weight: 600;
  border-bottom: 1px solid #e5e7eb;
  padding: 10px 12px;
}

.pretty-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f1f3;
}

.pretty-table tbody tr:hover {
  background: #f9fafb;
}
</style>