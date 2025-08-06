<template>
  <div class="login-panel">
    <h2>{{ isRegister ? 'Sign Up' : 'Sign In' }}</h2>

    <!-- Username -->
    <div class="input-group">
      <i class="fas fa-user"></i>
      <input type="text" placeholder="Username" v-model="form.username" />
    </div>

    <!-- Password -->
<div class="input-group">
  <i class="fas fa-lock"></i>
  <input
    :type="show.password ? 'text' : 'password'"
    placeholder="Password"
    v-model="form.password"
  />
  <button
    type="button"
    class="toggle-eye"
    @click="show.password = !show.password"
    :aria-label="show.password ? 'Hide password' : 'Show password'"
  >
    <i :class="show.password ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
  </button>
</div>

<!-- Confirm Password -->
<div v-if="isRegister" class="input-group">
  <i class="fas fa-lock"></i>
  <input
    :type="show.confirm ? 'text' : 'password'"
    placeholder="Confirm Password"
    v-model="form.confirmPassword"
  />
  <button
    type="button"
    class="toggle-eye"
    @click="show.confirm = !show.confirm"
    :aria-label="show.confirm ? 'Hide password' : 'Show password'"
  >
    <i :class="show.confirm ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
  </button>
</div>


    <!-- Submit -->
    <button class="sign-in-btn" @click="handleSubmit">
      {{ isRegister ? 'Sign Up' : 'Sign In' }}
    </button>

    <!-- Toggle Mode -->
    <p class="register-text">
      {{ isRegister ? 'Already have an account?' : 'Don’t have an account?' }}
      <span class="register-link" @click="toggleMode">
        {{ isRegister ? 'Login' : 'Register' }}
      </span>
    </p>

    <!-- Close -->
    <button class="close-btn" @click="emit('close')">×</button>
  </div>
</template>

<script setup>
/* global defineEmits defineExpose */  
import { ref ,reactive} from 'vue';
import { useAlerts } from '@/composables/useAlerts';
import { registerUser, loginUser, getUser, useUser } from '@/api/user'

const { showAlert, showConfirm } = useAlerts();
const { setUser, clearUser } = useUser();

const emit = defineEmits(['close', 'success']);

const isRegister = ref(false);
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
});

const show = reactive({
  password: false,
  confirm: false
});

const resetForm = () => {
  form.value = {
    username: '',
    password: '',
    confirmPassword: '',
  };
};

function toggleMode() {
  isRegister.value = !isRegister.value;
  resetForm();
}
 function logoutUser() {
  clearUser();
  localStorage.removeItem('savedPlaces');
  sessionStorage.removeItem('coords');
}
function isValidPassword(password) {
  const minLength = 6;
  const hasUppercase = /[A-Z]/.test(password);
  const hasLowercase = /[a-z]/.test(password);
  const hasNumber = /[0-9]/.test(password);
  return (
    password.length >= minLength &&
    hasUppercase &&
    hasLowercase &&
    hasNumber
  );
}


function isValidUsername(username) {
  const minLength = 3;
  const validPattern = /^[a-zA-Z0-9_-]+$/; 
  const startsOrEndsWithDash = /^[-_]|[-_]$/.test(username); 

  return (
    username.length >= minLength &&
    validPattern.test(username) &&
    !startsOrEndsWithDash
  );
}

async function handleSubmit() {
  const { username, password, confirmPassword } = form.value;

  if (!username || !password) {
    showAlert({ type: 'warning', title: 'Missing Fields', message: 'Please fill in all required fields.' });
    return;
  }


  if (isRegister.value) {
    if (password !== confirmPassword) {
      showAlert({ type: 'warning', title: 'Mismatch', message: 'Passwords do not match.' });
      return;
    }
    if (!isValidPassword(password)) {
      showAlert({
        type: 'warning',
        title: 'Weak Password',
        message: 'Password must be at least 6 characters and contain uppercase, lowercase letters, and a number.'
      });
      return;
    }
    if (!isValidUsername(username)) {
    showAlert({
      type: 'warning',
      title: 'Invalid Username',
      message: 'Username must be at least 3 characters and contain only letters, numbers, hyphens (-), or underscores (_). It cannot start or end with - or _.'
    });
    return;
    }
    try {
      await registerUser({ username, password });
      showAlert({ type: 'success', title: 'Account Created', message: 'Account created! Please sign in.' });
      emit('success');
    } catch (err) {
      const msg = err.response?.data || 'Registration failed. Please try again.';
      showAlert({ type: 'danger', title: 'Registration Error', message: msg });
    }
  } else {
    try {
      await loginUser( { username, password });
      const u = await getUser();
      setUser(u);

      showAlert({ type: 'success', title: 'Login Successful', message: 'Welcome!' });
      emit('success');
      resetForm();
    } catch (err) {
      const msg = 'Invalid username or password.';
      showAlert({ type: 'danger', title: 'Login Failed', message: msg });
    }
  }
}
defineExpose({
  logoutUser
})
</script>



<style scoped>
.login-panel {
  position: relative;
  left: 0;
  top: 0;
  bottom: 0;
  width: 320px;
  background-color: #1e4d41;
  padding: 2rem 1.5rem;
  color: white;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-group {
  display: flex;
  align-items: center;
  background-color: #163b33;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
}

.input-group i {
  margin-right: 0.5rem;
}

.input-group input {
  background: none;
  border: none;
  outline: none;
  color: white;
  flex: 1;
}

.sign-in-btn {
  background-color: #d58b4d;
  border: none;
  padding: 0.75rem;
  color: white;
  font-weight: bold;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.sign-in-btn:hover {
  background-color: #bf763d;
}

.register-text {
  font-size: 0.85rem;
  color: #ccc;
}

.register-link {
  color: #f0c04f;
  cursor: pointer;
}

.close-btn {
  position: absolute;
  top: 0.5rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.6rem;
  color: white;
  cursor: pointer;
  
}


.input-group { position: relative; display: flex; align-items: center; gap: 8px; }
.input-group input { width: 100%; padding-right: 2.25rem; }
.toggle-eye {
  position: absolute; right: 8px; top: 50%; transform: translateY(-50%);
  background: transparent; border: 0; cursor: pointer; padding: 4px;
}
.toggle-eye i { font-size: 1rem; }

</style>
