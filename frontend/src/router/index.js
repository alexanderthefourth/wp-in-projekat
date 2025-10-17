import { createRouter, createWebHistory } from 'vue-router'
import UnregisteredUser from '../views/UnregisteredUser.vue'
import RegisteredUser from '@/views/RegisteredUser.vue'

const routes = [
  {
    path: '/',
    name: 'unregistered',
    component: UnregisteredUser,
  },
  {
    path: '/registered',
    name: 'registered',
    component: RegisteredUser,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
