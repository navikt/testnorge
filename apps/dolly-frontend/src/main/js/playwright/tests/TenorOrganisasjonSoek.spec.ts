import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Tenor-søk testing', () => {
	const tenorSoekOrganisasjonOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekOrganisasjonTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\?type=Organisasjon/,
	)

	test('passes', async ({ page }) => {
		await page.goto('')

		// Naviger til Tenor-organisasjon-soek og post et soek
		page.getByTestId(CypressSelector.BUTTON_HEADER_ORGANISASJONER)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_HEADER_TENOR_ORGANISASJONER)
		await page.click()
		await expect(
			page
				.locator('h1')
				.getByText(/Søk etter organisasjoner i Tenor/)
				.first(),
		).toBeVisible()
		page.getByTestId(CypressSelector.CHECKBOX_ORGANISASJONER_TENORSOEK)
		await page.click()
		await page.locator('div').getByText(/TIGER/).first().click()
		await expect(page.locator('h2').getByText(/TIGER/).first()).toBeVisible()

		// Sjekk at antall valgt er 1, deretter clear soeket og sjekk at antall valgt er 0
		page.getByTestId(CypressSelector.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET)
		await page.FIXME_should('contain.text', 'Enhetsregisteret og Foretaksregisteret')
		await page.FIXME_should('contain.text', '1')
		page.getByTestId(CypressSelector.BUTTON_TENOR_CLEAR_HEADER)
		for (const locator of await page.all()) await locator.click()
		page.getByTestId(CypressSelector.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET)
		await page.FIXME_should('not.contain.text', '1')
	})
})
