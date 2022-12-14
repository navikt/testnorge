import { defineConfig } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgr from 'vite-plugin-svgr'
import proxyRoutes from './proxy-routes.json'
import { resolve } from 'path'
import EnvironmentPlugin from 'vite-plugin-environment'
import react from '@vitejs/plugin-react'
import * as child from 'child_process'
import externals from 'rollup-plugin-node-externals'
import { chunkSplitPlugin } from 'vite-plugin-chunk-split'

/** @type {import('vite').UserConfig} */

const commitHash = child.execSync('git rev-parse --short HEAD').toString()
const gitBranch = child.execSync('git branch --show-current').toString()

export default defineConfig({
	base: '/',
	build: {
		outDir: 'build',
		minify: 'terser',
	},
	resolve: {
		alias: {
			'@': resolve(__dirname, './src'),
		},
	},
	server: {
		proxy: proxyRoutes,
		port: 3000,
	},
	plugins: [
		svgr(),
		react(),
		externals(),
		viteTsconfigPaths(),
		chunkSplitPlugin(),
		EnvironmentPlugin({
			COMMIT_HASH: commitHash || '',
			GIT_BRANCH: gitBranch || '',
			APP_VERSION: process.env.npm_package_version || '',
		}),
	],
})
