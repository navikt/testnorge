import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';
import { playwright } from '@vitest/browser-playwright';
import { msw } from 'msw/vite';

const rootDir = import.meta.dirname;

export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(rootDir, './src'),
      '#': path.resolve(rootDir, './playwright'),
    },
  },
  plugins: [react(), msw({ mode: 'worker-only' })],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./vitest.setup.ts'],
    browser: {
      enabled: true,
      provider: playwright(),
      instances: [{ browser: 'chromium' }],
    },
  },
});
