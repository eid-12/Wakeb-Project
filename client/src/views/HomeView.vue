<template>

<div class="page-container" >
  <!-- Navbar -->  
    <nav class="navbar" aria-label="Main">
      <div class="navbar-left">
        <h1 class="logo">{{ t('app.title') }}</h1>
      </div>
      <div class="navbar-right">
        <span v-if="showNavbarSuccess" class="status-message">{{ t('nav.success') }}</span>
          
          <button v-if="isLoggedIn" type="button" class="user-flag" @click="selectedMenu = 'settings'">
          <i class="fa-solid fa-user" aria-hidden="true"></i>
          <span class="user-flag__name">{{ user?.name ?? user?.username }}</span>
          </button>

        <button id="Logout" v-if="isLoggedIn" type="button" class="logout-button" @click="logout">
          <i class="fas fa-sign-out-alt" aria-hidden="true"></i> {{ t('nav.logout') }}
        </button>
        <button v-else type="button" class="logout-button" @click="showLogin = true">
          <i class="fas fa-user" aria-hidden="true"></i> {{ t('nav.login') }}
        </button>
      </div>
    </nav>

<transition name="fade" @after-leave="fixMapSize">
  <WelcomeOverlay v-if="showWelcome" @start="hideWelcome " />
</transition>
    <div class="content-container" :class="{ 'with-login': showLogin }">

      <!-- Sliding login panel -->
  <LoginPanel
    v-if="showLogin"
    class="login-panel"
    @close="showLogin = false"
    @success="handleLoginSuccess"
    ref="loginPanelRef"
  />

      <!-- Sidebar (only when logged in) -->
      <div v-if="isLoggedIn" class="side-dashboard">
        <ul class="dashboard-menu">
          <li :class="{ active: selectedMenu === 'map' }" @click="selectedMenu = 'map'">{{ t('menu.map') }}</li>

          <li :class="{ active: selectedMenu === 'saved' }" @click="selectedMenu = 'saved'">{{ t('menu.saved') }}</li>

          <li :class="{ active: selectedMenu === 'history' }" @click="selectedMenu = 'history'">{{ t('menu.history') }}</li>

          <li :class="{ active: selectedMenu === 'add' }" @click="selectedMenu = 'add'">{{ t('menu.add') }}</li>

          <li :class="{ active: selectedMenu === 'settings' }" @click="selectedMenu = 'settings'">{{ t('menu.settings') }}</li>
          <li v-show="isProfileAdmin(user)" :class="{ active: selectedMenu === 'admin' }" @click="selectedMenu = 'admin'">{{ t('menu.admin') }}</li>          
        </ul>
      </div>

      <!-- Main area -->
      <div class="map-and-overlay">
        <!-- Leaflet map -->
        <div v-show="selectedMenu === 'map'" id="map" class="map" >
          <div id="custom-layer-control" class="custom-layer-control"></div>
        </div>

          <AddPlaceForm
          v-if="selectedMenu === 'add'"
            @view="flyTo"
            />   

            <SavedPlacesPanel
   v-if="selectedMenu === 'saved'"
    @view="flyTo"
    />
    <SettingsPanel
       v-if="selectedMenu === 'settings'"
  :selected-menu="selectedMenu"
  @change-menu="selectedMenu = $event"
  @logout = "logout"
/>
    <AdminPanel
       v-if="selectedMenu === 'admin'"
/>
<SearchHistoryPanel   v-if= "selectedMenu ==='history'" />

        <!-- Map features overlay -->
        <div v-if="selectedMenu === 'map'" class="search-overlay">
          <MapFeatures
            @toggleSearchResults="toggleSearchResults"
            @saveHistory="onSaveHistory"
            @getGeolocation="getGeolocation"
            @plotResult="plotResult"
            @removeResult="removeResult"
            @addRoute="addRoute"
            :coords="coords"
            :fetchCoords="fetchCoords"
            :searchResults="searchResults"
                ref="removeResultsRef"

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

<script setup>
/*************************
 * Imports
 *************************/
import { ref, onMounted, watch, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import leaflet from 'leaflet';
import 'leaflet-routing-machine';
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';
import { searchpo, addToFavorite, clearSearchHistory } from '@/api/user';
import { appSettings } from '@/composables/useAppSettings';
import { MAPBOX_ACCESS_TOKEN, MAPBOX_GEOCODING_BASE } from '@/config/env';



/* Shared components */
import GeoErrorModal      from '@/components/GeoErrorModal.vue';
import MapFeatures        from '@/components/MapFeatures.vue';
import LoginPanel         from '@/components/LoginPanel.vue';
/* New side-panel components */
import SettingsPanel      from '@/components/SettingsPanel.vue';
import AddPlaceForm       from '@/components/AddPlaceForm.vue';
import SavedPlacesPanel   from '@/components/SavedPlacesPanel.vue';
import SearchHistoryPanel from '@/components/SearchHistoryPanel.vue';
import AdminPanel from '@/components/AdminPanel.vue';
import WelcomeOverlay from '@/components/WelcomeOverlay.vue'
import { useUser, getUser, deleteTokenCookie, isProfileAdmin } from '@/api/user';
import { useAlerts } from '@/composables/useAlerts';
/*************************
 * Reactive state
 *************************/
const showLogin          = ref(false);
const showNavbarSuccess  = ref(false);
const isLoggedIn         = ref(false);
const selectedMenu       = ref('map');
const savedPlaces = ref(JSON.parse(localStorage.getItem('savedPlaces') || '[]'));
const latestQuery = ref("");
const showWelcome = ref(true)
/* Titles could still be useful somewhere else */
const { t } = useI18n();
const { user, setUser, clearUser } = useUser();
const { showAlert } = useAlerts();

const LAST_HISTORY_AUTO_CLEAR = 'wakeb.history.lastAutoClear';
const WEEK_MS = 7 * 24 * 60 * 60 * 1000;

async function maybeAutoClearSearchHistory() {
  if (!isLoggedIn.value || !appSettings.autoDeleteHistory) return;
  const now = Date.now();
  const last = Number(localStorage.getItem(LAST_HISTORY_AUTO_CLEAR) || '0');
  if (now - last < WEEK_MS) return;
  try {
    await clearSearchHistory();
    localStorage.setItem(LAST_HISTORY_AUTO_CLEAR, String(now));
  } catch (e) {
    console.warn('Auto-clear search history failed:', e);
  }
}
const loginPanelRef = ref(null)
const removeResultsRef = ref(null)


/*************************
 * Map-related refs
 *************************/
const coords         = ref(null);
const fetchCoords    = ref(null);
const geoMarker      = ref(null);
const geoError       = ref(null);
const geoErrorMsg    = ref(null);
const resultMarker   = ref(null);
const routeControl   = ref(null);
const searchResults  = ref(null);
const selectedPlace = ref(null);
let dest  = "";

let map;         
let mapInitialized = false;
let placeName = [];

/*************************
 * Helper functions
 *************************/
function getGeolocation() {
  if (!appSettings.locationTracking) {
    showAlert({
      type: 'info',
      title: t('settings.privacy'),
      message: t('geo.enableInSettings'),
    });
    return;
  }

  if (coords.value) {
    removeGeolocation();
    removeRoute();
    return;
  }

  if (sessionStorage.getItem('coords')) {
    coords.value = JSON.parse(sessionStorage.getItem('coords'));
    plotGeolocation(coords.value);
    return;
  }

  fetchCoords.value = true;
  navigator.geolocation.getCurrentPosition(setCoords, getLocError);
}

function setCoords(pos) {
  fetchCoords.value = null;
  const sessionCoords = { lat: pos.coords.latitude, lng: pos.coords.longitude };
  sessionStorage.setItem('coords', JSON.stringify(sessionCoords));
  coords.value = sessionCoords;
  plotGeolocation(coords.value);
}

function getLocError(err) {
  fetchCoords.value = null;
  geoError.value    = true;
  geoErrorMsg.value = err.message;
}

function plotGeolocation(coordObj) {
  const markerIcon = leaflet.icon({
    iconUrl : require('@/assets/map-marker-red.svg'),
    iconSize: [35, 35]
  });
  geoMarker.value = leaflet.marker([coordObj.lat, coordObj.lng], { icon: markerIcon }).addTo(map);
  map.setView([coordObj.lat, coordObj.lng], 13);
}

function removeGeolocation() {
  coords.value = null;
  sessionStorage.removeItem('coords');
  geoMarker.value?.setOpacity(0);
}

function plotResult(resultCoords) {
  if (resultMarker.value) map.removeLayer(resultMarker.value);
  removeRoute();

   dest = { lat: resultCoords.coordinates[1], lng: resultCoords.coordinates[0] };
  const markerIcon = leaflet.icon({
    iconUrl : require('@/assets/map-marker-blue.svg'),
    iconSize: [35, 35]
  });

  resultMarker.value = leaflet.marker([dest.lat, dest.lng], { icon: markerIcon }).addTo(map);
  map.setView([dest.lat, dest.lng], 14);
  closeSearchResults();
}

function addRoute() { 
  if (coords.value) {
     setTimeout(() => {
  removeRoute();
  let start =coords.value;
  let end= dest;
    routeControl.value = leaflet.Routing.control({
      waypoints         : [leaflet.latLng(start.lat, start.lng), leaflet.latLng(end.lat, end.lng)],
      lineOptions       : { styles: [{ color: 'green',weight: 4 }] },
      createMarker      : () => null,
      addWaypoints      : false,
      draggableWaypoints: false,
      fitSelectedRoutes : true
    }).addTo(map);
    }, 500);
    }  
    else{
    showAlert({ type: 'warning', title: 'Location Service Disabled ', message: 'Please enable location (GPS) on your device to display the route.' });
    }
}

function removeRoute() {
  if (routeControl.value) {
    map.removeControl(routeControl.value);
    routeControl.value = null;
  }
}

function toggleSearchResults() {
  searchResults.value = !searchResults.value;
}

function closeSearchResults() {
  searchResults.value = null;
}
function onSaveHistory(q) {
latestQuery.value = q
}

watch(latestQuery, async q => {
  if (!q) return
  try {
    await searchpo(q) ;
  } catch (err) {
    console.error('Failed to save history:', err)
  }
})

function removeResult() {
  if (resultMarker.value) {
    resultMarker.value?.setOpacity(0);
     resultMarker.value = null;
  }
  removeRoute();
}

function closeGeoError() {
  geoError.value    = null;
  geoErrorMsg.value = null;
}

async function handleLoginSuccess() {
   clearUser();
  showLogin.value = false;
  showNavbarSuccess.value = true;
  isLoggedIn.value = true;
setTimeout(() => { showNavbarSuccess.value = false }, 4000);
 const u = await getUser();
  setUser(u);
  maybeAutoClearSearchHistory();
}

function logout() {
  deleteTokenCookie() ;
  clearUser();
  loginPanelRef.value?.logoutUser?.()
removeResultsRef.value?.removeResults?.()

  isLoggedIn.value = false;
  window.location.href = '/';

removeRoute();
  removeGeolocation();
  coords.value = null;
  searchResults.value = null;
  resultMarker.value?.remove?.();
  resultMarker.value = null;
}

function hideWelcome () {
  showWelcome.value = false
}
function fixMapSize () {
  nextTick(() => {
    if (map) {
      map.invalidateSize(true)
      map.setView(map.getCenter(), map.getZoom())
    }
  })
}

watch(showLogin, () => {
  if (map) setTimeout(() => map.invalidateSize(), 350);
});

watch(selectedMenu, (val) => {
  if (val === 'map') {
    setTimeout(() => map?.invalidateSize(), 300);
    
  }
});

watch(isLoggedIn, (v) => {
  if (v) maybeAutoClearSearchHistory();
});

function flyTo (place) {
  selectedMenu.value = 'map';

  const lat = Number(place.lat  ?? place.latitude);
  const lng = Number(place.lng  ?? place.longitude);

  if (isNaN(lat) || isNaN(lng)) {
    console.error('Missing or invalid coordinates', place);
    return;
  }

  map.flyTo([lat, lng], 16);

  leaflet.popup()
    .setLatLng([lat, lng])
    .setContent(`<strong>${place.title?? place.description}</strong>`)
    .openOn(map);


}

async function addToFavorites(place) {
  const payload = {
    title:       placeName[0] || `Point  ${place.lat.toFixed(4)}, ${place.lng.toFixed(4)}`,
    latitude:    place.lat,
    longitude:   place.lng
  };

  try {
    const { data } = await addToFavorite(payload)
    savedPlaces.value.push(data);
    showAlert({ type: 'success', title: 'Saved ', message: ' Added to favorites ⭐' });
    if (
      appSettings.savedPlacesNotifications &&
      typeof Notification !== 'undefined' &&
      Notification.permission === 'granted'
    ) {
      try {
        new Notification('Wakeb Maps', { body: payload.title });
      } catch {
        /* ignore */
      }
    }
  } catch (err) {
    console.error('Failed to save place:', err);
    showAlert({ type: 'danger', title: ' Save Failed', message: ' Could not add to favorites ❌' });
  }
}

onMounted(async () => {
  try {
    const u = await getUser();
    setUser(u);
    isLoggedIn.value = true;
  } catch (e) {
    if (e.response?.status === 401) {
      clearUser();
      isLoggedIn.value = false;
    }
  }

  if (mapInitialized) return;
  const WORLD_BOUNDS = leaflet.latLngBounds([-90, -180], [90, 180]);
  map = leaflet.map('map', {
    center          : [24.7136, 46.6753],
    zoom            : 11,
     minZoom         : 5,
     maxZoom         : 16,
    maxBounds       : WORLD_BOUNDS,
    maxBoundsViscosity: 1,
    inertia         : false,
    worldCopyJump   : false
  });

  /***** base layers *****/
  const openStreetMap = leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  });
  const esriImagery = leaflet.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
    attribution: 'Tiles © Esri'
  });
  const cartoLight = leaflet.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; CartoDB'
  });
  const cartoDark = leaflet.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; CartoDB'
  });
  const openTopo = leaflet.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data © OpenTopoMap contributors'
  });
  const layerMap = {
    OpenStreetMap: openStreetMap,
    'Esri Imagery (Satellite)': esriImagery,
    'CartoDB Positron': cartoLight,
    'CartoDB Dark': cartoDark,
    'OpenTopoMap': openTopo,
  };

  if (MAPBOX_ACCESS_TOKEN) {
    layerMap['Mapbox Streets'] = leaflet.tileLayer(
      `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${MAPBOX_ACCESS_TOKEN}`,
      {
        id: 'mapbox/streets-v11',
        tileSize: 512,
        zoomOffset: -1,
        attribution: 'Mapbox © OpenStreetMap',
      }
    );
  }

  openStreetMap.addTo(map); // default

  leaflet.control.layers(layerMap).addTo(map);

  map.whenReady(() => {
    removeRoute();
    const ctl  = document.querySelector('.leaflet-control-layers');
    const dest = document.getElementById('custom-layer-control');
    if (ctl && dest) dest.appendChild(ctl);
  });
let lat, lng;
map.on('click', async (e) => {
  
   ({ lat, lng } = e.latlng);

  try {
    if (!MAPBOX_ACCESS_TOKEN) {
      placeName = [];
    } else {
    const geocodeUrl = `${MAPBOX_GEOCODING_BASE}/${lng},${lat}.json?access_token=${encodeURIComponent(
      MAPBOX_ACCESS_TOKEN
    )}&language=ar`;
    const resp = await fetch(geocodeUrl);
    const data = await resp.json();
    placeName = data.features?.map(f => f.place_name) ?? [];
    }
  } catch (err) {
    console.warn('Reverse-geocoding failed:', err);
          selectedPlace.value = { lat, lng };

  }
  const canSave = isLoggedIn.value;
  const btnId = `save-btn-${Date.now()}-${Math.random().toString(36).slice(2,8)}`;
const content = `
  <div style="text-align:right">
    <h3 style="margin:0 0 6px 0;font-size:1.1rem;color:#111;">${placeName[0]}</h3>
    <p style="margin:0 0 6px 0;color:#555;">${placeName[1]}</p>
    <p style="margin:0 0 6px 0;font-size:0.85rem;color:#666;">
      Location: ${lat.toFixed(5)}, ${lng.toFixed(5)}
    </p>
      ${
        canSave
          ? `<button id="${btnId}" type="button" style="margin-top:6px;padding:6px 14px;border-radius:6px;background:#059669;z-index:300;position:relative;color:#fff;font-size:0.9rem ">
               ⭐ Add to Saved Places
             </button>`
          : ``
      }
  </div>
`;
  const container = document.createElement('div');
  container.innerHTML = content;

  const btn = container.querySelector(`#${CSS.escape(btnId)}`);
  if (btn) {
    leaflet.DomEvent.disableClickPropagation(container);
    leaflet.DomEvent.disableScrollPropagation(container);
    
btn.addEventListener('click', (evt) => {
      leaflet.DomEvent.stop(evt); // preventDefault + stopPropagation
      addToFavorites({ lat, lng });
      map.closePopup();
    }, { once: true });
  }

  leaflet.popup({ closeButton: true, offset: [0, -10], autoPan: true })
    .setLatLng(e.latlng)
    .setContent(container)  
    .openOn(map);
});

  map.on('moveend', closeSearchResults);
  if (appSettings.locationTracking) {
    getGeolocation();
  }
  mapInitialized = true;


  
});

</script>

<style scoped>
/*************************
 * Layout & chrome
 *************************/
.page-container {
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  position: relative;
  user-select: none;
}

.navbar {
  background-color: #1e4d41;
  color: #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  flex-shrink: 0;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.08);
}

.logo {
  font-size: clamp(1.05rem, 2.5vw, 1.25rem);
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.status-message {
  font-size: 0.875rem;
  color: #a7f3d0;
  font-weight: 500;
}

.logout-button {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 0.875rem;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.35rem 0.5rem;
  border-radius: 8px;
  transition: background-color 0.2s, color 0.2s, transform 0.2s;
}

.logout-button:hover {
  background: rgba(255, 255, 255, 0.08);
}

.logout-button:focus-visible {
  outline: 2px solid #a7f3d0;
  outline-offset: 2px;
}

.logout-button i {
  font-size: 1rem;
}

#Logout:hover {
  color: #fecaca;
}

.content-container {
  display: flex;
  flex-direction: row;
  flex: 1;
  min-height: 0;
}

.login-panel {
  width: 320px;
  max-width: 100%;
  flex-shrink: 0;
  height: 100%;
  background: #1e4d41;
  color: white;
  z-index: 600;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.12);
}

.side-dashboard {
  width: 220px;
  flex-shrink: 0;
  background-color: #083c35;
  color: #fff;
  padding: 0.75rem 0.65rem;
  overflow: hidden;
}

.dashboard-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dashboard-menu li {
  padding: 0.65rem 0.85rem;
  margin-bottom: 0.35rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 0.9rem;
  line-height: 1.3;
}

.dashboard-menu li:hover {
  background-color: #115e54;
}

.dashboard-menu li.active {
  background-color: #0d6b5e;
  font-weight: 600;
}

.map-and-overlay {
  height: 100%;
  flex: 1;
  position: relative;
  min-width: 0;
}

#map,
.map {
  width: 100%;
  height: 100%;
}

.placeholder-page {
  width: 100%;
  height: 100%;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #1e4d41;
  font-weight: bold;
}

.search-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  z-index: 500;
  pointer-events: none;
}

.search-overlay > * {
  pointer-events: auto;
}

.custom-layer-control {
  position: absolute;
  bottom: 24px;
  right: 12px;
  z-index: 1000;
}

@media (min-width: 768px) {
  .search-overlay {
    top: 16px;
    left: 16px;
    right: auto;
  }

  .custom-layer-control {
    bottom: 30px;
    right: 24px;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.35s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.user-flag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.35rem 0.75rem;
  border-radius: 8px;
  border: 1px solid rgba(167, 243, 208, 0.35);
  color: #ecfdf5;
  background: rgba(255, 255, 255, 0.06);
  font-size: 0.875rem;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
  max-width: 160px;
  min-width: 0;
}

.user-flag__name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.user-flag:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(167, 243, 208, 0.55);
}

.user-flag:focus-visible {
  outline: 2px solid #a7f3d0;
  outline-offset: 2px;
}

.user-flag i {
  font-size: 0.875rem;
  flex-shrink: 0;
}

@media (min-width: 480px) {
  .user-flag {
    max-width: 220px;
  }
}

.leaflet-popup-content,
.leaflet-popup-content * {
  pointer-events: auto;
}

@media (max-width: 767px) {
  .content-container {
    flex-direction: column;
  }

  .side-dashboard {
    width: 100%;
    order: 1;
    padding: 0.5rem 0.35rem;
  }

  .dashboard-menu {
    display: flex;
    flex-direction: row;
    gap: 0.35rem;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    padding-bottom: 0.25rem;
    scrollbar-width: thin;
  }

  .dashboard-menu li {
    flex: 0 0 auto;
    margin-bottom: 0;
    white-space: nowrap;
    padding: 0.55rem 0.75rem;
    font-size: 0.8125rem;
  }

  .map-and-overlay {
    order: 2;
    flex: 1;
    min-height: 280px;
  }

  .content-container.with-login .login-panel {
    position: fixed;
    inset: 0;
    width: 100%;
    height: 100%;
    z-index: 650;
  }
}

</style>  