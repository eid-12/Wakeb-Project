<template>
  <div class="login-panel">
    <header class="login-panel__header">
      <h2 class="login-panel__title">{{ isRegister ? 'Sign Up' : 'Sign In' }}</h2>
      <button type="button" class="close-btn" @click="emit('close')" aria-label="Close">
        <span aria-hidden="true">×</span>
      </button>
    </header>

    <p class="login-panel__subtitle">
      {{ isRegister ? 'Create an account with username and password.' : 'Welcome back — sign in to continue.' }}
    </p>

    <div class="login-panel__fields">
      <div class="input-group">
        <i class="fas fa-user input-group__icon" aria-hidden="true"></i>
        <input
          v-model="form.username"
          type="text"
          name="username"
          autocomplete="username"
          placeholder="Username"
          class="input-group__input"
        />
      </div>

      <div class="input-group input-group--trail">
        <i class="fas fa-lock input-group__icon" aria-hidden="true"></i>
        <input
          v-model="form.password"
          :type="show.password ? 'text' : 'password'"
          name="password"
          autocomplete="current-password"
          placeholder="Password"
          class="input-group__input"
        />
        <button
          type="button"
          class="toggle-eye"
          @click="show.password = !show.password"
          :aria-label="show.password ? 'Hide password' : 'Show password'"
        >
          <i :class="show.password ? 'fas fa-eye-slash' : 'fas fa-eye'" aria-hidden="true"></i>
        </button>
      </div>

      <div v-if="isRegister" class="input-group input-group--trail">
        <i class="fas fa-lock input-group__icon" aria-hidden="true"></i>
        <input
          v-model="form.confirmPassword"
          :type="show.confirm ? 'text' : 'password'"
          autocomplete="new-password"
          placeholder="Confirm password"
          class="input-group__input"
        />
        <button
          type="button"
          class="toggle-eye"
          @click="show.confirm = !show.confirm"
          :aria-label="show.confirm ? 'Hide password' : 'Show password'"
        >
          <i :class="show.confirm ? 'fas fa-eye-slash' : 'fas fa-eye'" aria-hidden="true"></i>
        </button>
      </div>
    </div>

    <button type="button" class="sign-in-btn" @click="handleSubmit">
      {{ isRegister ? 'Sign Up' : 'Sign In' }}
    </button>

    <p class="register-text">
      {{ isRegister ? 'Already have an account?' : 'Don’t have an account?' }}
      <button type="button" class="register-link" @click="toggleMode">
        {{ isRegister ? 'Login' : 'Register' }}
      </button>
    </p>
  </div>
</template>

<script setup>
/* global defineEmits defineExpose */
import { ref, reactive } from 'vue';
import { useAlerts } from '@/composables/useAlerts';
import { registerUser, loginUser, getUser, useUser, deleteTokenCookie } from '@/api/user';

const { showAlert } = useAlerts();
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
  confirm: false,
});

const resetForm = () => {
  form.value = {
    username: '',
    password: '',
    confirmPassword: '',
  };
};

function apiErrorMessage(err, fallback) {
  const status = err.response?.status;
  const d = err.response?.data;
  if (typeof d === 'string' && d.trim()) return d.trim();
  if (d && typeof d.message === 'string' && d.message.trim()) return d.message.trim();
  if (status === 409) {
    return 'Username already exists. Please choose a different username.';
  }
  return fallback;
}

function toggleMode() {
  isRegister.value = !isRegister.value;
  resetForm();
}

function logoutUser() {
  deleteTokenCookie();
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
        message: 'Password must be at least 6 characters and contain uppercase, lowercase letters, and a number.',
      });
      return;
    }
    if (!isValidUsername(username)) {
      showAlert({
        type: 'warning',
        title: 'Invalid Username',
        message:
          'Username must be at least 3 characters and contain only letters, numbers, hyphens (-), or underscores (_). It cannot start or end with - or _.',
      });
      return;
    }
    try {
      await registerUser({ username, password });
      const u = await getUser();
      setUser(u);
      showAlert({ type: 'success', title: 'Account Created', message: 'Welcome! You are signed in.' });
      emit('success');
      resetForm();
    } catch (err) {
      const msg = apiErrorMessage(err, 'Registration failed. Please try again.');
      showAlert({ type: 'danger', title: 'Registration Error', message: msg });
    }
  } else {
    try {
      await loginUser({ username, password });
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
  logoutUser,
});
</script>

<style scoped>
.login-panel {
  position: relative;
  width: 320px;
  max-width: 100%;
  min-height: 100%;
  box-sizing: border-box;
  padding: 1.5rem 1.35rem 1.75rem;
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
  color: #f8fafc;
  background: linear-gradient(180deg, #1a5c4d 0%, #153d35 55%, #122f29 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 4px 0 32px rgba(0, 0, 0, 0.18);
}

.login-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 0.75rem;
  padding-right: 0.25rem;
}

.login-panel__title {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.login-panel__subtitle {
  margin: -0.35rem 0 0;
  font-size: 0.8125rem;
  line-height: 1.45;
  color: rgba(248, 250, 252, 0.65);
}

.login-panel__fields {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.55rem 0.85rem;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.input-group:focus-within {
  border-color: rgba(167, 243, 208, 0.45);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.18);
  background: rgba(0, 0, 0, 0.28);
}

.input-group__icon {
  flex-shrink: 0;
  width: 1.1rem;
  text-align: center;
  color: rgba(167, 243, 208, 0.75);
  font-size: 0.9rem;
}

.input-group__input {
  flex: 1;
  min-width: 0;
  width: 100%;
  padding: 0.35rem 0;
  border: none;
  background: transparent;
  outline: none;
  color: #f8fafc;
  font-size: 0.9375rem;
}

.input-group--trail .input-group__input {
  padding-right: 2rem;
}

.input-group__input::placeholder {
  color: rgba(248, 250, 252, 0.42);
}

.toggle-eye {
  position: absolute;
  right: 0.65rem;
  top: 50%;
  transform: translateY(-50%);
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  padding: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: rgba(248, 250, 252, 0.65);
  cursor: pointer;
  transition:
    color 0.15s ease,
    background 0.15s ease;
}

.toggle-eye:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.toggle-eye:focus-visible {
  outline: 2px solid #fde68a;
  outline-offset: 1px;
}

.toggle-eye i {
  font-size: 0.9rem;
}

.sign-in-btn {
  margin-top: 0.2rem;
  width: 100%;
  padding: 0.8rem 1rem;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #fff;
  background: linear-gradient(180deg, #d97757 0%, #c45d3a 100%);
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.12) inset,
    0 4px 14px rgba(0, 0, 0, 0.22);
  transition:
    transform 0.15s ease,
    box-shadow 0.2s ease,
    filter 0.2s ease;
}

.sign-in-btn:hover {
  filter: brightness(1.06);
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.14) inset,
    0 6px 18px rgba(0, 0, 0, 0.25);
}

.sign-in-btn:active {
  transform: translateY(1px);
}

.sign-in-btn:focus-visible {
  outline: 2px solid #fde68a;
  outline-offset: 3px;
}

.register-text {
  margin: 0.15rem 0 0;
  font-size: 0.8125rem;
  line-height: 1.5;
  color: rgba(248, 250, 252, 0.55);
  text-align: center;
}

.register-link {
  margin: 0;
  padding: 0;
  border: none;
  background: none;
  font: inherit;
  font-weight: 600;
  color: #f5d78e;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 3px;
  transition: color 0.15s ease;
}

.register-link:hover {
  color: #fde68a;
}

.register-link:focus-visible {
  outline: 2px solid #fde68a;
  outline-offset: 2px;
  border-radius: 4px;
}

.close-btn {
  flex-shrink: 0;
  display: grid;
  place-items: center;
  width: 2.25rem;
  height: 2.25rem;
  margin: -0.2rem -0.15rem 0 0;
  padding: 0;
  border: none;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.15);
  color: rgba(248, 250, 252, 0.85);
  font-size: 1.35rem;
  line-height: 1;
  cursor: pointer;
  transition:
    color 0.15s ease,
    background 0.15s ease;
}

.close-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.close-btn:focus-visible {
  outline: 2px solid #fde68a;
  outline-offset: 2px;
}
</style>
