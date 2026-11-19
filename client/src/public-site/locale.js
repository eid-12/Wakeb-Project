import homeSeo from './home.json';
import { siteCopy } from './copy';

export const isArabic = typeof window !== 'undefined' && /^\/ar(?:\/|$)/.test(window.location.pathname);
export const publicLanguage = isArabic ? 'ar' : 'en';
export const text = siteCopy[publicLanguage];
export const otherLanguageURL = isArabic ? '/' : '/ar/';

export function applyPublicMetadata() {
  const seo = homeSeo[publicLanguage];
  const path = window.location.pathname.replace(/\/+$/, '') || '/';
  const isHome = path === '/' || path === '/ar';
  document.documentElement.lang = seo.lang;
  document.documentElement.dir = seo.dir;
  document.title = seo.title;
  function meta(key, content, property = false) {
    const attribute = property ? 'property' : 'name';
    let tag = document.querySelector(`meta[${attribute}="${key}"]`);
    if (!tag) { tag = document.createElement('meta'); tag.setAttribute(attribute, key); document.head.append(tag); }
    tag.content = content;
  }
  meta('description', seo.description);
  meta('og:title', seo.title, true); meta('og:description', seo.description, true);
  meta('twitter:title', seo.title); meta('twitter:description', seo.description);
  meta('robots', isHome ? 'index, follow' : 'noindex, follow');
  let canonical = document.querySelector('link[rel="canonical"]');
  if (isHome) {
    if (!canonical) { canonical = document.createElement('link'); canonical.rel = 'canonical'; document.head.append(canonical); }
    canonical.href = seo.url;
  } else { canonical?.remove(); }
  document.querySelectorAll('link[data-public-home]').forEach(link => link.remove());
  if (isHome) for (const [lang, href] of [['en', homeSeo.en.url], ['ar', homeSeo.ar.url], ['x-default', homeSeo.en.url]]) {
    const link = document.createElement('link'); link.rel = 'alternate'; link.hreflang = lang; link.href = href;
    link.dataset.publicHome = 'true'; document.head.append(link);
  }
}
