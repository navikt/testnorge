import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src'),
			'#': path.resolve(__dirname, './playwright'),
		},
	},
	plugins: [react()],
	test: {
		environment: 'jsdom',
		globals: true,
		root: '__tests__',
		setupFiles: ['./vitest.setup.ts'],
		browser: {
			enabled: true,
			provider: 'playwright',
			instances: [{ browser: 'chromium' }],
		},
	},
})
