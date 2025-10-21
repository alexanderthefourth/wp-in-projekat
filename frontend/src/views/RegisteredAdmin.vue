<template>
  <div class="admin-page">
    <h2>Administratorski panel</h2>

    <div v-if="loading" class="loading">
      Učitavanje podataka...
    </div>

    <div v-else>
      <section class="dashboard">
        <h3>Statistika sistema</h3>
        <div class="dashboard-stats">
          <div class="stat-card">
            <h4>Ukupan broj korisnika</h4>
            <p>{{ dashboard.totalUsers }}</p>
          </div>
          <div class="stat-card">
            <h4>Aktivni korisnici (30 dana)</h4>
            <p>{{ dashboard.activeUsers }}</p>
          </div>
          <div class="stat-card">
            <h4>Prosečan balans aktivnih korisnika</h4>
            <p>{{ formatAmount(dashboard.avgBalanceActive) }} RSD</p>
          </div>
          <div class="stat-card">
            <h4>Ukupan novac u sistemu</h4>
            <p>{{ formatAmount(dashboard.totalMoney) }} RSD</p>
          </div>
        </div>

        <div class="top-tx-container">
          <div class="top-tx">
            <h4>Top 10 transakcija (poslednjih 30 dana)</h4>
            <table v-if="dashboard.top10Last30Days && dashboard.top10Last30Days.length">
              <thead>
              <tr>
                <th>ID</th>
                <th>Naziv</th>
                <th>Iznos</th>
                <th>Kategorija</th>
                <th>Datum</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="tx in dashboard.top10Last30Days" :key="tx.id">
                <td>{{ tx.id }}</td>
                <td>{{ tx.name }}</td>
                <td>{{ formatAmount(tx.amount) }}</td>
                <td>{{ tx.categoryName }}</td>
                <td>{{ formatDate(tx.dateOfExecution) }}</td>
              </tr>
              </tbody>
            </table>
            <p v-else>Nema transakcija u poslednjih 30 dana.</p>
          </div>

          <div class="top-tx">
            <h4>Top 10 transakcija (poslednji dan)</h4>
            <table v-if="dashboard.top10LastDay && dashboard.top10LastDay.length">
              <thead>
              <tr>
                <th>ID</th>
                <th>Naziv</th>
                <th>Iznos</th>
                <th>Kategorija</th>
                <th>Datum</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="tx in dashboard.top10LastDay" :key="tx.id">
                <td>{{ tx.id }}</td>
                <td>{{ tx.name }}</td>
                <td>{{ formatAmount(tx.amount) }}</td>
                <td>{{ tx.categoryName }}</td>
                <td>{{ formatDate(tx.dateOfExecution) }}</td>
              </tr>
              </tbody>
            </table>
            <p v-else>Nema transakcija u poslednjem danu.</p>
          </div>
        </div>
      </section>

      <section class="user-management">
        <h3>Upravljanje korisnicima</h3>

        <label for="userSelect">Izaberite korisnika:</label>
        <select id="userSelect" v-model.number="selectedUserId" @change="loadUserTransactionsAndComment">
          <option disabled value="">-- Odaberite korisnika --</option>
          <option v-for="u in filteredUsers" :key="u.id" :value="u.id">
            {{ u.firstName }} {{ u.lastName }} ({{ u.username }})
          </option>
        </select>

        <div v-if="selectedUser">
          <p><strong>Korisnik:</strong> {{ selectedUser.firstName }} {{ selectedUser.lastName }}</p>
          <p><strong>Status:</strong> {{ selectedUser.blocked ? "Blokiran" : "Aktivan" }}</p>

          <button @click="toggleBlockUser(selectedUser.id)">
            {{ selectedUser.blocked ? "Odblokiraj" : "Blokiraj" }} korisnika
          </button>

          <div class="comment-section">
            <h4>Administratorska beleška:</h4>
            <textarea
              v-model="adminComment"
              placeholder="Unesite ili izmenite komentar..."
            ></textarea>
            <button @click="saveComment(selectedUser.id)">Sačuvaj</button>
          </div>

          <div class="transactions">
            <h4>Transakcije korisnika</h4>
            <table v-if="userTransactions.length">
              <thead>
              <tr>
                <th>ID</th>
                <th>Naziv</th>
                <th>Iznos</th>
                <th>Datum</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="t in userTransactions" :key="t.id">
                <td>{{ t.id }}</td>
                <td>{{ t.name }}</td>
                <td>{{ t.amount }}</td>
                <td>{{ t.dateOfExecution }}</td>
              </tr>
              </tbody>
            </table>
            <p v-else>Nema transakcija za ovog korisnika.</p>
          </div>
        </div>
      </section>


      <section class="currency-management">
        <h3>Upravljanje valutama</h3>

        <div class="currencies-list">
          <h4>Lista valuta (vrednosti u dinarima)</h4>
          <table v-if="currencies.length">
            <thead>
            <tr>
              <th>Naziv valute</th>
              <th>Vrednost (RSD)</th>
              <th>Akcije</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="currency in currencies" :key="currency.name">
              <td>{{ currency.name }}</td>
              <td>
                <span v-if="currency.name === 'RSD'">1.00</span>
                <input
                  v-else
                  type="number"
                  v-model="currency.value"
                  step="0.01"
                  min="0.01"
                  @change="updateCurrency(currency)"
                >
              </td>
              <td>
                <button
                  v-if="currency.name !== 'RSD'"
                  @click="deleteCurrency(currency.name)"
                  class="btn-danger"
                >
                  Obriši
                </button>
                <span v-else class="read-only">Osnovna valuta</span>
              </td>
            </tr>
            </tbody>
          </table>
          <p v-else>Nema valuta u sistemu.</p>
        </div>

        <div class="add-currency">
          <h4>Dodaj novu valutu</h4>
          <div class="form-row">
            <div class="form-group">
              <label for="currencyName">Naziv valute:</label>
              <input
                id="currencyName"
                type="text"
                v-model="newCurrency.name"
                maxlength="3"
                style="text-transform: uppercase"
              >
            </div>
            <div class="form-group">
              <label for="currencyValue">Vrednost u dinarima:</label>
              <input
                id="currencyValue"
                type="number"
                v-model="newCurrency.value"
                step="0.01"
                min="0.01"
              >
            </div>
            <div class="form-group">
              <button @click="addCurrency" class="btn-primary add-btn">
                Dodaj valutu
              </button>
            </div>
          </div>
          <small>Unesite koliko dinara vredi 1 {{ newCurrency.name || 'novo' }}</small>
        </div>
      </section>

      <section class="monitoring">
        <h3>Monitoring transakcija</h3>

        <div class="filters">
          <div class="filter-row">
            <div class="filter-group">
              <label for="filterUser">Korisnik:</label>
              <select id="filterUser" v-model="filters.userId">
                <option value="">Svi korisnici</option>
                <option v-for="user in users" :key="user.id" :value="user.id">
                  {{ user.firstName }} {{ user.lastName }} ({{ user.username }})
                </option>
              </select>
            </div>

            <div class="filter-group">
              <label for="filterCategory">Kategorija:</label>
              <select id="filterCategory" v-model="filters.categoryName">
                <option value="">Sve kategorije</option>
                <option v-for="category in categories" :key="category.id" :value="category.name">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="filter-group">
              <label for="minAmount">Min iznos:</label>
              <input id="minAmount" type="number" v-model="filters.minAmount" step="0.01">
            </div>

            <div class="filter-group">
              <label for="maxAmount">Max iznos:</label>
              <input id="maxAmount" type="number" v-model="filters.maxAmount" step="0.01">
            </div>
          </div>

          <div class="filter-row">
            <div class="filter-group">
              <label for="filterDate">Datum:</label>
              <input id="filterDate" type="date" v-model="filters.date">
            </div>

            <div class="filter-group">
              <label for="sortBy">Sortiraj po:</label>
              <select id="sortBy" v-model="sortBy">
                <option value="dateDesc">Datum (novije ka starijim)</option>
                <option value="dateAsc">Datum (starije ka novijim)</option>
                <option value="amountDesc">Iznos (veće ka manjim)</option>
                <option value="amountAsc">Iznos (manje ka većim)</option>
              </select>
            </div>

            <div class="filter-actions">
              <button @click="applyFilters" class="btn-primary">Primeni filtere</button>
              <button @click="resetFilters" class="btn-secondary">Resetuj</button>
            </div>
          </div>
        </div>

        <div class="transactions-table">
          <h4>Sve transakcije ({{ filteredTransactions.length }})</h4>
          <table v-if="filteredTransactions.length">
            <thead>
            <tr>
              <th @click="sortByField('id')" class="sortable">ID</th>
              <th @click="sortByField('user.firstName')" class="sortable">Korisnik</th>
              <th>Naziv</th>
              <th @click="sortByField('amount')" class="sortable">Iznos</th>
              <th>Tip</th>
              <th>Kategorija</th>
              <th @click="sortByField('dateOfExecution')" class="sortable">Datum</th>
              <th>Novčanik</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="transaction in filteredTransactions" :key="transaction.id">
              <td>{{ transaction.id }}</td>
              <td>{{ transaction.user.firstName }} {{ transaction.user.lastName }}</td>
              <td>{{ transaction.name }}</td>
              <td :class="{ 'negative': transaction.amount < 0 }">
                {{ formatAmount(transaction.amount) }}
              </td>
              <td>{{ transaction.type }}</td>
              <td>{{ transaction.category ? transaction.category.name : 'N/A' }}</td>
              <td>{{ formatDate(transaction.dateOfExecution) }}</td>
              <td>{{ transaction.wallet ? `Novčanik #${transaction.wallet.id}` : 'N/A' }}</td>
            </tr>
            </tbody>
          </table>
          <p v-else class="no-data">Nema transakcija za prikaz sa trenutnim filterima.</p>
        </div>
      </section>
    </div>

    <LogoutButton />
  </div>
</template>

<script setup>
import { Stats } from "../services/api.js";
import { ref, onMounted, computed } from "vue";
import {
  Users,
  Transactions,
  Currencies,
  Categories
} from "../services/api.js";
import LogoutButton from "@/components/LogoutButton.vue";

const loading = ref(true);

const dashboard = ref({
  totalUsers: 0,
  activeUsers: 0,
  avgBalanceActive: 0,
  totalMoney: 0,
  top10Last30Days: [],
  top10LastDay: [],
});

const users = ref([]);
const selectedUserId = ref("");
const selectedUser = ref(null);
const userTransactions = ref([]); // Promenjeno ime da se ne bi mešalo sa allTransactions
const adminComment = ref("");

const currencies = ref([]);
const newCurrency = ref({
  name: "",
  value: null
});

const categories = ref([]);
const allTransactions = ref([]);
const filters = ref({
  userId: '',
  categoryName: '',
  minAmount: null,
  maxAmount: null,
  date: ''
});
const sortBy = ref('dateDesc');

const currentUser = JSON.parse(localStorage.getItem("user") || "{}");

const filteredUsers = computed(() =>
  users.value.filter((u) => u.id !== currentUser.id)
);

onMounted(async () => {
  try {
    await Promise.all([
      loadAllUsers(),
      loadCurrencies(),
      loadCategories(),
      loadAllTransactions()
    ]);
  } catch (error) {
    console.error("Greška pri učitavanju podataka:", error);
  } finally {
    loading.value = false;
  }

  try {
    await loadDashboard();
  } catch (error) {
    console.error("Greška pri učitavanju dashboarda:", error);
  }
});

async function loadAllUsers() {
  try {
    console.log('Učitavam korisnike...');
    const { data } = await Users.getAll();
    console.log('Korisnici učitani:', data);
    users.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju korisnika:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
    alert("Greška pri učitavanju korisnika.");
  }
}

async function loadDashboard() {
  try {
    console.log('Učitavam dashboard...');
    const { data } = await Stats.dashboard();
    console.log('Dashboard podaci:', data);
    dashboard.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju statistike:", error);
    console.error("Status:", error.response?.status);
    console.error("Poruka:", error.response?.data);
  }
}

async function loadUserTransactionsAndComment() {
  const id = Number(selectedUserId.value);
  selectedUser.value = users.value.find((u) => u.id === id) || null;
  if (!selectedUser.value) return;

  adminComment.value = selectedUser.value.adminComment || "";

  try {
    const { data } = await Transactions.byUser(id);
    userTransactions.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju transakcija korisnika:", error);
  }
}

async function toggleBlockUser(userId) {
  try {
    await Users.toggleBlock(userId);
    await loadAllUsers();
    await loadUserTransactionsAndComment();
  } catch (error) {
    console.error("Greška pri blokiranju korisnika:", error);
    alert("Greška pri blokiranju korisnika.");
  }
}

async function saveComment(userId) {
  try {
    await Users.updateComment(userId, adminComment.value);

    const user = users.value.find((u) => u.id === userId);
    if (user) user.adminComment = adminComment.value;

    alert("Komentar sačuvan.");
  } catch (error) {
    console.error("Greška pri čuvanju komentara:", error);
    alert("Greška pri čuvanju komentara.");
  }
}

async function loadCurrencies() {
  try {
    console.log('Učitavam valute...');
    const { data } = await Currencies.getAll();
    console.log('Valute učitane:', data);
    currencies.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju valuta:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
    alert("Greška pri učitavanju valuta.");
  }
}

async function addCurrency() {
  if (!newCurrency.value.name || newCurrency.value.value === null) {
    alert("Molimo popunite sva polja.");
    return;
  }

  if (newCurrency.value.value <= 0) {
    alert("Vrednost mora biti pozitivan broj.");
    return;
  }

  const currencyToAdd = {
    name: newCurrency.value.name.toUpperCase(),
    value: newCurrency.value.value
  };

  try {
    await Currencies.create(currencyToAdd);
    await loadCurrencies();
    newCurrency.value = { name: "", value: null };
    alert("Valuta uspešno dodata.");
  } catch (error) {
    console.error("Greška pri dodavanju valute:", error);
    alert("Greška pri dodavanju valute: " + (error.response?.data?.message || error.message));
  }
}

async function updateCurrency(currency) {
  if (currency.value <= 0) {
    alert("Vrednost mora biti pozitivan broj.");
    await loadCurrencies();
    return;
  }

  try {
    await Currencies.update(currency.name, {
      name: currency.name,
      value: currency.value
    });
    alert("Valuta uspešno ažurirana.");
  } catch (error) {
    console.error("Greška pri ažuriranju valute:", error);
    alert("Greška pri ažuriranju valute: " + (error.response?.data?.message || error.message));
    await loadCurrencies();
  }
}

async function deleteCurrency(currencyName) {
  if (!confirm(`Da li ste sigurni da želite da obrišete valutu ${currencyName}?`)) {
    return;
  }

  try {
    await Currencies.delete(currencyName);
    await loadCurrencies();
    alert("Valuta uspešno obrisana.");
  } catch (error) {
    console.error("Greška pri brisanju valute:", error);
    alert("Greška pri brisanju valute: " + (error.response?.data?.message || error.message));
  }
}

async function loadCategories() {
  try {
    console.log('Učitavam kategorije...');
    const { data } = await Categories.all();
    console.log('Kategorije učitane:', data);
    categories.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju kategorija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
  }
}

async function loadAllTransactions() {
  try {
    console.log('Učitavam sve transakcije...');
    const { data } = await Transactions.all();
    console.log('Transakcije učitane:', data);
    allTransactions.value = data;
  } catch (error) {
    console.error("Greška pri učitavanju transakcija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
  }
}

async function applyFilters() {
  try {
    const params = {
      userId: filters.value.userId || null,
      categoryName: filters.value.categoryName || null,
      minAmount: filters.value.minAmount || null,
      maxAmount: filters.value.maxAmount || null,
      date: filters.value.date || null
    };

    console.log('Primenjujem filtere:', params);
    const { data } = await Transactions.all(params);
    allTransactions.value = data;
  } catch (error) {
    console.error("Greška pri filtriranju transakcija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
  }
}

function resetFilters() {
  filters.value = {
    userId: '',
    categoryName: '',
    minAmount: null,
    maxAmount: null,
    date: ''
  };
  loadAllTransactions();
}

const filteredTransactions = computed(() => {
  let transactions = [...allTransactions.value];

  switch (sortBy.value) {
    case 'dateDesc':
      transactions.sort((a, b) => new Date(b.dateOfExecution) - new Date(a.dateOfExecution));
      break;
    case 'dateAsc':
      transactions.sort((a, b) => new Date(a.dateOfExecution) - new Date(b.dateOfExecution));
      break;
    case 'amountDesc':
      transactions.sort((a, b) => b.amount - a.amount);
      break;
    case 'amountAsc':
      transactions.sort((a, b) => a.amount - b.amount);
      break;
  }

  return transactions;
});

function sortByField(field) {

}

function formatAmount(amount) {
  return new Intl.NumberFormat('sr-RS', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount);
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString('sr-RS');
}
</script>

<style scoped>
.loading {
  text-align: center;
  padding: 20px;
  font-size: 18px;
}

.admin-page {
  padding: 20px;
  font-family: Arial, sans-serif;
}

select {
  margin-bottom: 15px;
  padding: 6px;
  border-radius: 6px;
}

button {
  margin-top: 10px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: #1976d2;
  color: white;
}

.btn-primary:hover {
  background-color: #0d47a1;
}

.btn-danger {
  background-color: #d32f2f;
  color: white;
}

.btn-danger:hover {
  background-color: #b71c1c;
}

.transactions table,
.currencies-list table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

.transactions th,
.transactions td,
.currencies-list th,
.currencies-list td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.comment-section {
  margin-top: 15px;
}

textarea {
  width: 100%;
  min-height: 60px;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.currency-management,
.monitoring {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid #eee;
}

.form-row {
  display: flex;
  align-items: flex-end;
  gap: 15px;
  flex-wrap: wrap;
}

.form-group {
  margin-bottom: 0;
  flex: 0 0 auto;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input {
  width: 120px;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.add-btn {
  margin-top: 0;
  white-space: nowrap;
}

.add-currency small {
  display: block;
  margin-top: 8px;
  color: #666;
  font-size: 0.85em;
}

.currencies-list input[type="number"] {
  width: 120px;
  padding: 4px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.read-only {
  color: #666;
  font-style: italic;
}

.filters {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  flex-wrap: wrap;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  min-width: 150px;
}

.filter-group label {
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 0.9em;
}

.filter-group input,
.filter-group select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.transactions-table {
  margin-top: 20px;
}

.transactions-table table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.transactions-table th,
.transactions-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.transactions-table th {
  background-color: #f8f9fa;
  font-weight: bold;
}

.sortable {
  cursor: pointer;
  user-select: none;
}

.sortable:hover {
  background-color: #e9ecef;
}

.negative {
  color: #dc3545;
  font-weight: bold;
}

.no-data {
  text-align: center;
  color: #6c757d;
  font-style: italic;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
  }

  .filter-group {
    min-width: 100%;
  }
}
.dashboard {
  margin-bottom: 40px;
  border-bottom: 2px solid #ccc;
  padding-bottom: 20px;
}
.dashboard-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.stat-card {
  background: #f3f3f3;
  padding: 15px;
  border-radius: 8px;
  flex: 1 1 220px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
.top-tx-container {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-top: 20px;
}
.top-tx {
  flex: 1;
  min-width: 400px;
}
</style>
