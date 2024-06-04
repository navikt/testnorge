import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Dolly-søk testing', () => {
	const dollySoekIdenter = new RegExp(/dolly-backend\/api\/v1\/elastic\/identer/)
	test('passes', async ({ page }) => {
		await page.goto('/gruppe')
		await page.getByTestId(CypressSelector.BUTTON_HEADER_FINNPERSON).click()
		await page.getByTestId(CypressSelector.BUTTON_HEADER_DOLLYSOEK).click()
		await page.getByTestId(CypressSelector.EXPANDABLE_PERSONINFORMASJON).click()
		await page.getByTestId(CypressSelector.TOGGLE_HAR_VERGE).click()
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		await page.getByTestId(CypressSelector.BUTTON_NULLSTILL_SOEK).click()
		await expect(
			page
				.locator('div')
				.getByText(/Ingen søk er gjort/)
				.first(),
		).toBeVisible()
		await page.locator('.select-kjoenn__control').click()
		await page.locator('.select-kjoenn__menu').click()
		await page.getByTestId(CypressSelector.BUTTON_VIS_I_GRUPPE).click()
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()
	})
})
