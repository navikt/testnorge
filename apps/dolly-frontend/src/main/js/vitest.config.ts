import { defineConfig } from 'vitest/config'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import { playwright } from '@vitest/browser-playwright'
import * as path from 'path'

const rootDir = import.meta.dirname

export default defineConfig({
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
	],
	optimizeDeps: {
		include: [
			'react-dom/client',
			'redux',
			'redux-thunk',
			'redux-promise-middleware',
			'history',
			'react-toastify',
			'@testing-library/user-event',
		],
	},
	test: {
		environment: 'jsdom',
		globals: true,
		setupFiles: ['./vitest.setup.ts'],
		include: ['**/__tests__/**/*.test.{ts,tsx}'],
		deps: {
			inline: ['@testing-library/user-event'],
		},
		browser: {
			enabled: true,
			provider: playwright(),
			instances: [{ browser: 'chromium' }],
		},
	},
})
