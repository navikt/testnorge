import { defineConfig } from 'vite';
import svgr from 'vite-plugin-svgr';
import react, { reactCompilerPreset } from '@vitejs/plugin-react';
import babel from '@rolldown/plugin-babel';

/** @type {import('vite').UserConfig} */

export default defineConfig(({ mode }) => ({
  base: '/',
  build: {
    outDir: 'build',
    cssCodeSplit: false,
  },
  resolve: {
    tsconfigPaths: true,
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
    forwardConsole: true,
  },
  plugins: [
    react(),
    babel({
      presets: [reactCompilerPreset()],
    }),
    svgr(),
  ],
}));
