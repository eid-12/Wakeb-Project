<template>
  <section class="flex flex-col items-center justify-start h-full py-12">
    <div class="w-full md:max-w-3xl bg-white shadow-lg rounded-xl p-8">
      <h1 class="text-2xl font-bold text-center mb-8 text-emerald-700">Add Unlisted Place</h1>
      <form @submit.prevent="submitPlace" class="space-y-5">

        <input required  v-model="place.name" placeholder="Place Name" class="input" />

        <textarea v-model="place.description" placeholder="Description" rows="3" class="input"></textarea>
        <select v-model="place.category" class="input">
          <option required  disabled value="">-- Select a category --</option>
          <option>Public</option><option>Private</option>
          <option>Confidential</option><option>Top Secret</option>
        </select>
        <div class="flex gap-4">
          <input required  v-model="place.latitude" placeholder="Latitude" class="input" />
          <input required  v-model="place.longitude" placeholder="Longitude" class="input" />
        </div>
        <input type="file" @change="handleImageUpload" class="input-file" />
        <textarea v-model="place.notes" placeholder="Optional Notes" rows="2" class="input"></textarea>
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" @click="resetForm" class="btn-cancel">Cancel</button>
          <button type="submit" class="btn-submit">Submit</button>
        </div>
      </form>
    </div>

    <div class="w-full md:max-w-3xl mt-10">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-emerald-700">Unlisted Places</h2>
        <button v-if="places.length" @click="deleteAllPlaces" class="text-red-600 hover:underline text-sm">
          Clear All
        </button>
      </div>

      <p v-if="!places.length" class="text-center text-gray-500">No saved places yet</p>
      <ul v-else class="space-y-4">
        <li v-for="p in limitedPlaces" :key="p.id" class="place-card">

          <div>
            <h3 class="font-semibold">{{ p.name }}</h3>
            <p class="text-sm text-gray-500">{{ p.description }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ p.latitude }}, {{ p.longitude }}</p>
          </div>
          <div class="flex flex-col gap-2 shrink-0">
            <button @click="$emit('view', p)" class="btn-view">View in Map</button>
            <button @click="deletePlace(p.id)" class="btn-delete">Delete</button>
          </div>
        </li>
      </ul>
      <div v-if="places.length > displayLimit" class="text-center mt-4">
  <button
    @click="displayLimit += 3"
    class="text-emerald-700 font-medium underline hover:text-emerald-900"
  >
    Load more
  </button>
</div>

    </div>
  </section>
</template>


<script setup>
/* global defineEmits */  

import { ref, onMounted ,computed } from 'vue'
import {submitPlac , fetchPlace ,deletePlac , deleteAllPlace } from '@/api/user'
import { useAlerts } from '@/composables/useAlerts';
const { showAlert, showConfirm } = useAlerts();

defineEmits(['view'])

const place = ref({
  name: '', description: '', category: '',
  latitude: '', longitude: '', notes: '', image: ''
})

const imageFile = ref(null)
const places = ref([])
const displayLimit = ref(3); // Show 5 places initially

const limitedPlaces = computed(() => places.value.slice(0, displayLimit.value));

function handleImageUpload(e) {
  imageFile.value = e.target.files[0] || null
}

function resetForm() {
  place.value = {
    name: '', description: '', category: '',
    latitude: '', longitude: '', notes: '', image: ''
  }
  imageFile.value = null
}

async function submitPlace() {
  if (!place.value.name || !place.value.category || !place.value.latitude || !place.value.longitude) {
    showAlert({
      type: 'warning',
      title: 'Missing Data',
      message: 'Please enter all required fields'
    });
    return;
  }
//  FormData
  const form = new FormData()
  form.append('name',         place.value.name)
  form.append('description',  place.value.description)
  form.append('category',     place.value.category)
  form.append('latitude',     place.value.latitude)
  form.append('longitude',    place.value.longitude)
  if (imageFile.value) {
    form.append('image', imageFile.value)   
  }
  try {
const res = await submitPlac(form) 
    places.value.push(res.data)

    showAlert({
      type: 'success',
      title: 'Saved',
      message: 'Place added successfully',
      duration: 3500
    });
    resetForm()
  } catch (err) {
    console.error(err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: err.response?.data ?? 'Failed to add place'
    });
  }
}

async function fetchPlaces() {
  try {
    const res = await fetchPlace()
    places.value = res.data
  } catch (err) {
    console.error(err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to load places'
    });
  }
}

async function deletePlace(id) {
  const ok = await showConfirm({
    title: 'Delete Place',
    message: 'Are you sure you want to delete this place?'
  });
  if (!ok) return;

  try {
    await deletePlac (id)
    places.value = places.value.filter(p => p.id !== id)
    showAlert({
      type: 'success',
      title: 'Deleted',
      message: 'Place deleted successfully'
    });
  } catch (err) {
    console.error(err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to delete place'
    });
  }
}

async function deleteAllPlaces() {
  if (!places.value.length) return;

  const ok = await showConfirm({
    title: 'Delete All Places',
    message: 'Do you want to delete all places?'
  });
  if (!ok) return;

  try {
    await deleteAllPlace()
    places.value = []
    showAlert({
      type: 'success',
      title: 'Deleted',
      message: 'All places deleted successfully'
    });
  } catch (err) {
    console.error(err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to delete all places'
    });
  }
}

onMounted(() => {
  fetchPlaces()
})
</script>



<style scoped>
.input {
  @apply w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none;
}
.input-file {
  @apply block w-full text-sm text-gray-700 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100;
}
.btn-cancel {
  @apply px-5 py-2 rounded-lg border border-gray-400 text-gray-600 hover:bg-gray-100 transition;
}
.btn-submit {
  @apply px-5 py-2 rounded-lg bg-emerald-600 text-white font-medium hover:bg-emerald-700 transition;
}
.btn-view {
  @apply px-3 py-1 text-sm rounded-lg bg-emerald-50 text-emerald-700 hover:bg-emerald-100;
}
.btn-delete {
  @apply px-3 py-1 text-sm rounded-lg bg-red-50 text-red-600 hover:bg-red-100;
}
.place-card {
  @apply border border-gray-200 rounded-lg p-4 flex justify-between items-start;
}



</style>
