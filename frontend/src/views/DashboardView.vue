<script setup>
import { onMounted, computed } from 'vue'
import { useDataStore } from '@/stores/data'
import { useAuthStore } from '@/stores/auth'

const data = useDataStore()
const auth = useAuthStore()

onMounted(() => {
  auth.refreshProfile()
  data.fetchProducts()
  data.fetchCategories()
  data.fetchOrders()
  data.fetchSalesSummary()
})

const stats = computed(() => {
  const products = Array.isArray(data.products) ? data.products : []
  const summary = data.salesSummary || {}
  
  return [
    { label: 'Total Revenue', value: '$' + Number(summary.totalRevenue || 0).toFixed(2), icon: '💰', color: '#fbbf24' },
    { label: 'Total Sales', value: Number(summary.totalOrders || 0), icon: '🧾', color: '#815fff' },
    { label: 'Today Revenue', value: '$' + Number(summary.todayRevenue || 0).toFixed(2), icon: '📈', color: '#22c55e' },
    { label: 'Today Orders', value: Number(summary.todayOrders || 0), icon: '🗓️', color: '#38bdf8' },
    { label: 'Active Products', value: products.filter(p => p.isActive).length, icon: '📦', color: '#34d399' },
    { label: 'Low Stock Items', value: Number(summary.lowStockProducts || 0), icon: '⚠️', color: '#fb7185' }
  ]
})
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Dashboard</h1>
        <p>Welcome back, {{ auth.user?.username }} 👋</p>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div
        v-for="(stat, i) in stats"
        :key="i"
        class="stat-card glass-card"
        :style="{ '--stat-color': stat.color, animationDelay: i * 0.1 + 's' }"
      >
        <div class="stat-icon">{{ stat.icon }}</div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-glow"></div>
      </div>
    </div>

    <!-- Recent Data -->
    <div class="dashboard-grid">
      <div class="glass-card recent-section">
        <h3>📦 Recent Products</h3>
        <div v-if="data.products.length === 0" class="empty-mini">No products yet</div>
        <div v-else class="recent-list">
          <div v-for="product in data.products.slice(0, 5)" :key="product.productId" class="recent-item">
            <div class="recent-info">
              <span class="recent-name">{{ product.productName }}</span>
              <span class="recent-sub">{{ product.category?.categoryName || 'No category' }}</span>
            </div>
            <div class="recent-meta">
              <span class="recent-price">${{ (product.productPrice || 0).toFixed(2) }}</span>
              <span class="badge" :class="product.isActive ? 'badge-success' : 'badge-danger'">
                {{ product.isActive ? 'Active' : 'Inactive' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="glass-card recent-section">
        <h3>🧾 Recent Sales</h3>
        <div v-if="data.orders.length === 0" class="empty-mini">No sales recorded yet</div>
        <div v-else class="recent-list">
          <div v-for="order in data.orders.slice(-5).reverse()" :key="order.orderId" class="recent-item">
            <div class="recent-info">
              <span class="recent-name">Order #{{ order.orderId }}</span>
              <span class="recent-sub">{{ new Date(order.orderDate).toLocaleString() }}</span>
            </div>
            <div class="recent-meta">
              <span class="recent-price">${{ (order.totalAmount || 0).toFixed(2) }}</span>
              <span class="badge badge-success">Paid</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 28px;
}

.stat-card {
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  animation: slideUp 0.5s ease both;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
}

.stat-glow {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--stat-color);
  opacity: 0.08;
  filter: blur(20px);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

@media (max-width: 900px) {
  .dashboard-grid { grid-template-columns: 1fr; }
}

.recent-section {
  padding: 24px;
}

.recent-section h3 {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
}

.recent-list {
  display: flex;
  flex-direction: column;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255,255,255,0.04);
}

.recent-item:last-child {
  border-bottom: none;
}

.recent-info {
  display: flex;
  flex-direction: column;
}

.recent-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.recent-sub {
  font-size: 12px;
  color: var(--text-muted);
}

.recent-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.recent-price {
  font-weight: 700;
  font-size: 14px;
  color: var(--success);
}

.empty-mini {
  text-align: center;
  padding: 24px;
  color: var(--text-muted);
  font-size: 14px;
}
</style>
