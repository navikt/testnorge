import { expect, test } from '#/globalSetup'

import { ERROR_NAVIGATE_IDENT } from '@/ducks/errors/ErrorMessages'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { personFragmentNavigerMock } from '#/mocks/BasicMocks'

const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

test.describe('Navigering til ident som finnes i bestilling og tilbake igjen til bestillingen', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER).click()

		await page.waitForTimeout(1000)

		for (const button_open_bestilling of await page
			.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING)
			.all()) {
			await button_open_bestilling.click()
		}

		await page
			.locator('Button')
			.getByText(/12345678912/)
			.first()
			.click()
		await expect(page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER)).toHaveAttribute(
			'aria-checked',
			'true',
		)

		await page.getByTestId(TestComponentSelectors.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER).click()
		await expect(
			page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER),
		).toHaveAttribute('aria-checked', 'true')
	})
})

test.describe('Navigering til ident som finnes i gruppe 1', () => {
	test('passes', async ({ page }) => {
		await page.goto('gruppe')

		//Midlertidig not found på navigering til ident etter søk
		await page.route(personFragmentNaviger, async (route) => {
			await route.fulfill({
				status: 404,
			})
		})

		await page.getByTestId(TestComponentSelectors.INPUT_DOLLY_SOEK).fill('12345')
		await page.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY).click()
		await page.waitForTimeout(400)

		await expect(page.getByTestId(TestComponentSelectors.ERROR_MESSAGE_NAVIGERING)).toHaveText(
			ERROR_NAVIGATE_IDENT,
		)

		//Korrekt navigering igjen
		await page.route(personFragmentNaviger, async (route) => {
			await route.fulfill({
				status: 200,
				body: JSON.stringify(personFragmentNavigerMock),
			})
		})

		await page.getByTestId(TestComponentSelectors.TOGGLE_SEARCH_BESTILLING).click()
		await page.getByTestId(TestComponentSelectors.INPUT_DOLLY_SOEK).fill('1')

		await page.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY).click()
		await page.getByTestId(TestComponentSelectors.TOGGLE_SEARCH_PERSON).click()
		await page.getByTestId(TestComponentSelectors.INPUT_DOLLY_SOEK).fill('12345')

		await page.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY).click()
		await page.waitForTimeout(400)
		await expect(page).toHaveURL(/\/gruppe\/1/)
	})
})
