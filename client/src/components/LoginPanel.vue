<template>
  <div class="login-panel">
    <h2>{{ isRegister ? "Sign Up" : "Sign In" }}</h2>

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

    <!-- Confirm Password (only for Sign Up) -->
    <div v-if="isRegister" class="input-group">
      <i class="fas fa-lock"></i>
      <input type="password" placeholder="Confirm Password" v-model="form.confirmPassword" />
    </div>

    <!-- Submit Button -->
    <button class="sign-in-btn">
      {{ isRegister ? "Sign Up" : "Sign In" }}
    </button>

    <!-- Toggle Sign Mode -->
    <p class="register-text">
      {{ isRegister ? "Already have an account?" : "Don’t have an account?" }}
      <span class="register-link" @click="toggleMode">
        {{ isRegister ? "Login" : "Register" }}
      </span>
    </p>

    <!-- Close -->
    <button class="close-btn" @click="$emit('close')">×</button>
  </div>
</template>

<script>
import { ref } from "vue";
export default {
  name: "LoginPanel",
  setup() {
    const isRegister = ref(false);
    const form = ref({
      username: "",
      password: "",
      confirmPassword: "",
    });

    const toggleMode = () => {
      isRegister.value = !isRegister.value;
      form.value = {
        username: "",
        password: "",
        confirmPassword: "",
      };
    };

    return {
      isRegister,
      form,
      toggleMode,
    };
  },
};
</script>

<style scoped>
.login-panel {
  position: absolute;
  left: 0;
  top: 64px;
  bottom: 0;
  width: 320px;
  background-color: #1e4d41;
  padding: 2rem 1.5rem;
  color: white;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 10;
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
  font-size: 1.5rem;
  color: white;
  cursor: pointer;
}
</style>
