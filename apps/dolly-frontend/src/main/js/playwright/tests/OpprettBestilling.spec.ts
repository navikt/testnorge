import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Oppretter bestilling med artifakter som er avhengige av Q1 eller Q2 og sjekker at disse blir huket av', async ({
	page,
}) => {
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	const fjernMiljoeAvhengige = await page
		.getByTestId(TestComponentSelectors.BUTTON_FJERN_MILJOE_AVHENGIG)
		.all()

	expect(fjernMiljoeAvhengige).not.toHaveLength(0)

	for (const button_velg_miljoeavhengig of await page
		.getByTestId(TestComponentSelectors.BUTTON_VELG_MILJOE_AVHENGIG)
		.all()) {
		await button_velg_miljoeavhengig.click()
		await page.waitForTimeout(100)
	}
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await expect(page.locator('#q1')).toBeChecked()
	await expect(page.locator('#q2')).toBeChecked()
	await expect(page.locator('#q4')).not.toBeChecked()

	await page.getByTestId(TestComponentSelectors.BUTTON_TILBAKE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_TILBAKE).click()
	for (const button_fjern_miljoeavhengig of fjernMiljoeAvhengige) {
		await button_fjern_miljoeavhengig.click()
	}
})

test('Oppretter bestilling med alle resterende artifakter', async ({ page }) => {
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()

	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	for (const button_velg_alle of await page
		.getByTestId(TestComponentSelectors.BUTTON_VELG_ALLE)
		.all()) {
		await button_velg_alle.click()
		await page.waitForTimeout(100)
	}
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.waitForTimeout(500)

	await page.getByTestId(TestComponentSelectors.BUTTON_TILBAKE).click()

	expect(page.url()).toContain('bestilling')
})
