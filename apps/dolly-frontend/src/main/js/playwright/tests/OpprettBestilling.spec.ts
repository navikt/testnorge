import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Oppretter bestilling med alle artifakter som er avhengige av Q1 eller Q2 og sjekker at disse blir huket av', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe/1')
		page.getByTestId(CypressSelector.BUTTON_OPPRETT_PERSONER)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_START_BESTILLING)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_FJERN_MILJOE_AVHENGIG)
		const fjernMiljoeAvhengige = page
		page.getByTestId(CypressSelector.BUTTON_VELG_MILJOE_AVHENGIG)
		const velgMiljoeAvhengige = page
		page.FIXME_each(async (element, index) => {
			await velgMiljoeAvhengige.nth(index).click()
			page.getByTestId(CypressSelector.BUTTON_VIDERE)
			await page.click()
			page.getByTestId(CypressSelector.BUTTON_VIDERE)
			await page.click()
			await expect(page.locator('#q1')).toBeChecked()
			await expect(page.locator('#q2')).toBeChecked()
			await expect(page.locator('#q4')).not.toBeChecked()
			page.getByTestId(CypressSelector.BUTTON_TILBAKE)
			await page.click()
			page.getByTestId(CypressSelector.BUTTON_TILBAKE)
			await page.click()
			await fjernMiljoeAvhengige.nth(index).click()
		})
	})
})
