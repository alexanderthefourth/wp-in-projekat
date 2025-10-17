<template>
  <div
    class="grid"
    style="
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      background: var(--bg-soft);
      padding: 40px 20px;
      text-align: center;
    "
  >
    <UiCard style="width: min(360px, 90vw); text-align: center">
      <h2>Dobrodosli</h2>
      <p style="color: var(--ink-muted); margin-top: 4px">Ulogujte se da biste nastavili</p>
      <div class="login" style="gap: 12px; margin-top: 16px">
        <input type="text" placeholder="Username" />
        <input type="password" placeholder="Lozinka" />
      </div>
      <button class="login-dugme">Uloguj se</button>
    </UiCard>
    <br></br><br></br>
    <p style="color: var(--ink-muted); margin-top: 10px">
      {{ loading ? 'Učitavanje...' : err || `Broj registrovanih korisnika: ${userCount}` }}
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import UiCard from '../components/UiCard.vue'
import { Users } from '../services/api'

const userCount = ref(null)
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  try {
    const { data } = await Users.userCount()
    userCount.value = data
  } catch (e) {
    err.value = 'Greška pri učitavanju broja korisnika.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
input {
  width: 100%;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--line);
  background: #fffaf3;
  color: var(--ink);
}

.grid {
  display: flex;
  justify-content: center;
}

.login {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.login-dugme {
  text-align: center;
  width: 100px;
  height: auto;
  background-color: sienna;
  border-radius: 9px;
  justify-content: center;
  display: flex;
}
</style>
