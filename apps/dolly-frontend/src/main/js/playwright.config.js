import { defineConfig, devices } from '@playwright/test'

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
	testDir: './playwright/tests',
	fullyParallel: true,
	// Increase timeout for CI to minimize test flakiness
	timeout: process.env.ci ? 50000 : 30000,
	expect: {
		timeout: process.env.ci ? 10000 : 5000,
	},

	/* Fail the build on CI if you accidentally left test.only in the source code. */
	forbidOnly: !!process.env.CI,
	retries: process.env.CI ? 2 : 1,
	// One worker on CI to make tests more stable
	workers: process.env.CI ? 1 : 3,

	reporter: 'html',
	/* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
	use: {
		baseURL: 'http://localhost:5678/',
		trace: 'on-first-retry',
	},

	/* Configure projects for major browsers */
	projects: [
		{
			name: 'firefox',
			use: { ...devices['Desktop Firefox'] },
		},
		{
			name: 'Google Chrome',
			use: { ...devices['Desktop Chrome'], channel: 'chrome' },
		},
		{
			name: 'Microsoft Edge',
			use: { ...devices['Desktop Edge'], channel: 'msedge' },
		},
	],

	/* Run the local dev server before starting the tests */
	webServer: {
		command: 'npm run test:start',
		url: 'http://localhost:5678',
		reuseExistingServer: !process.env.CI,
	},
})
