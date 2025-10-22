<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2>Registracija</h2>
      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label for="firstName">Ime:</label>
          <input v-model="firstName" id="firstName" required />
        </div>

        <div class="form-group">
          <label for="lastName">Prezime:</label>
          <input v-model="lastName" id="lastName" required />
        </div>

        <div class="form-group">
          <label for="username">Korisničko ime:</label>
          <input v-model="username" id="username" required />
        </div>

        <div class="form-group">
          <label for="email">Email:</label>
          <input v-model="email" id="email" type="email" required />
        </div>

        <div class="form-group">
          <label for="password">Lozinka:</label>
          <input v-model="password" id="password" type="password" required />
        </div>

        <button type="submit" class="btn btn-primary">Registruj se</button>
      </form>

      <p class="auth-link">
        Već imate nalog? <RouterLink to="/login">Prijavite se</RouterLink>
      </p>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const firstName = ref('')
const lastName = ref('')
const username = ref('')
const email = ref('')
const password = ref('')
const errorMessage = ref('')
const successMessage = ref('')

async function handleRegister() {
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const { data, status } = await axios.post(
      'http://localhost:8080/api/users/register',
      {
        firstName: firstName.value,
        lastName: lastName.value,
        username: username.value,
        email: email.value,
        unhashedPassword: password.value,
      },
      { withCredentials: true }
    )

    if ((status === 200 || status === 201) && data) {
      successMessage.value = 'Registracija uspešna!'
      localStorage.setItem('user', JSON.stringify({
        id: data.id,
        username: data.username,
        role: data.role
      }))
      const isAdmin = data.role === 'ADMIN'
      router.push(isAdmin ? '/registered-admin' : '/registered')
    }
  } catch (error) {
    console.error(error?.response?.data || error)
    errorMessage.value = 'Greška prilikom registracije.'
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.auth-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #1f2937;
  font-size: 28px;
  font-weight: 700;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.auth-link {
  text-align: center;
  margin-top: 20px;
  color: #6b7280;
}

.auth-link a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 600;
}

.auth-link a:hover {
  text-decoration: underline;
}

.error-message {
  color: #ef4444;
  text-align: center;
  margin-top: 16px;
  padding: 12px;
  background: #fef2f2;
  border-radius: 8px;
  border: 1px solid #fecaca;
}

.success-message {
  color: #10b981;
  text-align: center;
  margin-top: 16px;
  padding: 12px;
  background: #f0fdf4;
  border-radius: 8px;
  border: 1px solid #bbf7d0;
}
</style>
