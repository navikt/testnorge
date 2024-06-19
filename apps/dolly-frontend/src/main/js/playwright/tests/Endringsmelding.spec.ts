import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test.describe('Navigering til endringsmelding', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')
		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_ENDRINGSMELDING).click()
		await expect(page).toHaveURL(/\/endringsmelding/)
	})
})
