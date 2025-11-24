import { test } from '#/globalSetup'

import { TestComponentSelectors } from '#/mocks/Selectors'
import { testnorgeMalBestillinger } from '#/mocks/BasicMocks'

test('Dolly Bestillingsstatus testing', async ({ page }) => {
	await page.goto('/gruppe')
	const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
	await page.route(hentGruppeBestilling, (route) => {
		route.fulfill({ body: JSON.stringify(testnorgeMalBestillinger) })
	})

	await page.locator('div').getByText('Testytest', { exact: true }).click()

	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER).click()

	await page.locator(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
})
