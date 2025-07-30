<template>
  <main class="py-12 px-4 flex justify-center">
    <div class="saved-panel w-full max-w-4xl">
      <h1 class="panel-title">⭐ Saved Places</h1>

      <p v-if="!places.length" class="empty-msg">No saved places yet</p>

      <ul v-else class="space-y-4">
        <li v-for="place in places" :key="place.id" class="place-card">
          <div class="truncate">
            <h2 class="place-name">{{ place.title }}</h2>
          </div>

          <div class="btn-group-horizontal">
            <button @click="$emit('view', place)" class="btn view">View on map</button>
            <button @click="removePlace(place.id)" class="btn delete">Delete</button>
          </div>
        </li>
      </ul>
    </div>
  </main>
</template>

<script setup>
/* global defineProps, defineEmits */

import { ref, onMounted, watch } from 'vue'
import { fetchSavedPlace, deleteSavedPlace } from '@/api/user'
import { useAlerts } from '@/composables/useAlerts'

const emit = defineEmits(['view'])
const { showAlert } = useAlerts()

const places = ref([])

async function fetchSavedPlaces() {
  try {
    const { data } = await fetchSavedPlace()
    places.value = data
  } catch (err) {
    console.error('❌ Failed to load saved places:', err)
  }
}

async function removePlace(placeId) {
  try {
    await deleteSavedPlace(placeId)
    places.value = places.value.filter(p => p.id !== placeId)
  } catch (err) {
    console.error('❌ Failed to delete place:', err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to delete the place'
    })
  }
}

watch(places, () => {
  localStorage.setItem('savedPlaces', JSON.stringify(places.value))
})

onMounted(() => {
  fetchSavedPlaces()
})
</script>

<style scoped>
.saved-panel {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  width: 90%;
  max-width: 800px;
  min-width: 400px;

  max-height: 80vh;
  overflow-y: auto;

  background: #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
  padding: 1.5rem 1.25rem;
  z-index: 800;
  border-radius: 12px;
}



.panel-title {
  font-size: 2rem;
  font-weight: 700;
  color: #064e3b;
  text-align: center;
  margin-bottom: 2rem;
}

.empty-msg {
  text-align: center;
  color: #6b7280;
  font-size: 1rem;
}

.place-card {
  @apply flex flex-col sm:flex-row justify-between items-start sm:items-center rounded-lg border border-gray-200 p-4 bg-white shadow-sm gap-2;
}

.place-name {
  @apply text-lg font-semibold text-emerald-900 truncate;
}

.btn-group-horizontal {
  @apply flex gap-3;
}

.btn {
  @apply px-4 py-2 text-sm font-medium rounded-md border transition;
}

.view {
  @apply text-emerald-700 border-emerald-700 hover:bg-emerald-50;
}

.delete {
  @apply text-red-700 border-red-700 hover:bg-red-50;
}
@media (max-width: 640px) {
  .saved-panel {
    min-width: 90%;
    padding: 1rem;
  }

  .panel-title {
    font-size: 1.5rem;
  }

  .btn-group-horizontal {
    flex-direction: column;
    gap: 0.5rem;
  }

  .btn {
    width: 100%;
    text-align: center;
  }
}

</style>
