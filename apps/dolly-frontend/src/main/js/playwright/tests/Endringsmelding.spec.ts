import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Navigering til endringsmelding', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')
		page.getByTestId(CypressSelector.BUTTON_HEADER_ENDRINGSMELDING)
		await page.click()
		await expect(page).toHaveURL(/\/endringsmelding/)
	})
})
