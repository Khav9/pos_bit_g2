import { ref } from 'vue'
import { defineStore } from 'pinia'
import api from '@/services/api'

export const useDataStore = defineStore('data', () => {
    const categories = ref([])
    const products = ref([])
    const orders = ref([])
    const users = ref([])
    const salesSummary = ref({
        totalOrders: 0,
        totalRevenue: 0,
        todayOrders: 0,
        todayRevenue: 0,
        lowStockProducts: 0
    })
    const loading = ref(false)
    const error = ref('')

    function extractArray(response) {
        if (!response?.data) return []
        // Backend sometimes wraps data in { data: [...] } and sometimes returns raw [...]
        if (Array.isArray(response.data)) return response.data
        if (Array.isArray(response.data.data)) return response.data.data
        return []
    }

    function resetState() {
        categories.value = []
        products.value = []
        orders.value = []
        users.value = []
        salesSummary.value = {
            totalOrders: 0,
            totalRevenue: 0,
            todayOrders: 0,
            todayRevenue: 0,
            lowStockProducts: 0
        }
        loading.value = false
        error.value = ''
    }

    // Categories
    async function fetchCategories() {
        loading.value = true
        try {
            const response = await api.get('/product-category')
            categories.value = extractArray(response)
        } catch (err) {
            error.value = 'Failed to fetch categories'
            categories.value = []
        } finally {
            loading.value = false
        }
    }

    async function createCategory(category) {
        await api.post('/product-category', category)
        await fetchCategories()
    }

    async function updateCategory(category) {
        await api.put(`/product-category/${category.categoryId}`, category)
        await fetchCategories()
    }

    async function deleteCategory(id) {
        await api.delete(`/product-category/${id}`)
        await fetchCategories()
    }

    // Products
    async function fetchProducts() {
        loading.value = true
        try {
            const response = await api.get('/products')
            products.value = extractArray(response)
        } catch (err) {
            error.value = 'Failed to fetch products'
            products.value = []
        } finally {
            loading.value = false
        }
    }

    async function createProduct(product) {
        await api.post('/products', product)
        await fetchProducts()
    }

    async function updateProduct(product) {
        await api.put(`/products/${product.productId}`, product)
        await fetchProducts()
    }

    async function deleteProduct(id) {
        await api.delete(`/products/${id}`)
        await fetchProducts()
    }

    // Orders
    async function fetchOrders() {
        loading.value = true
        try {
            const response = await api.post('/sales-orders/all', {})
            orders.value = extractArray(response)
        } catch (err) {
            error.value = 'Failed to fetch orders'
            orders.value = []
        } finally {
            loading.value = false
        }
    }

    async function fetchSalesSummary() {
        try {
            const response = await api.post('/sales-orders/summary', {})
            const data = response?.data || {}
            salesSummary.value = {
                totalOrders: data.totalOrders || 0,
                totalRevenue: data.totalRevenue || 0,
                todayOrders: data.todayOrders || 0,
                todayRevenue: data.todayRevenue || 0,
                lowStockProducts: data.lowStockProducts || 0
            }
        } catch (err) {
            // Keep dashboard usable even if summary endpoint is temporarily unavailable.
            salesSummary.value = {
                totalOrders: orders.value.length || 0,
                totalRevenue: (orders.value || []).reduce((sum, order) => sum + (order.totalAmount || 0), 0),
                todayOrders: 0,
                todayRevenue: 0,
                lowStockProducts: (products.value || []).filter(p => (p.productQuantity || 0) <= 5).length
            }
        }
    }

    async function createOrder(order) {
        const response = await api.post('/sales-orders', order)
        await fetchProducts() // Refresh products for stock updates
        await fetchOrders()
        return response.data
    }

    // Users
    async function fetchUsers() {
        loading.value = true
        try {
            const response = await api.get('/users')
            users.value = extractArray(response)
        } catch (err) {
            error.value = 'Failed to fetch users'
            users.value = []
        } finally {
            loading.value = false
        }
    }

    async function toggleRole(user) {
        const newRoles = user.roles.includes('ROLE_ADMIN') ? 'ROLE_USER' : 'ROLE_USER,ROLE_ADMIN'
        await api.put(`/users/${user.id}`, { roles: newRoles })
        await fetchUsers()
    }

    async function deleteUser(id) {
        await api.delete(`/users/${id}`)
        await fetchUsers()
    }

    return {
        categories, products, orders, users, salesSummary, loading, error,
        resetState,
        fetchCategories, createCategory, updateCategory, deleteCategory,
        fetchProducts, createProduct, updateProduct, deleteProduct,
        fetchOrders, fetchSalesSummary, createOrder,
        fetchUsers, toggleRole, deleteUser
    }
})
