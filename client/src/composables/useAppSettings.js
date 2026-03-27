import { reactive, watch } from 'vue';

const STORAGE_KEY = 'wakeb.settings.v1';

const defaults = {
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
    const parsed = raw ? JSON.parse(raw) : {};
    if (parsed && typeof parsed === 'object') {
      delete parsed.language;
    }
    return { ...defaults, ...parsed };
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
  root.setAttribute('lang', 'en');
  root.setAttribute('dir', 'ltr');
}

function applyFromReactive() {
  const root = document.documentElement;
  root.classList.toggle('dark', appSettings.theme === 'Dark');
  root.style.fontSize = appSettings.fontSize === 'Small' ? '14px' : '16px';
  root.setAttribute('lang', 'en');
  root.setAttribute('dir', 'ltr');
  persistAppSettings();
}

let started = false;

export function initAppSettings() {
  if (started) return;
  started = true;
  applyFromReactive();
  watch(
    appSettings,
    () => {
      applyFromReactive();
    },
    { deep: true }
  );
}
