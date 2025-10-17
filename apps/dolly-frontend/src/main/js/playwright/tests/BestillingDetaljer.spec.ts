import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Dolly Bestillingsdetaljer testing', async ({ page, context }) => {
	await page.goto('/gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER).click()
	await page.locator(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
	await expect(
		page.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT),
	).toBeDisabled()
	await page.locator(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
	await page.locator(':nth-child(3) > .dot-body-row > .dot-body-row-columns').click()
	await page.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT).click()
	await page
		.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER)
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL).click()
	await page.locator('#malnavn').fill('Testmal')
	await page.getByTestId(TestComponentSelectors.BUTTON_MALMODAL_LAGRE).click()
})
