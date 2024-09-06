import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test.describe('Test-Norge søk testing', () => {
	test('passes', async ({ page }) => {
		await page.goto('')

		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TESTNORGE).click()
		await page.getByTestId(TestComponentSelectors.INPUT_TESTNORGE_FNR).fill('123456')

		await page.waitForTimeout(400)

		await page.getByTestId(TestComponentSelectors.INPUT_TESTNORGE_FNR).press('Tab')

		await page.getByTestId(TestComponentSelectors.TITLE_TESTNORGE).focus()

		await expect(page.locator('.skjemaelement__feilmelding')).toBeVisible()
		await page.getByTestId(TestComponentSelectors.INPUT_TESTNORGE_FNR).clear()

		await page.waitForTimeout(200)
		await expect(page.locator('.skjemaelement__feilmelding')).not.toBeVisible()
	})
})
