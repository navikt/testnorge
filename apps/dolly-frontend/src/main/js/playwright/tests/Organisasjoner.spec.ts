import { test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Åpne en organisasjon med alle tilvalg', () => {
	test('passes', async ({ page }) => {
		await page.goto('')
		page.getByTestId(CypressSelector.BUTTON_HEADER_ORGANISASJONER)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_HEADER_OPPRETT_ORGANISASJONER)
		await page.click()
		await page
			.locator('div')
			.getByText(/Logaritme/)
			.first()
			.click()
		await page
			.locator('div')
			.getByText(/Horisontal/)
			.first()
			.click()
		await page.locator('div').getByText(/Q2/).first().click()
	})
})

test.describe('Naviger til organisasjoner og start en bestilling med alle tilvalg', () => {
	test('passes', async ({ page }) => {
		await page.goto('http://localhost:5678/organisasjoner')
		page.getByTestId(CypressSelector.BUTTON_HEADER_ORGANISASJONER)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_OPPRETT_ORGANISASJON)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_START_BESTILLING)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_VELG_ALLE)
		page.FIXME_each(async (btn) =>
			(async () => {
				page.FIXME_wrap(btn)
				await page.click()
				return page
			})(),
		)
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
	})
})
