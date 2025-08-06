import axios from 'axios';

export const API_GATEWAY = axios.create({
  baseURL: process.env.VUE_APP_API_GATEWAY_URL,
  withCredentials: true,
});

export const AUTH_API = axios.create({
  baseURL: process.env.VUE_APP_AUTH_API_URL,
  withCredentials: true,
});

export const MAPBOX_API = axios.create({
  baseURL: process.env.VUE_APP_MAPBOX_API,
});
