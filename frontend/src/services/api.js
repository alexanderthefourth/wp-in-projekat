import axios from 'axios'

const BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api'


export const http = axios.create({
  baseURL: 'http://localhost:8080/api',
})

http.interceptors.response.use(
  (res) => {
    if (res.config.url?.startsWith('/stats')) {
      console.log('[axios][stats]', res.config.url, res.config.params || {}, '→', res.status, res.data)
    }
    return res
  },
  (err) => {
    const res = err.response
    if (res?.config?.url?.startsWith('/stats')) {
      console.error('[axios][stats][error]', res.config.url, res.config.params || {}, '→', res.status, res.data)
    }
    return Promise.reject(err)
  }
)

export const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
})

export const Users = {
  userCount: () => api.get('/users/userCount'),
  login: (body) => api.post('/users/login', body, { withCredentials: true }),
  register: (body) => api.post('/users/register', body, { withCredentials: true }),
  updateProfile: (userId, payload) =>
    api.put(`/users/profile`, payload, { params: { userId } }),
}

export const Wallets = {
  getAll: (userId) => api.get('/wallets', { params: { userId } }),
  get: (id) => api.get(`/wallets/${id}`),
  create: (userId, payload) => api.post('/wallets/walletCreate', payload, { params: { userId } }),
  viewCurrBal: (id) => api.get(`/wallets/${id}/viewCurrBal`),
  viewTransactions: (id, params) =>
    api.get(`/wallets/${id}/viewTransactions`, params ? { params } : undefined),
  updateName: (id, name) => api.put(`/wallets/${id}/nameUpdate`, null, { params: { name } }),
  updateSavings: (id, savings) =>
    api.put(`/wallets/${id}/savingsUpdate`, null, { params: { savings } }),
  archivedUpdate: (id, archived) =>
  api.put(`/wallets/${id}/archivedUpdate`, null, { params: { archived } }),
  updateCurrency: (id, currencyName) =>
    api.put(`/wallets/${id}/currencyUpdate`, null, { params: { currencyName } }),
  remove: (id) => api.delete(`/wallets/${id}/deleteWallet`),
}

export const Transactions = {
  create: (dto) => api.post('/transactions/createTransaction', dto),

  move: (dto) => api.post('/transactions/move', dto),

  byDay: () => api.get('/transactions/by-day'),
  byWeek: () => api.get('/transactions/by-week'),
  byMonth: () => api.get('/transactions/by-month'),
  byQuarter: () => api.get('/transactions/by-quarter'),
  byYear: () => api.get('/transactions/by-year'),

  setActiveRepeat: (id, active) =>
    api.put(`/transactions/${id}/activeRepeat`, null, { params: { active } }),

  setRepeatActive: (id, active) =>
    api.patch(`/transactions/${id}/repeat`, null, { params: { active } }),

  stopAllRepeats: ({ userId, walletId }) =>
    api.post('/transactions/stop-all-repeats', null, {
      params: { userId, walletId }
    }),
}

export const Stats = {
  summary: (params) => api.get('/stats/summary', { params }),
  series: (params) => api.get('/stats/series', { params }),
  byCategory: (params) => api.get('/stats/by-category', { params }),
  topExpenses: (params) => api.get('/stats/top-expenses', { params }),
}

export const Categories = {
  getForUser: (userId) => api.get( `/categories/${userId}`, { params: { userId } }),
  create: (payload) =>
    api.post(`/categories/createCategory`, payload),
  update: (payload) =>
    api.put(`/categories/updateCategory`, payload),
  remove: (id) => api.delete(`/categories/${id}`),
}

export const Goal = {
  getByWallet: (walletId) =>
    api.get('/goals/by-wallet', { params: { walletId } }),

  create: (payload) =>
    api.post('/goals/createGoal', payload),
};
