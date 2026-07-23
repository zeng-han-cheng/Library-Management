import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
const routes = [{ path: '/login', component: Login }, { path: '/', component: Home, children: [
  { path: '', redirect: '/dashboard' }, { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
  { path: 'books', component: () => import('../views/Books.vue') }, { path: 'categories', component: () => import('../views/Categories.vue') },
  { path: 'readers', component: () => import('../views/Readers.vue') }, { path: 'notices', component: () => import('../views/Notices.vue') },
  { path: 'borrows', component: () => import('../views/Borrows.vue') }, { path: 'statistics', component: () => import('../views/Statistics.vue') },
  { path: 'ai', component: () => import('../views/AI.vue') }
] }]
const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach(to => { if (to.path !== '/login' && !localStorage.getItem('library_token')) return '/login' })
export default router
