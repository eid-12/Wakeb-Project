<template>
  <h1 class="py-10 text-3xl font-bold text-center text-emerald-700">Search History</h1>
  <button
    @click="clearHistory"
    class="absolute left-10 -translate-y-1/2 text-2xl hover:text-red-600 flex items-center gap-1 m-4"
  >
    <svg class="h-7 w-7" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M6 18L18 6M6 6l12 12" />
    </svg>
    Clear
  </button>

  <section class="w-full h-full px-15 py-20 overflow-y-auto space-y-4 mt-3">
    <template v-if="history.length">
      <div
        v-for="item in history"
        :key="item.id"
        class="flex justify-between items-center bg-gray-50 rounded-full px-4 py-2 shadow-sm"
      >
        <span class="font-semibold text-gray-800 truncate min-w-0">
          {{ item.query }}
        </span>
        <span class="text-xs text-gray-600 whitespace-nowrap ml-4">
          {{ formatDate(item.searchedAt) }}
        </span>
      </div>
    </template>
    <p v-else class="text-center text-gray-500">
      No search history yet
    </p>
  </section>
</template>

<script setup>
/* global defineProps, defineEmits */
import { ref, onMounted} from 'vue'
import { fetchSearchHistory , clearSearchHistory} from '@/api/user'
const props = defineProps({

  newQuery: {
    type: String,
    default: ''
  }
})

const history = ref([])


async function fetchHistory() {
  try {
    const { data } = await fetchSearchHistory()
    history.value = data
  } catch (err) {
    console.error('Failed to load history:', err)
  }
}



async function clearHistory() {
  try {
    await clearSearchHistory()
    history.value = []
  } catch (err) {
    console.error('Failed to clear history:', err)
  }
}


function formatDate(isoString) {
  const date = new Date(isoString)
  return date.toLocaleString('en-US', {
    month: 'short',   // Jan, Feb, ...
    day: '2-digit',   // 01, 02, ...
    year: 'numeric',  // 2025
    hour: '2-digit',  // 01, 13
    minute: '2-digit',// 05, 45
    hour12: true      // 12-hour format
  })
}

onMounted(fetchHistory)
</script>