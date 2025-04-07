import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	tenorSoekOrganisasjonOversiktMock,
	tenorSoekOrganisasjonTestdataMock,
} from '#/mocks/BasicMocks'

test('Tenor-søk testing', async ({ page }) => {
	const tenorSoekOrganisasjonOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekOrganisasjonTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\?type=Organisasjon/,
	)

	await page.goto('gruppe')

	await page.route(tenorSoekOrganisasjonOversikt, (route) => {
		route.fulfill({ body: JSON.stringify(tenorSoekOrganisasjonOversiktMock) })
	})

	await page.route(tenorSoekOrganisasjonTestdata, (route) => {
		route.fulfill({ body: JSON.stringify(tenorSoekOrganisasjonTestdataMock) })
	})

	// Naviger til Tenor-organisasjon-soek og post et soek
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_ORGANISASJONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TENOR_ORGANISASJONER).click()
	await expect(
		page
			.locator('h1')
			.getByText(/Søk etter organisasjoner i Tenor/)
			.first(),
	).toBeVisible()
	await page.getByTestId(TestComponentSelectors.CHECKBOX_ORGANISASJONER_TENORSOEK).click()
	await page.locator('div').getByText(/TIGER/).first().click()
	await expect(page.locator('h2').getByText(/TIGER/).first()).toBeVisible()

	// Sjekk at antall valgt er 1, deretter clear soeket og sjekk at antall valgt er 0
	const titleTenor = page.getByTestId(
		TestComponentSelectors.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET,
	)
	await expect(titleTenor).toContainText('Enhetsregisteret og Foretaksregisteret')
	await expect(titleTenor).toContainText('1')

	for (const button_tenor_clear_header of await page
		.getByTestId(TestComponentSelectors.BUTTON_TENOR_CLEAR_HEADER)
		.all()) {
		await button_tenor_clear_header.click()
	}

	await expect(
		page.getByTestId(TestComponentSelectors.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET),
	).not.toContainText('1')
})
