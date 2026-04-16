import { defineConfig } from 'vite'
import svgr from 'vite-plugin-svgr'
import proxyRoutes from './proxy-routes.json'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

/** @type {import('vite').UserConfig} */

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

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		cssCodeSplit: false,
	},
	resolve: {
		tsconfigPaths: true,
	},
	server: mode === 'local-dev' && {
		proxy: proxyRoutes,
		port: 3000,
		forwardConsole: true,
	},
	plugins: [
		react(),
		babel({
			presets: [reactCompilerPreset(), styledComponentsPreset()],
		}),
		svgr(),
	],
}))
