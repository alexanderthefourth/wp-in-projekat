<template>
  <button @click="handleLogout">Log out</button>
</template>

<script setup>
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

async function handleLogout() {
  try {
    await axios.post("http://localhost:8080/api/users/logout", {}, { withCredentials: true });
    router.push("/login");
  } catch (error) {
    alert("Error during logging out.");
  }
}
</script>

<style scoped>
button {
  background-color: #d32f2f;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 14px;
  cursor: pointer;
  font-weight: bold;
}

button:hover {
  background-color: #b71c1c;
}
</style>
