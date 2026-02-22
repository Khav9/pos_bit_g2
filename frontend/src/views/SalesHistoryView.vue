<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDataStore } from '@/stores/data'
import { useAuthStore } from '@/stores/auth'

const data = useDataStore()
const auth = useAuthStore()
const search = ref('')

onMounted(() => {
  data.fetchOrders()
})

const filteredOrders = computed(() => {
  if (!search.value) return data.orders
  const q = search.value.toLowerCase()
  return data.orders.filter(order => 
    order.orderId.toString().includes(q) ||
    new Date(order.orderDate).toLocaleString().toLowerCase().includes(q) ||
    (order.cashierUsername || '').toLowerCase().includes(q)
  )
})

const totalRevenue = computed(() => {
  return filteredOrders.value.reduce((sum, order) => sum + (order.totalAmount || 0), 0)
})
</script>

<template>
  <div class="history-container">
    <div class="page-header">
      <div>
        <h1>Sales History</h1>
        <p>View and manage past transactions</p>
      </div>
      <div class="header-stats">
        <div class="header-stat glass-card">
          <span class="stat-label">Total Revenue</span>
          <span class="stat-value text-success">${{ totalRevenue.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- Search -->
    <div class="search-bar glass-card">
      <span class="search-icon">🔍</span>
      <input v-model="search" type="text" class="search-input" placeholder="Search by Order ID or Date..." />
    </div>

    <!-- Orders Table -->
    <div class="glass-card table-container">
      <div v-if="data.loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading sales history...</p>
      </div>
      
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="icon">🧾</div>
        <h3>No sales found</h3>
        <p>{{ search ? 'Try a different search term' : 'Start selling to see data here' }}</p>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Date & Time</th>
            <th>Items</th>
            <th v-if="auth.isAdmin">Cashier</th>
            <th>Method</th>
            <th>Total Amount</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in filteredOrders.slice().reverse()" :key="order.orderId">
            <td>
              <span class="order-id">#{{ order.orderId }}</span>
            </td>
            <td>
              <div class="date-cell">
                <span class="date-main">{{ new Date(order.orderDate).toLocaleDateString() }}</span>
                <span class="date-sub">{{ new Date(order.orderDate).toLocaleTimeString() }}</span>
              </div>
            </td>
            <td>
              <span class="badge badge-info">{{ order.totalItems || order.items?.length || 0 }} items</span>
            </td>
            <td v-if="auth.isAdmin">
              <span class="badge badge-info">{{ order.cashierUsername || 'Unknown' }}</span>
            </td>
            <td>
              <span class="badge badge-warning">{{ order.paymentMethod || 'CASH' }}</span>
            </td>
            <td class="text-success" style="font-weight: 700;">${{ (order.totalAmount || 0).toFixed(2) }}</td>
            <td>
              <span class="badge badge-success">Paid</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.history-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.header-stat {
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  min-width: 150px;
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
}

.order-id {
  font-family: monospace;
  font-weight: 700;
  color: var(--accent);
  font-size: 15px;
}

.date-cell {
  display: flex;
  flex-direction: column;
}

.date-main {
  font-weight: 600;
  color: var(--text-primary);
}

.date-sub {
  font-size: 12px;
  color: var(--text-muted);
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
