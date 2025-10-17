import axios from 'axios'

// Use Vite proxy (`/api`) or direct URL:
const BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api'

export const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
})

// ---------- USERS ----------
export const Users = {
  userCount: () => api.get('/users/userCount'), // GET -> int
}

// ---------- WALLETS (matches /api/wallets/* exactly) ----------
export const Wallets = {
  // POST /api/wallets/walletCreate  body: WalletDTO
  // WalletDTO fields you sent: name, initBal, currBal, currency (CurrencyDTO), creatingDate, archived, savings, goal(CreateGoalDTO)
  create: (dto) => api.post('/wallets/walletCreate', dto),

  // GET /api/wallets/{id}
  get: (id) => api.get(`/wallets/${id}`),

  // DELETE /api/wallets/{id}/deleteWallet
  remove: (id) => api.delete(`/wallets/${id}/deleteWallet`),

  // PUT /api/wallets/{id}/nameUpdate?name=...
  updateName: (id, name) => api.put(`/wallets/${id}/nameUpdate`, null, { params: { name } }),

  // PUT /api/wallets/{id}/savingsUpdate?savings=true|false
  updateSavings: (id, savings) =>
    api.put(`/wallets/${id}/savingsUpdate`, null, { params: { savings } }),

  // PUT /api/wallets/{id}/archivedUpdate?archived=true|false
  updateArchived: (id, archived) =>
    api.put(`/wallets/${id}/archivedUpdate`, null, { params: { archived } }),

  // PUT /api/wallets/{id}/currencyUpdate?currencyName=RSD|EUR|USD
  updateCurrency: (id, currencyName) =>
    api.put(`/wallets/${id}/currencyUpdate`, null, { params: { currencyName } }),

  // GET /api/wallets/{id}/viewCurrBal  -> BigDecimal
  viewCurrBal: (id) => api.get(`/wallets/${id}/viewCurrBal`),

  // GET /api/wallets/{id}/viewTransfers -> List<Transfer>
  viewTransfers: (id) => api.get(`/wallets/${id}/viewTransfers`),
}

// ---------- TRANSACTIONS (/api/transactions/*) ----------
export const Transactions = {
  // POST /api/transactions/createTransaction  body: CreateTransactionDTO
  // CreateTransactionDTO: name, amount, type, dateOfExecution, repeatable, activeRepeat, frequency, walletId
  create: (dto) => api.post('/transactions/createTransaction', dto),

  // POST /api/transactions/move  body: TransactionMoveDTO
  move: (dto) => api.post('/transactions/move', dto),

  // Grouping endpoints used for Statistics:
  byDay: () => api.get('/transactions/by-day'),
  byWeek: () => api.get('/transactions/by-week'),
  byMonth: () => api.get('/transactions/by-month'),
  byQuarter: () => api.get('/transactions/by-quarter'),
  byYear: () => api.get('/transactions/by-year'),
}
