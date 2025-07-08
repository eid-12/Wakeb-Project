<!-- SettingsPanel.vue -->
<template>
  <h1 class="py-10 text-3xl font-bold text-center">Settings</h1>

  <section class="p-6 space-y-8">
    <div class="grid md:grid-cols-2 gap-8 px-10">
      <!-- 1) General -->
      <div>
        <h2 class="font-semibold mb-3">General</h2>

        <label class="block mb-1">Language</label>
        <select v-model="settings.language" class="border rounded px-2 py-1 w-full mb-4">
          <option>English</option>
        </select>

        <label class="block mb-1">Theme</label>
        <div class="space-x-4">
          <label><input type="radio" value="Light" v-model="settings.theme" /> Light</label>
          <label><input type="radio" value="Dark"  v-model="settings.theme" /> Dark</label>
        </div>
      </div>

<!-- 2) Account -->
<div>
  <h2 class="font-semibold mb-3">Account</h2>
  <button class="btn-setting" @click="showPasswordModal = true">
    <i class="fas fa-key"></i> Change Password
  </button>
  <button class="btn-setting" @click="showEmailModal = true">
    <i class="fas fa-envelope"></i> Change Email
  </button>
  <button class="btn-setting-danger" @click="deleteAccount">
    <i class="fas fa-trash-alt"></i> Delete Account
  </button>
</div>


      <!-- 3) Notifications -->
      <div>
        <h2 class="font-semibold mb-3">Notifications</h2>
        <label class="block">
          <input type="checkbox" v-model="settings.savedPlacesNotifications" />
          Saved Places Notifications
        </label>
      </div>

      <!-- 4) Privacy & Location -->
      <div>
        <h2 class="font-semibold mb-3">Privacy & Location</h2>
        <label class="block">
          <input type="checkbox" v-model="settings.locationTracking" />
          Enable Location Tracking
        </label>
        <button @click="manageSearchData" class="mt-2 border px-3 py-1 rounded w-full">
          Manage Search Data
        </button>
      </div>

      <!-- 5) Interface -->
      <div>
        <h2 class="font-semibold mb-3">Interface</h2>
        <label class="block mb-1">Font Size</label>
        <label><input type="radio" value="Small"   v-model="settings.fontSize" /> Small</label>
        <label class="ml-4"><input type="radio" value="Medium"  v-model="settings.fontSize" /> Medium</label>
      </div>

      <!-- 6) Data Settings -->
      <div>
        <h2 class="font-semibold mb-3">Data Settings</h2>
        <label class="block">
          <input type="checkbox" v-model="settings.autoDeleteHistory" />
          Automatically delete search history every week
        </label>

        <label class="block">
          <input type="checkbox" v-model="settings.exportFavorites" />
          Export favorites to a CSV file
        </label>
      </div>
    </div>
  </section>

  <!-- ===== Password Modal ===== -->
  <div v-if="showPasswordModal" class="modal-overlay" @keydown.esc="closePasswordModal" tabindex="0">
    <div class="modal-card">
      <h3 class="text-xl font-semibold mb-4">Change Password</h3>

      <input v-model="passwordForm.current" type="password" placeholder="Current password" class="input" />
      <input v-model="passwordForm.new"     type="password" placeholder="New password"      class="input" />
      <input v-model="passwordForm.confirm" type="password" placeholder="Confirm new password" class="input" />

      <div class="modal-actions">
        <button class="btn-cancel"  @click="closePasswordModal">Cancel</button>
        <button class="btn-primary" @click="submitPassword">Save</button>
      </div>
    </div>
  </div>

  <!-- ===== Email Modal ===== -->
  <div v-if="showEmailModal" class="modal-overlay" @keydown.esc="closeEmailModal" tabindex="0">
    <div class="modal-card">
      <h3 class="text-xl font-semibold mb-4">Change Email</h3>

      <input v-model="emailForm.newEmail" type="email"    placeholder="New email address" class="input" />
      <input v-model="emailForm.password" type="password" placeholder="Current password"  class="input" />

      <div class="modal-actions">
        <button class="btn-cancel"  @click="closeEmailModal">Cancel</button>
        <button class="btn-primary" @click="submitEmail">Save</button>
      </div>
    </div>
  </div>
</template>

<script setup>
/* global defineProps, defineEmits */

import { reactive, ref, watch, onMounted } from 'vue';

const STORAGE_KEY = 'map-app-settings';
const emit = defineEmits(['change-menu']);

/* ============ Settings ============ */
const settings = reactive({
  language: 'English',
  theme: 'Light',
  savedPlacesNotifications: false,
  locationTracking: true,
  fontSize: 'Small',
  autoDeleteHistory: false,
  syncPlaces: false,
  exportFavorites: false,
});

/* استرجاع/حفظ في localStorage */
onMounted(() => {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved) Object.assign(settings, JSON.parse(saved));
});
watch(settings, v => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(v));
  document.documentElement.classList.toggle('dark', v.theme === 'Dark');
}, { deep: true, immediate: true });

/* ============ Password Modal ============ */
const showPasswordModal = ref(false);
const passwordForm = reactive({ current: '', new: '', confirm: '' });

function closePasswordModal() {
  showPasswordModal.value = false;
  Object.assign(passwordForm, { current: '', new: '', confirm: '' });
}
function submitPassword() {
  if (passwordForm.new !== passwordForm.confirm) {
    alert('Passwords do not match!');
    return;
  }
  // Call API here …
  alert('Password changed successfully ✔');
  closePasswordModal();
}

/* ============ Email Modal ============ */
const showEmailModal = ref(false);
const emailForm = reactive({ newEmail: '', password: '' });

function closeEmailModal() {
  showEmailModal.value = false;
  Object.assign(emailForm, { newEmail: '', password: '' });
}
function submitEmail() {
  if (!emailForm.newEmail.includes('@')) {
    alert('Invalid email!');
    return;
  }
  // Call API here …
  alert('Email updated successfully ✔');
  closeEmailModal();
}

/* ============ Other buttons ============ */
function deleteAccount() {
  if (confirm('⚠️ هل أنت متأكد من حذف الحساب نهائيًا؟')) {
    alert('تم حذف الحساب (تجريبي).');
  }
}
function manageSearchData() {
  emit('change-menu', 'history');
}
</script>

<style scoped>
/* ============= Tailwind shortcuts ============= */
/* زرّ عام داخل قائمة الإعدادات */
.btn-setting {
  @apply w-full flex items-center justify-between border border-emerald-600
         text-emerald-700 dark:text-emerald-300
         px-3 py-2 rounded mb-2
         hover:bg-emerald-50 dark:hover:bg-emerald-900/30
         transition-colors duration-150 ease-in-out;
}

/* لإضافة أيقونة صغيرة (اختياري) */
.btn-setting i {
  @apply mr-2;
}

/* زرّ الحذف (خطر) */
.btn-setting-danger {
  @apply w-full flex items-center justify-between border border-red-600
         text-red-600 dark:text-red-400
         px-3 py-2 rounded mb-2
         hover:bg-red-50 dark:hover:bg-red-900/30
         transition-colors duration-150 ease-in-out;
}

/* نافذة منبثقة */
.modal-overlay {
  @apply fixed inset-0 z-50 bg-black/40 flex items-center justify-center;
}
.modal-card {
  @apply bg-white dark:bg-gray-800 w-full max-w-md p-6 rounded-lg shadow-lg;
}
.input {
  @apply w-full border px-3 py-2 rounded mb-3 dark:bg-gray-700 dark:border-gray-600;
}
.modal-actions {
  @apply flex justify-end gap-3;
}
.btn-cancel {
  @apply border px-4 py-2 rounded hover:bg-gray-50 dark:hover:bg-gray-700;
}
.btn-primary {
  @apply bg-emerald-600 hover:bg-emerald-700 text-white px-4 py-2 rounded;
}
</style>
