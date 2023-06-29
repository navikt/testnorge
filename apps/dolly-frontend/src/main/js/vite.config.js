import { defineConfig, splitVendorChunkPlugin } from 'vite'
import viteTsconfigPaths from 'vite-tsconfig-paths'
import svgr from 'vite-plugin-svgr'
import proxyRoutes from './proxy-routes.json'
import { resolve } from 'path'
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

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		manifest: true,
		rollupOptions: {
			external: ['./src/nais.js'],
		},
		outDir: 'build',
		cssCodeSplit: false,
	},
	resolve: {
		alias: {
			'@': resolve(__dirname, './src'),
		},
	},
	server: mode === 'local-dev' && {
		proxy: proxyRoutes,
		port: 3000,
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
		svgr(),
		viteTsconfigPaths(),
		splitVendorChunkPlugin(),
		preserveRefPlugin(),
		EnvironmentPlugin({
			COMMIT_HASH: commitHash || '',
			GIT_BRANCH: gitBranch || '',
			APP_VERSION: process.env.npm_package_version || '',
		}),
	],
}))
