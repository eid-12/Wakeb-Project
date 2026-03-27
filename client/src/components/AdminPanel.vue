<template>
  <div class="admin-panel p-4 sm:p-6 space-y-6 w-full max-w-6xl mx-auto text-slate-800 dark:text-slate-100">
    <h1 class="text-2xl font-bold mb-4 text-emerald-700 dark:text-emerald-400">Admin Dashboard</h1>

    <!-- Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-center">
      <div
        class="bg-gray-100 dark:bg-slate-800 dark:border dark:border-slate-600/80 text-slate-800 dark:text-slate-200 p-4 rounded-lg"
      >
        👥 Total Users: {{ totalElements }}
      </div>
      <div
        class="bg-green-100 dark:bg-emerald-950/50 dark:border dark:border-emerald-800/50 text-green-900 dark:text-emerald-200 p-4 rounded-lg"
      >
        🟢 Active Users: {{ activeUserCount }}
      </div>
      <div
        class="bg-blue-100 dark:bg-slate-800 dark:border dark:border-blue-900/40 text-blue-900 dark:text-blue-200 p-4 rounded-lg"
      >
        🛡️ Admins: {{ activeAdminCount }}
      </div>
    </div>

    <!-- Users Table -->
    <div class="admin-table-wrap overflow-x-auto -mx-2 px-2">
      <table
        class="w-full mt-6 border border-gray-200 dark:border-slate-600 text-center min-w-[640px] text-sm"
      >
        <thead>
          <tr class="bg-gray-200 dark:bg-slate-700 text-slate-800 dark:text-slate-100">
            <th class="px-2 py-2">Name</th>
            <th class="px-2 py-2">Email</th>
            <th class="px-2 py-2">Role</th>
            <th class="px-2 py-2">Status</th>
            <th class="px-2 py-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user1 in users"
            :key="user1.id"
            class="border-b border-gray-100 dark:border-slate-700 odd:bg-transparent even:bg-slate-50/80 dark:even:bg-slate-800/40"
          >
            <td class="px-2 py-3 text-slate-800 dark:text-slate-200">{{ user1.name }}</td>
            <td class="px-2 py-3 text-slate-700 dark:text-slate-300">{{ user1.email }}</td>
            <td class="px-2 py-3">{{ user1.isUser ? 'User' : 'Admin' }}</td>
            <td class="px-2 py-3">{{ user1.active ? 'Active' : 'Inactive' }}</td>
            <td class="px-4 py-3 text-center space-x-2">
              <button
                v-show="user1.active"
                type="button"
                @click="toggleRole(user1)"
                class="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 dark:bg-blue-950/60 text-blue-700 dark:text-blue-300 hover:bg-blue-200 dark:hover:bg-blue-900/50 rounded-full text-sm font-medium transition"
              >
                <i class="fas fa-user-shield"></i> Toggle
              </button>

              <button
                v-show="user1.active"
                type="button"
                @click="resetPassword(user1)"
                class="inline-flex items-center gap-1 px-3 py-1 bg-yellow-100 dark:bg-amber-950/50 text-yellow-800 dark:text-amber-200 hover:bg-yellow-200 dark:hover:bg-amber-900/45 rounded-full text-sm font-medium transition"
              >
                <i class="fas fa-key"></i> Reset
              </button>

              <button
                type="button"
                @click="deleteUsers(user1.id)"
                v-show="user1.active && user1.id !== user?.id"
                class="inline-flex items-center gap-1 px-3 py-1 bg-red-100 dark:bg-red-950/50 text-red-700 dark:text-red-300 hover:bg-red-200 dark:hover:bg-red-900/45 rounded-full text-sm font-medium transition"
              >
                <i class="fas fa-trash-alt"></i> Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center items-center gap-4 mt-6 flex-wrap">
      <button
        type="button"
        @click="prevPage"
        :disabled="page <= 0"
        class="px-4 py-2 rounded-md border border-gray-300 dark:border-slate-600 bg-white dark:bg-slate-800 text-gray-700 dark:text-slate-200 hover:bg-gray-100 dark:hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition min-h-[44px]"
      >
        ⬅️ Prev
      </button>

      <span class="text-sm text-gray-600 dark:text-slate-400 font-medium"> Page {{ page + 1 }} </span>

      <button
        type="button"
        @click="nextPage"
        :disabled="!hasNextPage"
        class="px-4 py-2 rounded-md border border-gray-300 dark:border-slate-600 bg-white dark:bg-slate-800 text-gray-700 dark:text-slate-200 hover:bg-gray-100 dark:hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition min-h-[44px]"
      >
        Next ➡️
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed  } from 'vue'
import { useUser, getUser , deleteUser, toggleUserRole,resetUserPassword ,fetchUsersByPage} from '@/api/user'
import { useAlerts } from '@/composables/useAlerts'
const { showAlert, showConfirm , showPasswordPrompt } = useAlerts()      

const { user, setUser } = useUser()

const users = ref([])
const page = ref(0)
const totalElements = ref(0)
const activeAdminCount = ref(0)
const activeUserCount = ref(0)
const totalPages = ref(1)
const limit = 10;
const hasNextPage = computed(() => page.value + 1 < totalPages.value)

function prevPage() {
  if (page.value > 0) {
    page.value--
    fetchUsers()
  }
}

function nextPage() {
  if (hasNextPage.value) {
    page.value++
    fetchUsers()
  }
}


async function fetchUsers() {
  try {
    const res = await  fetchUsersByPage(page.value ,limit)
    const {
      users: { content, totalPages: tp, totalElements: te },
      activeAdminCount: adminCount,
      activeUserCount: userCount
    } = res.data
    users.value = content
    totalPages.value = tp
    totalElements.value = te
    activeAdminCount.value = adminCount
    activeUserCount.value = userCount

    const u = await getUser()
    setUser(u)
  } catch (err) {
    console.error(err)
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to load users'
    })
  }
}

async function toggleRole(user1) {
  try {
    await toggleUserRole(user1.id)
    fetchUsers()
    showAlert({
      type: 'success',
      title: 'Role Updated',
      message: `User "${user1.name}" role has been toggled`
    })
  } catch {
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to update role'
    })
  }
}

async function resetPassword(user1) {
  const newPassword = await showPasswordPrompt({
    title: `Reset Password for ${user1.name}`,
    label: 'New Password',
    confirmText: 'Reset',
    cancelText: 'Cancel'
  });

  if (newPassword) {
    try {
      await resetUserPassword(user1.id,  newPassword)
      showAlert({
        type: 'success',
        title: 'Password Reset',
        message: 'Password reset successfully'
      })
    } catch {
      showAlert({
        type: 'danger',
        title: 'Error',
        message: 'Failed to reset password'
      })
    }
  }
}



async function deleteUsers(id) {
  const ok = await showConfirm({
    title: 'Delete User',
    message: 'Are you sure you want to delete this user?'
  })
  if (!ok) return

  try {
    await deleteUser(id)
    fetchUsers()
    showAlert({
      type: 'success',
      title: 'User Deleted',
      message: 'User was deleted successfully'
    })
  } catch {
    showAlert({
      type: 'danger',
      title: 'Error',
      message: 'Failed to delete user'
    })
  }
}

onMounted(fetchUsers)

</script>
