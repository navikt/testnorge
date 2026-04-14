import { defineConfig } from 'vite';
import svgr from 'vite-plugin-svgr';
import react, { reactCompilerPreset } from '@vitejs/plugin-react';
import babel from '@rolldown/plugin-babel';

/** @type {import('vite').UserConfig} */

const styledComponentsPreset = () => ({
  preset: () => ({
    plugins: [
      [
        'babel-plugin-styled-components',
        {
          displayName: true,
          ssr: false,
          fileName: true,
          meaninglessFileNames: ['index', 'styles'],
        },
      ],
    ],
  }),
  rolldown: {
    filter: {
      code: /styled/,
    },
  },
});

export default defineConfig(({ mode }) => ({
  base: '/',
  build: {
    outDir: 'build',
    cssCodeSplit: false,
    rolldownOptions: {
      output: {
        codeSplitting: {
          groups: [
            {
              name: 'vendor',
              test: /node_modules/,
              priority: 10,
            },
          ],
        },
      },
    },
  },
  resolve: {
    tsconfigPaths: true,
  },
  server: mode === 'local-dev' && {
    proxy: {
      '/oauth2/authorization/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/endringsmelding-service/api/v1': {
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
      presets: [reactCompilerPreset(), styledComponentsPreset()],
    }),
    svgr(),
  ],
}));
