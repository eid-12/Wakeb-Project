/**
 * Central place for frontend environment-driven configuration.
 * Vue CLI injects only variables prefixed with VUE_APP_ at build time.
 */

function trimSlash(s) {
  if (s == null || s === '') return '';
  return String(s).replace(/\/+$/, '');
}

/** Base path for API Gateway routes (user, saved, search, place). Default: same-origin /api */
export const API_GATEWAY_BASE = trimSlash(process.env.VUE_APP_API_GATEWAY_URL) || '/api';

/**
 * Auth service base path (register, login, logout, password, username, admin reset).
 * Default: /api/auth so one Express proxy can route auth separately from the gateway.
 */
export const AUTH_API_BASE = trimSlash(process.env.VUE_APP_AUTH_API_URL) || `${API_GATEWAY_BASE}/auth`;

/** Mapbox Geocoding API base URL (trailing slash optional). */
export const MAPBOX_GEOCODING_BASE =
  trimSlash(process.env.VUE_APP_MAPBOX_API) ||
  'https://api.mapbox.com/geocoding/v5/mapbox.places';

/** Public Mapbox access token (Geocoding + map tiles if used). */
export const MAPBOX_ACCESS_TOKEN =
  process.env.VUE_APP_MAPBOX_ACCESS_TOKEN || process.env.VUE_APP_API_KEY || '';
