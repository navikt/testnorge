import { test } from '@playwright/test'
import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Login side og aksepter varsling', () => {
	test('passes', async ({ page }) => {
		await page.goto('login')
		page.getByTestId(CypressSelector.BUTTON_LOGIN_NAV)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_VARSLING_LUKK)
		await page.click()
	})
})
