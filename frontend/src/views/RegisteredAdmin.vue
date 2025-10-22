<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>Administratorski panel</h1>
      <div class="header-actions">
        <LogoutButton />
      </div>
    </div>

    <div v-if="loading" class="loading">
      Ucitavanje, budite strpljivi...
    </div>

    <div v-else>
      <section class="dashboard card">
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
            <table class="data-table" v-if="dashboard.top10Last30Days && dashboard.top10Last30Days.length">
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
            <p v-else class="text-muted">Nema transakcija u poslednjih 30 dana.</p>
          </div>

          <div class="top-tx">
            <h4>Top 10 transakcija (poslednji dan)</h4>
            <table class="data-table" v-if="dashboard.top10LastDay && dashboard.top10LastDay.length">
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
            <p v-else class="text-muted">Nema transakcija u poslednjem danu.</p>
          </div>
        </div>
      </section>

      <section class="user-management card mt-4">
        <h3>Upravljanje korisnicima</h3>

        <div class="form-group">
          <label for="userSelect">Izaberite korisnika:</label>
          <select id="userSelect" v-model.number="selectedUserId" @change="loadUserTransactionsAndComment">
            <option disabled value="">-- Odaberite korisnika --</option>
            <option v-for="u in filteredUsers" :key="u.id" :value="u.id">
              {{ u.firstName }} {{ u.lastName }} ({{ u.username }})
            </option>
          </select>
        </div>

        <div v-if="selectedUser" class="user-details">
          <p><strong>Korisnik:</strong> {{ selectedUser.firstName }} {{ selectedUser.lastName }}</p>
          <p><strong>Status:</strong> {{ selectedUser.blocked ? "Blokiran" : "Aktivan" }}</p>

          <button @click="toggleBlockUser(selectedUser.id)" class="btn btn-warn">
            {{ selectedUser.blocked ? "Odblokiraj" : "Blokiraj" }} korisnika
          </button>

          <div class="comment-section">
            <h4>Admin komentar:</h4>
            <textarea v-model="adminComment"></textarea>
            <button @click="saveComment(selectedUser.id)" class="btn btn-primary">Sacuvaj</button>
          </div>

          <div class="transactions">
            <h4>Transakcije korisnika</h4>
            <table class="data-table" v-if="userTransactions.length">
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
            <p v-else class="text-muted">Nema transakcija za ovog korisnika.</p>
          </div>
        </div>
      </section>

      <section class="currency-management card mt-4">
        <h3>Upravljanje valutama</h3>

        <div class="currencies-list">
          <h4>Lista valuta (vrednosti u dinarima)</h4>
          <table class="data-table" v-if="currencies.length">
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
                <input v-else type="number" v-model="currency.value" step="0.01" min="0.01" @change="updateCurrency(currency)" class="currency-input">
              </td>
              <td>
                <button v-if="currency.name !== 'RSD'" @click="deleteCurrency(currency.name)" class="btn btn-danger">
                  Obrisi
                </button>
                <span v-else class="text-muted">Osnovna valuta</span>
              </td>
            </tr>
            </tbody>
          </table>
          <p v-else class="text-muted">Nema valuta u sistemu.</p>
        </div>

        <div class="add-currency">
          <h4>Dodaj novu valutu</h4>
          <div class="form-grid">
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
              <input id="currencyValue" type="number" v-model="newCurrency.value" step="0.01" min="0.01">
            </div>
            <div class="form-group">
              <small class="text-muted">Unesite koliko dinara vredi jedinica valute {{ newCurrency.name }}</small>
              <button @click="addCurrency" class="btn btn-primary add-btn">
                Dodaj valutu
              </button>
            </div>
          </div>
        </div>
      </section>

      <section class="predefined-categories card mt-4">
        <h3>Dodavanje predefinisanih kategorija</h3>

        <div class="form-grid">
          <div class="form-group">
            <label for="predefName">Naziv kategorije:</label>
            <input id="predefName" type="text" v-model="newPredefinedCategory.name"/>
          </div>

          <div class="form-group">
            <label for="predefType">Tip kategorije:</label>
            <select id="predefType" v-model="newPredefinedCategory.type">
              <option disabled value="">-- Odaberite tip --</option>
              <option value="EXPENSE">Trošak</option>
              <option value="INCOME">Prihod</option>
              <option value="SAVINGS">Štednja</option>
            </select>
          </div>

          <div class="form-group">
            <button @click="addPredefinedCategory" class="btn btn-primary">
              Dodaj predefinisanu kategoriju
            </button>
          </div>
        </div>

      </section>


      <section class="monitoring card mt-4">
        <h3>Monitoring transakcija</h3>

        <div class="filters">
          <div class="filter-row">
            <div class="form-group">
              <label for="filterUser">Korisnik:</label>
              <select id="filterUser" v-model="filters.userId">
                <option value="">Svi korisnici</option>
                <option v-for="user in users" :key="user.id" :value="user.id">
                  {{ user.firstName }} {{ user.lastName }} ({{ user.username }})
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="filterCategory">Kategorija:</label>
              <select id="filterCategory" v-model="filters.categoryName">
                <option value="">Sve kategorije</option>
                <option v-for="category in categories" :key="category.id" :value="category.name">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="minAmount">Minimalan iznos:</label>
              <input id="minAmount" type="number" v-model="filters.minAmount" step="0.01">
            </div>

            <div class="form-group">
              <label for="maxAmount">Maksimalan iznos:</label>
              <input id="maxAmount" type="number" v-model="filters.maxAmount" step="0.01">
            </div>
          </div>

          <div class="filter-row">
            <div class="form-group">
              <label for="filterDate">Datum:</label>
              <input id="filterDate" type="date" v-model="filters.date">
            </div>

            <div class="form-group">
              <label for="sortBy">Sortiraj po:</label>
              <select id="sortBy" v-model="sortBy">
                <option value="dateDesc">Datum (novije ka starijim)</option>
                <option value="dateAsc">Datum (starije ka novijim)</option>
                <option value="amountDesc">Iznos (veće ka manjim)</option>
                <option value="amountAsc">Iznos (manje ka većim)</option>
              </select>
            </div>

            <div class="form-actions">
              <button @click="applyFilters" class="btn btn-primary">Primeni filtere</button>
              <button @click="resetFilters" class="btn btn-subtle">Resetuj</button>
            </div>
          </div>
        </div>

        <div class="transactions-table">
          <h4>Sve transakcije ({{ filteredTransactions.length }})</h4>
          <table class="data-table" v-if="filteredTransactions.length">
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
              <td>{{ transaction.user ? `${transaction.user.firstName} ${transaction.user.lastName}` : 'N/A' }}</td>
              <td>{{ transaction.name }}</td>
              <td :class="{ 'negative': transaction.amount < 0 }">
                {{ formatAmount(transaction.amount) }}
              </td>
              <td>{{ transaction.type }}</td>
              <td>{{ transaction.category ? transaction.category.name : 'N/A' }}</td>
              <td>{{ formatDate(transaction.dateOfExecution) }}</td>
              <td>{{ transaction.wallet ? `Wallet #${transaction.wallet.id}` : 'N/A' }}</td>
            </tr>
            </tbody>
          </table>
          <p v-else class="text-muted">Nema transakcija za prikaz sa trenutnim filterima.</p>
        </div>
      </section>
    </div>
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
const newPredefinedCategory = ref({
  name: "",
  type: "",
});
const users = ref([]);
const selectedUserId = ref("");
const selectedUser = ref(null);
const userTransactions = ref([]);
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
async function addPredefinedCategory() {
  if (!newPredefinedCategory.value.name || !newPredefinedCategory.value.type) {
    alert("Unesite naziv i izaberite tip kategorije.");
    return;
  }

  try {
    const response = await Categories.createPredefined({
      name: newPredefinedCategory.value.name,
      type: newPredefinedCategory.value.type,
    });

    alert("Predefinisana kategorija uspesno dodata!");
    newPredefinedCategory.value = { name: "", type: "" };
    await loadCategories();
  } catch (error) {
    console.error("Greska pri dodavanju predefinisane kategorije:", error);
    alert(
      "Greska pri dodavanju kategorije: " +
      (error.response?.data || error.message)
    );
  }
}

async function loadDashboard() {
  try {
    console.log('Ucitavam dashboard...');
    const { data } = await Stats.dashboard();
    console.log('Dashboard podaci:', data);
    dashboard.value = data;
  } catch (error) {
    console.error("Greska pri ucitavanju statistike:", error);
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
    console.error("Greska pri ucitavanju transakcija korisnika:", error);
  }
}

async function toggleBlockUser(userId) {
  try {
    await Users.toggleBlock(userId);
    await loadAllUsers();
    await loadUserTransactionsAndComment();
  } catch (error) {
    console.error("Greska pri blokiranju korisnika:", error);
    alert("Greska pri blokiranju korisnika.");
  }
}

async function saveComment(userId) {
  try {
    await Users.updateComment(userId, adminComment.value);

    const user = users.value.find((u) => u.id === userId);
    if (user) user.adminComment = adminComment.value;

    alert("Komentar sacuvan");
  } catch (error) {
    console.error("Greska pri cuvanju komentara:", error);
    alert("Greska pri cuvanju komentara.");
  }
}

async function loadCurrencies() {
  try {
    console.log('Ucitavam valute...');
    const { data } = await Currencies.getAll();
    console.log('Valute ucitane:', data);
    currencies.value = data;
  } catch (error) {
    console.error("Greska pri ucitavanju valuta:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
    alert("Greska pri ucitavanju valuta.");
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
    alert("Valuta uspesno dodata.");
  } catch (error) {
    console.error("Greska pri dodavanju valute:", error);
    alert("Greska pri dodavanju valute: " + (error.response?.data?.message || error.message));
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
    alert("Valuta uspesno azurirana.");
  } catch (error) {
    console.error("Greska pri azuriranju valute:", error);
    alert("Greska pri azuriranju valute: " + (error.response?.data?.message || error.message));
    await loadCurrencies();
  }
}

async function deleteCurrency(currencyName) {
  if (!confirm(`Da li ste sigurni da zelite da obrisete valutu ${currencyName}?`)) {
    return;
  }

  try {
    await Currencies.delete(currencyName);
    await loadCurrencies();
    alert("Valuta uspešno obrisana.");
  } catch (error) {
    console.error("Greska pri brisanju valute:", error);
    alert("Greska pri brisanju valute: " + (error.response?.data?.message || error.message));
  }
}

async function loadCategories() {
  try {
    console.log('Ucitavam kategorije...');
    const { data } = await Categories.all();
    console.log('Kategorije ucitane:', data);
    categories.value = data;
  } catch (error) {
    console.error("Greska pri ucitavanju kategorija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
  }
}

async function loadAllTransactions() {
  try {
    console.log('Ucitavam sve transakcije...');
    const { data } = await Transactions.all();
    console.log('Transakcije ucitane:', data);
    allTransactions.value = data;
  } catch (error) {
    console.error("Greska pri ucitavanju transakcija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
  }
}

async function applyFilters() {
  try {
    const selectedUser = users.value.find(u => u.id === filters.value.userId);

    const params = {
      username: selectedUser ? selectedUser.username : undefined,
      categoryName: filters.value.categoryName || undefined,
      minAmount: filters.value.minAmount || undefined,
      maxAmount: filters.value.maxAmount || undefined,
      date: filters.value.date || undefined
    };

    Object.keys(params).forEach(
      (key) => params[key] === undefined && delete params[key]
    );

    console.log('Primenjujem filtere:', params);

    const { data } = await Transactions.all(params);
    allTransactions.value = data;

    console.log('Filtrirane transakcije:', data);
  } catch (error) {
    console.error("Greska pri filtriranju transakcija:", error);
    console.error("Status:", error.response?.status);
    console.error("Data:", error.response?.data);
    alert("Greska pri filtriranju transakcija.");
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
  //uraditi
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
.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e5e7eb;
}

.admin-header h1 {
  color: #1f2937;
  font-size: 32px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  font-size: 18px;
  color: #6b7280;
}

.dashboard-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 20px 0;
}

.stat-card {
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  flex: 1 1 220px;
  text-align: center;
  border: 1px solid #e5e7eb;
}

.stat-card h4 {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-card p {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.top-tx-container {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-top: 30px;
}

.top-tx {
  flex: 1;
  min-width: 400px;
}

.user-details {
  margin-top: 20px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
}

.comment-section {
  margin: 20px 0;
}

.comment-section textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  margin-bottom: 12px;
  font-family: inherit;
}

.currency-input {
  width: 120px;
  padding: 8px;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
}

.add-currency {
  margin-top: 30px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
}

.filters {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.form-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.sortable {
  cursor: pointer;
  user-select: none;
}

.sortable:hover {
  background-color: #f3f4f6;
}

.negative {
  color: #ef4444;
  font-weight: 600;
}

@media (max-width: 768px) {
  .admin-container {
    padding: 16px;
  }

  .filter-row {
    flex-direction: column;
  }

  .top-tx {
    min-width: 100%;
  }

  .dashboard-stats {
    flex-direction: column;
  }
}
.predefined-categories {
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
}

.predefined-categories .form-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
  margin-bottom: 10px;
}

.predefined-categories .form-group {
  display: flex;
  flex-direction: column;
}

.predefined-categories input,
.predefined-categories select {
  padding: 10px;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  font-size: 14px;
  width: 220px;
}

.predefined-categories button {
  white-space: nowrap;
}

.predefined-categories small {
  color: #6b7280;
  display: block;
  margin-top: 8px;
}

</style>
