<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import appLogo from '@/assets/pos-systems-logo.svg'

const auth = useAuthStore()
const router = useRouter()
const username = ref('')
const password = ref('')

async function handleLogin() {
  const success = await auth.login(username.value, password.value)
  if (success) {
    router.push('/')
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-bg"></div>
    <div class="auth-card glass-card">
      <div class="auth-header">
        <img class="auth-logo" :src="appLogo" alt="PulsePOS logo" />
        <h1>Welcome Back</h1>
        <p>Sign in to your PulsePOS dashboard</p>
      </div>

      <div v-if="auth.error" class="alert alert-error">
        {{ auth.error }}
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label>Username</label>
          <input
            v-model="username"
            type="text"
            class="form-input"
            placeholder="Enter your username"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input
            v-model="password"
            type="password"
            class="form-input"
            placeholder="Enter your password"
            required
            autocomplete="current-password"
          />
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="auth.loading">
          <span v-if="auth.loading" class="spinner"></span>
          {{ auth.loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <p class="auth-footer">
        Don't have an account?
        <router-link to="/register" class="auth-link">Create one</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 50%, rgba(129, 95, 255, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(199, 125, 255, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 80%, rgba(96, 165, 250, 0.08) 0%, transparent 50%);
}

.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  position: relative;
  z-index: 1;
  animation: slideUp 0.5s ease;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
  margin: 0 auto 16px;
}

.auth-header h1 {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 4px;
}

.auth-header p {
  color: var(--text-muted);
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.auth-btn {
  width: 100%;
  justify-content: center;
  padding: 13px;
  font-size: 15px;
  margin-top: 4px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-muted);
}

.auth-link {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
}

.auth-link:hover {
  color: var(--accent-hover);
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
