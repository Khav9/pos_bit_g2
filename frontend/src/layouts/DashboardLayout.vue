<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const sidebarCollapsed = ref(false)

onMounted(() => {
  auth.refreshProfile()
})

const navItems = computed(() => {
  const items = [
    { name: 'Dashboard', path: '/', icon: '📊' },
    { name: 'Profile', path: '/profile', icon: '🙍' },
    { name: 'POS', path: '/pos', icon: '🛒' },
    { name: 'History', path: '/history', icon: '📜' },
    { name: 'Products', path: '/products', icon: '📦' },
    { name: 'Categories', path: '/categories', icon: '🏷️' }
  ]
  
  if (auth.isAdmin) {
    items.splice(3, 0, { name: 'Users', path: '/users', icon: '👥' })
  }
  
  return items
})

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
</script>

<template>
  <div class="dashboard" :class="{ collapsed: sidebarCollapsed }">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">⚡</span>
          <span v-if="!sidebarCollapsed" class="logo-text">POS<span class="text-accent">Bit</span></span>
        </div>
        <button class="toggle-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '→' : '←' }}
        </button>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span v-if="!sidebarCollapsed" class="nav-label">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info" v-if="!sidebarCollapsed">
          <div class="user-avatar">{{ auth.user?.username?.charAt(0).toUpperCase() }}</div>
          <div class="user-details">
            <span class="user-name">{{ auth.profile?.fullName || auth.user?.username }}</span>
            <span class="user-role">{{ auth.isAdmin ? 'Admin' : 'User' }}</span>
          </div>
        </div>
        <button class="nav-item logout-btn" @click="handleLogout">
          <span class="nav-icon">🚪</span>
          <span v-if="!sidebarCollapsed" class="nav-label">Logout</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ───── Sidebar ───── */
.sidebar {
  width: var(--sidebar-width);
  background: var(--bg-sidebar);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  flex-shrink: 0;
}

.collapsed .sidebar {
  width: 72px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  border-bottom: 1px solid var(--border);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--gradient-start), var(--gradient-end));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 20px;
  font-weight: 800;
  white-space: nowrap;
}

.toggle-btn {
  background: rgba(255,255,255,0.06);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  width: 28px;
  height: 28px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
  flex-shrink: 0;
}

.toggle-btn:hover {
  background: rgba(255,255,255,0.1);
  color: var(--text-primary);
}

/* ───── Nav Items ───── */
.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  text-decoration: none;
  transition: var(--transition);
  cursor: pointer;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  width: 100%;
  white-space: nowrap;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--text-primary);
}

.nav-item.active {
  background: var(--accent-glow);
  color: var(--accent);
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

/* ───── Sidebar Footer ───── */
.sidebar-footer {
  padding: 12px 8px;
  border-top: 1px solid var(--border);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  margin-bottom: 4px;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--gradient-start), var(--gradient-end));
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 11px;
  color: var(--text-muted);
}

.logout-btn:hover {
  background: var(--danger-bg) !important;
  color: var(--danger) !important;
}

/* ───── Main Content ───── */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 32px;
  background:
    radial-gradient(ellipse at 80% 0%, rgba(129, 95, 255, 0.05) 0%, transparent 50%),
    var(--bg-primary);
}
</style>
