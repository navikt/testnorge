import { defineConfig } from 'vite'
import proxyRoutes from './proxy-routes.json'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import * as child from 'child_process'
import * as fs from 'fs'
import * as path from 'path'

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

const styledComponentsPreset = () => ({
	preset: () => ({
		plugins: [
			[
				'babel-plugin-styled-components',
				{
					displayName: true,
					ssr: false,
					fileName: true,
					meaninglessFileNames: ['index', 'styles'],
				},
			],
		],
	}),
	rolldown: {
		filter: {
			code: /styled/,
		},
	},
})

const versionJsonPlugin = () => ({
	name: 'generate-version-json',
	closeBundle() {
		const versionData = JSON.stringify({
			commitHash: (commitHash || '').trim(),
		})
		fs.writeFileSync(path.resolve(__dirname, 'build', 'version.json'), versionData)
	},
})

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		sourcemap: true,
		cssCodeSplit: false,
		rolldownOptions: {
			external: ['./nais.js'],
			output: {
				sourcemapExcludeSources: false,
				codeSplitting: {
					groups: [
						{
							name: 'navikt',
							test: /node_modules[\\/]@navikt/,
							priority: 20,
						},
						{
							name: 'vendor',
							test: /node_modules/,
							priority: 10,
						},
					],
				},
			},
		},
	},
	define: {
		'process.env.COMMIT_HASH': JSON.stringify((commitHash || '').trim()),
		'process.env.GIT_BRANCH': JSON.stringify((gitBranch || '').trim()),
		'process.env.APP_VERSION': JSON.stringify(process.env.npm_package_version || ''),
	},
	resolve: {
		tsconfigPaths: true,
	},
	server: mode === 'local-dev' && {
		proxy: createProxyConfig(proxyRoutes),
		port: 3000,
		forwardConsole: true,
	},
	test: {
		globals: true,
		environment: 'jsdom',
		exclude: ['**/node_modules/**', '**/playwright/**'],
	},
	plugins: [
		react(),
		babel({
			presets: [reactCompilerPreset(), styledComponentsPreset()],
		}),
		versionJsonPlugin(),
	],
}))
