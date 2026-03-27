import { createI18n } from 'vue-i18n';
import en from '@/locales/en.json';
import ar from '@/locales/ar.json';

function initialLocale() {
  try {
    const raw = localStorage.getItem('wakeb.settings.v1');
    const s = raw ? JSON.parse(raw) : {};
    return s.language === 'Arabic' ? 'ar' : 'en';
  } catch {
    return 'en';
  }
}

export const i18n = createI18n({
  legacy: false,
  locale: initialLocale(),
  fallbackLocale: 'en',
  globalInjection: true,
  messages: { en, ar },
});
