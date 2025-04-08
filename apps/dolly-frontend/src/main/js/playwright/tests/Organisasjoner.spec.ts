import { test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Åpne en organisasjon med alle tilvalg', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_ORGANISASJONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_OPPRETT_ORGANISASJONER).click()
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

test('Naviger til organisasjoner og start en bestilling med alle tilvalg', async ({ page }) => {
	await page.goto('http://localhost:5678/organisasjoner')

	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_ORGANISASJON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	for (const button_velg_alle of await page
		.getByTestId(TestComponentSelectors.BUTTON_VELG_ALLE)
		.all()) {
		await button_velg_alle.click()
	}

	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
})
