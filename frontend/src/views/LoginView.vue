<template>
  <div class="auth-container">
    <h2>Prijava</h2>
    <form @submit.prevent="handleLogin">
      <label for="username">Username:</label>
      <input v-model="username" id="username" type="text" required />

      <label for="password">Password:</label>
      <input v-model="password" id="password" type="password" required />

      <button type="submit">Login</button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref } from "vue";
import { useRouter } from "vue-router";

const username = ref("");
const password = ref("");
const router = useRouter();

async function handleLogin() {
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

    if (user.role === "ADMIN") router.push("/registered-admin");
    else router.push("/registered");
  } catch (error) {
    if (error.response?.status === 401) alert("Neispravno korisničko ime ili lozinka!");
    else alert("Greška prilikom prijavljivanja.");
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
  background-color: #2e7d32;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}
button:hover {
  background-color: #1b5e20;
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
