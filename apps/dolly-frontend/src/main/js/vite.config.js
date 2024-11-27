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

const preserveRefPlugin = () => {
	const preverseRefFunc = `
function __preserveRef(key, v) {
  if (import.meta.env.PROD) return v;

  import.meta.hot.data ??= {}
  import.meta.hot.data.contexts ??= {}
  const old = import.meta.hot.data.contexts[key];
  const now = old || v;

  import.meta.hot.on('vite:beforeUpdate', () => {
    import.meta.hot.data.contexts[key] = now;
  });

  return now;
}
`
	return {
		name: 'preserveRef',
		transform(code) {
			if (!code.includes('__preserveRef')) return

			return {
				code: code + preverseRefFunc,
				map: null,
			}
		},
	}
}

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

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		sourcemap: true,
		cssCodeSplit: false,
		rollupOptions: {
			external: ['./nais.js'],
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
		preserveRefPlugin(),
		EnvironmentPlugin({
			COMMIT_HASH: commitHash || '',
			GIT_BRANCH: gitBranch || '',
			APP_VERSION: process.env.npm_package_version || '',
		}),
	],
}))
