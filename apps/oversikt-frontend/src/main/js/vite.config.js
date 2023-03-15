import { defineConfig, splitVendorChunkPlugin } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgr from 'vite-plugin-svgr'
import proxyRoutes from './proxy-routes.json'
import { resolve } from 'path'
import react from '@vitejs/plugin-react'

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
		proxy: proxyRoutes,
		port: 3000,
	},
	plugins: [react(), svgr(), viteTsconfigPaths(), splitVendorChunkPlugin()],
}))
