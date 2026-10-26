import { defineConfig } from 'vite';
import svgr from 'vite-plugin-svgr';
import react, { reactCompilerPreset } from '@vitejs/plugin-react';
import babel from '@rolldown/plugin-babel';
import { msw } from 'msw/vite';

/** @type {import('vite').UserConfig} */

export default defineConfig(({ command }) => ({
  base: '/',
  build: {
    outDir: 'build',
    cssCodeSplit: false,
  },
  resolve: {
    tsconfigPaths: true,
  },
  plugins: [
    react(),
    babel({
      presets: [reactCompilerPreset()],
    }),
    svgr(),
    command === 'serve' && msw({ mode: 'worker-only' }),
  ],
}));
