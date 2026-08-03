import { defineConfig } from 'vite'
import svgr from 'vite-plugin-svgr'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import { fileURLToPath } from 'url'

/** @type {import('vite').UserConfig} */

export default defineConfig(({ mode }) => ({
	base: '/',
	build: {
		outDir: 'build',
		cssCodeSplit: false,
		rolldownOptions: {
			output: {
				codeSplitting: {
					groups: [
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
	resolve: {
		tsconfigPaths: true,
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url)),
		},
	},
	server: mode === 'local-dev' && {
		port: 3000,
		forwardConsole: true,
	},
	plugins: [
		react(),
		babel({
			presets: [reactCompilerPreset()],
		}),
		svgr(),
	],
}))
