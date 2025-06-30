<template>
  <div class="page-container">
    <!-- ===== Navbar ===== -->
    <nav class="navbar">
      <div class="navbar-left"><h1 class="logo">Wakeb Maps</h1></div>

      <div class="navbar-right">
        <span v-if="showNavbarSuccess" class="status-message">Success! ✓</span>

        <!-- Login / Logout toggle -->
        <button
          v-if="isLoggedIn"
          class="logout-button"
          @click="logout"
        >
          <i class="fas fa-sign-out-alt"></i> Logout
        </button>
        <button
          v-else
          class="logout-button"
          @click="showLogin = true"
        >
          <i class="fas fa-user"></i> Login
        </button>
      </div>
    </nav>

    <!-- ===== Main content ===== -->
    <div class="content-container" :class="{ 'with-login': showLogin }">
      <!-- Side Login panel -->
      <LoginPanel
        v-if="showLogin"
        class="login-panel"
        @close="showLogin = false"
        @success="handleLoginSuccess"
      />

      <!-- Map & overlay -->
      <div class="map-and-overlay">
        <div id="map" class="map">
          <div id="custom-layer-control" class="custom-layer-control"></div>
        </div>

        <!-- Search overlay -->
        <div class="search-overlay">
          <MapFeatures
            @toggleSearchResults="toggleSearchResults"
            @getGeolocation="getGeolocation"
            @plotResult="plotResult"
            @removeResult="removeResult"
            :coords="coords"
            :fetchCoords="fetchCoords"
            :searchResults="searchResults"
          />
        </div>
      </div>
    </div>

    <!-- Geolocation error modal -->
    <GeoErrorModal
      v-if="geoError"
      :geoErrorMsg="geoErrorMsg"
      @closeGeoError="closeGeoError"
    />
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import leaflet from 'leaflet';
import GeoErrorModal from '@/components/GeoErrorModal.vue';
import MapFeatures from '@/components/MapFeatures.vue';
import LoginPanel from '@/components/LoginPanel.vue';

export default {
  name: 'HomeView',
  components: { GeoErrorModal, MapFeatures, LoginPanel },
  setup() {
    /* ---------- reactive state ---------- */
    const showLogin        = ref(false);
    const showNavbarSuccess = ref(false);
    const isLoggedIn        = ref(false); // toggles login / logout button

    const coords        = ref(null);
    const fetchCoords   = ref(null);
    const geoMarker     = ref(null);
    const geoError      = ref(null);
    const geoErrorMsg   = ref(null);
    const resultMarker  = ref(null);
    const searchResults = ref(null);

    let map; // Leaflet map instance

    /* ---------- geolocation ---------- */
    const getGeolocation = () => {
      if (coords.value) {                          // clear marker
        coords.value = null;
        sessionStorage.removeItem('coords');
        map.removeLayer(geoMarker.value);
        return;
      }
      if (sessionStorage.getItem('coords')) {      // cached
        coords.value = JSON.parse(sessionStorage.getItem('coords'));
        plotGeolocation(coords.value);
        return;
      }
      fetchCoords.value = true;
      navigator.geolocation.getCurrentPosition(setCoords, getLocError);
    };

    const setCoords = (pos) => {
      fetchCoords.value = null;
      const setSessionCoords = {
        lat : pos.coords.latitude,
        lng : pos.coords.longitude
      };
      sessionStorage.setItem('coords', JSON.stringify(setSessionCoords));
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
        iconUrl : require('@/assets/map-marker-red.svg'),
        iconSize: [35, 35]
      });
      geoMarker.value = leaflet
        .marker([coords.lat, coords.lng], { icon: customMarker })
        .addTo(map);
      map.setView([coords.lat, coords.lng], 13);
    };

    const closeGeoError = () => {
      geoError.value = null;
      geoErrorMsg.value = null;
    };

    /* ---------- search result plotting ---------- */
    const plotResult = (coords) => {
      if (resultMarker.value) map.removeLayer(resultMarker.value);

      const customMarker = leaflet.icon({
        iconUrl : require('@/assets/map-marker-blue.svg'),
        iconSize: [35, 35]
      });
      resultMarker.value = leaflet
        .marker([coords.coordinates[1], coords.coordinates[0]], { icon: customMarker })
        .addTo(map);

      map.setView([coords.coordinates[1], coords.coordinates[0]], 14);
      closeSearchResults();
    };

    const toggleSearchResults = () => {
      searchResults.value = !searchResults.value;
    };
    const closeSearchResults = () => (searchResults.value = null);
    const removeResult       = () => resultMarker.value && map.removeLayer(resultMarker.value);

    /* ---------- auth UI handlers ---------- */
    const handleLoginSuccess = () => {
      showLogin.value       = false;
      showNavbarSuccess.value = true;
      isLoggedIn.value      = true;
      setTimeout(() => (showNavbarSuccess.value = false), 4000);
    };

    const logout = () => {
      isLoggedIn.value = false;
      sessionStorage.removeItem('coords');
      coords.value = null;
      if (geoMarker.value) map.removeLayer(geoMarker.value);
    };

    /* ---------- Leaflet initialisation ---------- */
    watch(showLogin, () => {
      if (map) {
        setTimeout(() => map.invalidateSize(), 350);
      }
    });

    onMounted(() => {
const WORLD_BOUNDS = leaflet.latLngBounds([-90, -180], [90, 180]);
map = leaflet.map('map', {
  center: [24.7136, 46.6753], 
  zoom:   11,               
  minZoom: 5,
  maxZoom: 16,
  maxBounds: WORLD_BOUNDS,
  maxBoundsViscosity: 1.0,      // 1 = صلب، 0.0-0.99 = “مطّاط”
  inertia: false,               // يوقف الدفع بعد الإفلات
  worldCopyJump: false             
});

      /* --- base layers --- */
      const openStreetMap = leaflet.tileLayer(
        'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
        { attribution: '&copy; OpenStreetMap contributors' }
      );

      const esriImagery = leaflet.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
        { attribution: 'Tiles © Esri' }
      );

      const cartoLight = leaflet.tileLayer(
        'https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png',
        { attribution: '&copy; CartoDB' }
      );

      const cartoDark = leaflet.tileLayer(
        'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png',
        { attribution: '&copy; CartoDB' }
      );

      const openTopo = leaflet.tileLayer(
        'https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png',
        { attribution: 'Map data © OpenTopoMap contributors' }
      );

      const mapboxStreets = leaflet
        .tileLayer(
          `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${process.env.VUE_APP_API_KEY}`,
          {
            id: 'mapbox/streets-v11',
            tileSize: 512,
            zoomOffset: -1,
            attribution: 'Mapbox © OpenStreetMap'
          }
        )
        .addTo(map); // default layer

      const baseMaps = {
        OpenStreetMap            : openStreetMap,
        'Esri Imagery (Satellite)': esriImagery,
        'CartoDB Positron'       : cartoLight,
        'CartoDB Dark'           : cartoDark,
        OpenTopoMap              : openTopo,
        'Mapbox Streets'         : mapboxStreets
      };

      leaflet.control.layers(baseMaps).addTo(map);

      /* move control to custom container */
      map.whenReady(() => {
        const ctl = document.querySelector('.leaflet-control-layers');
        const dest = document.getElementById('custom-layer-control');
        if (ctl && dest) dest.appendChild(ctl);
      });

      map.on('moveend', closeSearchResults);
      getGeolocation(); // auto-locate on first load (optional)
    });

    /* ---------- exposed to template ---------- */
    return {
      showLogin,
      showNavbarSuccess,
      isLoggedIn,
      logout,
      coords,
      fetchCoords,
      geoError,
      geoErrorMsg,
      searchResults,
      /* methods */
      getGeolocation,
      plotResult,
      toggleSearchResults,
      closeSearchResults,
      removeResult,
      closeGeoError,
      handleLoginSuccess
    };
  }
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

.content-container {
  display: flex;
  flex-direction: row;
  height: 100%;
}

.login-panel {
  width: 320px;
  height: 100%;
  background: #1e4d41;
  color: white;
  z-index: 600;
  transition: all 0.3s ease;
}

.map-and-overlay {
  flex: 1;
  position: relative;
}

.map {
  width: 100%;
  height: 100%;
}

.search-overlay {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 500;
}

.custom-layer-control {
  position: absolute;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
  transform: scale(1.1);
}
</style>
