<template>
  <div class="blocked-container">
    <div class="blocked-card">
      <div class="blocked-icon">🚫</div>
      <h2>Blokirani ste</h2>
      <p>Vaš nalog je privremeno onemogućen. Za više informacija kontaktirajte administratora.</p>
      <LogoutButton />
    </div>
  </div>
</template>

<script setup>
import LogoutButton from "@/components/LogoutButton.vue"
</script>

<style scoped>
.blocked-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.blocked-card {
  background: white;
  padding: 50px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  text-align: center;
  max-width: 500px;
  width: 100%;
}

.blocked-icon {
  font-size: 64px;
  margin-bottom: 24px;
}

h2 {
  color: #dc2626;
  margin-bottom: 16px;
  font-size: 28px;
  font-weight: 700;
}

p {
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 30px;
  font-size: 16px;
}
</style>
