import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Navigering til endringsmelding', async ({ page }) => {
	await page.goto('gruppe')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_ENDRINGSMELDING).click()
	await expect(page).toHaveURL(/\/endringsmelding/)
})
