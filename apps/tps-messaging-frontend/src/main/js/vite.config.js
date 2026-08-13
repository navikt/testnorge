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
  plugins: [
    react(),
    babel({
      presets: [reactCompilerPreset()],
    }),
    svgr(),
  ],
}));
