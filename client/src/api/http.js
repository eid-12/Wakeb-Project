import axios from 'axios';
import { API_GATEWAY_BASE, AUTH_API_BASE, MAPBOX_GEOCODING_BASE } from '@/config/env';

/**
 * Axios instances for backend integration.
 * Use relative URLs in production (same origin as Express proxy) unless overrides are set.
 */
export const API_GATEWAY = axios.create({
  baseURL: API_GATEWAY_BASE,
  withCredentials: true,
});

export const AUTH_API = axios.create({
  baseURL: AUTH_API_BASE,
  withCredentials: true,
});

export const MAPBOX_API = axios.create({
  baseURL: `${MAPBOX_GEOCODING_BASE}/`,
});
