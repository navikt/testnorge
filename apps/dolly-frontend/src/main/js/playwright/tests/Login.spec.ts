import { test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { varslingerVelkommenResponseMock } from '#/mocks/BasicMocks'

test('Login side og aksepter varsling', async ({ page }) => {
	await page.route('**/api/v1/varslinger', async (route) => {
		await route.fulfill({ body: JSON.stringify(varslingerVelkommenResponseMock) })
	})

	await page.goto('login')
	await page.getByTestId(TestComponentSelectors.BUTTON_LOGIN_NAV).click()
	await page.goto('gruppe')
	await page.getByTestId(TestComponentSelectors.BUTTON_VARSLING_LUKK).click()
})
