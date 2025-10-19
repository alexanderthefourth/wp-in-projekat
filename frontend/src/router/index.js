import { createRouter, createWebHistory } from 'vue-router'
import RegisteredUser from '@/views/RegisteredUser.vue'
import RegisteredAdmin from '@/views/RegisteredAdmin.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'

const routes = [
  { path: '/', redirect: '/login'},
  { path: '/registered', name: 'registered', component: RegisteredUser },
  { path: '/registered-admin', name: 'registered-admin', component: RegisteredAdmin },
  { path: '/:pathMatch(.*)*', redirect: '/' },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const raw = localStorage.getItem('user')
  const user = raw ? JSON.parse(raw) : null

  if (to.path === '/registered-admin') {
    if (!user || user.role !== 'ADMIN') return '/'
  }

  if (to.path === '/registered') {
    if (!user) return '/'
  }

  return true
})

export default router
