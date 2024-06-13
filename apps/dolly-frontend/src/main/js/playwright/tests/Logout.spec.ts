import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test.describe('Tester at bruker blir sendt til login side dersom man ikke er autorisert', () => {
	test('passes', async ({ page }) => {
		await page.route('**/current', async (route) => {
			await route.fulfill({ status: 401 })
		})

		await page.goto('gruppe')
		await expect(page).toHaveURL(/login/)
		await expect(page.getByTestId(TestComponentSelectors.BUTTON_LOGIN_NAV)).toBeVisible()
	})
})
