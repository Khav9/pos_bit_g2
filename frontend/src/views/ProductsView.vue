<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDataStore } from '@/stores/data'
import { useAuthStore } from '@/stores/auth'

const data = useDataStore()
const auth = useAuthStore()
const showModal = ref(false)
const isEditing = ref(false)
const search = ref('')

const form = ref({
  productId: null,
  productName: '',
  productDescription: '',
  productPrice: 0,
  productQuantity: 0,
  isActive: true,
  category: { categoryId: '' }
})

onMounted(() => {
  data.fetchProducts()
  data.fetchCategories()
})

const filteredProducts = computed(() => {
  if (!search.value) return data.products
  const q = search.value.toLowerCase()
  return data.products.filter(p =>
    p.productName?.toLowerCase().includes(q) ||
    p.productDescription?.toLowerCase().includes(q) ||
    p.category?.categoryName?.toLowerCase().includes(q)
  )
})

function openCreate() {
  isEditing.value = false
  form.value = {
    productId: null,
    productName: '',
    productDescription: '',
    productPrice: 0,
    productQuantity: 0,
    isActive: true,
    category: { categoryId: data.categories[0]?.categoryId || '' }
  }
  showModal.value = true
}

function openEdit(product) {
  isEditing.value = true
  form.value = {
    productId: product.productId,
    productName: product.productName,
    productDescription: product.productDescription || '',
    productPrice: product.productPrice,
    productQuantity: product.productQuantity || 0,
    isActive: product.isActive ?? true,
    category: { categoryId: product.category?.categoryId || '' }
  }
  showModal.value = true
}

async function handleSave() {
  try {
    if (isEditing.value) {
      await data.updateProduct(form.value)
    } else {
      await data.createProduct(form.value)
    }
    showModal.value = false
  } catch (err) {
    alert('Error saving product: ' + (err.response?.data || err.message))
  }
}

async function handleDelete(productId) {
  if (!confirm('Are you sure you want to delete this product?')) return
  try {
    await data.deleteProduct(productId)
  } catch (err) {
    alert('Error deleting product')
  }
}
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Products</h1>
        <p>Manage your product inventory</p>
      </div>
      <button v-if="auth.isAdmin" class="btn btn-primary" @click="openCreate">+ Add Product</button>
    </div>

    <!-- Search -->
    <div class="search-bar glass-card">
      <span class="search-icon">🔍</span>
      <input v-model="search" type="text" class="search-input" placeholder="Search products by name, description, or category..." />
    </div>

    <!-- Products Table -->
    <div class="glass-card table-container">
      <div v-if="filteredProducts.length === 0" class="empty-state">
        <div class="icon">📦</div>
        <h3>No products found</h3>
        <p>{{ search ? 'Try a different search term' : 'Add your first product to get started' }}</p>
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Category</th>
            <th>Price</th>
            <th>Qty</th>
            <th>Status</th>
            <th v-if="auth.isAdmin">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in filteredProducts" :key="product.productId">
            <td>
              <div class="product-cell">
                <span class="product-name">{{ product.productName }}</span>
                <span class="product-desc">{{ product.productDescription || '—' }}</span>
              </div>
            </td>
            <td>
              <span class="badge badge-info">{{ product.category?.categoryName || '—' }}</span>
            </td>
            <td class="text-success" style="font-weight: 700;">${{ product.productPrice.toFixed(2) }}</td>
            <td>{{ product.productQuantity || 0 }}</td>
            <td>
              <span class="badge" :class="product.isActive ? 'badge-success' : 'badge-danger'">
                {{ product.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td v-if="auth.isAdmin">
              <div class="actions">
                <button class="btn btn-outline btn-sm" @click="openEdit(product)">Edit</button>
                <button class="btn btn-danger btn-sm" @click="handleDelete(product.productId)">Delete</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <Transition name="fade">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>{{ isEditing ? 'Edit Product' : 'New Product' }}</h3>
            <button class="btn btn-icon btn-outline" @click="showModal = false">✕</button>
          </div>

          <form @submit.prevent="handleSave" class="modal-form">
            <div class="form-group">
              <label>Product Name</label>
              <input v-model="form.productName" type="text" class="form-input" placeholder="e.g. Coca Cola" required />
            </div>

            <div class="form-group">
              <label>Description</label>
              <input v-model="form.productDescription" type="text" class="form-input" placeholder="Short description" />
            </div>

            <div class="grid-2">
              <div class="form-group">
                <label>Price ($)</label>
                <input v-model.number="form.productPrice" type="number" step="0.01" min="0" class="form-input" required />
              </div>
              <div class="form-group">
                <label>Quantity</label>
                <input v-model.number="form.productQuantity" type="number" min="0" class="form-input" />
              </div>
            </div>

            <div class="form-group">
              <label>Category</label>
              <select v-model="form.category.categoryId" class="form-input" required>
                <option value="" disabled>Select a category</option>
                <option v-for="cat in data.categories" :key="cat.categoryId" :value="cat.categoryId">
                  {{ cat.categoryName }}
                </option>
              </select>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-outline" @click="showModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary">
                {{ isEditing ? 'Save Changes' : 'Create Product' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 16px;
  margin-bottom: 20px;
}

.search-icon { font-size: 16px; opacity: 0.5; }

.search-input {
  flex: 1;
  padding: 12px 0;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  outline: none;
}

.search-input::placeholder { color: var(--text-muted); }

.table-container {
  padding: 4px;
  overflow-x: auto;
}

.product-cell {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 600;
  color: var(--text-primary);
}

.product-desc {
  font-size: 12px;
  color: var(--text-muted);
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
