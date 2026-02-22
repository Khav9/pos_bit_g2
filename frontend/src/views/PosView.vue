<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDataStore } from '@/stores/data'

const data = useDataStore()
const search = ref('')
const cart = ref([])
const cashReceived = ref(0)
const processing = ref(false)
const paymentMethod = ref('CASH')
const cashierNote = ref('')

const changeDue = computed(() => {
  if (cashReceived.value <= 0) return 0
  return Math.max(0, cashReceived.value - cartTotal.value)
})

onMounted(() => {
  data.fetchProducts()
})

const filteredProducts = computed(() => {
  const products = Array.isArray(data.products) ? data.products : []
  if (!search.value) return products
  const q = search.value.toLowerCase()
  return products.filter(p =>
    p.productName?.toLowerCase().includes(q) ||
    p.productDescription?.toLowerCase().includes(q)
  )
})

const cartTotal = computed(() => {
  return cart.value.reduce((sum, item) => sum + (item.productPrice * item.quantity), 0)
})

function addToCart(product) {
  if (product.productQuantity <= 0) {
    alert('This product is out of stock!')
    return
  }

  const existing = cart.value.find(item => item.productId === product.productId)
  if (existing) {
    if (existing.quantity >= product.productQuantity) {
      alert('Cannot add more than available stock!')
      return
    }
    existing.quantity++
  } else {
    cart.value.push({ ...product, quantity: 1 })
  }
}

function removeFromCart(index) {
  cart.value.splice(index, 1)
}

function updateQuantity(item, delta) {
  const newQty = item.quantity + delta
  if (newQty <= 0) {
    removeFromCart(cart.value.indexOf(item))
    return
  }
  
  if (delta > 0) {
    const products = Array.isArray(data.products) ? data.products : []
    const product = products.find(p => p.productId === item.productId)
    if (product && newQty > product.productQuantity) {
      alert('Cannot exceed available stock!')
      return
    }
  }
  
  item.quantity = newQty
}

async function handleCheckout() {
  if (cart.value.length === 0) return
  
  processing.value = true
  try {
    const orderItems = cart.value.map(item => ({
      product: { productId: item.productId },
      quantity: item.quantity
    }))
    
    await data.createOrder({
      items: orderItems,
      paymentMethod: paymentMethod.value,
      note: cashierNote.value || null
    })
    alert('Order placed successfully!')
    cart.value = []
    cashReceived.value = 0
    cashierNote.value = ''
    paymentMethod.value = 'CASH'
  } catch (err) {
    alert('Checkout failed: ' + (err.response?.data?.message || err.message))
  } finally {
    processing.value = false
  }
}
</script>

<template>
  <div class="pos-container">
    <div class="page-header">
      <div>
        <h1>Point of Sale</h1>
        <p>Real-time checkout and inventory management</p>
      </div>
    </div>

    <div class="pos-grid">
      <!-- Product Selection -->
      <div class="product-section">
        <div class="search-bar glass-card">
          <span class="search-icon">🔍</span>
          <input v-model="search" type="text" class="search-input" placeholder="Search products..." />
        </div>

        <div class="products-list">
          <div v-if="filteredProducts.length === 0" class="empty-state glass-card">
            <div class="icon">📦</div>
            <h3>No products found</h3>
          </div>
          <div
            v-for="product in filteredProducts"
            :key="product.productId"
            class="pos-product-card glass-card"
            :class="{ disabled: product.productQuantity <= 0 }"
            @click="addToCart(product)"
          >
            <div class="pos-product-info">
              <span class="pos-product-name">{{ product.productName }}</span>
              <span class="pos-product-stock">Stock: {{ product.productQuantity }}</span>
            </div>
            <span class="pos-product-price">${{ (product.productPrice || 0).toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- Bill / Cart -->
      <div class="bill-section glass-card">
        <div class="bill-header">
          <h3>Current Bill</h3>
          <span class="item-count">{{ cart.length }} items</span>
        </div>

        <div class="bill-items">
          <div v-if="cart.length === 0" class="empty-bill">
            <div class="empty-icon">🛒</div>
            <p>Your cart is empty</p>
          </div>
          <div v-for="(item, i) in cart" :key="item.productId" class="bill-item">
            <div class="item-main">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-price">${{ (item.productPrice * item.quantity).toFixed(2) }}</span>
            </div>
            <div class="item-controls">
              <div class="qty-btn" @click="updateQuantity(item, -1)">-</div>
              <span class="qty-val">{{ item.quantity }}</span>
              <div class="qty-btn" @click="updateQuantity(item, 1)">+</div>
              <div class="remove-btn" @click="removeFromCart(i)">🗑️</div>
            </div>
          </div>
        </div>

        <div class="bill-footer">
          <div class="total-row">
            <span>Total</span>
            <span class="total-val">${{ cartTotal.toFixed(2) }}</span>
          </div>
          
          <div v-if="cart.length > 0" class="payment-details">
            <div class="payment-row">
              <span>Payment Method</span>
              <select v-model="paymentMethod" class="payment-select">
                <option value="CASH">Cash</option>
                <option value="CARD">Card</option>
                <option value="TRANSFER">Transfer</option>
              </select>
            </div>
            <div class="payment-row">
              <span>Cash Received</span>
              <input v-model.number="cashReceived" type="number" class="payment-input" step="0.01" />
            </div>
            <div class="payment-row highlight">
              <span>Change Due</span>
              <span class="change-val">${{ changeDue.toFixed(2) }}</span>
            </div>
            <div class="payment-row">
              <span>Cashier Note</span>
              <input v-model="cashierNote" type="text" class="payment-input payment-note" placeholder="Optional note" />
            </div>
          </div>

          <button
            class="btn btn-primary btn-block checkout-btn"
            :disabled="cart.length === 0 || processing || (cashReceived > 0 && cashReceived < cartTotal)"
            @click="handleCheckout"
          >
            {{ processing ? 'Processing...' : 'Complete Checkout' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pos-container {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pos-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  flex: 1;
  min-height: 0;
  margin-top: 20px;
}

.product-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
}

.products-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  overflow-y: auto;
  padding-bottom: 20px;
}

.pos-product-card {
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 12px;
  border: 1px solid transparent;
}

.pos-product-card:hover:not(.disabled) {
  border-color: var(--accent);
  background: var(--accent-glow);
  transform: translateY(-2px);
}

.pos-product-card.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pos-product-info {
  display: flex;
  flex-direction: column;
}

.pos-product-name {
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
}

.pos-product-stock {
  font-size: 12px;
  color: var(--text-muted);
}

.pos-product-price {
  font-weight: 800;
  color: var(--success);
  font-size: 17px;
}

/* Bill Section */
.bill-section {
  display: flex;
  flex-direction: column;
  padding: 24px;
  height: 100%;
  min-height: 0;
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.bill-items {
  flex: 1;
  overflow-y: auto;
  min-height: 120px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-bill {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-muted);
}

.empty-icon { font-size: 40px; margin-bottom: 12px; opacity: 0.3; }

.bill-item {
  padding: 12px;
  background: rgba(255,255,255,0.03);
  border-radius: 12px;
}

.item-main {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.item-name { font-weight: 600; font-size: 14px; }
.item-price { font-weight: 700; color: var(--success); }

.item-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-btn {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: rgba(255,255,255,0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
}

.qty-btn:hover { background: rgba(255,255,255,0.15); }
.qty-val { font-size: 13px; font-weight: 700; width: 20px; text-align: center; }

.remove-btn {
  margin-left: auto;
  cursor: pointer;
  opacity: 0.5;
}

.remove-btn:hover { opacity: 1; }

.bill-footer {
  border-top: 1px solid var(--border);
  padding-top: 20px;
  margin-top: auto;
  position: sticky;
  bottom: 0;
  background: linear-gradient(to top, rgba(22, 22, 42, 0.98), rgba(22, 22, 42, 0.9));
  padding-bottom: 8px;
}

.payment-details {
  background: rgba(255, 255, 255, 0.03);
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-input {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border);
  color: white;
  padding: 6px 12px;
  border-radius: 8px;
  width: 100px;
  text-align: right;
  font-weight: 600;
}

.payment-select {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border);
  color: white;
  padding: 6px 12px;
  border-radius: 8px;
  width: 120px;
  font-weight: 600;
}

.payment-note {
  width: 170px;
  text-align: left;
}

.payment-row.highlight .change-val {
  color: var(--accent);
  font-weight: 800;
  font-size: 18px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.total-row span:first-child { font-size: 16px; color: var(--text-muted); }
.total-val { font-size: 32px; font-weight: 800; color: var(--text-primary); }

.btn-block { width: 100%; }
.checkout-btn { padding: 16px; font-size: 16px; font-weight: 700; }

@media (max-width: 1000px) {
  .pos-grid { grid-template-columns: 1fr; }
  .bill-section { min-height: 0; height: auto; }
}
</style>
