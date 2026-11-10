import axios from 'axios';
import { tokenStorage } from './tokenStorage';

// Event name the AuthProvider listens to, so a 401 anywhere logs the user out.
export const LOGOUT_EVENT = 'alijenta:logout';

// In development VITE_API_URL is empty and requests go to /api (the Vite proxy).
// In production it points to the deployed backend, e.g. https://api.alijenta.dev
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
});

// Attach the JWT to every request.
api.interceptors.request.use((config) => {
  const token = tokenStorage.get();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// The token expired or is invalid: forget it and tell the app.
api.interceptors.response.use(
  (response) => response,

  (error) => {
    if (error.response?.status === 401 && tokenStorage.get()) {
      tokenStorage.clear();
      window.dispatchEvent(new Event(LOGOUT_EVENT));
    }

    return Promise.reject(error);
  },
);

// Small helper: most endpoints only need the response body.
export const unwrap = (request) => request.then((response) => response.data);
