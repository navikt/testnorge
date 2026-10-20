import { defineConfig } from 'vitest/config'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import { playwright } from '@vitest/browser-playwright'
import { msw } from 'msw/vite'
import * as path from 'path'

const rootDir = import.meta.dirname

export default defineConfig({
	publicDir: false,
	resolve: {
		alias: {
			'@': path.resolve(rootDir, 'src'),
		},
		tsconfigPaths: true,
	},
	css: {
		preprocessorOptions: {
			less: {
				paths: [path.resolve(rootDir, 'src')],
			},
		},
	},
	plugins: [
		react(),
		babel({
			presets: [reactCompilerPreset()],
		}),
		msw({ mode: 'worker-only' }),
	],
	optimizeDeps: {
		include: [
			'react-dom/client',
			'redux-thunk',
			'redux-promise-middleware',
			'history',
			'@testing-library/user-event',
		],
	},
	test: {
		globals: true,
		setupFiles: ['./vitest.setup.ts'],
		include: ['**/__tests__/**/*.test.{ts,tsx}'],
		browser: {
			enabled: true,
			provider: playwright(),
			instances: [{ browser: 'chromium' }],
		},
	},
})
