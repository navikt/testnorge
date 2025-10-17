import {defineConfig} from 'vite';
import viteTsconfigPaths from 'vite-tsconfig-paths';
import svgr from 'vite-plugin-svgr';
import path from 'path'
import react from '@vitejs/plugin-react';

/** @type {import('vite').UserConfig} */

export default defineConfig(({mode}) => ({
    base: '/',
    build: {
        lib: {
            entry: path.resolve(__dirname, 'src/index.js'),
            name: 'dolly-assets',
            formats: ['umd'],
            fileName: () => `dolly-assets.js`,
        },
        rollupOptions: {
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
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
            '~': path.resolve(__dirname, './src'),
        },
    },
    plugins: [react(), svgr(), viteTsconfigPaths()],
}));
