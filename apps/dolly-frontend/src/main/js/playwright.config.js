import { defineConfig, devices } from '@playwright/test'

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
	testDir: './playwright/tests',
	fullyParallel: true,
	// Increase timeout for CI to minimize test flakiness
	timeout: process.env.CI ? 50000 : 30000,
	expect: {
		timeout: process.env.CI ? 10000 : 5000,
	},

	/* Fail the build on CI if you accidentally left test.only in the source code. */
	forbidOnly: !!process.env.CI,
	retries: process.env.CI ? 2 : 1,
	// One worker on CI to make tests more stable
	workers: process.env.CI ? 1 : 3,

	reporter: [
		[
			process.env.CI ? 'blob' : 'html',
			{
				attachments: true,
			},
		],
		...(process.env.CI ? [['dot']] : []),
	],

	use: {
		baseURL: 'http://localhost:5678/',
		trace: 'on-first-retry',
		screenshot: {
			mode: 'on',
			fullPage: true,
		},
	},

	projects: [
		{
			name: 'Google Chrome',
			use: {
				...devices['Desktop Chrome'],
				channel: 'chrome',
				// Ubuntu 24.04 GitHub Actions runners restrict unprivileged user namespaces via AppArmor,
				// which breaks Chrome's sandbox and hangs the browser launch. CI is already an isolated VM.
				launchOptions: process.env.CI ? { args: ['--no-sandbox'] } : {},
			},
		},
	],

	/* Run the local dev server before starting the tests */
	webServer: {
		command: 'pnpm run test:start',
		url: 'http://localhost:5678',
		reuseExistingServer: !process.env.CI,
	},
})
