const fs = require('fs');
const path = require('path');
const base = path.resolve(__dirname, '..');
const locales = require('../src/public-site/home.json');
const template = fs.readFileSync(path.join(base, 'dist/index.html'), 'utf8').replace(/<link[^>]+rel="canonical"[^>]*>/g, '');
const escape = value => value.replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
function localized(seo) {
  let html = template.replace(/<html[^>]*>/, `<html lang="${seo.lang}" dir="${seo.dir}">`).replace(/<title>.*?<\/title>/s, () => `<title>${escape(seo.title)}</title>`);
  for (const key of ['description', 'og:description', 'twitter:description']) html = html.replace(new RegExp(`(<meta (?:name|property)="${key}" content=")[^"]*(">)`), (_, a, b) => a + escape(seo.description) + b);
  for (const key of ['og:title', 'twitter:title']) html = html.replace(new RegExp(`(<meta (?:name|property)="${key}" content=")[^"]*(">)`), (_, a, b) => a + escape(seo.title) + b);
  const alternates = [['en', locales.en.url], ['ar', locales.ar.url], ['x-default', locales.en.url]].map(([lang, href]) => `<link rel="alternate" hreflang="${lang}" href="${href}" data-public-home="true">`).join('');
  const canonical = seo.lang === 'ar' ? `<link rel="canonical" href="${seo.url}">` : '';
  if (seo.lang === 'ar') html = html.replace('<head>', '<head><base href="/">');
  return html.replace('</head>', canonical + alternates + '</head>');
}
fs.writeFileSync(path.join(base, 'dist/index.html'), localized(locales.en));
fs.mkdirSync(path.join(base, 'dist/ar'), { recursive: true });
fs.writeFileSync(path.join(base, 'dist/ar/index.html'), localized(locales.ar));
console.log('Generated English and Arabic Maps metadata.');
