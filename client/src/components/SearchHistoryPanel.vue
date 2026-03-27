<template>
  <div class="history-panel">
    <header class="history-header">
      <h1 class="history-title">Search History</h1>
      <button
        type="button"
        @click="clearHistory"
        class="history-clear"
      >
        <svg class="h-5 w-5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
        </svg>
        Clear all
      </button>
    </header>

    <section class="history-list-wrap">
      <template v-if="history.length">
        <div
          v-for="item in history"
          :key="item.id"
          class="history-row"
        >
          <span class="history-query">
            {{ item.query }}
          </span>
          <span class="history-date">
            {{ formatDate(item.searchedAt) }}
          </span>
        </div>
      </template>
      <p v-else class="history-empty">
        No search history yet
      </p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted} from 'vue'
import { fetchSearchHistory , clearSearchHistory} from '@/api/user'

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

<style scoped>
.history-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding: 1rem 1rem 1.5rem;
  max-width: 48rem;
  margin: 0 auto;
  width: 100%;
}

.history-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  flex-shrink: 0;
}

.history-title {
  margin: 0;
  font-size: clamp(1.35rem, 3vw, 1.75rem);
  font-weight: 700;
  color: #047857;
}

.history-clear {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.45rem 0.85rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #b91c1c;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}

.history-clear:hover {
  background: #fee2e2;
  border-color: #f87171;
}

.history-clear:focus-visible {
  outline: 2px solid #ef4444;
  outline-offset: 2px;
}

.history-list-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
  padding-right: 0.15rem;
}

.history-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 0.65rem 1rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.history-query {
  font-weight: 600;
  color: #1e293b;
  min-width: 0;
  flex: 1;
  text-align: left;
}

.history-date {
  font-size: 0.75rem;
  color: #64748b;
  white-space: nowrap;
  flex-shrink: 0;
}

.history-empty {
  text-align: center;
  color: #64748b;
  padding: 3rem 1rem;
  margin: 0;
}
</style>