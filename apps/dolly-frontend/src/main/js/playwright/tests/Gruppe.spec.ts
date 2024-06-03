import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

const uferdigBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/2$/)
const uferdigeBestillinger = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/2\/ikkeferdig/)
const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

test.describe('Opprett gruppe og start bestilling med alle mulige tilvalg', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')

		// Naviger mellom tabs
		page.getByTestId(CypressSelector.TOGGLE_FAVORITTER)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_ALLE)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_MINE)
		await page.click()

		// Opprett ny gruppe
		page.getByTestId(CypressSelector.BUTTON_NY_GRUPPE)
		await page.click()
		page.getByTestId(CypressSelector.INPUT_NAVN)
		await page.fill('Testing med Cypress')
		page.getByTestId(CypressSelector.INPUT_HENSIKT)
		await page.fill('Masse testing med Cypress')
		page.getByTestId(CypressSelector.BUTTON_OPPRETT)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_OPPRETT_PERSONER)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_EKSISTERENDE_PERSON)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_NY_PERSON)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_MAL)
		await page.click()
		await expect(page).toHaveURL(/\/gruppe\/2/)
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
		page.getByTestId(CypressSelector.BUTTON_VELG_MILJOE_AVHENGIG)
		page.FIXME_each(async (btn) =>
			(async () => {
				page.FIXME_wrap(btn)
				await page.click()
				return page
			})(),
		)
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
		await page.waitForTimeout(500)
		page.getByTestId(CypressSelector.BUTTON_TILBAKE)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_FJERN_ALLE)
		page.FIXME_each(async (btn) =>
			(async () => {
				page.FIXME_wrap(btn)
				await page.click()
				return page
			})(),
		)
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_BESTILLING_MAL)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_BESTILLING_MAL)
		await page.FIXME_should('be.enabled')
		page.FIXME_dollyType(CypressSelector.INPUT_BESTILLING_MALNAVN, 'Fornuftig navn på mal')

		//Midlertidig aktiv bestilling intercept
		page.getByTestId(CypressSelector.TITLE_SEND_KOMMENTAR)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_FULLFOER_BESTILLING)
		await page.click()
		await page.waitForTimeout(1000)

		//Avbrutt bestilling intercept
		page.getByTestId(CypressSelector.BUTTON_AVBRYT_BESTILLING)
		await page.click()
		await page.waitForTimeout(500)
		page.getByTestId(CypressSelector.BUTTON_LUKK_BESTILLING_RESULTAT)
		await page.click()
	})
})
