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
      <input type="password" placeholder="Password" v-model="form.password" />
    </div>





    <!-- Confirm Password (for Sign-Up) -->
    <div v-if="isRegister" class="input-group">
      <i class="fas fa-lock"></i>
      <input
        type="password"
        placeholder="Confirm Password"
        v-model="form.confirmPassword"
      />
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
/* global defineEmits */  
import { ref } from 'vue';
import axios from 'axios';
// eslint-disable-next-line no-undef
const emit = defineEmits(['close', 'success']);          // events to parent
const API_URL = 'http://localhost:3001/users';           // JSON-Server endpoint

/* reactive state */
const isRegister = ref(false);
const form = ref({ username: '', password: '', confirmPassword: '' });

const resetForm = () =>
  (form.value = { username: '', password: '', confirmPassword: '' });

function toggleMode() {
  isRegister.value = !isRegister.value;
  resetForm();
}

async function handleSubmit() {
  if (!form.value.username || !form.value.password) {
    alert('Please fill in all required fields.');
    return;
  }

  if (isRegister.value) {
    /* ---------- Sign-Up flow ---------- */
    if (form.value.password !== form.value.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }
    try {
      const exists = await axios.get(
        `${API_URL}?username=${encodeURIComponent(form.value.username)}`
      );
      if (exists.data.length) {
        alert('Username already taken.');
        return;
      }
      await axios.post(API_URL, {
        username: form.value.username,
        password: form.value.password,
      });
      alert('Account created! Please sign in.');
      toggleMode(); // switch to Sign-In
    } catch (err) {
      console.error(err);
      alert('Registration failed. Please try again.');
    }
  } else {
    /* ---------- Sign-In flow ---------- */
    try {
      const res = await axios.get(
        `${API_URL}?username=${encodeURIComponent(
          form.value.username
        )}&password=${encodeURIComponent(form.value.password)}`
      );
      if (res.data.length) {
        emit('success'); // notify parent
        resetForm();
      } else {
        alert('Invalid username or password.');
      }
    } catch (err) {
      console.error(err);
      alert('Login failed. Please try again.');
    }
  }
}
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
</style>
