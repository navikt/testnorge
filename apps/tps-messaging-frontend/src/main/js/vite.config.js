import { defineConfig } from 'vite';
import viteTsconfigPaths from 'vite-tsconfig-paths';
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
  // server: mode === 'local-dev' && {
  //   proxy: {
  //     '/oauth2/authorization/aad': {
  //       target: 'http://localhost:8080',
  //       changeOrigin: true,
  //       secure: false,
  //     },
  //     '/tps-messaging-service/api/v1': {
  //       target: 'http://localhost:8080',
  //       changeOrigin: true,
  //       secure: false,
  //     },
  //   },
  //   port: 3000,
  // },
  plugins: [react(), svgr(), viteTsconfigPaths()],
}));
