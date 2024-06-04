import { test as base } from '@playwright/test'

export const test = base.extend({
	page: async ({ baseURL, page }, use) => {
		await page.routeFromHAR('playwright/mocks/BaseMocks.har', {
			url: '**/api/**',
			update: false,
			notFound: 'fallback',
		})
		await page.route('**/api**', async (route) => {
			await route.fulfill({ body: '[]' })
		})

		use(page)
	},
})
export { expect } from '@playwright/test'
