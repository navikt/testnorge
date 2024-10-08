import { defineConfig } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';
import svgr from 'vite-plugin-svgr';
import { resolve } from 'path';
import react from '@vitejs/plugin-react';

/** @type {import('vite').UserConfig} */

export default defineConfig(({ mode }) => ({
  base: '/',
  build: {
    outDir: 'build',
    cssCodeSplit: false,
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
      '~': resolve(__dirname, './src'),
    },
  },
  server: mode === 'local-dev' && {
    proxy: {
      '/testnav-person-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-organisasjon-faste-data-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-person-faste-data-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnorge-profil-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-organisasjon-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/oauth2/authorization/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/login/oauth2/code/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
    port: 3000,
  },
  plugins: [react(), svgr(), tsconfigPaths()],
}));
