import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Tenor-søk testing', () => {
	const tenorSoekOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\?kilde=FREG&type=AlleFelter/,
	)

	test('passes', async ({ page }) => {
		await page.goto('')

		// Naviger til Tenor-soek og gjoer et soek
		page.getByTestId(CypressSelector.BUTTON_HEADER_FINNPERSON)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_HEADER_TENOR)
		await page.click()
		await expect(
			page
				.locator('h1')
				.getByText(/Søk etter personer i Tenor/)
				.first(),
		).toBeVisible()
		page.getByTestId(CypressSelector.CHECKBOX_TENORSOEK)
		await page.click()
		await page.waitForTimeout(1000)

		// Velg person som ikke ligger i Dolly og start import av personen
		await page
			.locator('div')
			.getByText(/TIGER ULV/)
			.first()
			.click()
		await expect(
			page
				.locator('h2')
				.getByText(/TIGER ULV/)
				.first(),
		).toBeVisible()
		page.getByTestId(CypressSelector.BUTTON_IMPORTER_PERSONER)
		await page.click()
		await page.waitForTimeout(500)
		await expect(
			page
				.locator('h1')
				.getByText(/Importer person/)
				.first(),
		).toBeVisible()
		page.getByTestId(CypressSelector.BUTTON_IMPORTER)
		await page.click()
		await page.waitForTimeout(500)
		await expect(page.locator('.bestillingsveileder')).toBeVisible()
		page.getByTestId(CypressSelector.BUTTON_AVBRYT)
		await page.click()
		await page.waitForTimeout(500)
		page.getByTestId(CypressSelector.BUTTON_BEKREFT)
		await page.click()
		await page.waitForTimeout(1000)
		await expect(
			page
				.locator('h1')
				.getByText(/Søk etter personer i Tenor/)
				.first(),
		).toBeVisible()

		// Naviger til foerste person som ligger i Dolly
		page.getByTestId(CypressSelector.BUTTON_VIS_I_GRUPPE)
		await page.first().click()
		await page.waitForTimeout(500)
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()

		// Gaa til soek fra gruppe
		page.getByTestId(CypressSelector.BUTTON_IMPORTER_PERSONER)
		await page.click()
		await expect(
			page
				.locator('h1')
				.getByText(/Søk etter personer i Tenor/)
				.first(),
		).toBeVisible()
	})
})
