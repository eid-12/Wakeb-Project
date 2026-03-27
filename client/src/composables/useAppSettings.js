import { reactive, watch } from 'vue';

const STORAGE_KEY = 'wakeb.settings.v1';

const defaults = {
  language: 'English',
  theme: 'Light',
  savedPlacesNotifications: false,
  locationTracking: true,
  fontSize: 'Small',
  autoDeleteHistory: false,
  exportFavorites: false,
};

function readStored() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return { ...defaults, ...(raw ? JSON.parse(raw) : {}) };
  } catch {
    return { ...defaults };
  }
}

export const appSettings = reactive(readStored());

export function persistAppSettings() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(appSettings));
}

/** Run before Vue mount to avoid light-mode flash */
export function hydrateUiFromStorage() {
  const s = readStored();
  const root = document.documentElement;
  root.classList.toggle('dark', s.theme === 'Dark');
  root.style.fontSize = s.fontSize === 'Small' ? '14px' : '16px';
  const loc = s.language === 'Arabic' ? 'ar' : 'en';
  root.setAttribute('lang', loc);
  root.setAttribute('dir', loc === 'ar' ? 'rtl' : 'ltr');
}

function applyFromReactive(i18n) {
  const root = document.documentElement;
  root.classList.toggle('dark', appSettings.theme === 'Dark');
  root.style.fontSize = appSettings.fontSize === 'Small' ? '14px' : '16px';
  const loc = appSettings.language === 'Arabic' ? 'ar' : 'en';
  root.setAttribute('lang', loc);
  root.setAttribute('dir', loc === 'ar' ? 'rtl' : 'ltr');
  if (i18n?.global?.locale) {
    i18n.global.locale.value = loc;
  }
  persistAppSettings();
}

let started = false;

export function initAppSettings(i18n) {
  if (started) return;
  started = true;
  applyFromReactive(i18n);
  watch(
    appSettings,
    () => {
      applyFromReactive(i18n);
    },
    { deep: true }
  );
}
