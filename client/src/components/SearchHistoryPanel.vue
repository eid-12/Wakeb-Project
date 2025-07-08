<template>   
<h1 class="py-10 text-3xl font-bold text-center ">Search History</h1>
  <button
        @click="clearHistory"
        class="absolute left-10  -translate-y-1/2 text-2xl text-600 hover:text-red-600 flex items-center gap-1 m-4 "
      >
        <svg class="h-7 w-7" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M6 18L18 6M6 6l12 12" />
        </svg>
        Clear
      </button>
<!-- داخل الشريط الجانبي -->
<section
  class="w-full h-full px-15 py-20 overflow-y-auto space-y-4 mt-3  "
>
  <template v-if="history.length">
    <div
      v-for="item in history"
      :key="item.id"
      class="px-20 w-full flex justify-between items-center bg-gray-50 rounded-full px-4 py-2 shadow-sm "
    >
      <!-- min-w-0 يتيح لـ truncate أن يقصّ النص الطويل -->
      <span class="font-semibold text-gray-800 truncate min-w-0">
        {{ item.query }}
      </span>

      <!-- نمنع التوقيت من الالتفاف -->
      <span class="text-xs text-gray-600 whitespace-nowrap ml-4">
        {{ item.timestamp }}
      </span>
    </div>
  </template>

  <p v-else class="text-center text-gray-500">
    No search history yet
  </p>
</section>

</template>

<script setup>
import { ref } from 'vue';

/* ❶ قراءة البداية من localStorage */
const history = ref(
  JSON.parse(localStorage.getItem('searchHistory')) || []
);
function clearHistory () {
   localStorage.removeItem('searchHistory');
history.value = [];
}


</script>
