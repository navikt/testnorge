import { defineConfig } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgr from 'vite-plugin-svgr'
import { resolve } from 'path'
import react from '@vitejs/plugin-react'

/** @type {import('vite').UserConfig} */

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		rollupOptions: {
			output: {
				manualChunks: (id) => {
					if (id.includes('node_modules')) {
						return 'vendor'
					}
				},
			},
		},
		cssCodeSplit: false,
	},
	resolve: {
		alias: {
			'@': resolve(__dirname, './src'),
			'~': resolve(__dirname, './src'),
		},
	},
	server: mode === 'local-dev' && {
		port: 3000,
	},
	plugins: [react(), svgr(), viteTsconfigPaths()],
}))
