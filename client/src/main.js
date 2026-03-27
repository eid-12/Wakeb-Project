import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { i18n } from './i18n';
import { hydrateUiFromStorage, initAppSettings } from '@/composables/useAppSettings';
import '../src/assets/tailwind.css';
import '../src/assets/dark-theme.css';
import 'sweetalert2/dist/sweetalert2.min.css';

hydrateUiFromStorage();

const app = createApp(App);
app.use(router);
app.use(i18n);
initAppSettings(i18n);
app.mount('#app');

