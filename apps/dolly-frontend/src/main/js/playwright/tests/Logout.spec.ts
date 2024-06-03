import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Tester at bruker blir sendt til login side dersom man ikke er autorisert', () => {
	test('passes', async ({ page }) => {
		const current = new RegExp(/current/)
		await page.goto('gruppe')
		await expect(page).toHaveURL(/login/)
		page.getByTestId(CypressSelector.BUTTON_LOGIN_NAV)
		await page.FIXME_should('exist')
	})
})
