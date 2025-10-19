<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <nav>
    <RouterLink to="/login">Login</RouterLink>
    <RouterLink to="/register">Register</RouterLink>
  </nav>

  <RouterView />
</template>

<style scoped>
nav {
  display: flex;
  gap: 15px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

a {
  text-decoration: none;
  color: #333;
}

a:hover {
  text-decoration: underline;
}
</style>