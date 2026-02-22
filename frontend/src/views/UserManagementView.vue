<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDataStore } from '@/stores/data'
import { useAuthStore } from '@/stores/auth'

const data = useDataStore()
const auth = useAuthStore()
const search = ref('')
const actionError = ref('')
const DEFAULT_ADMIN_USERNAME = 'admin_pos'

onMounted(() => {
  data.fetchUsers()
})

const filteredUsers = computed(() => {
  const users = Array.isArray(data.users) ? data.users : []
  if (!search.value) return users
  const q = search.value.toLowerCase()
  return users.filter(u => u.username?.toLowerCase().includes(q))
})

async function handleToggleRole(user) {
  actionError.value = ''
  try {
    await data.toggleRole(user)
  } catch (err) {
    actionError.value = err.response?.data?.message || 'Failed to update roles'
  }
}

async function handleDelete(id) {
  actionError.value = ''
  if (!confirm('Are you sure you want to delete this user?')) return
  try {
    await data.deleteUser(id)
  } catch (err) {
    actionError.value = err.response?.data?.message || 'Failed to delete user'
  }
}

function canRevokeAdmin(user) {
  return user.username !== DEFAULT_ADMIN_USERNAME && user.roles.includes('ROLE_ADMIN')
}
</script>

<template>
  <div class="users-container">
    <div class="page-header">
      <div>
        <h1>User Management</h1>
        <p>Manage system access and roles</p>
      </div>
    </div>

    <!-- Search -->
    <div class="search-bar glass-card">
      <span class="search-icon">🔍</span>
      <input v-model="search" type="text" class="search-input" placeholder="Search by username..." />
    </div>

    <div class="alert alert-error" v-if="actionError">
      {{ actionError }}
    </div>

    <!-- Users Table -->
    <div class="glass-card table-container">
      <div v-if="data.loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading users...</p>
      </div>

      <div v-else-if="filteredUsers.length === 0" class="empty-state">
        <div class="icon">👥</div>
        <h3>No users found</h3>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Roles</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>#{{ user.id }}</td>
            <td>
              <span class="username">{{ user.username }}</span>
            </td>
            <td>
              <div class="roles-tags">
                <span v-for="role in (user.roles || 'ROLE_USER').split(',')" :key="role" class="badge" 
                      :class="role === 'ROLE_ADMIN' ? 'badge-danger' : 'badge-info'">
                  {{ role.replace('ROLE_', '') }}
                </span>
              </div>
            </td>
            <td>
              <div class="actions">
                <button v-if="canRevokeAdmin(user)" class="btn btn-outline btn-sm" @click="handleToggleRole(user)">
                  Revoke Admin
                </button>
                <span v-else class="badge badge-info">Role Managed</span>
                <button class="btn btn-danger btn-sm" :disabled="user.username === auth.user?.username || user.username === DEFAULT_ADMIN_USERNAME" @click="handleDelete(user.id)">
                  Delete
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.users-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.username {
  font-weight: 700;
  color: var(--text-primary);
}

.roles-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.loading-state {
  padding: 60px;
  text-align: center;
  color: var(--text-muted);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255,255,255,0.1);
  border-top-color: var(--accent);
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
