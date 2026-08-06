import { defineConfig } from 'vite'
import svgr from 'vite-plugin-svgr'
import proxyRoutes from './proxy-routes.json' with { type: 'json' }
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

/** @type {import('vite').UserConfig} */

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
			presets: [reactCompilerPreset()],
		}),
		svgr(),
	],
}))
