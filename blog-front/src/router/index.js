import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/HomeView.vue') },
  { path: '/article/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue') },
  { path: '/categories', name: 'Categories', component: () => import('../views/CategoryView.vue') },
  { path: '/search', name: 'Search', component: () => import('../views/SearchView.vue') },
  { path: '/about', name: 'About', component: () => import('../views/AboutView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
