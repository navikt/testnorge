import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Dolly-søk testing', () => {
	const dollySoekIdenter = new RegExp(/dolly-backend\/api\/v1\/elastic\/identer/)
	test('passes', async ({ page }) => {
		await page.goto('')
		page.getByTestId(CypressSelector.BUTTON_HEADER_FINNPERSON)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_HEADER_DOLLYSOEK)
		await page.click()
		page.getByTestId(CypressSelector.EXPANDABLE_PERSONINFORMASJON)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_HAR_VERGE)
		await page.click()
		await page.waitForTimeout(200)
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.FIXME_invoke('show')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		await page.waitForTimeout(1000)
		page.getByTestId(CypressSelector.BUTTON_NULLSTILL_SOEK)
		await page.click()
		await expect(
			page
				.locator('div')
				.getByText(/Ingen søk er gjort/)
				.first(),
		).toBeVisible()
		await page.waitForTimeout(1000)
		await page.locator('.select-kjoenn__control').click()
		await page.locator('.select-kjoenn__menu').click()
		await page.waitForTimeout(200)
		page.getByTestId(CypressSelector.BUTTON_VIS_I_GRUPPE)
		await page.click()
		await page.waitForTimeout(500)
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()
	})
})
