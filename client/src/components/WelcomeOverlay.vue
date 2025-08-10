<!-- src/components/WelcomeOverlay.vue -->
<template>
  <div class="welcome-overlay" @keyup.esc="$emit('start')" tabindex="0">
    <div class="welcome-card">
      <h1>Welcome to Wakeb Maps</h1>

      <p class="en">Begin your journey by searching✨</p>

      <hr class="divider" />

      <p class="en">
        During my summer training at <strong>Wakeb Data</strong> in Riyadh, I worked on
        <strong>Wakeb Maps</strong>, an interactive web mapping system that combines a Vue.js + Leaflet
        front end with Spring Boot microservices to process data and expose REST APIs. We secured the
        platform with JWT and Spring Security for role- and permission-based access. The stack was containerized with Docker for
        easier deployment, and Postman was used to validate integration across services.
      </p>

    </div>

    <button class="start-btn" @click="$emit('start')">
      Start
    </button>
  </div>
</template>

<script setup>
// لا شيء هنا حالياً
</script>

<style scoped>
.welcome-overlay{
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.35);
  backdrop-filter: blur(4px);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  animation: fadeIn .35s ease;
  outline: none;
}

.welcome-card{
  /* background: rgba(255,255,255,.88); */
    background: #1e4d41;
width: min(100vw, 750px); 
  padding: 2.2rem 2.6rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,.18);
  text-align: center;
  /* max-width: 520px;
  max-height: 70vh;              لو النص طويل */
  overflow-y: auto;
  scrollbar-width: thin;
}
.welcome-card::-webkit-scrollbar{
  width: 6px;
}
.welcome-card::-webkit-scrollbar-thumb{
  background: rgba(0,0,0,.25);
  border-radius: 3px;
}

.welcome-card h1{
  margin: 0 0 .9rem;
  font-size: 2rem;
  color: #fff;
}

.en{
  margin: 0;
  font-size: .95rem;
  line-height: 1.6;
  color: #fff;
  text-align: left;
  direction: ltr;
  font-size: 1.05rem;
  line-height: 1.9;
}
.divider{
  border: none;
  height: 1px;
  background: rgba(0,0,0,.08);
  margin: 1.1rem 0;
}

.start-btn{
  position: absolute;
  bottom: 48px;
  left: 50%;
  transform: translateX(-50%);
  padding: 14px 42px;
  font-size: 18px;
  font-weight: 600;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  background: #1e4d41;
  color: #fff;
  box-shadow: 0 6px 16px rgba(0,0,0,.2);
  transition: opacity .2s, transform .2s;
}
.start-btn:hover{
  opacity: .9;
  transform: translateX(-50%) scale(1.03);
}

@keyframes fadeIn{
  from{opacity:0}
  to{opacity:1}
}

@media (max-width:480px){
  .welcome-card{ padding:1.6rem 1.8rem; max-width: 90vw; }
  .welcome-card h1{ font-size:1.4rem; }
  .ar{ font-size:.95rem; }
  .en{ font-size:.9rem; }
  .start-btn{ bottom:40px; padding:12px 36px; font-size:.95rem; }
}
</style>
