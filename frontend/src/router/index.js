import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/DashboardLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue')
        },
        { path: 'pos', name: 'pos', component: () => import('../views/PosView.vue') },
        { path: 'history', name: 'history', component: () => import('../views/SalesHistoryView.vue') },
        { path: 'profile', name: 'profile', component: () => import('../views/ProfileView.vue') },
        { path: 'users', name: 'users', component: () => import('../views/UserManagementView.vue'), meta: { adminOnly: true } },
        { path: 'products', name: 'products', component: () => import('../views/ProductsView.vue') },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('@/views/CategoriesView.vue')
        }
      ]
    }
  ]
})

// Navigation guard
router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }

  if (to.meta.guest && auth.isAuthenticated) {
    return '/'
  }

  if (to.meta.adminOnly && !auth.isAdmin) {
    return '/' // Only admins can access user management
  }
})

export default router
