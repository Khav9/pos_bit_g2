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
  categoryId: '',
  categoryName: '',
  categoryDescription: '',
  status: 'active'
})

onMounted(() => {
  data.fetchCategories()
})

const filteredCategories = computed(() => {
  if (!search.value) return data.categories
  const q = search.value.toLowerCase()
  return data.categories.filter(c =>
    c.categoryName?.toLowerCase().includes(q) ||
    c.categoryId?.toLowerCase().includes(q) ||
    c.categoryDescription?.toLowerCase().includes(q)
  )
})

function openCreate() {
  isEditing.value = false
  form.value = { categoryId: '', categoryName: '', categoryDescription: '', status: 'active' }
  showModal.value = true
}

function openEdit(category) {
  isEditing.value = true
  form.value = { ...category }
  showModal.value = true
}

async function handleSave() {
  try {
    if (isEditing.value) {
      await data.updateCategory(form.value)
    } else {
      await data.createCategory(form.value)
    }
    showModal.value = false
  } catch (err) {
    alert('Error saving category: ' + (err.response?.data || err.message))
  }
}

async function handleDelete(categoryId) {
  if (!confirm('Are you sure you want to delete this category?')) return
  try {
    await data.deleteCategory(categoryId)
  } catch (err) {
    alert('Error deleting category. Make sure no products are linked to it.')
  }
}
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Categories</h1>
        <p>Organize your products into categories</p>
      </div>
      <button v-if="auth.isAdmin" class="btn btn-primary" @click="openCreate">+ Add Category</button>
    </div>

    <!-- Search -->
    <div class="search-bar glass-card">
      <span class="search-icon">🔍</span>
      <input v-model="search" type="text" class="search-input" placeholder="Search categories..." />
    </div>

    <!-- Categories Grid -->
    <div v-if="filteredCategories.length === 0" class="glass-card">
      <div class="empty-state">
        <div class="icon">🏷️</div>
        <h3>No categories found</h3>
        <p>{{ search ? 'Try a different search term' : 'Add your first category to get started' }}</p>
      </div>
    </div>

    <div v-else class="categories-grid">
      <div v-for="cat in filteredCategories" :key="cat.categoryId" class="category-card glass-card">
        <div class="category-header">
          <div class="category-icon">🏷️</div>
          <div v-if="auth.isAdmin" class="category-actions">
            <button class="btn btn-outline btn-sm" @click="openEdit(cat)">Edit</button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(cat.categoryId)">Delete</button>
          </div>
        </div>
        <h3 class="category-name">{{ cat.categoryName }}</h3>
        <p class="category-desc">{{ cat.categoryDescription || 'No description' }}</p>
        <div class="category-footer">
          <span class="badge badge-info">{{ cat.categoryId }}</span>
          <span class="badge" :class="cat.status === 'active' ? 'badge-success' : 'badge-warning'">
            {{ cat.status || 'active' }}
          </span>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <Transition name="fade">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>{{ isEditing ? 'Edit Category' : 'New Category' }}</h3>
            <button class="btn btn-icon btn-outline" @click="showModal = false">✕</button>
          </div>

          <form @submit.prevent="handleSave" class="modal-form">
            <div class="form-group">
              <label>Category ID</label>
              <input
                v-model="form.categoryId"
                type="text"
                class="form-input"
                placeholder="e.g. CAT001"
                :disabled="isEditing"
                required
              />
            </div>

            <div class="form-group">
              <label>Category Name</label>
              <input v-model="form.categoryName" type="text" class="form-input" placeholder="e.g. Beverages" required />
            </div>

            <div class="form-group">
              <label>Description</label>
              <input v-model="form.categoryDescription" type="text" class="form-input" placeholder="Short description" />
            </div>

            <div class="form-group">
              <label>Status</label>
              <select v-model="form.status" class="form-input">
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
              </select>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-outline" @click="showModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary">
                {{ isEditing ? 'Save Changes' : 'Create Category' }}
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

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.category-card {
  padding: 22px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.category-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: rgba(96, 165, 250, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.category-actions {
  display: flex;
  gap: 6px;
}

.category-name {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: 4px;
}

.category-desc {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 16px;
  line-height: 1.5;
}

.category-footer {
  display: flex;
  gap: 8px;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
