<template>
  <div class="blocked-container">
    <h2>Blokirani ste</h2>
    <p>Za više detalja kontaktirajte administratora.</p>
    <LogoutButton />
  </div>
</template>

<script setup>
import LogoutButton from "@/components/LogoutButton.vue"
</script>

<style scoped>
.blocked-container {
  text-align: center;
  margin-top: 100px;
  font-family: Arial, sans-serif;
}
h2 {
  color: #c62828;
  margin-bottom: 10px;
}
p {
  color: #555;
  margin-bottom: 20px;
}
</style>
