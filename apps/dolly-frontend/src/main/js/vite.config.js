import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import path from 'path'
import proxyRoutes from './proxy-routes.json'

/** @type {import('vite').UserConfig} */
export default defineConfig({
	build: {
		outDir: 'build',
		loader: { '.js': 'jsx' },
	},
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src'),
		},
	},
	server: {
		proxy: proxyRoutes,
		port: 3000,
	},
	plugins: [react(), viteTsconfigPaths()],
	// define: {
	// 	VITE_APP_VERSION: JSON.stringify(process.env.npm_package_version),
	// 	VITE_BRANCH_NAME: execSync('git rev-parse --abbrev-ref HEAD').toString().trimEnd(),
	// 	VITE_COMMIT_HASH: execSync('git rev-parse HEAD').toString().trimEnd(),
	// },
})
