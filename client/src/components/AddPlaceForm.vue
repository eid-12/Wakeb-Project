<template>
  <section class="flex flex-col items-center justify-start h-full py-12">

    <!-- ★ بطاقة الإدخال ★ -->
    <div class="w-full md:max-w-3xl bg-white shadow-lg rounded-xl p-8">
      <h1 class="text-2xl font-bold text-center mb-8 text-emerald-700">
        Add Unlisted Place
      </h1>

      <!-- نموذج الإضافة -->
      <form @submit.prevent="submitPlace" class="space-y-5">
        <!-- الاسم -->
        <input v-model="place.name" placeholder="Place Name"
               class="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none" />

        <!-- الوصف -->
        <textarea v-model="place.description" placeholder="Description" rows="3"
                  class="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none resize-none"></textarea>

        <!-- الفئة -->
        <select v-model="place.category"
                class="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none">
          <option disabled value="">-- Select a category --</option>
          <option>Public</option><option>Private</option>
          <option>Confidential</option><option>Top Secret</option>
        </select>

        <!-- الإحداثيات -->
        <div class="flex gap-4">
          <input v-model="place.latitude" placeholder="Latitude"
                 class="flex-1 border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none" />
          <input v-model="place.longitude" placeholder="Longitude"
                 class="flex-1 border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none" />
        </div>

        <!-- الصورة -->
        <input type="file" @change="handleImageUpload"
               class="block w-full text-sm text-gray-700
                      file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0
                      file:text-sm file:font-semibold
                      file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100"/>

        <!-- الملاحظات -->
        <textarea v-model="place.notes" placeholder="Optional Notes" rows="2"
                  class="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-emerald-600 focus:outline-none resize-none"></textarea>

        <!-- أزرار التحكم -->
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" @click="resetForm"
                  class="px-5 py-2 rounded-lg border border-gray-400 text-gray-600 hover:bg-gray-100 transition">
            Cancel
          </button>
          <button type="submit"
                  class="px-5 py-2 rounded-lg bg-emerald-600 text-white font-medium hover:bg-emerald-700 transition">
            Submit
          </button>
        </div>
      </form>
    </div>

    <!-- ★ قائمة الأماكن المحفوظة ★ -->
    <div class="w-full md:max-w-3xl mt-10">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-emerald-700">Unlisted Places</h2>
        <button v-if="unlisted.length"
                @click="clearAll"
                class="text-red-600 hover:underline text-sm">
          Clear All
        </button>
      </div>

      <p v-if="!unlisted.length" class="text-center text-gray-500">
        لا توجد أماكن محفوظة بعد
      </p>

      <ul v-else class="space-y-4">
        <li v-for="(p, idx) in unlisted" :key="idx"
            class="border border-gray-200 rounded-lg p-4 flex justify-between items-start">
          <div>
            <h3 class="font-semibold">{{ p.name }}</h3>
            <p class="text-sm text-gray-500" v-if="p.descriptions">{{ p.descriptions }}</p>
            <p class="text-xs text-gray-400 mt-1">
              {{ p.latitude }}, {{ p.longitude }}
            </p>
          </div>

          <div class="flex flex-col gap-2 shrink-0">
            <button @click="$emit('view', p)"
                    class="px-3 py-1 text-sm rounded-lg bg-emerald-50 text-emerald-700 hover:bg-emerald-100">
              View in Map
            </button>
            <button @click="deletePlace(idx)"
                    class="px-3 py-1 text-sm rounded-lg bg-red-50 text-red-600 hover:bg-red-100">
              Delete
            </button>
          </div>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
/* global defineProps, defineEmits */

defineEmits(['view'])

/* نموذج فارغ */
const emptyPlace = () => ({
  name: '', descriptions: '', category: '',
  latitude: '', longitude: '', notes: '', image: null
})

const place     = ref(emptyPlace())
const imageFile = ref(null)

/* الأماكن المحفوظة */
const unlisted  = ref(JSON.parse(localStorage.getItem('places') || '[]'))

function handleImageUpload (e) {
  imageFile.value = e.target.files[0] || null
}

/* إضافة مكان */
function submitPlace () {
  if (!place.value.name || !place.value.category) {
    alert('❗ يرجى ملء الاسم والفئة على الأقل')
    return
  }

  const newPlace = { ...place.value }
  if (imageFile.value) newPlace.image = URL.createObjectURL(imageFile.value)

  unlisted.value.push(newPlace)
  localStorage.setItem('places', JSON.stringify(unlisted.value))
  alert('✅ تمّت إضافة المكان بنجاح')
  resetForm()
}

/* حذف مكان واحد */
function deletePlace (idx) {
  unlisted.value.splice(idx, 1)
  localStorage.setItem('places', JSON.stringify(unlisted.value))
}

/* مسح الجميع */
function clearAll () {
  if (confirm('هل تريد مسح جميع الأماكن؟')) {
    unlisted.value = []
    localStorage.removeItem('places')
  }
}

/* إعادة تعيين النموذج */
function resetForm () {
  place.value   = emptyPlace()
  imageFile.value = null
}
</script>
