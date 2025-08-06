<template>
  <div class="search-container">
    <!-- Search Section -->
    <div class="search-box">
      <input
        class="search-input"
        type="text"
        placeholder="Start your search..."
        v-model="searchQuery"
        @input="search"
        @focus="$emit('toggleSearchResults')"
      />
      <div class="search-icon">
        <i class="fas fa-search"></i>
      </div>

      <!-- Search Results -->
      <div class="search-results-wrapper">
        <div v-if="searchQuery && searchResults" class="search-results">
          <LoadingSpinner v-if="!searchData" />
          <div v-else>
            <div
              class="result-item"
              v-for="(result, index) in searchData"
              :key="index"
              @click="selectResult(result)"
            >
              <i class="fas fa-map-marker-alt"></i>
              <p class="result-text">
                {{ result.place_name_en }}
                <span v-if="result.distance">
                  – {{ result.distance.toFixed(1) }} km
                </span>
              </p>
            </div>
          </div>
        </div>

        <!-- Selected Result -->
        <div v-if="selectedResult" class="selected-result">
          <i @click="removeResults" class="close-icon far fa-times-circle"></i>
          <i @click="$emit('addRoute')" class="route-icon fas fa-route"></i>
          <h1 class="selected-removeResult-title">{{ selectedResult.text }}</h1>
          <p class="selected-subtext">
            {{ selectedResult.properties.address }},
            {{ selectedResult.city }},
            {{ selectedResult.state }}
          </p>
          <p class="selected-category">
            {{ selectedResult.properties.category }}
          </p>
        </div>
      </div>
    </div>

    <!-- Geolocation Button -->
    <div
      class="geolocation-btn"
      :class="{ 'geo-active': coords }"
      @click="$emit('getGeolocation')"
    >
      <i
        class="fas fa-location-arrow geo-icon"
        :class="{ 'text-white': coords, 'animate-pulse': fetchCoords }"
      ></i>
    </div>
  </div>
</template>

<script setup>
/* global defineProps, defineEmits ,defineExpose*/

import { ref, onUnmounted } from "vue";
import LoadingSpinner from "./LoadingSpinner.vue";
import { searchPlace ,searchpo } from '@/api/user.js'; 

const props = defineProps({
  fetchCoords: Boolean,
  coords: Object,
  searchResults: Boolean
});

const emit = defineEmits([
  "plotResult",
  "removeResult",
  "toggleSearchResults",
  "getGeolocation",
  "addRoute",
  "saveHistory"
]);

const searchQuery    = ref("");
const searchData     = ref(null);
const queryTimeout   = ref(null);
const selectedResult = ref(null);

function haversineDistance(c1, c2) {
  const toRad = v => (v * Math.PI) / 180;
  const R = 6371;
  const dLat = toRad(c2.lat - c1.lat);
  const dLon = toRad(c2.lng - c1.lng);
  const lat1 = toRad(c1.lat);
  const lat2 = toRad(c2.lat);

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.sin(dLon / 2) ** 2 * Math.cos(lat1) * Math.cos(lat2);

  return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

const search = () => {
  clearTimeout(queryTimeout.value);
  searchData.value = null;

  queryTimeout.value = setTimeout(async () => {
    const q = searchQuery.value.trim();
    if (!q) {
      searchData.value = null;
      return;
    }

    try {

const data = await searchPlace(q, {
  fuzzyMatch: true,
  language: "en",
  limit: 10,
  proximity: props.coords
    ? `${props.coords.lng},${props.coords.lat}`
    : "0,0",
});


      let features = data.features || [];

      if (props.coords) {
        features = features
          .map(f => {
            f.distance = haversineDistance(
              props.coords,
              { lat: f.geometry.coordinates[1], lng: f.geometry.coordinates[0] }
            );
            return f;
          })
          .sort((a, b) => a.distance - b.distance);
      }

      searchData.value = features;

      emit("saveHistory", q);

    } catch (err) {
      console.error("Search error:", err);
      searchData.value = [];
    }
  }, 750);
};

const selectResult = async (feature) => {
  if (!feature) return;

  selectedResult.value = feature;
  console.log(feature)

  try {
    await searchpo(feature.place_name);   
    emit('removeResult');
    emit('plotResult', feature.geometry);
  } catch (err) {
    console.error('search failed:', err);
  }
};


const removeResults = () => {
  selectedResult.value = null;
  emit("removeResult");
  searchQuery.value = null ;

};

onUnmounted(() => clearTimeout(queryTimeout.value));
defineExpose({
  removeResults
})
</script>






<style scoped>
.search-container {
  position: absolute;
  z-index: 2;
  display: flex;
  gap: 1rem;
  padding: 2rem 1.5rem;
  width: 100%;
  top: 0;
  left: 0;
}

@media (min-width: 768px) {
  .search-container {
    width: auto;
    top: 40px;
    left: 60px;
    padding: 0;
  }
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 100%;
}

@media (min-width: 768px) {
  .search-box {
    min-width: 350px;
  }
}

.search-input {
  padding: 0.75rem 1rem 0.75rem 2.25rem;
  font-size: 0.875rem;
  width: 100%;
  border-radius: 0.5rem;
  border: none;
  outline: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-icon {
  position: absolute;
  top: 0;
  left: 8px;
  height: 100%;
  display: flex;
  align-items: center;
}

.search-results-wrapper {
  position: absolute;
  margin-top: 0.5rem;
  width: 100%;
}

.search-results {
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border-radius: 0.5rem;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.result-item:hover {
  background-color: #475569;
  color: white;
}

.result-text {
  font-size: 0.75rem;
}

.selected-result {
  margin-top: 0.5rem;
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 0.5rem;
}

.close-icon {
  float: right;
  cursor: pointer;
  margin-bottom: 0.5rem;
  color:red;
}

.route-icon{
  cursor: pointer;
    position: absolute;
  margin-bottom: 0.25rem;
  bottom: 5px;
   right: 20px;
   transform: scale(1.5);
    color: #0d6b5e;


}

.selected-title {
  font-size: 1.125rem;
  margin-bottom: 0.25rem;
}

.selected-subtext,
.selected-category {
  font-size: 0.75rem;
  margin-bottom: 0.25rem;
}

.geolocation-btn {
  padding: 0.5rem 1rem;
  background: white;
  display: flex;
  align-items: center;
  min-height: 45px;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.geo-active {
  background-color: #0d6b5e;
}

.geo-icon {
  font-size: 18px;
  color: #0d6b5e;
}

.geo-active .geo-icon,
.text-white {
  color: white;
}

.animate-pulse {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
