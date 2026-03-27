import { API_GATEWAY, AUTH_API, MAPBOX_API } from '@/api/http';
import { ref } from 'vue';
import { MAPBOX_ACCESS_TOKEN } from '@/config/env';

/**
 * أدمن = في User-Service عمود is_user قيمته 0 (أو false في JSON).
 * مستخدم عادي = is_user = 1 (أو true).
 */
function rawIsUserFlag(u) {
  if (u.isUser !== undefined && u.isUser !== null) return u.isUser;
  if (u.is_user !== undefined && u.is_user !== null) return u.is_user;
  if (u.user !== undefined && u.user !== null) return u.user;
  return undefined;
}

/** يعيد true إن كان الملف الشخصي أدمن (is_user / isUser = 0 أو false). */
export function isProfileAdmin(u) {
  if (!u || typeof u !== 'object') return false;
  const v = rawIsUserFlag(u);
  if (v === false || v === 0) return true;
  if (typeof v === 'string') {
    const s = v.trim().toLowerCase();
    if (s === '0' || s === 'false') return true;
  }
  return false;
}

function normalizeUserProfile(d) {
  if (!d || typeof d !== 'object') return d;
  const out = { ...d };
  if (out.isUser === undefined) {
    if (out.is_user !== undefined) out.isUser = out.is_user;
    else if (out.user !== undefined) out.isUser = out.user;
  }
  if (typeof out.isUser === 'number') {
    out.isUser = out.isUser !== 0;
  } else if (typeof out.isUser === 'string') {
    const s = out.isUser.trim().toLowerCase();
    out.isUser = s !== '0' && s !== 'false' && s !== '';
  }
  return out;
}

export const getUser = async () => {
  const { data } = await API_GATEWAY.get('/user');
  return normalizeUserProfile(data);
};

/** Backend EmailUpdateRequest: { oldEmail, newEmail } — oldEmail may be null if the account has no email yet */
export const changeEmail = (body) => API_GATEWAY.patch('/user/email', body);

export const searchpo = (query) => API_GATEWAY.post('/search', { query });

export const addToFavorite = (payload) => API_GATEWAY.post('/saved', payload);

export const fetchSavedPlace = () => API_GATEWAY.get('/saved');

export const deleteSavedPlace = (placeId) => API_GATEWAY.delete(`/saved/${placeId}`);

export const toggleUserRole = (userId) => API_GATEWAY.patch(`/user/admin/role/${userId}`);

export const fetchUsersByPage = (page = 0, size = 10) =>
  API_GATEWAY.get(`/user/admin?page=${page}&size=${size}`);

export const fetchSearchHistory = () => API_GATEWAY.get('/search');

export const clearSearchHistory = () => API_GATEWAY.delete('/search');

export function submitPlac(formData) {
  return API_GATEWAY.post('/place/places', formData);
}

export const fetchPlace = () => API_GATEWAY.get('/place');

export const deletePlac = (id) => API_GATEWAY.delete(`/place/${id}`);

export const deleteAllPlace = () => API_GATEWAY.delete('/place');

/** Auth service: PasswordChangeRequest — aliases: current → oldPassword, _new → newPassword */
export const changePassword = (current, _new, userId) =>
  AUTH_API.patch('/password', { current, _new, userId });

/** Auth service path is /dele/{id} (matches backend) */
export const deleteUser = (userId) => AUTH_API.delete(`/dele/${userId}`);

export const changeUsername = (userId, newUsername) =>
  AUTH_API.patch('/username', { userId, newUsername });

export const registerUser = ({ username, password }) =>
  AUTH_API.post('/register', { username, password });

export const loginUser = ({ username, password }) =>
  AUTH_API.post('/login', { username, password });

export const resetUserPassword = (userId, password) =>
  AUTH_API.patch(`/admin/users/${userId}`, { password });

const user = ref(null);

function setUser(val) {
  user.value = val;
}

function clearUser() {
  user.value = null;
}

export function useUser() {
  return { user, setUser, clearUser };
}

export async function deleteTokenCookie() {
  try {
    await AUTH_API.post('/logout');
  } catch {
    /* still clear local state if network fails */
  }
}

export const searchPlace = async (query, params) => {
  const urlParams = new URLSearchParams({
    access_token: MAPBOX_ACCESS_TOKEN,
    ...params,
  });

  const response = await MAPBOX_API.get(`${encodeURIComponent(query)}.json?${urlParams.toString()}`);
  return response.data;
};
