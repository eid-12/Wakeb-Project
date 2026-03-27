<template>
  <div class="geo-modal-overlay">
    <div class="geo-modal-content">
      <h1 class="geo-modal-title">Error: {{ geoErrorMsg }}</h1>
      <p class="geo-modal-message">
        To take advantage of this application's features, please ensure location services
        are enabled.
      </p>
      <button type="button" @click="$emit('closeGeoError')" class="geo-modal-button">
        OK
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: ["geoErrorMsg"],
};
</script>

<style scoped>
.geo-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1.5rem;
  padding-top: max(1.5rem, env(safe-area-inset-top));
  background-color: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.geo-modal-content {
  display: flex;
  flex-direction: column;
  @apply bg-white dark:bg-slate-800 border border-black/[0.06] dark:border-slate-600/60;
  width: 100%;
  max-width: 420px;
  padding: 1.5rem 1.35rem;
  border-radius: 12px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.06),
    0 20px 40px rgba(0, 0, 0, 0.15);
}

.geo-modal-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: #b91c1c;
  margin: 0 0 0.5rem;
  line-height: 1.35;
  word-break: break-word;
}

.geo-modal-message {
  font-size: 0.875rem;
  @apply text-slate-600 dark:text-slate-300;
  margin: 0 0 1.25rem;
  line-height: 1.55;
}

.geo-modal-button {
  align-self: flex-end;
  padding: 0.55rem 1.15rem;
  background-color: #1e4d41;
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.geo-modal-button:hover {
  background-color: #166534;
}

.geo-modal-button:focus-visible {
  outline: 2px solid #0d9488;
  outline-offset: 2px;
}
</style>
