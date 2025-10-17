import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { tenorSoekOversiktMock, tenorSoekTestdataMock } from '#/mocks/BasicMocks'

test('Tenor-søk testing', async ({ page }) => {
	const tenorSoekOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\?kilde=FREG&type=AlleFelter/,
	)

	await page.goto('')

	await page.route(tenorSoekOversikt, (route) => {
		route.fulfill({ body: JSON.stringify(tenorSoekOversiktMock) })
	})
	await page.route(tenorSoekTestdata, (route) => {
		route.fulfill({ body: JSON.stringify(tenorSoekTestdataMock) })
	})

	// Naviger til Tenor-soek og gjoer et soek
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TENOR).click()
	await expect(
		page
			.locator('h1')
			.getByText(/Søk etter personer i Tenor/)
			.first(),
	).toBeVisible()
	await page.getByTestId(TestComponentSelectors.CHECKBOX_TENORSOEK).click()
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
	await page.getByTestId(TestComponentSelectors.BUTTON_IMPORTER_PERSONER).click()
	await page.waitForTimeout(500)
	await expect(page.locator('.bestillingsveileder')).toBeVisible()
	await page.getByTestId(TestComponentSelectors.BUTTON_AVBRYT).click()
	await page.waitForTimeout(500)
	await page.getByTestId(TestComponentSelectors.BUTTON_BEKREFT).click()
	await page.waitForTimeout(1000)
	await expect(
		page
			.locator('h1')
			.getByText(/Søk etter personer i Tenor/)
			.first(),
	).toBeVisible()

	// Naviger til foerste person som ligger i Dolly
	await page.getByTestId(TestComponentSelectors.BUTTON_VIS_I_GRUPPE).first().click()
	await page.waitForTimeout(500)
	await expect(
		page
			.locator('h1')
			.getByText(/Testytest/)
			.first(),
	).toBeVisible()

	// Gaa til soek fra gruppe
	await page.getByTestId(TestComponentSelectors.BUTTON_IMPORTER_PERSONER).click()
	await expect(
		page
			.locator('h1')
			.getByText(/Søk etter personer i Tenor/)
			.first(),
	).toBeVisible()
})
