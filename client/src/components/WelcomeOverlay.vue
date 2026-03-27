<!-- src/components/WelcomeOverlay.vue -->
<template>
  <div
    class="welcome-overlay"
    role="dialog"
    aria-modal="true"
    aria-labelledby="welcome-title"
    @keyup.esc="$emit('start')"
    tabindex="0"
  >
    <div class="welcome-card">
      <h1 id="welcome-title">Welcome to Wakeb Maps</h1>

      <p class="en lead">Begin your journey by searching ✨</p>

      <hr class="divider" />

      <p class="en">
        During my summer training at <strong>Wakeb Data</strong> in Riyadh, I worked on
        <strong>Wakeb Maps</strong>, an interactive web mapping system that combines a Vue.js + Leaflet
        front end with Spring Boot microservices to process data and expose REST APIs. We secured the
        platform with JWT and Spring Security for role- and permission-based access. The stack was containerized with Docker for
        easier deployment, and Postman was used to validate integration across services.
      </p>

    </div>

    <button type="button" class="start-btn" @click="$emit('start')">
      Get started
    </button>
  </div>
</template>

<script setup>
</script>

<style scoped>
.welcome-overlay {
  position: fixed;
  inset: 0;
  padding: max(1rem, env(safe-area-inset-top)) max(1rem, env(safe-area-inset-right))
    max(1rem, env(safe-area-inset-bottom)) max(1rem, env(safe-area-inset-left));
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.25rem;
  animation: fadeIn 0.35s ease;
  outline: none;
}

.welcome-card {
  background: linear-gradient(165deg, #1e5c4d 0%, #1a4339 100%);
  width: min(100%, 42rem);
  max-height: min(78vh, 36rem);
  padding: 2rem 1.75rem;
  border-radius: 16px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.06),
    0 20px 40px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
  text-align: center;
  overflow-y: auto;
  scrollbar-width: thin;
}

.welcome-card::-webkit-scrollbar {
  width: 6px;
}

.welcome-card::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 3px;
}

.welcome-card h1 {
  margin: 0 0 0.75rem;
  font-size: clamp(1.5rem, 4vw, 2rem);
  color: #fff;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.en {
  margin: 0;
  color: rgba(255, 255, 255, 0.92);
  text-align: left;
  direction: ltr;
  font-size: 0.95rem;
  line-height: 1.75;
}

.en.lead {
  text-align: center;
  font-size: 1.05rem;
  font-weight: 500;
  color: #d1fae5;
}

.en strong {
  color: #fff;
  font-weight: 600;
}

.divider {
  border: none;
  height: 1px;
  background: rgba(255, 255, 255, 0.15);
  margin: 1.25rem 0;
}

.start-btn {
  flex-shrink: 0;
  padding: 0.85rem 2.25rem;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  background: #d58b4d;
  color: #fff;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.2);
  transition:
    background 0.2s,
    transform 0.2s,
    box-shadow 0.2s;
}

.start-btn:hover {
  background: #c97d3f;
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.22);
}

.start-btn:focus-visible {
  outline: 2px solid #fde68a;
  outline-offset: 3px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 480px) {
  .welcome-card {
    padding: 1.35rem 1.25rem;
    max-height: 82vh;
  }

  .en {
    font-size: 0.875rem;
    line-height: 1.65;
  }

  .en.lead {
    font-size: 0.95rem;
  }

  .start-btn {
    width: 100%;
    max-width: 280px;
    padding: 0.75rem 1.5rem;
  }
}
</style>
