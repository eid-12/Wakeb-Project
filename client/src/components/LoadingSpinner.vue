<template>
  <div class="loading-spinner" role="status" aria-label="Loading">
    <span class="loading-spinner__ring" aria-hidden="true" />
    <span class="loading-spinner__text">Loading…</span>
  </div>
</template>

<script setup>
</script>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem 1rem;
  min-height: 4.5rem;
}

.loading-spinner__ring {
  width: 2rem;
  height: 2rem;
  border: 3px solid #e2e8f0;
  border-top-color: #0d9488;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

.loading-spinner__text {
  font-size: 0.8125rem;
  color: #64748b;
  font-weight: 500;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
