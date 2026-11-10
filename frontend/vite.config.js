import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In development the browser talks only to Vite (http://localhost:5173).
// Every request that starts with /api is forwarded to Spring Boot,
// so the backend does not even need CORS while you develop.
export default defineConfig({
  plugins: [react()],

  server: {
    port: 5173,

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
