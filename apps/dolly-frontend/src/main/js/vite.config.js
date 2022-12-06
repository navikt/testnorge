import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgrPlugin from 'vite-plugin-svgr'
import path from 'path'

/** @type {import('vite').UserConfig} */
export default defineConfig({
	build: {
		loader: { '.js': 'jsx' },
	},
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src'),
		},
	},
	server: {
		proxy: {
			'/': 'http://localhost:8020',
		},
		port: 3000,
	},
	plugins: [react(), viteTsconfigPaths(), svgrPlugin()],
	// define: {
	// 	VITE_APP_VERSION: JSON.stringify(process.env.npm_package_version),
	// 	VITE_BRANCH_NAME: execSync('git rev-parse --abbrev-ref HEAD').toString().trimEnd(),
	// 	VITE_COMMIT_HASH: execSync('git rev-parse HEAD').toString().trimEnd(),
	// },
})
