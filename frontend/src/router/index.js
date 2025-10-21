import { createRouter, createWebHistory } from 'vue-router'
import RegisteredUser from '@/views/RegisteredUser.vue'
import RegisteredAdmin from '@/views/RegisteredAdmin.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import BlockedUser from '@/views/BlockedUser.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/registered', name: 'registered', component: RegisteredUser },
  { path: '/registered-admin', name: 'registered-admin', component: RegisteredAdmin },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/blocked', name: 'blocked', component: BlockedUser },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const raw = localStorage.getItem('user')
  const user = raw ? JSON.parse(raw) : null

  console.log('Navigation to:', to.path, 'User:', user) // Dodaj ovo za debug

  // 🔹 Dozvoli login i register uvek
  if (to.path === '/login' || to.path === '/register') {
    return true
  }

  // 🔹 Ako korisnik ne postoji (nije logovan), preusmeri na login
  if (!user) {
    return '/login'
  }

  // 🔹 Ako je blokiran i pokušava da ide bilo gde osim /blocked — prebaci ga tamo
  if (user.blocked && to.path !== '/blocked') {
    return '/blocked'
  }

  // 🔹 Ako nije blokiran, ali pokušava ručno na /blocked — vrati ga na registered
  if (to.path === '/blocked' && !user.blocked) {
    return user.role === 'ADMIN' ? '/registered-admin' : '/registered'
  }

  // 🔹 Admin ruta - samo admin može pristupiti
  if (to.path === '/registered-admin' && user.role !== 'ADMIN') {
    return '/registered' // Ili '/login' ako želiš da ga izbaciš
  }

  // 🔹 User ruta - običan korisnik ne može pristupiti admin ruti, ali to je već rešeno gore
  if (to.path === '/registered' && user.role === 'ADMIN') {
    // Ako je admin a pokušava na user rutu, možeš da ga preusmeriš na admin rutu
    // ili dozvoliš pristup - zavisi od zahteva
    return '/registered-admin'
  }

  return true
})

export default router
