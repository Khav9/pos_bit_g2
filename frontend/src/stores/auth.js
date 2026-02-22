import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import api from '@/services/api'
import { useDataStore } from '@/stores/data'

function extractError(err, fallback) {
    console.error('Auth error:', err)
    if (err.response?.data) {
        const d = err.response.data
        if (typeof d === 'string') return d
        if (d.message) return d.message
        if (d.error) return d.error
        return JSON.stringify(d)
    }
    if (err.message) return err.message
    return fallback
}

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || '')
    const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
    const profile = ref(JSON.parse(localStorage.getItem('profile') || 'null'))
    const loading = ref(false)
    const error = ref('')

    const tokenRoles = computed(() => {
        if (!token.value) return []
        try {
            const payload = JSON.parse(atob(token.value.split('.')[1]))
            return Array.isArray(payload.roles) ? payload.roles : []
        } catch (e) {
            return []
        }
    })

    const tokenSubject = computed(() => {
        if (!token.value) return ''
        try {
            const payload = JSON.parse(atob(token.value.split('.')[1]))
            return payload?.sub || ''
        } catch (e) {
            return ''
        }
    })

    const resolvedRoles = computed(() => {
        const profileRoles = profile.value?.roles
        if (typeof profileRoles === 'string' && profileRoles.trim()) {
            return profileRoles.split(',').map(r => r.trim()).filter(Boolean)
        }
        return tokenRoles.value
    })

    const isAdmin = computed(() => resolvedRoles.value.includes('ROLE_ADMIN'))
    const isAuthenticated = computed(() => !!token.value)
    const accountUsername = computed(() => tokenSubject.value || profile.value?.username || user.value?.username || '')

    function persistUserState() {
        if (user.value) {
            localStorage.setItem('user', JSON.stringify(user.value))
        } else {
            localStorage.removeItem('user')
        }

        if (profile.value) {
            localStorage.setItem('profile', JSON.stringify(profile.value))
        } else {
            localStorage.removeItem('profile')
        }
    }

    function applyProfile(profileData, fallbackUsername = '') {
        profile.value = profileData || null
        const canonicalUsername = profileData?.username || fallbackUsername || tokenSubject.value || user.value?.username || ''
        user.value = {
            username: canonicalUsername,
            fullName: profileData?.fullName || '',
            roles: profileData?.roles || resolvedRoles.value.join(',')
        }
        persistUserState()
    }

    async function refreshProfile() {
        if (!token.value) return null
        try {
            const response = await api.get('/users/me')
            applyProfile(response.data)
            return response.data
        } catch (err) {
            // Keep session usable even if profile endpoint temporarily fails.
            if (!user.value?.username) {
                applyProfile(null)
            }
            return null
        }
    }

    async function login(username, password) {
        loading.value = true
        error.value = ''
        try {
            const dataStore = useDataStore()
            dataStore.resetState()

            const response = await api.post('/users/login', { username, password })
            token.value = response.data
            localStorage.setItem('token', response.data)
            applyProfile(null, username)
            await refreshProfile()
            if (!user.value?.username) {
                applyProfile(null, username)
            }
            return true
        } catch (err) {
            error.value = extractError(err, 'Login failed. Please check your credentials.')
            return false
        } finally {
            loading.value = false
        }
    }

    async function register(payloadOrUsername, password) {
        loading.value = true
        error.value = ''
        try {
            const payload = typeof payloadOrUsername === 'string'
                ? { username: payloadOrUsername, password }
                : payloadOrUsername
            const response = await api.post('/users/register', payload)
            console.log('Register success:', response.data)
            return true
        } catch (err) {
            error.value = extractError(err, 'Registration failed.')
            return false
        } finally {
            loading.value = false
        }
    }

    async function updateProfile(payload) {
        loading.value = true
        error.value = ''
        try {
            const response = await api.put('/users/me', payload)
            applyProfile(response.data)
            return { ok: true, data: response.data }
        } catch (err) {
            error.value = extractError(err, 'Failed to update profile.')
            return { ok: false, message: error.value }
        } finally {
            loading.value = false
        }
    }

    async function changePassword(payload) {
        loading.value = true
        error.value = ''
        try {
            const response = await api.put('/users/me/password', payload)
            return { ok: true, message: response.data }
        } catch (err) {
            error.value = extractError(err, 'Failed to update password.')
            return { ok: false, message: error.value }
        } finally {
            loading.value = false
        }
    }

    function logout() {
        const dataStore = useDataStore()
        dataStore.resetState()

        token.value = ''
        user.value = null
        profile.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('profile')
    }

    return {
        token,
        user,
        profile,
        loading,
        error,
        isAuthenticated,
        isAdmin,
        resolvedRoles,
        accountUsername,
        login,
        register,
        refreshProfile,
        updateProfile,
        changePassword,
        logout
    }
})

