<template>
  <div class="auth-container">
    <h2>Registracija</h2>

    <form @submit.prevent="handleRegister">
      <label for="firstName">Name:</label>
      <input v-model="firstName" id="firstName" required />

      <label for="lastName">Last name:</label>
      <input v-model="lastName" id="lastName" required />

      <label for="username">Username:</label>
      <input v-model="username" id="username" required />

      <label for="email">Email:</label>
      <input v-model="email" id="email" type="email" required />

      <label for="password">Password:</label>
      <input v-model="password" id="password" type="password" required />

      <button type="submit">Register</button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
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
      // data is a User entity (role will be USER by default)
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
  width: 340px;
  margin: 80px auto;
  background: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

button {
  padding: 10px;
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}

button:hover {
  background-color: #0d47a1;
}

.error {
  color: #c62828;
  margin-top: 10px;
}

.success {
  color: #2e7d32;
  margin-top: 10px;
}
</style>
