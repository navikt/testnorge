import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test.describe('Dolly-søk testing', () => {
	const dollySoekIdenter = new RegExp(/dolly-backend\/api\/v1\/elastic\/identer/)
	test('passes', async ({ page }) => {
		await page.route(dollySoekIdenter, async (route) => {
			await route.fulfill({
				body: `{"identer": ["12345678912"],"totalHits": 1}`,
				headers: { 'content-type': 'application/json' },
			})
		})

		await page.goto('/gruppe')
		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK).click()
		await page.getByTestId(TestComponentSelectors.EXPANDABLE_PERSONINFORMASJON).click()
		await page.getByTestId(TestComponentSelectors.TOGGLE_HAR_VERGE).click()

		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()

		await page.getByTestId(TestComponentSelectors.BUTTON_NULLSTILL_SOEK).click()

		await expect(
			page
				.locator('div')
				.getByText(/Ingen søk er gjort/)
				.first(),
		).toBeVisible()

		await page.locator('.select-kjoenn__control').click()
		await page.locator('.select-kjoenn__menu').click()
		await page.getByTestId(TestComponentSelectors.BUTTON_VIS_I_GRUPPE).click()
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()
	})
})
