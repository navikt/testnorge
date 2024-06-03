import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'
import { ERROR_NAVIGATE_IDENT } from '@/ducks/errors/ErrorMessages'

const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

test.describe('Navigering til ident som finnes i bestilling og tilbake igjen til bestillingen', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.TOGGLE_VISNING_BESTILLINGER)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_OPEN_BESTILLING)
		page.FIXME_each(async (element) => {
			page.FIXME_wrap(element)
			await page.click()
		})
		await page
			.locator('Button')
			.getByText(/12345678912/)
			.first()
			.click()
		page.getByTestId(CypressSelector.TOGGLE_VISNING_PERSONER)
		await page.FIXME_should('have.attr', 'aria-checked', 'true')
		page.getByTestId(CypressSelector.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_VISNING_BESTILLINGER)
		await page.FIXME_should('have.attr', 'aria-checked', 'true')
	})
})

test.describe('Navigering til ident som finnes i gruppe 1', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')

		//Midlertidig not found på navigering til ident etter søk
		page.FIXME_dollyType(CypressSelector.INPUT_DOLLY_SOEK, '12345')
		page.getByTestId(CypressSelector.BUTTON_NAVIGER_DOLLY)
		await page.click()
		await page.waitForTimeout(400)
		page.getByTestId(CypressSelector.ERROR_MESSAGE_NAVIGERING)
		await page.FIXME_should('contains.text', ERROR_NAVIGATE_IDENT)

		//Korrekt navigering igjen
		page.getByTestId(CypressSelector.TOGGLE_SEARCH_BESTILLING)
		await page.click()
		page.FIXME_dollyType(CypressSelector.INPUT_DOLLY_SOEK, '1')
		page.getByTestId(CypressSelector.BUTTON_NAVIGER_DOLLY)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_SEARCH_PERSON)
		await page.click()
		page.FIXME_dollyType(CypressSelector.INPUT_DOLLY_SOEK, '12345')
		page.getByTestId(CypressSelector.BUTTON_NAVIGER_DOLLY)
		await page.click()
		await page.waitForTimeout(400)
		await expect(page).toHaveURL(/\/gruppe\/1/)
	})
})
