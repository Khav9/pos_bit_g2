<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const profileForm = reactive({
  fullName: '',
  email: '',
  phone: '',
  address: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileMessage = ref('')
const profileError = ref('')
const passwordMessage = ref('')
const passwordError = ref('')

function fillProfileForm() {
  profileForm.fullName = auth.profile?.fullName || ''
  profileForm.email = auth.profile?.email || ''
  profileForm.phone = auth.profile?.phone || ''
  profileForm.address = auth.profile?.address || ''
}

onMounted(async () => {
  await auth.refreshProfile()
  fillProfileForm()
})

async function saveProfile() {
  profileMessage.value = ''
  profileError.value = ''

  const result = await auth.updateProfile({
    fullName: profileForm.fullName,
    email: profileForm.email,
    phone: profileForm.phone,
    address: profileForm.address
  })

  if (result.ok) {
    profileMessage.value = 'Profile updated successfully.'
  } else {
    profileError.value = result.message || 'Failed to update profile.'
  }
}

async function updatePassword() {
  passwordMessage.value = ''
  passwordError.value = ''

  if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    passwordError.value = 'Please fill all password fields.'
    return
  }

  if (passwordForm.newPassword.length < 6) {
    passwordError.value = 'New password must be at least 6 characters.'
    return
  }

  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordError.value = 'New password and confirm password do not match.'
    return
  }

  const result = await auth.changePassword({
    currentPassword: passwordForm.currentPassword,
    newPassword: passwordForm.newPassword,
    confirmPassword: passwordForm.confirmPassword
  })

  if (result.ok) {
    passwordMessage.value = result.message || 'Password updated successfully.'
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } else {
    passwordError.value = result.message || 'Failed to change password.'
  }
}
</script>

<template>
  <div class="profile-page">
    <div class="page-header">
      <div>
        <h1>My Profile</h1>
        <p>View your account details and security settings</p>
      </div>
    </div>

    <div class="profile-grid">
      <section class="glass-card card">
        <h3>Profile Details</h3>
        <p class="section-note">Username: <strong>{{ auth.accountUsername }}</strong></p>
        <p class="section-note">Role: <strong>{{ auth.isAdmin ? 'Admin' : 'User' }}</strong></p>

        <div v-if="profileMessage" class="alert alert-success">{{ profileMessage }}</div>
        <div v-if="profileError" class="alert alert-error">{{ profileError }}</div>

        <form class="form" @submit.prevent="saveProfile">
          <div class="form-group">
            <label>Username</label>
            <input :value="auth.accountUsername" class="form-input readonly-input" type="text" readonly />
          </div>
          <div class="form-group">
            <label>Full Name</label>
            <input v-model="profileForm.fullName" class="form-input" type="text" placeholder="Your full name" />
          </div>
          <div class="form-group">
            <label>Email</label>
            <input v-model="profileForm.email" class="form-input" type="email" placeholder="you@example.com" />
          </div>
          <div class="form-group">
            <label>Phone</label>
            <input v-model="profileForm.phone" class="form-input" type="text" placeholder="+1-xxx-xxx-xxxx" />
          </div>
          <div class="form-group">
            <label>Address</label>
            <input v-model="profileForm.address" class="form-input" type="text" placeholder="Address" />
          </div>

          <button class="btn btn-primary" type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Saving...' : 'Save Profile' }}
          </button>
        </form>
      </section>

      <section class="glass-card card">
        <h3>Change Password</h3>
        <p class="section-note">Use a strong password with at least 6 characters.</p>

        <div v-if="passwordMessage" class="alert alert-success">{{ passwordMessage }}</div>
        <div v-if="passwordError" class="alert alert-error">{{ passwordError }}</div>

        <form class="form" @submit.prevent="updatePassword">
          <div class="form-group">
            <label>Current Password</label>
            <input v-model="passwordForm.currentPassword" class="form-input" type="password" autocomplete="current-password" />
          </div>
          <div class="form-group">
            <label>New Password</label>
            <input v-model="passwordForm.newPassword" class="form-input" type="password" autocomplete="new-password" />
          </div>
          <div class="form-group">
            <label>Confirm New Password</label>
            <input v-model="passwordForm.confirmPassword" class="form-input" type="password" autocomplete="new-password" />
          </div>

          <button class="btn btn-primary" type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Updating...' : 'Update Password' }}
          </button>
        </form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.card {
  padding: 22px;
}

.card h3 {
  margin-bottom: 4px;
  font-size: 18px;
}

.section-note {
  color: var(--text-muted);
  margin-bottom: 10px;
  font-size: 14px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form .btn {
  width: fit-content;
  margin-top: 4px;
}

.readonly-input {
  opacity: 0.85;
  cursor: not-allowed;
}

@media (max-width: 980px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
