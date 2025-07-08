<template>
  <!-- صفحة محفوظات الأماكن -->
  <main class="py-12 px-4 flex justify-center">
    <div class="saved-panel w-full max-w-4xl">
      <h1 class="panel-title">⭐ Saved Places</h1>

      <p v-if="!places.length" class="empty-msg">لا توجد أماكن محفوظة بعد</p>

      <ul v-else class="space-y-4">
        <li
          v-for="(place, idx) in places"
          :key="idx"
          class="place-card"
        >
          <div class="truncate">
            <h2 class="place-name">{{ placeName[0] }}</h2>
            <p class="place-desc">{{ placeName[1] }}</p>
          </div>

          <!-- أزرار أفقية -->
          <div class="btn-group-horizontal">
            <button @click="$emit('view', place)"  class="btn view">View on map</button>
            <button @click="$emit('delete', idx)" class="btn delete">Delete</button>
          </div>
        </li>
      </ul>
    </div>
  </main>
</template>

<script setup>
/* global defineProps, defineEmits */
import { toRefs } from 'vue';

let props = defineProps({
  places: { type: Array, default: () => [] },
  placeName: { type: Array, default: () => [] },
});

defineEmits(['delete', 'view']);
const { places } = toRefs(props);
let { placeName } = toRefs(props);

</script>

<style scoped>
/* ===== الحاوية ===== */
.saved-panel {
  position: fixed;
  top: 50%;
  left: 55%;
  transform: translate(-50%, -50%);

  width:80%;         /* اضبط العرض كما تشاء */
  max-width: 90vw;
  max-height: 80vh;     /* كي لا تتجاوز مساحة الرؤية */
  overflow-y: auto;

  background: #ffffff;
  box-shadow: 0 8px 24px rgba(0,0,0,.18);
  padding: 1.25rem 1rem;
  z-index: 800;         
}

/* العنوان */
.panel-title {
  font-size: 2.25rem;     /* text-4xl */
  font-weight: 700;       /* font-old ytyei  */
  color: #064e3b;         /* emerald-900 */
  text-align: center;
  margin-bottom: 2.5rem;
}

/* رسالة الفراغ */
.empty-msg {
  text-align: center;
  color: #6b7280;         /* gray-500 */
}

/* ===== البطاقة ===== */
.place-card {
  @apply flex justify-between items-center rounded-lg border border-gray-200 p-4 bg-white shadow-sm;
}

/* النصوص */
.place-name { @apply text-xl font-semibold text-emerald-900 truncate; }
.place-desc { @apply text-gray-600 truncate; }

/* مجموعة الأزرار (أفقي) */
.btn-group-horizontal { @apply flex gap-3 shrink-0; }

/* زرّ أساسي */
.btn{ @apply px-4 py-1.5 text-sm font-medium rounded-md border transition; }

.view{ @apply text-emerald-700 border-emerald-700 hover:bg-emerald-50; }
.delete { @apply text-red-700    border-red-700    hover:bg-red-50; }
</style>
