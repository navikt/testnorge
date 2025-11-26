import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { gjeldendeBankidBrukerMock } from '#/mocks/BasicMocks'

test('shouldAllowUncheckingBankIdMiljo', async ({ page }) => {
	await page.route(new RegExp(/current/), async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(gjeldendeBankidBrukerMock),
			headers: { 'content-type': 'application/json' },
		})
	})
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TENOR).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_IMPORTER_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_NY_GRUPPE).click()
	await page.getByTestId(TestComponentSelectors.INPUT_NY_GRUPPE_NAVN).fill('TestNavn')
	await page.getByTestId(TestComponentSelectors.INPUT_NY_GRUPPE_HENSIKT).fill('TestHensikt')
	await page.getByTestId(TestComponentSelectors.BUTTON_NY_GRUPPE_OPPRETT).click()

	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByRole('img', { name: 'pensjon' }).click()
	await page.getByRole('checkbox', { name: 'Har uføretrygd' }).click()

	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	const q1 = page.locator('#q1')
	await q1.click()
	await expect(q1).toBeChecked()
})
