import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Test-Norge søk testing', () => {
	test('passes', async ({ page }) => {
		await page.goto('')
		page.getByTestId(CypressSelector.BUTTON_HEADER_FINNPERSON)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_HEADER_TESTNORGE)
		await page.click()
		page.FIXME_dollyType(CypressSelector.INPUT_TESTNORGE_FNR, '123456')
		page.getByTestId(CypressSelector.TITLE_TESTNORGE)
		await page.FIXME_invoke('show')
		await page.click()
		await expect(page.locator('.skjemaelement__feilmelding')).toBeVisible()
		page.getByTestId(CypressSelector.INPUT_TESTNORGE_FNR)
		await page.clear()
		await page.waitForTimeout(200)
		await expect(page.locator('.skjemaelement__feilmelding')).not.toBeVisible()
	})
})
