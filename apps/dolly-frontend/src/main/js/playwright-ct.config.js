import { defineConfig, devices } from '@playwright/experimental-ct-react'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

export default defineConfig({
	testDir: './playwright/ct',
	fullyParallel: true,
	timeout: process.env.ci ? 50000 : 30000,
	expect: {
		timeout: process.env.ci ? 10000 : 5000,
	},

	forbidOnly: !!process.env.CI,
	retries: process.env.CI ? 2 : 1,
	workers: process.env.CI ? 1 : 3,

	reporter: [
		[
			process.env.CI ? 'blob' : 'html',
			{
				attachments: true,
			},
		],
	],

	use: {
		trace: 'on-first-retry',
		screenshot: {
			mode: 'on',
			fullPage: true,
		},
		ctTemplateDir: './playwright',
		ctViteConfig: {
			resolve: {
				alias: {
					'@': path.resolve(__dirname, './src'),
				},
			},
			css: {
				preprocessorOptions: {
					less: {
						javascriptEnabled: true,
					},
				},
			},
			server: {
				fs: {
					allow: [path.resolve(__dirname, './src'), path.resolve(__dirname, './node_modules')],
				},
			},
		},
	},

	projects: [
		{
			name: 'Google Chrome',
			use: { ...devices['Desktop Chrome'], channel: 'chrome' },
		},
	],
})
