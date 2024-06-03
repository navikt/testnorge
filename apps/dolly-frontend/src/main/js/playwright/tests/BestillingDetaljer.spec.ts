import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'
import { gjeldendeProfilMock } from '../mocks/BasicMocks'

test.describe('Dolly Bestillingsdetaljer testing', () => {
	test('passes', async ({ page, context }) => {
		await context.route('*', (route) => {
			route.fulfill({ body: '[]' })
		})
		await page.route('**/api/v1/current*', (route) => {
			route.fulfill({
				body: gjeldendeProfilMock,
			})
		})
		await page.goto('http://localhost:5678/gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()
		await page.locator(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
		await expect(
			page.getByTestId(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT),
		).toBeDisabled()
		await page.locator(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
		await page.locator(':nth-child(3) > .dot-body-row > .dot-body-row-columns').click()
		page.getByTestId(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT).click()
		page.getByTestId(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER).click()
		page.getByTestId(CypressSelector.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL).click()
		await page.locator('#malnavn').fill('Testmal')
		page.getByTestId(CypressSelector.BUTTON_MALMODAL_LAGRE).click()
	})
})
