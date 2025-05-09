import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Navigering til api-oversikt', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_DOKUMENTASJON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_OVERSIKT).click()
	await expect(page).toHaveURL(/\/oversikt/)
	await expect(
		page
			.locator('h1')
			.getByText(/API-oversikt/)
			.first(),
	).toBeVisible()
})
