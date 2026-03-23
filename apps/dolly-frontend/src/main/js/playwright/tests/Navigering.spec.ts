import { expect, test } from '#/globalSetup'

import { ERROR_NAVIGATE_IDENT } from '@/ducks/errors/ErrorMessages'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { personFragmentNavigerMock } from '#/mocks/BasicMocks'

const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

const searchInput = (page) =>
	page.getByTestId(TestComponentSelectors.CONTAINER_FINN_PERSON_BESTILLING).locator('input')

test.describe('Navigering', () => {
	test('til ident som finnes i bestilling og tilbake igjen til bestillingen', async ({
		page,
	}) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER).click()

		await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING).first().waitFor()

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
		await expect(
			page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER),
		).toHaveAttribute('aria-checked', 'true')

		await page.getByTestId(TestComponentSelectors.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER).click()
		await expect(
			page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER),
		).toHaveAttribute('aria-checked', 'true')
	})

	test('til ident som IKKE finnes, deretter til ident som finnes i gruppe 1', async ({
		page,
	}) => {
		await page.goto('gruppe')

		await page.route(personFragmentNaviger, async (route) => {
			await route.fulfill({
				status: 404,
			})
		})

		const input = searchInput(page)

		await input.fill('12345')
		await page
			.getByRole('option', { name: '- TESTYTEST CAFE' })
			.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY)
			.click()

		await expect(
			page.getByTestId(TestComponentSelectors.ERROR_MESSAGE_NAVIGERING),
		).toHaveText(ERROR_NAVIGATE_IDENT)

		await page.route(personFragmentNaviger, async (route) => {
			await route.fulfill({
				status: 200,
				body: JSON.stringify(personFragmentNavigerMock),
			})
		})

		await input.fill('12345')

		await page
			.getByRole('option', { name: '- TESTYTEST CAFE' })
			.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY)
			.click()

		await expect(page).toHaveURL(/\/gruppe\/1/)
	})

	test('til bestilling fra søkeresultat', async ({ page }) => {
		await page.goto('/')

		const input = searchInput(page)
		await input.fill('test')

		await page
			.getByRole('option', { name: '#1 - Testytest' })
			.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY)
			.click()

		await expect(page.getByRole('heading', { name: 'Bestillingsstatus' })).toBeVisible()
	})

	test('til gruppe fra søkeresultat', async ({ page }) => {
		await page.goto('/')

		const input = searchInput(page)
		await input.fill('test')

		await page
			.getByRole('option', { name: 'Gruppe 1 - Testytest' })
			.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY)
			.click()

		await expect(page).toHaveURL(/\/gruppe\/1/)
	})

	test('til gruppe fra søkeresultat med gruppe-id', async ({ page }) => {
		await page.goto('/')

		const input = searchInput(page)
		await input.fill('Min test')

		await page
			.getByRole('option', { name: 'Gruppe 2 - Min testgruppe' })
			.getByTestId(TestComponentSelectors.BUTTON_NAVIGER_DOLLY)
			.click()

		await expect(page).toHaveURL(/\/gruppe\/2/)
	})
})
