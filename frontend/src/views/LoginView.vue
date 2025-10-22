<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2>Prijava</h2>
      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="username">Korisničko ime:</label>
          <input v-model="username" id="username" type="text" required />
        </div>

        <div class="form-group">
          <label for="password">Lozinka:</label>
          <input v-model="password" id="password" type="password" required />
        </div>

        <button type="submit" class="btn btn-primary">Prijavi se</button>
      </form>

      <p class="auth-link">
        Nemate nalog? <RouterLink to="/register">Registrujte se</RouterLink>
      </p>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref } from "vue";
import { useRouter, RouterLink } from "vue-router";

const username = ref("");
const password = ref("");
const errorMessage = ref("");
const successMessage = ref("");
const router = useRouter();

async function handleLogin() {
  errorMessage.value = "";
  successMessage.value = "";

  try {
    const response = await axios.post(
      "http://localhost:8080/api/users/login",
      { username: username.value, password: password.value },
      { withCredentials: true }
    );

    const user = response.data;
    localStorage.setItem("user", JSON.stringify(user));

    if (user.blocked) {
      router.push("/blocked");
      return;
    }

    if (user.role === "ADMIN") {
      router.push("/registered-admin");
    } else {
      router.push("/registered");
    }
  } catch (error) {
    if (error.response?.status === 401) {
      errorMessage.value = "Neispravno korisničko ime ili lozinka!";
    } else {
      errorMessage.value = "Greška prilikom prijavljivanja.";
    }
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
  max-width: 400px;
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
