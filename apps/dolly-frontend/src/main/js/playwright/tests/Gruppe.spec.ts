import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	avbruttBestillingMock,
	uferdigBestillingMock,
	uferdigeBestillingerMock,
} from '#/mocks/BasicMocks'

const uferdigBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/2$/)
const postBestilling = new RegExp(/dolly-backend\/api\/v1\/gruppe\/2\/bestilling/)
const uferdigeBestillinger = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/2\/ikkeferdig/)

test('Opprett gruppe og start bestilling med alle mulige tilvalg', async ({ page }) => {
	await page.goto('gruppe')

	// Naviger mellom tabs
	await page.getByTestId(TestComponentSelectors.TOGGLE_FAVORITTER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_ALLE).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MINE).click()

	// Opprett ny gruppe
	await page.getByTestId(TestComponentSelectors.BUTTON_NY_GRUPPE).click()
	await page.getByTestId(TestComponentSelectors.INPUT_NAVN).click()
	await page.getByTestId(TestComponentSelectors.INPUT_NAVN).fill('Testing med Playwright')
	await page.getByTestId(TestComponentSelectors.INPUT_HENSIKT).fill('Masse testing med Playwright')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()

	await page.getByTestId(TestComponentSelectors.TOGGLE_EKSISTERENDE_PERSON).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_NY_PERSON).click()
	await expect(page).toHaveURL(/\/gruppe\/2/)
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	for (const button_velg of await page.getByTestId(TestComponentSelectors.BUTTON_VELG_ALLE).all()) {
		await button_velg.click()
	}

	for (const button_miljoe_avhengig of await page
		.getByTestId(TestComponentSelectors.BUTTON_VELG_MILJOE_AVHENGIG)
		.all()) {
		await button_miljoe_avhengig.click()
	}
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.waitForTimeout(200)
	await page.getByTestId(TestComponentSelectors.BUTTON_TILBAKE).click()

	for (const button_avhuk of await page
		.getByTestId(TestComponentSelectors.BUTTON_FJERN_ALLE)
		.all()) {
		await button_avhuk.click()
	}

	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_BESTILLING_MAL).click()
	await expect(page.getByTestId(TestComponentSelectors.TOGGLE_BESTILLING_MAL)).toBeChecked()

	await page
		.getByTestId(TestComponentSelectors.INPUT_BESTILLING_MALNAVN)
		.fill('Fornuftig navn på mal')

	//Midlertidig aktiv bestilling intercept
	await page.route(uferdigBestilling, async (route) => {
		await route.fulfill({ body: JSON.stringify(uferdigBestillingMock) })
	})

	//Send bestilling med forsinkelse
	await page.route(postBestilling, async (route) => {
		await new Promise((f) => setTimeout(f, 1500))
		await route.fulfill({ status: 201, body: JSON.stringify(uferdigBestillingMock) })
	})

	await page.route(uferdigeBestillinger, async (route) => {
		await route.fulfill({ body: JSON.stringify(uferdigeBestillingerMock) })
	})

	await page.getByTestId(TestComponentSelectors.TITLE_SEND_KOMMENTAR).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_FULLFOER_BESTILLING).click()

	await expect(page.locator('[class="loading-component"]')).toContainText(
		'Oppretter bestilling ...',
	)

	await page.getByTestId(TestComponentSelectors.BUTTON_AVBRYT_BESTILLING).click()

	//Avbrutt bestilling intercept
	await page.route(uferdigBestilling, async (route) => {
		await route.fulfill({ body: JSON.stringify(avbruttBestillingMock) })
	})

	await page.waitForTimeout(500)
	await page.getByTestId(TestComponentSelectors.BUTTON_LUKK_BESTILLING_RESULTAT).click()
})
