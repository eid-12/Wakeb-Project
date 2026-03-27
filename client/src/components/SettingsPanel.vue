<template>
  <h1 class="py-10 text-3xl font-bold text-center text-emerald-700 dark:text-emerald-400">
    {{ t('settings.title') }}
  </h1>

  <section class="p-4 sm:p-6 space-y-8">
    <div class="grid md:grid-cols-2 gap-6 md:gap-8 px-0 sm:px-4 md:px-10">

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.general') }}</h2>

        <label class="block mb-1">{{ t('settings.language') }}</label>
        <select v-model="appSettings.language" class="border rounded px-2 py-1 w-full mb-4 dark:bg-gray-800 dark:border-gray-600">
          <option value="English">{{ t('settings.langEnglish') }}</option>
          <option value="Arabic">{{ t('settings.langArabic') }}</option>
        </select>

        <label class="block mb-1">{{ t('settings.theme') }}</label>
        <div class="space-x-4 rtl:space-x-reverse">
          <label><input type="radio" value="Light" v-model="appSettings.theme" /> {{ t('settings.themeLight') }}</label>
          <label><input type="radio" value="Dark" v-model="appSettings.theme" /> {{ t('settings.themeDark') }}</label>
        </div>
      </div>

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.account') }}</h2>
        <button type="button" class="btn-setting" @click="showPasswordModal = true">
          <i class="fas fa-key"></i> {{ t('settings.changePassword') }}
        </button>
        <button type="button" class="btn-setting" @click="showUsernameModal = true">
          <i class="fa-solid fa-user-pen"></i> {{ t('settings.changeUsername') }}
        </button>
        <button type="button" class="btn-setting-danger" :disabled="deleting" @click="deleteAccount">
          <i class="fas fa-trash-alt"></i> {{ deleting ? t('settings.deleting') : t('settings.deleteAccount') }}
        </button>
      </div>

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.notifications') }}</h2>
        <label class="block">
          <input type="checkbox" v-model="appSettings.savedPlacesNotifications" />
          {{ t('settings.savedPlacesNotif') }}
        </label>
      </div>

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.privacy') }}</h2>
        <label class="block">
          <input
            type="checkbox"
            v-model="appSettings.locationTracking"
            @change="onLocationTrackingChange"
          />
          {{ t('settings.locationTracking') }}
        </label>
        <button type="button" @click="manageSearchData" class="mt-2 border px-3 py-1 rounded w-full dark:border-gray-600 dark:hover:bg-gray-800">
          {{ t('settings.manageSearchData') }}
        </button>
      </div>

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.interface') }}</h2>
        <label class="block mb-1">{{ t('settings.fontSize') }}</label>
        <label><input type="radio" value="Small" v-model="appSettings.fontSize" /> {{ t('settings.fontSmall') }}</label>
        <label class="ms-4 rtl:ms-0 rtl:me-4"><input type="radio" value="Medium" v-model="appSettings.fontSize" /> {{ t('settings.fontMedium') }}</label>
      </div>

      <div>
        <h2 class="font-semibold mb-3">{{ t('settings.dataSettings') }}</h2>
        <label class="block">
          <input type="checkbox" v-model="appSettings.autoDeleteHistory" />
          {{ t('settings.autoDeleteHistory') }}
        </label>

        <label class="block mt-2">
          <input type="checkbox" v-model="appSettings.exportFavorites" />
          {{ t('settings.exportFavorites') }}
        </label>
        <button
          v-if="appSettings.exportFavorites"
          type="button"
          class="mt-2 w-full border border-emerald-600 text-emerald-700 dark:text-emerald-300 px-3 py-2 rounded hover:bg-emerald-50 dark:hover:bg-emerald-900/30"
          :disabled="exportingCsv"
          @click="exportFavoritesCsv"
        >
          {{ exportingCsv ? '…' : t('settings.exportCsvNow') }}
        </button>
      </div>
    </div>
  </section>

  <div v-if="showPasswordModal" class="modal-overlay" @keydown.esc="closePasswordModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">{{ t('settings.pwdModalTitle') }}</h3>

      <div class="relative">
        <input
          v-model="passwordForm.current"
          :type="showPwd.current ? 'text' : 'password'"
          :placeholder="t('settings.pwdCurrent')"
          class="input pr-10"
          autocomplete="current-password"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showPwd.current = !showPwd.current"
          :aria-label="showPwd.current ? 'Hide password' : 'Show password'"
        >
          <i :class="showPwd.current ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye'"></i>
        </button>
      </div>

      <div class="relative">
        <input
          v-model="passwordForm.new"
          :type="showPwd.new ? 'text' : 'password'"
          :placeholder="t('settings.pwdNew')"
          class="input pr-10"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showPwd.new = !showPwd.new"
          :aria-label="showPwd.new ? 'Hide password' : 'Show password'"
        >
          <i :class="showPwd.new ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye'"></i>
        </button>
      </div>

      <div class="relative">
        <input
          v-model="passwordForm.confirm"
          :type="showPwd.confirm ? 'text' : 'password'"
          :placeholder="t('settings.pwdConfirm')"
          class="input pr-10"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showPwd.confirm = !showPwd.confirm"
          :aria-label="showPwd.confirm ? 'Hide password' : 'Show password'"
        >
          <i :class="showPwd.confirm ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye'"></i>
        </button>
      </div>

      <div class="modal-actions">
        <button type="button" class="btn-cancel" @click="closePasswordModal">{{ t('settings.cancel') }}</button>
        <button type="button" class="btn-primary" :disabled="passwordLoading" @click="submitPassword">
          {{ passwordLoading ? t('settings.saving') : t('settings.save') }}
        </button>
      </div>
    </div>
  </div>

  <div v-if="showUsernameModal" class="modal-overlay" @click.self="closeUsernameModal" @keydown.esc="closeUsernameModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">{{ t('settings.usernameModalTitle') }}</h3>

      <input v-model.trim="usernameForm.newUsername" type="text" :placeholder="t('settings.usernamePlaceholder')" class="input" />

      <p v-if="invalidUsername" class="text-red-500 text-sm mt-1">
        {{ t('settings.usernameInvalid') }}
      </p>

      <div class="modal-actions mt-4">
        <button type="button" class="btn-cancel" @click="closeUsernameModal">{{ t('settings.cancel') }}</button>
        <button
          type="button"
          class="btn-primary"
          :disabled="savingUsername || invalidUsername || !usernameForm.newUsername.trim()"
          @click="submitUsername"
        >
          {{ savingUsername ? t('settings.saving') : t('settings.save') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
/* global defineEmits */
import { reactive, ref, onMounted, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import Swal from 'sweetalert2';

import {
  getUser,
  changeUsername,
  changePassword,
  deleteUser,
  clearSearchHistory,
  fetchSavedPlace,
  useUser,
} from '@/api/user';
import { appSettings } from '@/composables/useAppSettings';
import { useAlerts } from '@/composables/useAlerts';

const { t } = useI18n();
const { showAlert, showConfirm } = useAlerts();
const emit = defineEmits(['change-menu', 'logout']);

const showPwd = reactive({ current: false, new: false, confirm: false });
const { user, setUser } = useUser();

onMounted(async () => {
  const u = await getUser();
  setUser(u);
  if (
    appSettings.savedPlacesNotifications &&
    typeof Notification !== 'undefined' &&
    Notification.permission !== 'granted'
  ) {
    appSettings.savedPlacesNotifications = false;
  }
});

watch(
  () => appSettings.savedPlacesNotifications,
  async (enabled) => {
    if (!enabled) return;
    if (typeof Notification === 'undefined') {
      showAlert({ type: 'warning', title: t('settings.notifications'), message: t('settings.notifNoBrowser') });
      appSettings.savedPlacesNotifications = false;
      return;
    }
    if (Notification.permission === 'denied') {
      showAlert({ type: 'warning', title: t('settings.notifications'), message: t('settings.notifDenied') });
      appSettings.savedPlacesNotifications = false;
      return;
    }
    if (Notification.permission === 'default') {
      const p = await Notification.requestPermission();
      if (p !== 'granted') {
        appSettings.savedPlacesNotifications = false;
        showAlert({ type: 'warning', title: t('settings.notifications'), message: t('settings.notifDenied') });
      }
    }
  }
);

function onLocationTrackingChange() {
  if (!appSettings.locationTracking) return;
  if (!navigator.geolocation) {
    showAlert({ type: 'warning', title: t('settings.privacy'), message: t('settings.locationNoBrowser') });
    appSettings.locationTracking = false;
    return;
  }
  navigator.geolocation.getCurrentPosition(
    () => {},
    () => {
      showAlert({ type: 'info', title: t('settings.privacy'), message: t('settings.locationDenied') });
    },
    { timeout: 8000, maximumAge: 0 }
  );
}

const showPasswordModal = ref(false);
const passwordForm = reactive({ current: '', new: '', confirm: '' });
const passwordLoading = ref(false);

const mismatch = computed(
  () =>
    passwordForm.new.trim() &&
    passwordForm.confirm.trim() &&
    passwordForm.new.trim() !== passwordForm.confirm.trim()
);

function closePasswordModal() {
  showPasswordModal.value = false;
  passwordForm.current = '';
  passwordForm.new = '';
  passwordForm.confirm = '';
}

async function submitPassword() {
  if (mismatch.value) {
    showAlert({ type: 'warning', title: 'Warning', message: 'Passwords do not match!' });
    return;
  }
  if (!passwordForm.current || !passwordForm.new || !passwordForm.confirm) {
    showAlert({ type: 'warning', title: 'Notice', message: 'All fields are required' });
    return;
  }
  passwordLoading.value = true;
  try {
    await changePassword(passwordForm.current, passwordForm.new, user.value.id);
    showAlert({ type: 'success', title: 'Success', message: 'Password changed successfully ✔' });
    closePasswordModal();
  } catch (e) {
    showAlert({
      type: 'danger',
      title: 'Error',
      message: e.response?.data?.message || 'Error changing password or The Wrong Current Password',
    });
  } finally {
    passwordLoading.value = false;
  }
}

const showUsernameModal = ref(false);
const usernameForm = reactive({ newUsername: '' });
const savingUsername = ref(false);

const invalidUsername = computed(
  () =>
    usernameForm.newUsername.trim() &&
    !/^[A-Za-z0-9._]{3,30}$/.test(usernameForm.newUsername.trim())
);

function closeUsernameModal() {
  showUsernameModal.value = false;
  usernameForm.newUsername = '';
}

async function submitUsername() {
  if (!usernameForm.newUsername.trim()) {
    showAlert({ type: 'warning', title: 'Notice', message: 'Please enter a new username' });
    return;
  }
  if (invalidUsername.value) {
    showAlert({ type: 'warning', title: 'Notice', message: t('settings.usernameInvalid') });
    return;
  }
  const confirm = await showConfirm({
    title: 'Confirm Change',
    message: `Are you sure you want to change the username to "${usernameForm.newUsername}"?`,
  });
  if (!confirm) return;

  savingUsername.value = true;
  try {
    await changeUsername(user.value.id, usernameForm.newUsername);
    const u = await getUser();
    setUser(u);
    showAlert({ type: 'success', title: 'Updated', message: 'Username updated successfully ✔' });
    closeUsernameModal();
  } catch (e) {
    showAlert({ type: 'danger', title: 'Error', message: e.response?.data?.message || 'Error updating username' });
  } finally {
    savingUsername.value = false;
  }
}

const deleting = ref(false);

async function deleteAccount() {
  const { value, isConfirmed } = await Swal.fire({
    title: t('settings.deleteConfirmTitle'),
    html: t('settings.deleteConfirmHtml'),
    input: 'text',
    inputPlaceholder: t('settings.deleteTypePlaceholder'),
    showCancelButton: true,
    confirmButtonText: t('settings.deleteAccount'),
    cancelButtonText: t('settings.cancel'),
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#6b7280',
    inputValidator: (v) => {
      if (v !== 'DELETE') return t('settings.deleteMismatch');
      return null;
    },
  });
  if (!isConfirmed || value !== 'DELETE') return;

  deleting.value = true;
  try {
    await deleteUser(user.value.id);
    showAlert({ type: 'success', title: 'Deleted', message: 'Account deleted successfully' });
    emit('logout');
  } catch (e) {
    showAlert({
      type: 'danger',
      title: 'Error',
      message: e.response?.data?.message || 'An error occurred while deleting the account',
    });
  } finally {
    deleting.value = false;
  }
}

async function manageSearchData() {
  const ok = await showConfirm({
    title: t('settings.manageSearchTitle'),
    message: t('settings.manageSearchBody'),
    confirmText: t('settings.clear'),
    cancelText: t('settings.cancel'),
  });
  if (!ok) return;
  try {
    await clearSearchHistory();
    showAlert({ type: 'success', title: t('settings.manageSearchData'), message: t('settings.historyCleared') });
    emit('change-menu', 'history');
  } catch (e) {
    showAlert({
      type: 'danger',
      title: 'Error',
      message: e.response?.data?.message || 'Could not clear search history',
    });
  }
}

const exportingCsv = ref(false);

function toCsvValue(cell) {
  const s = cell == null ? '' : String(cell);
  return `"${s.replace(/"/g, '""')}"`;
}

async function exportFavoritesCsv() {
  exportingCsv.value = true;
  try {
    const { data: places } = await fetchSavedPlace();
    const list = Array.isArray(places) ? places : [];
    const rows = [['id', 'title', 'latitude', 'longitude', 'createdAt']];
    for (const p of list) {
      rows.push([
        p.id,
        p.title,
        p.latitude,
        p.longitude,
        p.createdAt ?? '',
      ]);
    }
    const csv = rows.map((r) => r.map(toCsvValue).join(',')).join('\n');
    const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `wakeb-favorites-${Date.now()}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    showAlert({ type: 'success', title: t('settings.dataSettings'), message: t('settings.exportDone') });
  } catch (e) {
    showAlert({ type: 'danger', title: 'Error', message: t('settings.exportFailed') });
  } finally {
    exportingCsv.value = false;
  }
}
</script>

<style scoped>
.btn-setting {
  @apply w-full flex items-center justify-between border border-emerald-600
         text-emerald-700 dark:text-emerald-300
         px-3 py-2 rounded mb-2
         hover:bg-emerald-50 dark:hover:bg-emerald-900/30
         transition-colors duration-150 ease-in-out;
}
.btn-setting i {
  @apply mr-2 rtl:mr-0 rtl:ml-2;
}

.btn-setting-danger {
  @apply w-full flex items-center justify-between border border-red-600
         text-red-600 dark:text-red-400
         px-3 py-2 rounded mb-2
         hover:bg-red-50 dark:hover:bg-red-900/30
         transition-colors duration-150 ease-in-out;
}

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
  @apply bg-emerald-600 hover:bg-emerald-700 text-white px-4 py-2 rounded disabled:opacity-50;
}
.modal-heading {
  @apply text-slate-900 dark:text-slate-100;
}

.eye-btn {
  @apply absolute right-2 rtl:right-auto rtl:left-2 top-1/2 -translate-y-1/2
         h-8 w-8 grid place-items-center rounded
         text-gray-600 dark:text-gray-300
         hover:bg-black/5 dark:hover:bg-white/10;
}
</style>
