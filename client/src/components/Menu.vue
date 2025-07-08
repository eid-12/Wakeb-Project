<template>
  <div class="p-6 max-w-5xl mx-auto space-y-12">

    <!-- 1. Settings -->
    <section>
      <h1 class="text-3xl font-bold mb-6">Settings</h1>
      <div class="grid grid-cols-2 gap-8">
        <div>
          <h2 class="font-semibold mb-3">General</h2>
          <div class="mb-4">
            <label class="block mb-1">Language</label>
            <select v-model="language" class="border rounded px-2 py-1">
              <option>English</option>
              <option>Arabic</option>
            </select>
          </div>
          <div>
            <label class="block mb-1">Theme</label>
            <label><input type="radio" value="Light" v-model="theme" /> Light</label>
            <label class="ml-4"><input type="radio" value="Dark" v-model="theme" /> Dark</label>
          </div>
        </div>

        <div>
          <h2 class="font-semibold mb-3">Account</h2>
          <div class="mb-2">Change Password</div>
          <div>Manage Account</div>
        </div>

        <div>
          <h2 class="font-semibold mb-3">Notifications</h2>
          <label><input type="checkbox" v-model="savedPlacesNotifications" /> Saved Places Notifications</label><br />
          <label><input type="checkbox" v-model="weatherNotifications" /> Weather Notifications</label>
        </div>

        <div>
          <h2 class="font-semibold mb-3">Privacy & Location</h2>
          <label><input type="checkbox" v-model="locationTracking" /> Enable Location Tracking</label><br />
          <div>Manage Search Data</div>
        </div>

        <div>
          <h2 class="font-semibold mb-3">Interface</h2>
          <label>Font Size:</label><br />
          <label><input type="radio" value="Small" v-model="fontSize" /> Small</label>
          <label><input type="radio" value="Medium" v-model="fontSize" class="ml-4" /> Medium</label><br />
          <label>Menu Layout:</label><br />
          <label><input type="radio" value="Horizontal" v-model="menuLayout" /> Horizontal</label>
          <label><input type="radio" value="Vertical" v-model="menuLayout" class="ml-4" /> Vertical</label>
        </div>

        <div>
          <h2 class="font-semibold mb-3">Data Settings</h2>
          <label><input type="checkbox" v-model="autoDeleteHistory" /> Automatically delete search history every week</label><br />
          <label><input type="checkbox" v-model="syncPlaces" /> Sync saved places with your account</label><br />
          <label><input type="checkbox" v-model="exportFavorites" /> Export favorites to a CSV file</label>
        </div>
      </div>
    </section>

    <!-- 2. Add Unlisted Place -->
    <section>
      <h1 class="text-3xl font-bold mb-6">Add Unlisted Place</h1>
      <form @submit.prevent="submitPlace" class="space-y-4">
        <input v-model="place.name" placeholder="Place Name" class="w-full border px-3 py-2 rounded" />
        <textarea v-model="place.description" placeholder="Description" class="w-full border px-3 py-2 rounded" rows="3"></textarea>
        <select v-model="place.category" class="w-full border px-2 py-2 rounded">
          <option disabled value="">-- Select a category --</option>
          <option>Park</option>
          <option>Historical Site</option>
          <option>Restaurant</option>
        </select>
        <div class="flex gap-4">
          <input v-model="place.latitude" placeholder="Latitude" class="flex-1 border px-2 py-1 rounded" />
          <input v-model="place.longitude" placeholder="Longitude" class="flex-1 border px-2 py-1 rounded" />
        </div>
        <input type="file" @change="handleImageUpload" />
        <textarea v-model="place.notes" placeholder="Optional Notes" class="w-full border px-2 py-2 rounded"></textarea>
        <div class="flex gap-4">
          <button type="button" @click="pickOnMap" class="border px-4 py-1 rounded">Pick Location</button>
          <button type="submit" class="bg-green-600 text-white px-4 py-1 rounded">Submit</button>
          <button type="button" @click="resetForm" class="border px-4 py-1 rounded">Cancel</button>
        </div>
      </form>
    </section>

    <!-- 3. Saved Places -->
    <section>
      <h1 class="text-3xl font-bold mb-6">Saved Places</h1>
      <div v-for="(place, index) in savedPlaces" :key="index" class="border rounded px-4 py-3 mb-4 flex justify-between items-center">
        <div>
          <h2 class="text-lg font-semibold">{{ place.name }}</h2>
          <p class="text-gray-600">{{ place.description }}</p>
        </div>
        <div class="flex gap-2">
          <button @click="deletePlace(index)" class="border px-3 py-1 rounded">Delete</button>
          <button @click="viewOnMap(place)" class="border px-3 py-1 rounded">View on map</button>
        </div>
      </div>
    </section>

    <!-- 4. Search History -->
    <section>
      <h1 class="text-3xl font-bold text-center mb-6">Search History</h1>
      <div v-for="(item, index) in history" :key="index" class="flex justify-between items-center bg-gray-50 rounded-full px-6 py-3 shadow-sm mb-2">
        <span class="font-semibold text-gray-800">{{ item.query }}</span>
        <span class="text-sm text-gray-600">{{ item.timestamp }}</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const language = ref('English')
const theme = ref('Light')
const savedPlacesNotifications = ref(true)
const weatherNotifications = ref(true)
const locationTracking = ref(true)
const fontSize = ref('Small')
const menuLayout = ref('Horizontal')
const autoDeleteHistory = ref(false)
const syncPlaces = ref(false)
const exportFavorites = ref(false)

const place = ref({ name: '', description: '', category: '', latitude: '', longitude: '', notes: '' })
const imageFile = ref(null)

const handleImageUpload = (e) => {
  const file = e.target.files[0]
  if (file) imageFile.value = file
}
const pickOnMap = () => alert('Pick location on map')
const submitPlace = () => alert('Place submitted: ' + JSON.stringify(place.value))
const resetForm = () => {
  place.value = { name: '', description: '', category: '', latitude: '', longitude: '', notes: '' }
  imageFile.value = null
}

const savedPlaces = ref([
  { name: 'Central Library', description: 'Main city library' },
  { name: 'Giardino Restaurant', description: 'Italian cuisine' },
  { name: 'Landmark Tower', description: 'Observation deck & museum' },
  { name: 'Grove Park', description: 'Public park with walking trails' }
])

const deletePlace = (index) => savedPlaces.value.splice(index, 1)
const viewOnMap = (place) => alert('Viewing: ' + place.name)

const history = ref([
  { query: 'Global warming', timestamp: 'Today at 08:34 AM' },
  { query: 'Eiffel tower', timestamp: 'Today at 01:33 AM' },
  { query: 'Covid 19', timestamp: 'December 23, 2023 at 08:09 AM' },
  { query: 'Albert Eintein', timestamp: 'December 22, 2023 at 11:19 PM' },
  { query: 'Barcelona', timestamp: 'December 22, 2023 at 05:00 PM' },
  { query: 'Mona Lisa', timestamp: 'December 16, 2023 at 8:45 AM' },
  { query: 'Australia', timestamp: 'December 16, 2023 at 7:51 AM' },
  { query: 'Ping pong Tennis', timestamp: 'December 06, 2023 at 0:12 PM' },
  { query: 'New Year', timestamp: 'November 28, 2023 at 04:56 PM' },
  { query: 'Michael Jordan', timestamp: 'November 26, 2023 at 06:09 AM' },
])
</script>

<style scoped>
input,
textarea,
select {
  outline: none;
}
</style>