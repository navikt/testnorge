import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Mine maler testing', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL_MINE_MALER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_KONTAKTINFO).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_KONTAKTSKJEMA).click()
	await expect(page.getByTestId(TestComponentSelectors.BUTTON_SEND_MELDING)).toBeDisabled()

	await page
		.getByTestId(TestComponentSelectors.INPUT_KONTAKT_MODAL)
		.fill('When you wish upon a star')

	await page.getByTestId(TestComponentSelectors.CHECKBOX_KONTAKT_ANONYM).click()
	await expect(page.getByTestId(TestComponentSelectors.CHECKBOX_KONTAKT_ANONYM)).toBeEnabled()

	await page.getByTestId(TestComponentSelectors.CHECKBOX_KONTAKT_ANONYM).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_SEND_MELDING).click()

	await page.getByTestId(TestComponentSelectors.INPUT_MALER_SOEK).fill('mal')
	await page.getByTestId(TestComponentSelectors.INPUT_MALER_SOEK).clear()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MALER_ORGANISASJONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MALER_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_MALER_SLETT).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_MALER_SLETT_BEKREFT).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_MALER_ENDRE_MALNAVN).click()
	await page.getByTestId(TestComponentSelectors.INPUT_MALER_ENDRE_MALNAVN).clear()

	await page.getByTestId(TestComponentSelectors.INPUT_MALER_ENDRE_MALNAVN).fill('Nytt navn på mal')

	await page.getByTestId(TestComponentSelectors.BUTTON_MALER_LAGRE_MALNAVN).click()
})
