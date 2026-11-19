<template>
  <div class="welcome-overlay" :class="{ 'copy-ar': isArabic }" role="dialog" aria-modal="true" aria-labelledby="welcome-title" @keyup.esc="$emit('start')" tabindex="0">
    <div class="welcome-card">
      <h1 id="welcome-title">{{ text.heading }}</h1>
      <a class="language-switch" :href="otherLanguageURL" :lang="isArabic ? 'en' : 'ar'">{{ text.switchLanguage }}</a>
      <p class="en lead">{{ text.lead }}</p>
      <hr class="divider" />
      <p class="en">{{ text.description }}</p>
      <ul class="public-features"><li v-for="item in text.features" :key="item">{{ item }}</li></ul>
      <nav class="project-links" :aria-label="text.projects">
        <a href="https://www.cloudbase.website/">{{ text.cloud }}</a>
        <a :href="isArabic ? 'https://mawrid.cloudbase.website/ar/' : 'https://mawrid.cloudbase.website/'">{{ text.mawrid }}</a>
      </nav>
    </div>
    <button type="button" class="start-btn" @click="$emit('start')">{{ text.start }}</button>
  </div>
</template>
<script setup>
import { text, isArabic, otherLanguageURL } from '@/public-site/locale';
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

.copy-ar .en { direction: rtl; text-align: right; }
.copy-ar h1 { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; letter-spacing: normal; }
.copy-ar .en.lead { text-align: center; }
.language-switch { display: inline-block; margin-bottom: 1rem; color: #fff; text-decoration: underline; }
.public-features { text-align: start; line-height: 1.8; padding-inline-start: 1.25rem; margin-block: 1rem; color: #f1f5f9; }
.project-links { display: grid; gap: 0.5rem; margin-top: 1rem; text-align: start; }
.project-links a { color: #d1fae5; text-decoration: underline; line-height: 1.7; }
</style>
