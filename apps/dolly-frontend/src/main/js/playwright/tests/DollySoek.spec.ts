import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { dollySearchMock, fagsystemTyperMock } from '#/mocks/BasicMocks'

test.describe('Dolly-søk testing', () => {
	const dollySoekIdenter = new RegExp(/testnav-dolly-search-service\/api\/v1\/personer/)
	const personerTyper = new RegExp(/testnav-dolly-search-service\/api\/v1\/personer\/typer/)

	test('passes', async ({ page }) => {
		await page.goto('/gruppe')

		await page.route(dollySoekIdenter, (route) => {
			route.fulfill({ body: JSON.stringify(dollySearchMock) })
		})

		await page.route(personerTyper, (route) => {
			route.fulfill({ body: JSON.stringify(fagsystemTyperMock) })
		})

		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
		await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK).click()
		await expect(
			page
				.locator('h1')
				.getByText(/Søk etter personer i Dolly/)
				.first(),
		).toBeVisible()
		await page.getByTestId(TestComponentSelectors.EXPANDABLE_PERSONINFORMASJON).click()
		await page.getByTestId(TestComponentSelectors.TOGGLE_HAR_VERGE).click()
		await page.locator('div').getByText(/Cafe/).first().click()
		await page.getByTestId(TestComponentSelectors.BUTTON_NULLSTILL_SOEK).click()

		await expect(
			page
				.locator('div')
				.getByText(/Ingen søk er gjort/)
				.first(),
		).toBeVisible()

		await page.locator('.select-kjoenn__control').click()
		await page.locator('.select-kjoenn__menu').click()
		await page.getByTestId(TestComponentSelectors.BUTTON_VIS_I_GRUPPE).click()
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()
	})
})
