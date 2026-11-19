#!/usr/bin/env bash
set -euo pipefail
image="${1:?Pass the frontend image to test}"
container=maps-locales-smoke
trap 'docker rm -f "$container" >/dev/null 2>&1 || true' EXIT
docker run -d --name "$container" -p 127.0.0.1:3312:3000 "$image" >/dev/null
ready=false
for _ in $(seq 1 30); do
  if curl --silent --fail http://127.0.0.1:3312/ >/dev/null; then ready=true; break; fi
  sleep 1
done
if [[ "$ready" != true ]]; then docker logs "$container"; exit 1; fi
if [[ "$(docker exec "$container" id -u)" == 0 ]]; then echo 'Frontend must not run as root'; exit 1; fi
node <<'JS'
const assert = require('node:assert/strict');
const fs = require('node:fs');
const locales = JSON.parse(fs.readFileSync('client/src/public-site/home.json', 'utf8'));
(async () => {
  for (const [lang, route] of [['en', '/'], ['ar', '/ar/']]) {
    const response = await fetch('http://127.0.0.1:3312' + route);
    assert.equal(response.status, 200);
    assert.match(response.headers.get('cache-control'), /no-store/);
    assert.equal(response.headers.get('x-content-type-options'), 'nosniff');
    assert.equal(response.headers.get('x-powered-by'), null);
    const html = await response.text();
    assert.ok(html.includes(`lang="${lang}"`)); assert.ok(html.includes(`dir="${locales[lang].dir}"`));
    assert.ok(html.includes(locales[lang].description));
    for (const href of [locales.en.url, locales.ar.url]) assert.ok(html.includes(href));
    if (lang === 'ar') assert.ok(html.includes('<base href="/">'));
    const entry = html.match(/<script[^>]+src="([^"]+)"/)[1];
    const asset = await fetch(new URL(entry, 'http://127.0.0.1:3312/'));
    assert.equal(asset.status, 200); assert.match(asset.headers.get('content-type'), /javascript/);
  }
  const xml = await (await fetch('http://127.0.0.1:3312/sitemap.xml')).text();
  for (const locale of Object.values(locales)) assert.ok(xml.includes(`<loc>${locale.url}</loc>`));
  console.log('Maps localized pages, assets, headers and sitemap passed.');
})().catch(error => { console.error(error); process.exitCode = 1; });
JS
