<template>
  <div class="alerts-wrapper">
    <transition-group name="fade" tag="div">
      <div v-for="a in alerts"
           :key="a.id"
           :class="['alert', 'alert--' + a.type]"
           role="alert">
        <strong class="alert__label">{{ labels[a.type] }}</strong>
        <span class="alert__msg">{{ a.message }}</span>

        <!-- زر الإغلاق -->
        <button class="alert__close"
                aria-label="Close"
                @click="removeAlert(a.id)">
          ×
        </button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useAlerts } from '@/composables/useAlerts';

const { alerts, removeAlert } = useAlerts();

const labels = {
  success: 'Success!',
  danger : 'Danger!',
  info   : 'Info!',
  warning: 'Warning!',
};
</script>

<style scoped>
.alerts-wrapper {
  position: fixed;
  top: 14px;
  right: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 9999;
}

.alert {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 14px 18px;
  min-width: 250px;
  color: #fff;
  font-size: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0,0,0,.15);
}

.alert__close {
  margin-left: auto;
  background: none;
  border: none;
  color: inherit;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
}

.alert--success { background:#27ae60; }
.alert--danger  { background:#e74c3c; }
.alert--info    { background:#3498db; }
.alert--warning { background:#f39c12; }

.fade-enter-active, .fade-leave-active { transition: all .3s ease; }
.fade-enter-from   { opacity: 0; transform: translateY(-8px); }
.fade-leave-to     { opacity: 0; transform: translateY(-8px); }
</style>
