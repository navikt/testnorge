import { defineConfig } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgr from 'vite-plugin-svgr'
import { resolve } from 'path'
import EnvironmentPlugin from 'vite-plugin-environment'
import proxyRoutes from './proxy-routes.json'
import react from '@vitejs/plugin-react'
import * as child from 'child_process'

/** @type {import('vite').UserConfig} */

const commitHash = child.execSync('git rev-parse --short HEAD').toString()
const gitBranch = child.execSync('git branch --show-current').toString()

export default defineConfig(({ mode }) => ({
	build: {
		lib: {
			entry: resolve(__dirname, './src/index.tsx'),
			name: 'dolly',
			formats: ['es'],
			fileName: () => `bundle.js`,
		},
		outDir: 'build',
		assetsDir: '',
	},
	resolve: {
		alias: {
			'@': resolve(__dirname, './src'),
		},
	},
	server: mode === 'development' && {
		proxy: proxyRoutes,
		port: 3000,
	},
	plugins: [
		svgr(),
		react(),
		viteTsconfigPaths(),
		EnvironmentPlugin({
			COMMIT_HASH: commitHash || '',
			GIT_BRANCH: gitBranch || '',
			APP_VERSION: process.env.npm_package_version || '',
		}),
	],
}))
