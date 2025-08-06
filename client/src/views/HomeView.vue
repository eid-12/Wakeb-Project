<template>

<div class="page-container" >
     <AlertsContainer />

  <!-- Navbar -->  
    <nav class="navbar">
      <div class="navbar-left"><h1 class="logo">Wakeb Maps</h1></div>
      <div class="navbar-right">
        <span v-if="showNavbarSuccess" class="status-message">Success! ✓</span>
          
          <button v-if="isLoggedIn" class="user-flag" @click="selectedMenu = 'settings'">
          <i class="fa-solid fa-user"></i> {{ user?.name }}
          </button>

        <button id="Logout" v-if="isLoggedIn" class="logout-button" @click="logout">
          <i class="fas fa-sign-out-alt" ></i> Logout
        </button>
        <button v-else class="logout-button" @click="showLogin = true">
          <i class="fas fa-user"></i> Login
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
          <li :class="{ active: selectedMenu === 'map' }" @click="selectedMenu = 'map'">Map</li>

          <li   
  :class="{ active: selectedMenu === 'saved' }"     @click="selectedMenu = 'saved'">Saved Places</li>

          <li :class="{ active: selectedMenu === 'history' }" @click="selectedMenu= 'history'">Search History</li>

          <li     :class="{ active: selectedMenu === 'add' }" @click="selectedMenu = 'add'"   >Add Unlisted Place</li>

          <li :class="{ active: selectedMenu === 'settings' }" @click="selectedMenu = 'settings'">Settings</li>
          <li v-show="!user?.isUser"      :class="{ active: selectedMenu === 'admin' }" @click="selectedMenu = 'admin'">Administration </li>          
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
import { ref, onMounted, watch , nextTick} from 'vue';
import leaflet from 'leaflet';
import 'leaflet-routing-machine';
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';
import { searchpo ,addToFavorite } from '@/api/user';
import AlertsContainer from '@/components/AlertsContainer.vue';



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
import { useUser ,getUser } from '@/api/user';
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
const { user, setUser , clearUser } = useUser();
const { showAlert} = useAlerts();
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
const btnId = `fav-${Date.now()}`;
let dest  = "";

let map;         
let mapInitialized = false;
let placeName = '';

/*************************
 * Helper functions
 *************************/
function getGeolocation() {
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

}

function logout() {
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
  } catch (err) {
    console.error('Failed to save place:', err);
    showAlert({ type: 'danger', title: ' Save Failed', message: ' Could not add to favorites ❌' });
  }
}

onMounted( () => {
  if (mapInitialized) return;
  const WORLD_BOUNDS = leaflet.latLngBounds([-90, -180], [90, 180]);
  map = leaflet.map('map', {
    center          : [24.7136, 46.6753],
    zoom            : 11,
    // minZoom         : 5,
    // maxZoom         : 16,
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
  const mapboxStreets = leaflet.tileLayer(`https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${process.env.VUE_APP_API_KEY}`, {
    id        : 'mapbox/streets-v11',
    tileSize  : 512,
    zoomOffset: -1,
    attribution: 'Mapbox © OpenStreetMap'
  });

  openStreetMap.addTo(map); // default

  leaflet.control.layers({
    OpenStreetMap: openStreetMap,
    'Esri Imagery (Satellite)' : esriImagery,
    'CartoDB Positron'         : cartoLight,
    'CartoDB Dark'             : cartoDark,
    'OpenTopoMap '               : openTopo,
    'Mapbox Streets'           : mapboxStreets
  }).addTo(map);

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
    const resp = await fetch(
      `${process.env.VUE_APP_MAPBOX_API}${lng},${lat}.json` +
      `?access_token=${process.env.VUE_APP_API_KEY}&language=ar`
    );
    const data = await resp.json();
 placeName = data.features?.map(f => f.place_name) ?? [];
  } catch (err) {
    console.warn('Reverse-geocoding failed:', err);
          selectedPlace.value = { lat, lng };

  }
  const canSave = isLoggedIn.value;

const content = `
  <div style="text-align:right">
    <h3 style="margin:0 0 6px 0;font-size:1.1rem;color:#111;">${placeName[0]}</h3>
    <p style="margin:0 0 6px 0;color:#555;">${placeName[1]}</p>
    <p style="margin:0 0 6px 0;font-size:0.85rem;color:#666;">
      الموقع: ${lat.toFixed(5)}, ${lng.toFixed(5)}
    </p>
      ${
        canSave
          ? `<button id="${btnId}" style="margin-top:6px;padding:6px 14px;border-radius:6px;background:#059669;z-index:6000;color:#fff;font-size:0.9rem ">
               ⭐ Add to Saved Places
             </button>`
          : ``
      }
  </div>
`;


  leaflet
    .popup({ closeButton: true, offset: [0, -10] })
    .setLatLng(e.latlng)
    .setContent(content)
    .openOn(map);
});


  map.on('popupopen', () => {
    const btn = document.getElementById(btnId);
    if (btn) {
      btn.addEventListener('click', () => {
        addToFavorites({ lat, lng });   
      });
    }
  });


  map.on('moveend', closeSearchResults);
  getGeolocation();
  mapInitialized = true;


  
});

</script>

<style scoped>
/*************************
 * Keep your existing Tailwind-like styles
 *************************/
.page-container { height: 100vh; display: flex; flex-direction: column; position: relative;   user-select: none;}
.navbar { background-color: #1e4d41; color: #f1f5f9; display: flex; justify-content: space-between; align-items: center; padding: 1rem 1.5rem; }
.logo { font-size: 1.25rem; font-weight: bold; }
.navbar-right { display: flex; align-items: center; gap: 1rem; }
.status-message { font-size: 0.9rem; color: #d1fae5; }
.logout-button { background: none; border: none; color: inherit; cursor: pointer; font-size: 0.9rem; display: flex; align-items: center; gap: 0.25rem; }
.logout-button i { font-size: 1rem; }
.logout-button:hover{scale: 1.25;}
#Logout:hover { color: #e53935; }
.content-container { display: flex; flex-direction: row; height: calc(100vh - 64px); }
.login-panel { width: 320px; height: 100%; background: #1e4d41; color: white; z-index: 600; transition: all 0.3s ease; }
.side-dashboard { width: 220px; background-color: #083c35; color: #fff; padding: 1rem; }
.dashboard-menu { list-style: none; padding: 0; margin: 0; }
.dashboard-menu li { padding: 0.75rem 1rem; margin-bottom: 0.5rem; border-radius: 8px; cursor: pointer; transition: background 0.2s; }
.dashboard-menu li:hover { background-color: #115e54; }
.dashboard-menu li.active { background-color: #0d6b5e; }
.map-and-overlay {height: 100%; flex: 1; position: relative; }
#map, .map { width: 100%; height: 100%; }
.placeholder-page { width: 100%; height: 100%; background-color: #ffffff; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; color: #1e4d41; font-weight: bold; }
.search-overlay { position: absolute; top: 16px; left: 16px; z-index: 500; }
.custom-layer-control { position: absolute; bottom: 30px; right: 30px; z-index: 1000; transform: scale(1.1); }

/* .map-blur{
  filter: blur(2px) brightness(.8);
  transition: filter .3s;
} */

/* حركة fade للترانزيشن */
.fade-enter-active,
.fade-leave-active{ transition: opacity .35s }
.fade-enter-from,
.fade-leave-to{ opacity: 0 }
.welcome-card h1{
  font-size: 32px;
  margin-bottom: .75rem;
  color:#1f2937;            /* أغمق لوضوح أعلى */
  text-shadow: 0 1px 2px rgba(0,0,0,.15);
}
.welcome-card p{
  font-size: 18px;
line-height: 1.9;
  color:#374151;
}
.user-flag{
  @apply flex items-center gap-2 px-3 py-1 rounded
         border border-emerald-600 text-emerald-700
         hover:bg-emerald-50 transition;
  background: transparent;
}
.user-flag i{ @apply text-sm; }

</style>  