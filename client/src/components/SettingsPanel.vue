<template>
  <h1 class="py-10 text-3xl font-bold text-center text-emerald-700">Settings</h1>

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
          <label><input type="radio" value="Dark"  v-model="settings.theme"  /> Dark</label>
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
        <button class="btn-setting" @click="showUsernameModal = true">
          <i class="fa-solid fa-user-pen"></i> Change Username
        </button>
        <button class="btn-setting-danger" :disabled="deleting" @click="deleteAccount">
          <i class="fas fa-trash-alt"></i> {{ deleting ? 'Deleting…' : 'Delete Account' }}
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
        <label><input type="radio" value="Small"  v-model="settings.fontSize" /> Small</label>
        <label class="ml-4"><input type="radio" value="Medium" v-model="settings.fontSize" /> Medium</label>
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

  <!-- =================== Modals =================== -->

  <!-- Password Modal -->
  <div v-if="showPasswordModal" class="modal-overlay" @keydown.esc="closePasswordModal" tabindex="0">
    <div class="modal-card">
      <h3 class="text-xl font-semibold mb-4" id="changtit">Change Password</h3>


      <input v-model="passwordForm.current" :type= "text" placeholder="Current password" class="input " />
 

    <div class="relative">

      <input v-model="passwordForm.new"     :type="showPwd.new  ? 'text' : 'password'" placeholder="New password"      class="input pr-10" />
            <button
        type="button"
        class="eye-btn"
        @click="showPwd.new  = !showPwd.new "
        :aria-label="showPwd.new  ? 'Hide password' : 'Show password'">
        <i :class="showPwd.new  ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye'"></i>
      </button>

</div>
          <div class="relative">

      <input v-model="passwordForm.confirm" :type="showPwd.confirm  ? 'text' : 'password'" placeholder="Confirm new password" class="input pr-10" />
            <button
        type="button"
        class="eye-btn"
        @click="showPwd.confirm  = !showPwd.confirm "
        :aria-label="showPwd.confirm  ? 'Hide password' : 'Show password'">
        <i :class="showPwd.confirm  ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye'"></i>
      </button>

      </div>
      <div class="modal-actions">
        <button class="btn-cancel"  @click="closePasswordModal" id="changtit">Cancel</button>
        <button class="btn-primary" @click="submitPassword">Save</button>
      </div>
    </div>
  </div>

  <!-- Email Modal -->
  <div v-if="showEmailModal" class="modal-overlay" @keydown.esc="closeEmailModal" tabindex="0">
    <div class="modal-card">
      <h3 class="text-xl font-semibold mb-4" id="changtit">Change Email</h3>

      <input v-model="emailForm.newEmail" type="email"    placeholder="New email address" class="input" />
      <input v-model="emailForm.oldEmail" type="email" placeholder="Current Email or Confirm "  class="input" />

      <div class="modal-actions">
        <button class="btn-cancel"  @click="closeEmailModal" id="changtit">Cancel</button>
        <button class="btn-primary" @click="submitEmail">Save</button>
      </div>
    </div>
  </div>

  <!-- Username Modal -->
  <div v-if="showUsernameModal" class="modal-overlay" @click.self="closeUsernameModal" @keydown.esc="closeUsernameModal" tabindex="0">
    <div class="modal-card">
      <h3 class="text-xl font-semibold mb-4" id="changtit" >Change Username</h3>

      <input v-model.trim="usernameForm.newUsername" type="text" placeholder="New username" class="input" />

      <p v-if="invalidUsername" class="text-red-500 text-sm mt-1">
        Only letters, numbers, . _ - (3–20 chars)
      </p>

      <div class="modal-actions mt-4">
        <button class="btn-cancel"  @click="closeUsernameModal" id="changtit" >Cancel</button>
        <button class="btn-primary"
                :disabled="savingUsername || invalidUsername || !usernameForm.newUsername.trim()"
                @click="submitUsername">
          {{ savingUsername ? 'Saving...' : 'Save' }}
        </button>
      </div>
    </div>
  </div>

</template>

<script setup>
/* global defineProps, defineEmits */

import { reactive, ref, onMounted, computed } from 'vue';

import {
  getUser,
  changeEmail,
  changeUsername,
  changePassword,
  deleteUser,
  useUser 
} from '@/api/user';
import { useAlerts } from '@/composables/useAlerts';

const { showAlert, showConfirm } = useAlerts();
const emit = defineEmits(['change-menu','logout']);

logout
const settings = reactive({
  language: 'English',
  theme: 'Light',
  savedPlacesNotifications: false,
  locationTracking: true,
  fontSize: 'Small',
  autoDeleteHistory: false,
  syncPlaces: false,
  exportFavorites: false
});
const showPwd = reactive({ new: false, confirm: false });

const { user, setUser } = useUser();

onMounted(async () => {
  const u = await getUser();
  setUser(u);
});

const showPasswordModal = ref(false);
const passwordForm = reactive({ current: '', new: '', confirm: '' });
const passwordLoading = ref(false);

const mismatch = computed(() =>
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
    showAlert({ type: 'danger', title: 'Error', message: e.response?.data?.message || 'Error changing password or The Wrong Current Password' });
  } finally {
    passwordLoading.value = false;
  }
}

const showEmailModal = ref(false);
const emailForm = reactive({ newEmail: '', oldEmail: '' });

function closeEmailModal() {
  showEmailModal.value = false;
  Object.assign(emailForm, { newEmail: '', oldEmail: '' });
}

async function submitEmail() {
if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
  showAlert({ type: 'warning', title: 'Notice', message: 'Invalid email format!' });
  return;
}

  try {
    await changeEmail(emailForm.newEmail, emailForm.oldEmail);
    showAlert({ type: 'success', title: 'Updated', message: 'Email updated successfully ✔' });
    closeEmailModal();
  } catch (e) {
    showAlert({ type: 'danger', title: 'Error', message: e.response?.data?.message || 'Error updating email' });
  }
}

const showUsernameModal = ref(false);
const usernameForm = reactive({ newUsername: '' });
const savingUsername = ref(false);

const invalidUsername = computed(() =>
  usernameForm.newUsername.trim() &&
  !/^[A-Za-z0-9._-]{3,20}$/.test(usernameForm.newUsername.trim())
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
    showAlert({ type: 'warning', title: 'Notice', message: 'Username must be 3-20 characters long and may include . _ - only' });
    return;
  }
  const confirm = await showConfirm({
    title: 'Confirm Change',
  message: `Are you sure you want to change the username to "${usernameForm.newUsername}"?`

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
  const confirm = await showConfirm({
    title: 'Confirm Deletion',
    message: 'Are you sure you want to permanently delete your account? This action cannot be undone.⚠️'
  });
  if (!confirm) return;
  deleting.value = true;
  try {
    await deleteUser(user.value.id);
    showAlert({ type: 'success', title: 'Deleted', message: 'Account deleted successfully' });
      emit('logout');
  } catch (e) {
    showAlert({ type: 'danger', title: 'Error', message: e.response?.data?.message || 'An error occurred while deleting the account' });
  } finally {
    deleting.value = false;
  }
}

function manageSearchData() {
  emit('change-menu', 'history');
}
</script>


<style scoped>
/* ============= Tailwind shortcuts ============= */
.btn-setting {
  @apply w-full flex items-center justify-between border border-emerald-600
         text-emerald-700 dark:text-emerald-300
         px-3 py-2 rounded mb-2
         hover:bg-emerald-50 dark:hover:bg-emerald-900/30
         transition-colors duration-150 ease-in-out;
}
.btn-setting i { @apply mr-2; }

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
  @apply bg-emerald-600 hover:bg-emerald-700 text-white px-4 py-2 rounded;
}
#changtit{
  color: white;
}
.eye-btn {
  @apply absolute right-2 top-1/2 -translate-y-1/2
         h-8 w-8 grid place-items-center rounded
         text-gray-600 dark:text-gray-300
         hover:bg-black/5 dark:hover:bg-white/10;
}

</style>
