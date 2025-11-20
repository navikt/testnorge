import { defineConfig } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import proxyRoutes from './proxy-routes.json'
import path from 'path'
import EnvironmentPlugin from 'vite-plugin-environment'
import react from '@vitejs/plugin-react'
import * as child from 'child_process'

/** @type {import('vite').UserConfig} */

const commitHash = child.execSync('git rev-parse --short HEAD').toString()
const gitBranch = child.execSync('git branch --show-current').toString()

const createProxyConfig = (routes) => {
	const target = 'http://localhost:8020'
	const secure = false
	return Object.fromEntries(
		Object.entries(routes).map(([path, { changeOrigin }]) => [
			path,
			{ target, changeOrigin, secure },
		]),
	)
}

const ReactCompilerConfig = {
	target: '19',
}

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		sourcemap: true,
		cssCodeSplit: false,
		rollupOptions: {
			external: ['./nais.js'],
			output: {
				manualChunks(id) {
					if (id.includes('node_modules') && !id.includes('navikt')) {
						return id.toString().split('node_modules/')[1].split('/')[0].toString()
					} else if (id.includes('navikt')) {
						return 'navikt'
					}
				},
			},
		},
	},
	css: {
		preprocessorOptions: {
			scss: {
				api: 'modern-compiler',
			},
		},
	},
	optimizeDeps: { exclude: ['node_modules/.cache', 'node_modules/.vite'] },
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src'),
			'#': path.resolve(__dirname, './playwright'),
		},
	},
	server: mode === 'local-dev' && {
		proxy: createProxyConfig(proxyRoutes),
		port: 3000,
	},
	test: {
		globals: true,
		environment: 'jsdom',
		exclude: ['**/node_modules/**', '**/playwright/**'],
	},
	plugins: [
		react({
			babel: {
				plugins: [
					['babel-plugin-react-compiler', ReactCompilerConfig],
					[
						'babel-plugin-styled-components',
						{
							displayName: true,
							ssr: false,
						},
					],
				],
			},
		}),
		viteTsconfigPaths(),
		EnvironmentPlugin({
			COMMIT_HASH: commitHash || '',
			GIT_BRANCH: gitBranch || '',
			APP_VERSION: process.env.npm_package_version || '',
		}),
	],
}))
