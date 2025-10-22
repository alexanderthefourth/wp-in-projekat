<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: #333;
}

.app-container {
  min-height: 100vh;
}

/* Common button styles */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  text-align: center;
}

.btn-primary {
  background: #4f46e5;
  color: white;
}

.btn-primary:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

.btn-accent {
  background: #ec4899;
  color: white;
}

.btn-accent:hover {
  background: #db2777;
  transform: translateY(-2px);
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover {
  background: #dc2626;
  transform: translateY(-2px);
}

.btn-warn {
  background: #f59e0b;
  color: white;
}

.btn-warn:hover {
  background: #d97706;
  transform: translateY(-2px);
}

.btn-subtle {
  background: #6b7280;
  color: white;
}

.btn-subtle:hover {
  background: #4b5563;
  transform: translateY(-2px);
}

/* Common card styles */
.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

/* Common form styles */
.form-grid {
  display: grid;
  gap: 16px;
  margin: 20px 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #374151;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4f46e5;
}

/* Common table styles */
.data-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.data-table th {
  background: #f8fafc;
  font-weight: 600;
  color: #374151;
}

.data-table tr:hover {
  background: #f9fafb;
}

/* Utility classes */
.text-center { text-align: center; }
.text-muted { color: #6b7280; }
.mt-4 { margin-top: 24px; }
.mb-4 { margin-bottom: 24px; }
.p-4 { padding: 24px; }
.flex { display: flex; }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.gap-4 { gap: 16px; }
</style>
