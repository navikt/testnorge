import { defineConfig } from 'vite';
import svgr from 'vite-plugin-svgr';
import path from 'path';
import react from '@vitejs/plugin-react';

/** @type {import('vite').UserConfig} */

export default defineConfig(({ mode }) => ({
  base: '/',
  build: {
    lib: {
      entry: path.resolve(__dirname, 'src/index.js'),
      name: 'dolly-assets',
      formats: ['umd'],
      fileName: () => `dolly-assets.js`,
    },
    rolldownOptions: {
      external: ['react', 'react-dom', 'styled-components'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
          'styled-components': 'styled',
        },
      },
    },
    outDir: 'dist',
    cssCodeSplit: false,
  },
  plugins: [react(), svgr()],
}));
