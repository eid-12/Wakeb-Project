<template>
  <h1 class="py-10 text-3xl font-bold text-center text-emerald-700 dark:text-emerald-400">
    Settings
  </h1>

  <section class="p-4 sm:p-6 space-y-8">
    <div class="grid md:grid-cols-2 gap-6 md:gap-8 px-0 sm:px-4 md:px-10">
      <div>
        <h2 class="font-semibold mb-3">General</h2>

        <label class="block mb-1">Theme</label>
        <div class="space-x-4">
          <label><input type="radio" value="Light" v-model="appSettings.theme" /> Light</label>
          <label><input type="radio" value="Dark" v-model="appSettings.theme" /> Dark</label>
        </div>
      </div>

      <div>
        <h2 class="font-semibold mb-3">Account</h2>
        <button type="button" class="btn-setting" @click="openEmailModal">
          <i class="fas fa-envelope"></i> Change email
        </button>
        <button type="button" class="btn-setting" @click="openPhoneModal">
          <i class="fas fa-mobile-screen"></i> Mobile number
        </button>
        <button type="button" class="btn-setting" @click="showPasswordModal = true">
          <i class="fas fa-key"></i> Change Password
        </button>
        <button type="button" class="btn-setting" @click="showUsernameModal = true">
          <i class="fa-solid fa-user-pen"></i> Change Username
        </button>
        <button type="button" class="btn-setting-danger" :disabled="deleting" @click="deleteAccount">
          <i class="fas fa-trash-alt"></i> {{ deleting ? 'Deleting…' : 'Delete Account' }}
        </button>
      </div>

      <div>
        <h2 class="font-semibold mb-3">Notifications</h2>
        <label class="block">
          <input type="checkbox" v-model="appSettings.savedPlacesNotifications" />
          Saved Places Notifications
        </label>
      </div>

      <div>
        <h2 class="font-semibold mb-3">Privacy &amp; Location</h2>
        <label class="block">
          <input
            type="checkbox"
            v-model="appSettings.locationTracking"
            @change="onLocationTrackingChange"
          />
          Enable Location Tracking
        </label>
        <button type="button" @click="manageSearchData" class="mt-2 border px-3 py-1 rounded w-full dark:border-gray-600 dark:hover:bg-gray-800">
          Manage Search Data
        </button>
      </div>

      <div>
        <h2 class="font-semibold mb-3">Interface</h2>
        <label class="block mb-1">Font Size</label>
        <label><input type="radio" value="Small" v-model="appSettings.fontSize" /> Small</label>
        <label class="ms-4"><input type="radio" value="Medium" v-model="appSettings.fontSize" /> Medium</label>
      </div>

      <div>
        <h2 class="font-semibold mb-3">Data Settings</h2>
        <label class="block">
          <input type="checkbox" v-model="appSettings.autoDeleteHistory" />
          Automatically delete search history every week
        </label>

        <label class="block mt-2">
          <input type="checkbox" v-model="appSettings.exportFavorites" />
          Export favorites to a CSV file
        </label>
        <button
          v-if="appSettings.exportFavorites"
          type="button"
          class="mt-2 w-full border border-emerald-600 text-emerald-700 dark:text-emerald-300 px-3 py-2 rounded hover:bg-emerald-50 dark:hover:bg-emerald-900/30"
          :disabled="exportingCsv"
          @click="exportFavoritesCsv"
        >
          {{ exportingCsv ? '…' : 'Download CSV now' }}
        </button>
      </div>
    </div>
  </section>

  <div v-if="showPasswordModal" class="modal-overlay" @keydown.esc="closePasswordModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">Change Password</h3>

      <div class="relative">
        <input
          v-model="passwordForm.current"
          :type="showPwd.current ? 'text' : 'password'"
          placeholder="Current password"
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
          placeholder="New password"
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
          placeholder="Confirm new password"
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
        <button type="button" class="btn-cancel" @click="closePasswordModal">Cancel</button>
        <button type="button" class="btn-primary" :disabled="passwordLoading" @click="submitPassword">
          {{ passwordLoading ? 'Saving…' : 'Save' }}
        </button>
      </div>
    </div>
  </div>

  <div v-if="showUsernameModal" class="modal-overlay" @click.self="closeUsernameModal" @keydown.esc="closeUsernameModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">Change Username</h3>

      <input v-model.trim="usernameForm.newUsername" type="text" placeholder="New username" class="input" />

      <p v-if="invalidUsername" class="text-red-500 text-sm mt-1">
        Only letters, numbers, dot and underscore (3–30 chars)
      </p>

      <div class="modal-actions mt-4">
        <button type="button" class="btn-cancel" @click="closeUsernameModal">Cancel</button>
        <button
          type="button"
          class="btn-primary"
          :disabled="savingUsername || invalidUsername || !usernameForm.newUsername.trim()"
          @click="submitUsername"
        >
          {{ savingUsername ? 'Saving…' : 'Save' }}
        </button>
      </div>
    </div>
  </div>

  <div v-if="showEmailModal" class="modal-overlay" @click.self="closeEmailModal" @keydown.esc="closeEmailModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">Change email</h3>
      <p v-if="user?.email" class="text-sm text-gray-600 dark:text-gray-400 mb-2">
        Current: {{ user.email }}
      </p>
      <input
        v-if="user?.email"
        v-model.trim="emailForm.oldEmail"
        type="email"
        autocomplete="email"
        placeholder="Current email"
        class="input"
      />
      <input
        v-model.trim="emailForm.newEmail"
        type="email"
        autocomplete="email"
        placeholder="New email"
        class="input"
      />

      <div class="modal-actions mt-4">
        <button type="button" class="btn-cancel" @click="closeEmailModal">Cancel</button>
        <button type="button" class="btn-primary" :disabled="savingEmail" @click="submitEmail">
          {{ savingEmail ? 'Saving…' : 'Save' }}
        </button>
      </div>
    </div>
  </div>

  <div v-if="showPhoneModal" class="modal-overlay" @click.self="closePhoneModal" @keydown.esc="closePhoneModal" tabindex="0">
    <div class="modal-card">
      <h3 class="modal-heading text-xl font-semibold mb-4">Mobile number</h3>
      <input
        v-model.trim="phoneForm.phone"
        type="tel"
        inputmode="tel"
        maxlength="30"
        placeholder="e.g. +966 5xx xxx xxx"
        class="input"
      />
      <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Leave empty and save to remove your number.</p>

      <div class="modal-actions mt-4">
        <button type="button" class="btn-cancel" @click="closePhoneModal">Cancel</button>
        <button type="button" class="btn-primary" :disabled="savingPhone" @click="submitPhone">
          {{ savingPhone ? 'Saving…' : 'Save' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
/* global defineEmits */
import { reactive, ref, onMounted, computed, watch } from 'vue';
import Swal from 'sweetalert2';

import {
  getUser,
  changeUsername,
  changePassword,
  changeEmail,
  changePhone,
  deleteUser,
  clearSearchHistory,
  fetchSavedPlace,
  useUser,
} from '@/api/user';
import { appSettings } from '@/composables/useAppSettings';
import { useAlerts } from '@/composables/useAlerts';

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
      showAlert({
        type: 'warning',
        title: 'Notifications',
        message: 'Notifications are not supported in this browser.',
      });
      appSettings.savedPlacesNotifications = false;
      return;
    }
    if (Notification.permission === 'denied') {
      showAlert({
        type: 'warning',
        title: 'Notifications',
        message: 'Notifications were blocked. Enable them in the browser settings to use this option.',
      });
      appSettings.savedPlacesNotifications = false;
      return;
    }
    if (Notification.permission === 'default') {
      const p = await Notification.requestPermission();
      if (p !== 'granted') {
        appSettings.savedPlacesNotifications = false;
        showAlert({
          type: 'warning',
          title: 'Notifications',
          message: 'Notifications were blocked. Enable them in the browser settings to use this option.',
        });
      }
    }
  }
);

function onLocationTrackingChange() {
  if (!appSettings.locationTracking) return;
  if (!navigator.geolocation) {
    showAlert({
      type: 'warning',
      title: 'Privacy & Location',
      message: 'This browser does not support geolocation.',
    });
    appSettings.locationTracking = false;
    return;
  }
  navigator.geolocation.getCurrentPosition(
    () => {},
    () => {
      showAlert({
        type: 'info',
        title: 'Privacy & Location',
        message: 'Location permission was denied or unavailable.',
      });
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
    showAlert({
      type: 'warning',
      title: 'Notice',
      message: 'Only letters, numbers, dot and underscore (3–30 chars)',
    });
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

const showEmailModal = ref(false);
const emailForm = reactive({ oldEmail: '', newEmail: '' });
const savingEmail = ref(false);

function openEmailModal() {
  emailForm.oldEmail = '';
  emailForm.newEmail = '';
  showEmailModal.value = true;
}

function closeEmailModal() {
  showEmailModal.value = false;
  emailForm.oldEmail = '';
  emailForm.newEmail = '';
}

async function submitEmail() {
  const newE = emailForm.newEmail.trim();
  if (!newE) {
    showAlert({ type: 'warning', title: 'Notice', message: 'Please enter a new email address.' });
    return;
  }
  if (user.value?.email) {
    if (!emailForm.oldEmail.trim()) {
      showAlert({ type: 'warning', title: 'Notice', message: 'Enter your current email to confirm.' });
      return;
    }
  }
  savingEmail.value = true;
  try {
    await changeEmail({
      oldEmail: user.value?.email ? emailForm.oldEmail.trim() : null,
      newEmail: newE,
    });
    const u = await getUser();
    setUser(u);
    showAlert({ type: 'success', title: 'Updated', message: 'Email updated successfully.' });
    closeEmailModal();
  } catch (e) {
    const msg =
      e.response?.data?.message ||
      e.response?.data?.error ||
      e.message ||
      'Could not update email.';
    showAlert({ type: 'danger', title: 'Error', message: msg });
  } finally {
    savingEmail.value = false;
  }
}

const showPhoneModal = ref(false);
const phoneForm = reactive({ phone: '' });
const savingPhone = ref(false);

function openPhoneModal() {
  phoneForm.phone = user.value?.phone != null ? String(user.value.phone) : '';
  showPhoneModal.value = true;
}

function closePhoneModal() {
  showPhoneModal.value = false;
  phoneForm.phone = '';
}

async function submitPhone() {
  savingPhone.value = true;
  try {
    const trimmed = phoneForm.phone.trim();
    await changePhone({ phone: trimmed === '' ? null : trimmed });
    const u = await getUser();
    setUser(u);
    showAlert({ type: 'success', title: 'Updated', message: 'Mobile number saved.' });
    closePhoneModal();
  } catch (e) {
    const msg =
      e.response?.data?.message ||
      e.response?.data?.error ||
      e.message ||
      'Could not save mobile number.';
    showAlert({ type: 'danger', title: 'Error', message: msg });
  } finally {
    savingPhone.value = false;
  }
}

const deleting = ref(false);

async function deleteAccount() {
  const { value, isConfirmed } = await Swal.fire({
    title: 'Delete account?',
    html: 'This permanently deletes your account and cannot be undone. Type <strong>DELETE</strong> to confirm.',
    input: 'text',
    inputPlaceholder: 'Type DELETE',
    showCancelButton: true,
    confirmButtonText: 'Delete Account',
    cancelButtonText: 'Cancel',
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#6b7280',
    inputValidator: (v) => {
      if (v !== 'DELETE') return 'Please type DELETE exactly to confirm.';
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
    title: 'Clear search history?',
    message: 'This removes all saved searches from the server for your account.',
    confirmText: 'Clear history',
    cancelText: 'Cancel',
  });
  if (!ok) return;
  try {
    await clearSearchHistory();
    showAlert({
      type: 'success',
      title: 'Manage Search Data',
      message: 'Search history was cleared.',
    });
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
    showAlert({ type: 'success', title: 'Data Settings', message: 'Favorites exported.' });
  } catch (e) {
    showAlert({ type: 'danger', title: 'Error', message: 'Could not export favorites.' });
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
  @apply mr-2;
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
  @apply absolute right-2 top-1/2 -translate-y-1/2
         h-8 w-8 grid place-items-center rounded
         text-gray-600 dark:text-gray-300
         hover:bg-black/5 dark:hover:bg-white/10;
}
</style>
