<template>
  <div class="admin-panel p-6 space-y-6">
    <h1 class="text-2xl font-bold mb-4 text-emerald-700">Admin Dashboard</h1>

    <!-- Stats -->
    <div   class="grid grid-cols-3 gap-4 text-center">
      <div class="bg-gray-100 p-4 rounded">👥 Total Users: {{ totalElements  }}</div>
      <div class="bg-green-100 p-4 rounded">🟢 Active Users: {{activeUserCount }}   </div>
      <div class="bg-blue-100 p-4 rounded">🛡️ Admins: {{ activeAdminCount}}</div>
    </div>

    <!-- Users Table -->
    <table class="w-full mt-6 border text-center">
      <thead>
        <tr class="bg-gray-200">
          <th>Name</th>
          <th>Email</th>
          <th>Role</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user1 in users" v-show="  user1.id !=user.id" :key="user1.id">
          <td>{{ user1.name }}</td>
          <td>{{ user1.email }}</td>
          <td>{{ user1.isUser ? 'User' : 'Admin' }}</td>
          <td>{{ user1.active ? 'Active' : 'Inactive' }}</td>
<td class="px-6 py-4 text-center space-x-2">
  <button v-show="user1.active "
    @click="toggleRole(user1)"
    class="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-700 hover:bg-blue-200 rounded-full text-sm font-medium transition"
  >
    <i class="fas fa-user-shield"></i> Toggle
  </button>

  <button v-show="user1.active "
    @click="resetPassword(user1)"
    class="inline-flex items-center gap-1 px-3 py-1 bg-yellow-100 text-yellow-800 hover:bg-yellow-200 rounded-full text-sm font-medium transition"
  >
    <i class="fas fa-key"></i> Reset
  </button>

  <button
    @click="deleteUsers(user1.id) " v-show="user1.active "
    class="inline-flex items-center gap-1 px-3 py-1 bg-red-100 text-red-700 hover:bg-red-200 rounded-full text-sm font-medium transition"
  >
    <i class="fas fa-trash-alt"></i> Delete
  </button>
</td>

        </tr>
      </tbody>
    </table>

    <!-- Pagination -->
<div class="flex justify-center items-center gap-4 mt-6">
  <button
    @click="prevPage"

    :disabled="page <= 0"
    class="px-4 py-2 rounded-md border border-gray-300 bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition"
  >
    ⬅️ Prev
  </button>

  <span class="text-sm text-gray-600 font-medium">
    Page {{ page + 1 }}
  </span>

  <button
    @click="nextPage"
    :disabled="!hasNextPage"
    class="px-4 py-2 rounded-md border border-gray-300 bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition"
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
import { text } from 'express'
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
    type : 'text',
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
