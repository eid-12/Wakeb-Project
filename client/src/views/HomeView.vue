// HomeView.vue
<template>
  <div :class="['page-container', { 'login-open': showLogin }]">
    <nav class="navbar">
      <div class="navbar-left">
        <h1 class="logo">Wakeb Maps</h1>
      </div>
      <div class="navbar-right">
        <span v-if="showNavbarSuccess" class="status-message">Success! ✓</span>
        <button class="logout-button" @click="showLogin = true">
          <i class="fas fa-user"></i> Login
        </button>
      </div>
    </nav>

    <LoginPanel
      v-if="showLogin"
      @close="showLogin = false"
      @success="handleLoginSuccess"
    />

    <GeoErrorModal v-if="geoError" :geoErrorMsg="geoErrorMsg" @closeGeoError="closeGeoError" />

    <MapFeatures
      @toggleSearchResults="toggleSearchResults"
      @getGeolocation="getGeolocation"
      @plotResult="plotResult"
      @removeResult="removeResult"
      :coords="coords"
      :fetchCoords="fetchCoords"
      :searchResults="searchResults"
    />

    <div id="map" class="map"></div>
  </div>
</template>

<script type="module">
import { ref, onMounted } from "vue";
import leaflet from "leaflet";
import GeoErrorModal from "@/components/GeoErrorModal.vue";
import MapFeatures from "@/components/MapFeatures.vue";
import LoginPanel from "@/components/LoginPanel.vue";

export default {
  name: "HomeView",
  components: {
    GeoErrorModal,
    MapFeatures,
    LoginPanel,
  },
  setup() {
    const showLogin = ref(false);
    const showNavbarSuccess = ref(false);
    const coords = ref(null);
    const fetchCoords = ref(null);
    const geoMarker = ref(null);
    const geoError = ref(null);
    const geoErrorMsg = ref(null);
    const resultMarker = ref(null);
    const searchResults = ref(null);
    let map;

    const getGeolocation = () => {
      if (coords.value) {
        coords.value = null;
        sessionStorage.removeItem("coords");
        map.removeLayer(geoMarker.value);
        return;
      }
      if (sessionStorage.getItem("coords")) {
        coords.value = JSON.parse(sessionStorage.getItem("coords"));
        plotGeolocation(coords.value);
        return;
      }
      fetchCoords.value = true;
      navigator.geolocation.getCurrentPosition(setCoords, getLocError);
    };

    const setCoords = (pos) => {
      fetchCoords.value = null;
      const setSessionCoords = {
        lat: pos.coords.latitude,
        lng: pos.coords.longitude,
      };
      sessionStorage.setItem("coords", JSON.stringify(setSessionCoords));
      coords.value = setSessionCoords;
      plotGeolocation(coords.value);
    };

    const getLocError = (err) => {
      fetchCoords.value = null;
      geoError.value = true;
      geoErrorMsg.value = err.message;
    };

    const plotGeolocation = (coords) => {
      const customMarker = leaflet.icon({
        iconUrl: require("@/assets/map-marker-red.svg"),
        iconSize: [35, 35],
      });
      geoMarker.value = leaflet.marker([coords.lat, coords.lng], {
        icon: customMarker,
      }).addTo(map);
      map.setView([coords.lat, coords.lng], 13);
    };

    const closeGeoError = () => {
      geoError.value = null;
      geoErrorMsg.value = null;
    };

    const plotResult = (coords) => {
      if (resultMarker.value) map.removeLayer(resultMarker.value);
      const customMarker = leaflet.icon({
        iconUrl: require("@/assets/map-marker-blue.svg"),
        iconSize: [35, 35],
      });
      resultMarker.value = leaflet.marker([
        coords.coordinates[1],
        coords.coordinates[0],
      ], { icon: customMarker }).addTo(map);
      map.setView([coords.coordinates[1], coords.coordinates[0]], 14);
      closeSearchResults();
    };

    const toggleSearchResults = () => {
      searchResults.value = !searchResults.value;
    };

    const closeSearchResults = () => {
      searchResults.value = null;
    };

    const removeResult = () => {
      if (resultMarker.value) map.removeLayer(resultMarker.value);
    };

    const handleLoginSuccess = () => {
      showLogin.value = false;
      showNavbarSuccess.value = true;
      setTimeout(() => (showNavbarSuccess.value = false), 4000);
    };

    onMounted(() => {
      map = leaflet.map("map").setView([24.7136, 46.6753], 11);
      leaflet.tileLayer(
        `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${process.env.VUE_APP_API_KEY}`,
        {
          maxZoom: 19,
          id: "mapbox/streets-v11",
          accessToken: process.env.VUE_APP_API_KEY,
        }
      ).addTo(map);
      map.on("moveend", closeSearchResults);
      getGeolocation();
    });

    return {
      showLogin,
      showNavbarSuccess,
      coords,
      fetchCoords,
      geoMarker,
      geoError,
      geoErrorMsg,
      closeGeoError,
      getGeolocation,
      plotResult,
      searchResults,
      toggleSearchResults,
      closeSearchResults,
      removeResult,
      handleLoginSuccess,
    };
  },
};
</script>

<style scoped>
.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.navbar {
  background-color: #1e4d41;
  color: #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  font-family: sans-serif;
}

.logo {
  font-size: 1.25rem;
  font-weight: bold;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.status-message {
  font-size: 0.9rem;
  color: #d1fae5;
}

.logout-button {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.logout-button i {
  font-size: 1rem;
}

.map {
  flex-grow: 1;
  transition: margin-left 0.3s ease;
}

.login-open .map {
  margin-left: 320px;
}
</style>
