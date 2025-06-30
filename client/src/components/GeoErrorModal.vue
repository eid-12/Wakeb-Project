<template>
  <div class="geo-modal-overlay">
    <div class="geo-modal-content">
      <h1 class="geo-modal-title">Error: {{ geoErrorMsg }}</h1>
      <p class="geo-modal-message">
        To take advantage of this application's features, please ensure location services
        are enabled.
      </p>
      <button @click="$emit('closeGeoError')" class="geo-modal-button">
        Close
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
  position: absolute;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100%;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 125px;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100000;
}

.geo-modal-content {
  display: flex;
  flex-direction: column;
  background-color: white;
  width: 80%;
  max-width: 450px;
  padding: 1.5rem;
  border-radius: 0.5rem;
}

.geo-modal-title {
  font-size: 1.125rem;
  margin-bottom: 0.25rem;
}

.geo-modal-message {
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

.geo-modal-button {
  align-self: flex-start;
  padding: 0.5rem 1rem;
  background-color: #ef4444; /* Red-500 */
  color: white;
  font-size: 0.875rem;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
}
</style>
