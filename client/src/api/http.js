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

export const searchPlace = async (query, params) => {
  try {
    const urlParams = new URLSearchParams({
      access_token: process.env.VUE_APP_API_KEY,
      ...params,
    });

    const response = await MAPBOX_API.get(`${encodeURIComponent(query)}.json?${urlParams.toString()}`);
    return response.data;
  } catch (error) {
    console.error('Search error:', error.message);
    throw error;
  }
};