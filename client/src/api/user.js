import { API_GATEWAY ,AUTH_API  } from '@/api/http'
import {  onMounted , ref } from 'vue';

export const getUser = async () => {
  const { data } = await API_GATEWAY.get('/user');
  return data;
};


export const changeEmail = ( newEmail, password) =>
  API_GATEWAY.patch('/user/email', { newEmail, password });


export const searchpo = ( query) =>
  API_GATEWAY.post('/search', { query });


export const addToFavorite = (payload) => 
  API_GATEWAY.post('/saved', payload);

export const fetchSavedPlace = () =>
  API_GATEWAY.get('/saved');          

export const deleteSavedPlace = (placeId) =>
  API_GATEWAY.delete(`/saved/${placeId}`);

export const toggleUserRole = (userId) =>
  API_GATEWAY.patch(`/user/admin/role/${userId}`);


export const fetchUsersByPage = (page = 0, size = 10) =>
  API_GATEWAY.get(`/user/admin?page=${page}&size=${size}`);



export const fetchSearchHistory = () =>
  API_GATEWAY.get('/search');


export const clearSearchHistory = () =>
  API_GATEWAY.delete('/search');


export const submitPlac = (place) =>
  API_GATEWAY.post('/place', place)

export const fetchPlace = () =>
  API_GATEWAY.get('/place')

export const deletePlac = (id) =>
  API_GATEWAY.delete(`/place/${id}`)

export const deleteAllPlace = () =>
  API_GATEWAY.delete('/place')




export const changePassword = (current, _new,userId) =>
  AUTH_API.patch('/password', { current, _new ,userId});

export const deleteUser = (userId) =>
  AUTH_API.delete(`/dele/${userId}`);

export const changeUsername = (userId, newUsername) =>
  AUTH_API.patch('/username', { userId,newUsername});


export const registerUser = ({ username, password }) =>
  AUTH_API.post('/register', { username, password });

export const loginUser = ({ username, password }) =>
  AUTH_API.post('/login', { username, password });


export const resetUserPassword = (userId, password) =>
  AUTH_API.patch(`/admin/users/${userId}`, { password });


onMounted(async () => {
  try {
    user.value = await getUser();
  } catch (e) {
    console.error('Failed to load user', e);
  }
});
const user = ref(null);

function setUser(val)  { user.value = val; }
function clearUser()   { user.value = null; }

export function useUser() {
  return { user, setUser, clearUser };
}



